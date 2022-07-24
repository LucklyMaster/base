package com.master.lib.permission

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

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
        dispatchCallback(it)
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
            if (viewModel.permissions.isEmpty()) {
                viewModel.permissions.addAll(this@RequestFragment.permissions!!)
            }
            if (viewModel.callback == null) {
                viewModel.callback = callback
            }
            requestPermissionLauncher.launch(viewModel.permissions.toTypedArray())
        }
    }

    private fun dispatchCallback(result: Map<String, Boolean>) {
        val allMap = mutableMapOf<String, Int>()
        val grantedList = mutableListOf<String>()
        val deniedList = mutableListOf<String>()
        result.forEach {
            if (it.value) {
                grantedList.add(it.key)
                allMap[it.key] = State.GRANTED
            } else {
                deniedList.add(it.key)
                if (!shouldShowRequestPermissionRationale(it.key)) {
                    allMap[it.key] = State.NEVER
                } else {
                    allMap[it.key] = State.DENIED
                }
            }
        }
        viewModel.onRequestPermissionsResultCallback(
            Response(allMap, viewModel.permissions, grantedList, deniedList)
        )
    }
}