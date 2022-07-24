package com.masterchan.mybase.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import com.master.lib.ext.println
import com.master.lib.permission.MPermissions
import com.masterchan.mybase.databinding.ActivityPermissionsBinding

class PermissionsActivity : MyBaseActivity<ActivityPermissionsBinding>(), View.OnClickListener {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setOnViewClickListeners(this) { arrayOf(btnStorge) }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnStorge -> {
                MPermissions.with(this)
                    .permissions(
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS
                    )
                    .request {
                        it.println()
                    }
            }
        }
    }

}