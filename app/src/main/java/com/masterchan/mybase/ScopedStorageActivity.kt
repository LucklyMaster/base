package com.masterchan.mybase

import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.net.toUri
import com.masterchan.lib.MToast
import com.masterchan.lib.log.MLog
import com.masterchan.lib.sandbox.ScopedStorage
import com.masterchan.mybase.databinding.ActivityScopedStorageBinding
import java.io.File

/**
 * 分区存储测试
 * @author: MasterChan
 * @date: 2022-6-12 1:01
 */
class ScopedStorageActivity : MyBaseActivity<ActivityScopedStorageBinding>() {

    private val relativePath = "${Environment.DIRECTORY_DOCUMENTS}/MyBase1/a.txt"
    private val relativePath2 = "${Environment.DIRECTORY_DOCUMENTS}/MyBase2/b.log"
    private val relativePath3 = "${Environment.DIRECTORY_DOCUMENTS}/MyBase3/good"

    override fun onCreated(savedInstanceState: Bundle?) {

    }

    fun createFile(view: View) {
        val result1 = ScopedStorage.file.createFile(
            Environment.getExternalStorageDirectory().absolutePath + "/" + relativePath
        )
        val result2 = ScopedStorage.file.createFile(relativePath2)
        val result3 = ScopedStorage.file.createFile("$relativePath3/c.log", "OK".toByteArray())
        val result4 = ScopedStorage.file.createFile("$relativePath3/d", "OK".toByteArray()) {
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        }
        MToast.showLong("创建文件:$result1,$result2,$result3,$result4")
    }

    fun exist(view: View) {
        val result1 = ScopedStorage.file.exist(relativePath)
        val result2 = ScopedStorage.file.exist(relativePath2)
        val result3 = ScopedStorage.file.exist(relativePath3)
        MToast.showLong("exist:$result1,$result2,$result3")
    }

    fun isFile(view: View) {
        val result1 = ScopedStorage.file.isFile(relativePath)
        val result2 = ScopedStorage.file.isFile(relativePath2)
        val result3 = ScopedStorage.file.isFile(relativePath3)
        MToast.showLong("isFile:$result1,$result2,$result3")
    }

    fun isDir(view: View) {
        val result1 = ScopedStorage.file.isDirectory(relativePath)
        val result2 = ScopedStorage.file.isDirectory(relativePath2)
        val result3 = ScopedStorage.file.isDirectory(relativePath3)
        MToast.showLong("isDir:$result1,$result2,$result3")
    }

    fun writeFile(view: View) {
        ScopedStorage.file.write(
            File(absolutePath(relativePath)).toUri(), "writeFile写入".toByteArray()
        )
        ScopedStorage.file.write(
            File(absolutePath(relativePath)).toUri(), "writeFile追加".toByteArray(), true
        )
        ScopedStorage.file.write(relativePath2, "writeFile写入".toByteArray())
        ScopedStorage.file.write(relativePath2, "writeFile追加".toByteArray(), true)
    }

    fun deleteFile(view: View) {
        val result1 = ScopedStorage.file.delete(relativePath)
        val result2 = ScopedStorage.file.delete(relativePath2)
        val result3 = ScopedStorage.file.delete(relativePath3)
        MToast.showLong("删除文件:$result1,$result2,$result3")
    }

    fun getAllFiles(view: View) {
        ScopedStorage.file.listFiles(Environment.DIRECTORY_DOCUMENTS,withChildDir = false)?.forEach {
            var msg = "$it\n是否是文件："
            msg += if (it.uri == null) {
                ScopedStorage.file.isFile(it.file.absolutePath)
            } else {
                ScopedStorage.file.isFile(it.uri!!)
            }
            msg += "\n是否是文件夹："
            msg += if (it.uri == null) {
                ScopedStorage.file.isDirectory(it.file.absolutePath)
            } else {
                ScopedStorage.file.isDirectory(it.uri!!)
            }
            if (it.file.name == "c.log" && it.uri != null) {
                ScopedStorage.file.renameTo(it.uri!!, "ccc.newlog")
                ScopedStorage.file.copyTo(
                    it.uri!!, File(Environment.DIRECTORY_DOWNLOADS + "copy.newlog").toUri()
                )
            }
            // msg += "\n删除文件："
            // msg += if (it.uri == null) {
            //     ScopedStorage.file.delete(it.file.absolutePath)
            // } else {
            //     ScopedStorage.file.delete(it.uri!!)
            // }
            MLog.d(msg)
        }
    }

    fun rename(view: View) {
        // ScopedStorage.file.renameTo(relativePath2, "ccc.newlog")
        ScopedStorage.file.renameTo(
            File(
                Environment.getExternalStorageDirectory().absolutePath + "/" + relativePath2
            ).toUri(), "ccc.newlog"
        )
    }

    fun copyFile(view: View) {
        ScopedStorage.file.copyTo(
            relativePath2, Environment.DIRECTORY_DOWNLOADS + "/copy.txt"
        )
    }

    fun moveFile(view: View) {
        ScopedStorage.file.moveTo(
            relativePath, Environment.DIRECTORY_DOWNLOADS + "/move.txt"
        )
    }

    private fun absolutePath(path: String): String {
        return Environment.getExternalStorageDirectory().absolutePath + "/" + path
    }
}