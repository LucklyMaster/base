package com.master.lib.permission

import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.master.BuildConfig
import com.master.lib.utils.AndroidVersion
import com.master.lib.utils.DeviceUtils
import com.master.lib.utils.XmlUtils
import com.master.lib.widget.ActivityResultHelper
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * RequestFragment
 * @author: MasterChan
 * @date: 2022-07-24 15:55
 */
class RequestFragment : Fragment() {

    /**
     * 权限申请结果回调
     */
    private var resultCallback: PermissionsResultCallback? = null

    /**
     * 特殊权限拦截器
     */
    private var interceptorMap = mutableMapOf<String, SpecialPermissionInterceptor>()
    private val viewModel by lazy { ViewModelProvider(this).get(RequestModel::class.java) }

    /**
     * 注册危险权限申请
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        onPermissionsResultCallback()
    }

    /**
     * 注册特殊权限申请
     */
    private val activityResultHelper = ActivityResultHelper()

    companion object {
        fun request(
            activity: FragmentActivity,
            permissions: MutableList<String>,
            callback: PermissionsResultCallback?,
            interceptorMap: Map<String, SpecialPermissionInterceptor>?
        ) {
            val fragment = RequestFragment()
            fragment.attachActivity(activity)
            fragment.setResultCallback(callback)
            fragment.request(permissions)
            fragment.addSpecialPermissionInterceptor(interceptorMap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultHelper.register(this)
    }

    private fun attachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(this, toString())
            .commitAllowingStateLoss()
    }

    private fun detachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
        activityResultHelper.unregister()
    }

    private fun setResultCallback(callback: PermissionsResultCallback?) {
        this.resultCallback = callback
    }

    private fun addSpecialPermissionInterceptor(interceptorMap: Map<String, SpecialPermissionInterceptor>?) {
        interceptorMap?.let { this.interceptorMap.putAll(it) }
    }

    private fun request(permissions: MutableList<String>) {
        lifecycleScope.launchWhenResumed {
            if (viewModel.permissionsResultCallback == null) {
                viewModel.permissionsResultCallback = resultCallback
            }
            if (viewModel.interceptorMap.isEmpty()) {
                viewModel.interceptorMap.putAll(interceptorMap)
            }
            val permissionsMap = Utils.convertPermissions2CurVersion(permissions)
            if (BuildConfig.DEBUG) {
                checkPermissions(permissionsMap.keys.toList())
            }
            val requestPermissions = permissionsMap.filter { it.value }.keys.toList()
            if (viewModel.permissions.isEmpty()) {
                viewModel.permissions.addAll(requestPermissions)
            }
            //如果权限已经全部申请，直接回调结果
            if (Utils.isAllGranted(requireContext(), requestPermissions)) {
                dispatchCallback()
            } else {
                //拆分出特殊权限
                val specialPermissions = requestPermissions.intersect(
                    SpecialPermissions.list.toSet()
                )
                //包含特殊权限，申请特殊权限
                if (specialPermissions.isNotEmpty()) {
                    requestSpecialPermissions(specialPermissions.toList())
                    //延迟一段时间获取特殊权限结果，防止授权了，却回调失败
                    delay(getSpecialPermissionResultDelay())
                }
                requestPermissionLauncher.launch(requestPermissions.toTypedArray())
            }
        }
    }

    /**
     * 检查AndroidManifest中是否申明权限
     * @param requestPermissions List<String>
     */
    private fun checkPermissions(requestPermissions: List<String>) {
        val manifestPermissions = XmlUtils.getManifestPermissions(requireContext()).map { it.key }
        requestPermissions.forEach {
            check(manifestPermissions.contains(it)) {
                "the request permissions[$it] must be contains int the AndroidManifest.xml"
            }
        }
    }

    private fun onPermissionsResultCallback() {
        dispatchCallback()
        detachActivity(requireActivity())
    }

    private fun getSpecialPermissionResultDelay(): Long {
        val product = DeviceUtils.getProduct().lowercase()
        return when {
            product.contains("huawei") -> if (AndroidVersion.isAndroid8()) 300 else 500
            else -> if (AndroidVersion.isAndroid11()) 200 else 300
        }
    }

    private suspend fun requestSpecialPermissions(permissions: List<String>) {
        permissions.forEach { permission ->
            if (Utils.isGranted(requireContext(), permission)) {
                return@forEach
            }
            if (!specialPermissionIntercept(permission)) {
                return@forEach
            }
            suspendCoroutine {
                try {
                    activityResultHelper.launch(
                        Utils.getAppDetailIntent(requireContext(), permission)
                    ) {
                        it.resume(onSpecialPermissionsResultCallback(this))
                    }
                } catch (e: Exception) {
                    activityResultHelper.launch(
                        Utils.getAppDetailIntent(requireContext(), "")
                    ) {
                        it.resume(onSpecialPermissionsResultCallback(this))
                    }
                }
            }
        }
    }

    private suspend fun specialPermissionIntercept(permission: String) = suspendCoroutine<Boolean> {
        val interceptor = viewModel.interceptorMap[permission]
        if (interceptor != null) {
            interceptor.onIntercept { result ->
                it.resume(result)
            }
        } else {
            it.resume(true)
        }
    }

    private fun onSpecialPermissionsResultCallback(activityResult: ActivityResult) {

    }

    private fun dispatchCallback() {
        val grantedList = mutableListOf<String>()
        val deniedList = mutableListOf<String>()
        val neverAskList = mutableListOf<String>()
        viewModel.permissions.forEach {
            val isGranted = Utils.isGranted(requireContext(), it)
            if (isGranted) {
                grantedList.add(it)
            } else {
                deniedList.add(it)
            }
            if (Utils.isNeverAsk(requireContext(), it)) {
                neverAskList.add(it)
            }
        }
        viewModel.permissionsResultCallback?.callback(
            PermissionResponse(viewModel.permissions, grantedList, deniedList, neverAskList)
        )
    }
}