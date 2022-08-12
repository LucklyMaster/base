package com.master.mybase.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.master.lib.dialog.AlertDialog
import com.master.lib.ext.logD
import com.master.lib.ext.toast
import com.master.lib.permission.MPermissions
import com.master.mybase.databinding.ActivityPermissionsBinding

class PermissionsActivity : MyBaseActivity<ActivityPermissionsBinding>(), View.OnClickListener {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        installSplashScreen()
        setOnViewClickListeners(this) {
            arrayOf(
                btnStorge, btnPackage, btnAudio, btnBluetooth, btnAllFile, btnAlertWindow, btnDetail
            )
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnStorge -> {
                MPermissions.with(this)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request {
                        it.logD()
                        toast(if (it.isAllGranted) "success" else "failed")
                    }
            }
            binding.btnPackage -> {
                MPermissions.with(this)
                    .permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                    .request {
                        it.logD()
                        toast(if (it.isAllGranted) "success" else "failed")
                    }
            }
            binding.btnAllFile -> {
                MPermissions.with(this)
                    .permissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .addSpecialPermissionInterceptor(Manifest.permission.MANAGE_EXTERNAL_STORAGE) {
                        AlertDialog.Builder(context)
                            .setMessage("程序运行需要管理文件权限")
                            .setPositiveText("确定")
                            .setNegativeText("取消")
                            .setOnPositiveClickListener { dialog ->
                                dialog.dismiss()
                                it.onResult(true)
                            }
                            .setOnNegativeClickListener { dialog ->
                                dialog.dismiss()
                                it.onResult(false)
                            }
                            .create()
                            .setCancellable(false)
                            .show()
                    }
                    .addSpecialPermissionInterceptor(Manifest.permission.SYSTEM_ALERT_WINDOW) {
                        AlertDialog.Builder(context)
                            .setMessage("程序运行需要悬浮窗权限")
                            .setPositiveText("确定")
                            .setNegativeText("取消")
                            .setOnPositiveClickListener { dialog ->
                                dialog.dismiss()
                                it.onResult(true)
                            }
                            .setOnNegativeClickListener { dialog ->
                                dialog.dismiss()
                                it.onResult(false)
                            }
                            .create()
                            .setCancellable(false)
                            .show()
                    }
                    .request {
                        it.logD()
                        toast(if (it.isAllGranted) "success" else "failed")
                    }
            }
            binding.btnAudio -> {
                //经测试，在Android模拟器上，权限申请Dialog未设置setCancelable(false)，导致shouldShowRequestPermissionRationale()
                //返回false，通过shouldShowRequestPermissionRationale()判断的“不再询问”将会错误的返回
                //true，其他框架同样存在
                MPermissions.with(this)
                    .permissions(Manifest.permission.RECORD_AUDIO)
                    .permissions(Manifest.permission.READ_PHONE_NUMBERS)
                    .request {
                        it.logD()
                        toast(if (it.isAllGranted) "success" else "failed")
                    }
            }
            binding.btnBluetooth -> {
                MPermissions.with(this)
                    .permissions(Manifest.permission.BLUETOOTH_CONNECT)
                    .permissions(Manifest.permission.BLUETOOTH_SCAN)
                    .permissions(Manifest.permission.BLUETOOTH_ADVERTISE)
                    .request {
                        it.logD()
                        toast(if (it.isAllGranted) "success" else "failed")
                    }
            }
            binding.btnAlertWindow -> {
                MPermissions.with(this)
                    .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .permissions(Manifest.permission.WRITE_SETTINGS)
                    .isNeedAllGranted(false)
                    .request {
                        it.logD()
                        toast(if (it.isAllGranted) "success" else "failed")
                    }
            }
            binding.btnDetail -> {
                startActivity(MPermissions.getAppDetailIntent(""))
            }
        }
    }
}