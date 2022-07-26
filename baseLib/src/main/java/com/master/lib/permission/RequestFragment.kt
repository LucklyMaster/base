package com.master.lib.permission

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.master.lib.ext.isScopedStorage
import com.master.lib.utils.XmlUtils
import com.masterchan.lib.BuildConfig


/**
 * RequestFragment
 * @author: MasterChan
 * @date: 2022-07-24 15:55
 */
class RequestFragment : Fragment() {

    private var callback: Callback? = null
    private val viewModel by lazy { ViewModelProvider(this).get(RequestModel::class.java) }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        dispatchCallback(it.toMutableMap())
        detachActivity(requireActivity())
    }

    companion object {
        fun request(
            activity: FragmentActivity,
            permissions: MutableList<String>,
            callback: Callback?
        ) {
            val fragment = RequestFragment()
            fragment.attachActivity(activity)
            fragment.setCallback(callback)
            fragment.request(permissions)
        }
    }

    private fun attachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(this, toString())
            .commitAllowingStateLoss()
    }

    private fun detachActivity(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
    }

    private fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    private fun request(permissions: MutableList<String>) {
        lifecycleScope.launchWhenResumed {
            if (viewModel.callback == null) {
                viewModel.callback = callback
            }
            val permissionsMap = Utils.convertPermissions2CurVersion(permissions)
            if (BuildConfig.DEBUG) {
                checkPermissions(permissionsMap.keys.toList())
            }
            val requestPermissions = permissionsMap.filter { it.value }.keys.toList()
            if (viewModel.permissions.isEmpty()) {
                viewModel.permissions.addAll(requestPermissions)
            }
            //适配了分区存储，去掉存储权限不申请，直接返回true
            val optimizedPermissions = optimizeScopedStoragePermission(requestPermissions)
            if (optimizedPermissions.isEmpty() ||
                Utils.isAllGranted(requireContext(), optimizedPermissions)
            ) {
                dispatchCallback(requestPermissions.associateWith { true }.toMutableMap())
            } else {
                //拆分出特殊权限
                val specialPermissions = optimizedPermissions.intersect(
                    SpecialPermissions.list.toSet()
                )
                if (specialPermissions.isEmpty()) {
                    requestPermissionLauncher.launch(optimizedPermissions.toTypedArray())
                } else {
                    requestSpecialPermissions(specialPermissions.toList())
                }
            }
        }
    }

    private fun optimizeScopedStoragePermission(permissions: List<String>): MutableList<String> {
        val list = permissions.toMutableList()
        if (isScopedStorage) {
            list.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            list.remove(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return list
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

    private suspend fun requestSpecialPermissions(permissions: List<String>) {
        permissions.forEach {
        }
    }

    private fun dispatchCallback(result: Map<String, Boolean>) {
        val grantedList = mutableListOf<String>()
        val deniedList = mutableListOf<String>()
        val neverAskList = mutableListOf<String>()
        result.keys.forEach {
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
        viewModel.onRequestPermissionsResultCallback(
            Response(viewModel.permissions, grantedList, deniedList, neverAskList)
        )
    }
}