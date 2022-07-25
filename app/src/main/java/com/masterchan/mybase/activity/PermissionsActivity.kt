package com.masterchan.mybase.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import com.master.lib.ext.logD
import com.master.lib.ext.println
import com.master.lib.permission.MPermissions
import com.masterchan.mybase.databinding.ActivityPermissionsBinding

class PermissionsActivity : MyBaseActivity<ActivityPermissionsBinding>(), View.OnClickListener {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setOnViewClickListeners(this) { arrayOf(btnStorge, btnAudio, btnPhone, btnBluetooth) }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnStorge -> {
                MPermissions.with(this)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request {
                        it.logD()
                    }
            }
            binding.btnAudio -> {
                //经测试，在Android模拟器上，权限申请Dialog未设置setCancelable(false)，导致shouldShowRequestPermissionRationale()
                //返回false，通过shouldShowRequestPermissionRationale()判断的“不再询问”将会错误的返回
                //true，其他框架同样存在
                MPermissions.with(this).permissions(Manifest.permission.RECORD_AUDIO).request {
                    it.logD()
                }
            }
            binding.btnPhone -> {
                MPermissions.with(this)
                    .permissions(Manifest.permission.READ_PHONE_NUMBERS)
                    .request {
                        it.logD()
                    }
            }
            binding.btnBluetooth -> {
                // MPermissions.with(this).permissions(Manifest.permission.BLUETOOTH_CONNECT).request {
                //     it.logD()
                // }
                checkSelfPermission(Manifest.permission.BLUETOOTH).println()
            }
        }
    }

}