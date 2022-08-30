package com.master.lib.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize

/**
 * startForActivityResult帮助类，在调用[launch]之前需要先调用[register]方法注册
 * @author: MasterChan
 * @date: 2022-06-26 17:12
 */
open class ActivityResultHelper {

    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private val activityResult = mutableMapOf<String, ActivityResult.() -> Unit>()
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    open fun register(activity: ComponentActivity) {
        this.activity = activity
        activityResultLauncher = activity.activityResultRegistry.register(
            System.currentTimeMillis().toString(), ActivityForContract()
        ) {
            val key = it.input.getStringExtra("flag")
            activityResult[key]?.invoke(ActivityResult(it.resultCode, it.output))
            activityResult.remove(key)
        }
    }

    open fun register(fragment: Fragment) {
        this.fragment = fragment
        activityResultLauncher = fragment.registerForActivityResult(ActivityForContract()) {
            val key = it.input.getStringExtra("flag")
            activityResult[key]?.invoke(ActivityResult(it.resultCode, it.output))
            activityResult.remove(key)
        }
    }

    open fun launch(clazz: Class<out Activity>, result: ActivityResult.() -> Unit) {
        if (activity != null) {
            launch(Intent(activity, clazz), result)
        } else {
            launch(Intent(fragment!!.requireContext(), clazz), result)
        }
    }

    open fun launch(intent: Intent, result: ActivityResult.() -> Unit) {
        val key = System.currentTimeMillis().toString()
        activityResult[key] = result
        activityResultLauncher?.launch(intent.apply { putExtra("flag", key) })
    }

    @MainThread
    open fun unregister() {
        activityResultLauncher?.unregister()
    }

    open class ActivityForContract : ActivityResultContract<Intent, ActivityForResult>() {
        private lateinit var requestIntent: Intent

        override fun createIntent(context: Context, input: Intent): Intent {
            this.requestIntent = input
            return requestIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ActivityForResult {
            return ActivityForResult(resultCode, requestIntent, intent)
        }
    }

    @Parcelize
    open class ActivityForResult(
        val resultCode: Int, val input: Intent, val output: Intent?
    ) : Parcelable
}