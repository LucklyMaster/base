package com.master.mybase.activity

import android.os.Bundle
import com.master.mybase.R
import com.master.mybase.databinding.ActivityAutoFoldKeyboardBinding
import com.master.mybase.fragment.AutoFoldKeyboardFragment

class AutoFoldKeyboardActivity : MyBaseActivity<ActivityAutoFoldKeyboardBinding>() {

    override var isAutoFoldKeyboard = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction()
            .add(R.id.container1, AutoFoldKeyboardFragment())
            .commit()
    }
}