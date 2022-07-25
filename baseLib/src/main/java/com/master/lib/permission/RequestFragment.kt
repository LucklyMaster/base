package com.master.lib.permission

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.master.lib.ext.isScopedStorage
import com.master.lib.utils.AndroidVersion
import com.master.lib.utils.XmlUtils
import com.masterchan.lib.BuildConfig

/**
 * RequestFragment
 * @author: MasterChan
 * @date: 2022-07-24 15:55
 */
class RequestFragment : Fragment() {

    private var callback: Callback? = null
    private var permissions: MutableList<String>? = null
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
        this.permissions = permissions
        lifecycleScope.launchWhenResumed {
            if (viewModel.callback == null) {
                viewModel.callback = callback
            }
            val list = Utils.transformPermissions(permissions)
            if (BuildConfig.DEBUG) {
                checkPermissions(list)
            }
            if (viewModel.permissions.isEmpty()) {
                viewModel.permissions.addAll(this@RequestFragment.permissions!!)
            }
            val list = optimizeStoragePermission(viewModel.permissions)
            if (list.isEmpty()) {
                dispatchCallback(viewModel.permissions.associateWith { true }.toMutableMap())
            } else {
                requestPermissionLauncher.launch(list.toTypedArray())
            }
        }
    }

    private fun optimizePermissions(permissions: MutableList<String>) {
        //Android12之前扫描蓝牙需要精确定位权限
        if (!AndroidVersion.isAndroid12() &&
            permissions.contains(Manifest.permission.BLUETOOTH_SCAN) &&
            !permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            permissions.remove(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        //Android11存储权限
        if (permissions.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
            if (!AndroidVersion.isAndroid11()) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        //Android10活动步数权限
        if (!AndroidVersion.isAndroid10() &&
            permissions.contains(Manifest.permission.ACTIVITY_RECOGNITION) &&
            !permissions.contains(Manifest.permission.BODY_SENSORS)
        ) {
            permissions.add(Manifest.permission.BODY_SENSORS)
        }
        //Android8手机号码权限
        if (permissions.contains(Manifest.permission.READ_PHONE_NUMBERS)) {
            if (!AndroidVersion.isAndroid8()) {
                permissions.remove(Manifest.permission.READ_PHONE_NUMBERS)
                permissions.add(Manifest.permission.READ_PHONE_STATE)
            }
        }
    }

    private fun optimizeStoragePermission(permissions: MutableList<String>): MutableList<String> {
        val list = permissions.toMutableList()
        if (isScopedStorage) {
            list.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            list.remove(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return list
    }

    private fun checkPermissions(requestPermissions: MutableList<String>) {
        val manifestPermissions = XmlUtils.getManifestPermissions(requireContext()).map { it.key }
        requestPermissions.forEach {
            check(manifestPermissions.contains(it)) {
                "the request permissions[$it] must be contains int the AndroidManifest.xml"
            }
        }
    }

    private fun dispatchCallback(result: MutableMap<String, Boolean>) {
        val grantedList = mutableListOf<String>()
        val deniedList = mutableListOf<String>()
        val neverAskList = mutableListOf<String>()
        if (isScopedStorage) {
            if (viewModel.permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                result[Manifest.permission.WRITE_EXTERNAL_STORAGE] = true
            }
            if (viewModel.permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                result[Manifest.permission.READ_EXTERNAL_STORAGE] = true
            }
        }
        result.forEach {
            if (it.value) {
                grantedList.add(it.key)
            } else {
                deniedList.add(it.key)
                if (!shouldShowRequestPermissionRationale(it.key)) {
                    neverAskList.add(it.key)
                }
            }
        }
        viewModel.onRequestPermissionsResultCallback(
            Response(viewModel.permissions, grantedList, deniedList, neverAskList)
        )
    }
}