package com.masterchan.mybase

import android.os.Bundle
import com.masterchan.mybase.databinding.ActivityAutoFoldKeyboardBinding
import com.masterchan.mybase.fragment.AutoFoldKeyboardFragment

class AutoFoldKeyboardActivity : MyBaseActivity<ActivityAutoFoldKeyboardBinding>() {

    override var isAutoFoldKeyboard = true

    override fun onCreated(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction()
            .add(R.id.container1, AutoFoldKeyboardFragment())
            .commit()
    }
}