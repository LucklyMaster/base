package com.master.lib

import android.os.Build
import android.os.Process
import com.master.lib.ext.application
import com.master.lib.ext.create
import com.master.lib.ext.versionCode
import com.master.lib.ext.versionName
import com.master.lib.log.MLog
import com.master.lib.utils.DateUtils
import com.master.lib.utils.DateUtils.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.*
import kotlin.system.exitProcess

/**
 * Crash捕获
 * @author: MasterChan
 * @date: 2022-06-07 22:24
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    private var saveCrash = BuildConfig.DEBUG
    private var saveDir = "${application.filesDir.absolutePath}/MCLib/crash"
    private var saveDays = 10
    private var action: (() -> Unit) = { defaultAction() }
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null

    companion object {
        val instance: CrashHandler by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CrashHandler() }
    }

    fun saveCrash(saveCrash: Boolean) = apply {
        this.saveCrash = saveCrash
    }

    fun setSaveDir(saveDir: String) = apply {
        this.saveDir = saveDir
    }

    fun setSaveDays(saveDays: Int) = apply {
        this.saveDays = saveDays
    }

    fun setAction(action: () -> Unit) = apply {
        this.action = action
    }

    private fun defaultAction() {
        ActivityStack.instance.finishAll()
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    fun init() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        GlobalScope.launch(Dispatchers.IO) {
            File(saveDir).listFiles { file -> file.isFile }?.forEach {
                if (DateUtils.dayDiff(Date(it.lastModified()), Date()) > saveDays) {
                    it.delete()
                }
            }
        }
    }

    override fun uncaughtException(thread: Thread, tr: Throwable) {
        if (!handleException(tr) && defaultHandler != null) {
            defaultHandler?.uncaughtException(thread, tr)
        }
    }

    private fun handleException(tr: Throwable): Boolean {
        MLog.e(tr)
        saveCrash(tr, collectDeviceInfo())
        action.invoke()
        return true
    }

    private fun collectDeviceInfo(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        try {
            map["versionName"] = versionName
            map["versionCode"] = versionCode.toString()
            Build::class.java.declaredFields.forEach {
                it.isAccessible = true
                map[it.name] = it[null]?.toString() ?: "NULL"
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return map
    }

    private fun saveCrash(ex: Throwable, map: Map<String, String>) {
        val sb = StringBuilder()
        sb.append(JSONObject(map).toString(2))
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        sb.append("\n").append(result)
        val path = "$saveDir/${Date().toString("yyyyMMddHHmmss")}.crash"
        File(path).apply { create() }.writeText(sb.toString())
    }
}