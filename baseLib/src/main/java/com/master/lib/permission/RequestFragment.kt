package com.master.lib.permission

import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.master.lib.BuildConfig
import com.master.lib.utils.AndroidVersion
import com.master.lib.utils.DeviceUtils
import com.master.lib.utils.XmlUtils
import com.master.lib.widget.ActivityResultHelper
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 权限申请的逻辑处理
 * @author: MasterChan
 * @date: 2022-07-24 15:55
 */
class RequestFragment : Fragment() {

    /**
     * 权限申请结果回调
     */
    private var resultCallback: OnResultCallback? = null

    /**
     * 是否将所有权限皆授权判断为授权成功，只针对失败拦截器生效
     */
    private var isNeedAllGranted = false

    /**
     * 失败拦截器
     */
    private var onDeniedInterceptor: OnDeniedInterceptor? = null

    /**
     * 特殊权限拦截器
     */
    private var specialInterceptors = mutableMapOf<String, SpecialPermissionInterceptor>()
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
            resultCallback: OnResultCallback?,
            isNeedAllGranted: Boolean,
            onDeniedInterceptor: OnDeniedInterceptor?,
            specialInterceptors: Map<String, SpecialPermissionInterceptor>?
        ) {
            val fragment = RequestFragment()
            fragment.attachActivity(activity)
            fragment.resultCallback = resultCallback
            fragment.isNeedAllGranted = isNeedAllGranted
            fragment.onDeniedInterceptor = onDeniedInterceptor
            specialInterceptors?.let { fragment.specialInterceptors.putAll(it) }
            fragment.request(permissions)
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
        lifecycleScope.launchWhenCreated {
            viewModel.permissionsResultCallback = resultCallback
            viewModel.specialInterceptors.putAll(specialInterceptors)
            viewModel.isNeedAllGranted = isNeedAllGranted
            viewModel.onDeniedInterceptor = onDeniedInterceptor
        }
    }

    private fun detachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
        activityResultHelper.unregister()
    }

    private fun request(permissions: MutableList<String>) {
        lifecycleScope.launchWhenResumed {
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
        val interceptor = viewModel.specialInterceptors[permission]
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
        //权限申请结果
        val response = PermissionResponse(
            viewModel.permissions, grantedList, deniedList, neverAskList
        )
        if (viewModel.onDeniedInterceptor != null) {
            //需要全部申请成功
            if (viewModel.isNeedAllGranted) {
                if (!response.isAllGranted) {
                    //未全部申请成功
                    viewModel.onDeniedInterceptor!!.callback(requireActivity(), response)
                } else {
                    //全部申请成功
                    viewModel.permissionsResultCallback?.callback(response)
                }
            } else {
                if (!response.isGranted) {
                    //全部申请失败
                    viewModel.onDeniedInterceptor!!.callback(requireActivity(), response)
                } else {
                    //部分申请成功
                    viewModel.permissionsResultCallback?.callback(response)
                }
            }
        } else {
            viewModel.permissionsResultCallback?.callback(response)
        }
    }
}