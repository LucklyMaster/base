package com.master.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.XmlResourceParser
import android.text.TextUtils
import com.master.ext.logE
import com.master.ext.packageName
import java.lang.reflect.InvocationTargetException


/**
 * XmlUtils
 * @author: MasterChan
 * @date: 2022-07-25 11:20
 */
object XmlUtils {

    fun getManifestPermissions(context: Context): Map<String, Int> {
        val permissions = mutableMapOf<String, Int>()
        val parser = parseAndroidManifest(context)
        if (parser != null) {
            try {
                do {
                    // 当前节点必须为标签头部
                    if (parser.eventType != XmlResourceParser.START_TAG) {
                        continue
                    }
                    // 当前标签必须为 uses-permission
                    if ("uses-permission" != parser.name) {
                        continue
                    }
                    val nameSpace = "http://schemas.android.com/apk/res/android"
                    permissions[parser.getAttributeValue(nameSpace, "name")] =
                        parser.getAttributeIntValue(nameSpace, "maxSdkVersion", Int.MAX_VALUE)
                } while (parser.next() != XmlResourceParser.END_DOCUMENT)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                parser.close()
            }
        }
        if (permissions.isEmpty()) {
            try {
                // 当清单文件没有注册任何权限的时候，那么这个数组对象就是空的
                val requestedPermissions = context.packageManager.getPackageInfo(
                    packageName, PackageManager.GET_PERMISSIONS
                ).requestedPermissions
                if (requestedPermissions != null) {
                    for (permission in requestedPermissions) {
                        permissions[permission] = Int.MAX_VALUE
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return permissions
    }

    fun parseAndroidManifest(context: Context): XmlResourceParser? {
        val cookie = findApkPathCookie(context) ?: return null
        val result = runCatching {
            val parser = context.assets.openXmlResourceParser(cookie, "AndroidManifest.xml")
            do {
                // 当前节点必须为标签头部
                if (parser.eventType != XmlResourceParser.START_TAG) {
                    continue
                }
                if ("manifest" == parser.name) {
                    // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
                    if (TextUtils.equals(packageName, parser.getAttributeValue(null, "package"))) {
                        return parser
                    }
                }
            } while (parser.next() != XmlResourceParser.END_DOCUMENT)
        }
        if (result.isFailure) {
            result.exceptionOrNull()?.logE()
        }
        return null
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun findApkPathCookie(context: Context): Int? {
        val assets = context.assets
        val apkPath = context.applicationInfo.sourceDir
        try {
            val method = assets.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
            val cookie = method.invoke(assets, apkPath)
            if (cookie != null) {
                return cookie as Int
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return null
    }
}