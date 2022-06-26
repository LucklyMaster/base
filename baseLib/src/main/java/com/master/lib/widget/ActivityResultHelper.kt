package com.master.lib.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.parcelize.Parcelize

/**
 * startForActivityResult帮助类
 * @author: MasterChan
 * @date: 2022-06-26 17:12
 */
open class ActivityResultHelper(private val activity: ComponentActivity) {

    private lateinit var activityResult: HashMap<String, ActivityResult.() -> Unit>
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    fun registerForActivityResult() {
        activityResult = hashMapOf()
        activityResultLauncher =
            activity.activityResultRegistry.register(
                System.currentTimeMillis().toString(), ActivityForContract()
            ) {
                val key = it.input.getStringExtra("flag")
                activityResult[key]?.invoke(ActivityResult(it.resultCode, it.output))
                activityResult.remove(key)
            }
    }

    fun launch(clazz: Class<out Activity>, result: ActivityResult.() -> Unit) {
        val key = System.currentTimeMillis().toString()
        activityResult[key] = result
        activityResultLauncher?.launch(Intent(activity, clazz).apply { putExtra("flag", key) })
    }

    class ActivityForContract : ActivityResultContract<Intent, ActivityForResult>() {
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
    class ActivityForResult(
        val resultCode: Int, val input: Intent, val output: Intent?
    ) : Parcelable
}