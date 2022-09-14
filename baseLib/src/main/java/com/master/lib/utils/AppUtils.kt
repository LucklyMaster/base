package com.master.lib.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import com.master.lib.ext.application

/**
 * AppUtils
 * @author: MasterChan
 * @date: 2022-9-13 23:08
 */
object AppUtils {

    /**
     * 获取进程名称
     * @param pid [android.os.Process.myPid]
     * @return String?
     */
    fun getProcessName(pid: Int): String? {
        val activityManager = application.getSystemService(ActivityManager::class.java)
        activityManager.runningAppProcesses.forEach {
            if (it.pid == pid) {
                return it.processName
            }
        }
        return null
    }

    /**
     * 获取App名称
     * @param packageName String
     * @return String?
     */
    fun getAppName(packageName: String): String? {
        return try {
            val pi = application.packageManager.getPackageInfo(packageName, 0)
            pi?.applicationInfo?.loadLabel(application.packageManager)?.toString()
        } catch (e: Throwable) {
            null
        }
    }

    /**
     * 是否前台运行
     * @return Boolean
     */
    fun isForeground(): Boolean {
        val activityManager = application.getSystemService(ActivityManager::class.java)
        activityManager.runningAppProcesses.forEach {
            if (it.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (it.processName == application.applicationInfo.processName) {
                    return true
                }
            }
        }
        return false
    }
}