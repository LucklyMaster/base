package com.masterchan.mybase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.net.toUri
import com.masterchan.lib.MToast
import com.masterchan.lib.ext.toFileResponse
import com.masterchan.lib.log.MLog
import com.masterchan.lib.sandbox.FileAccess
import com.masterchan.mybase.databinding.ActivityScopedStorageBinding
import java.io.ByteArrayOutputStream
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
    private val relativePath4 = "${Environment.DIRECTORY_DCIM}/MyBase/img.png"
    private val relativePath5 = "${Environment.DIRECTORY_ALARMS}/MyBase/tts.mp3"

    private var pathAccessMode = true

    override fun onCreated(savedInstanceState: Bundle?) {
        // TODO: API28的文件赠送改，需要物理删除文件
    }

    fun createFile(view: View) {
        val result1 = FileAccess.createFile(
            Environment.getExternalStorageDirectory().absolutePath + "/" + relativePath
        )
        val result2 = FileAccess.createFile(relativePath2)
        val result3 = FileAccess.createFile("$relativePath3/c.txt", "OK".toByteArray())
        val result4 = FileAccess.createFile("$relativePath3/d.log", "OK".toByteArray()) {
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        }
        val os = ByteArrayOutputStream()
        BitmapFactory.decodeResource(resources, R.mipmap.img)
            .compress(Bitmap.CompressFormat.PNG, 100, os)
        val result5 = FileAccess.createFile(relativePath4, os.toByteArray())

        val result6 = FileAccess.createFile(
            relativePath5, resources.openRawResource(R.raw.tts).readBytes()
        )
        MToast.showLong("创建文件:$result1,$result2,$result3,$result4,$result5,$result6")
    }

    fun exist(view: View) {
        val result1: Boolean
        val result2: Boolean
        val result3: Boolean
        if (pathAccessMode) {
            result1 = FileAccess.exist(relativePath)
            result2 = FileAccess.exist(relativePath2)
            result3 = FileAccess.exist(relativePath3)
        } else {
            result1 = FileAccess.exist(relativePath.toFileResponse()?.uri!!)
            result2 = FileAccess.exist(relativePath.toFileResponse()?.uri!!)
            result3 = FileAccess.exist(relativePath.toFileResponse()?.uri!!)
        }
        MToast.showLong("exist:$result1,$result2,$result3")
    }

    fun isFile(view: View) {
        val result1: Boolean
        val result2: Boolean
        val result3: Boolean
        if (pathAccessMode) {
            result1 = !FileAccess.isDirectory(relativePath)
            result2 = !FileAccess.isDirectory(relativePath2)
            result3 = !FileAccess.isDirectory(relativePath3)
        } else {
            result1 = !FileAccess.isDirectory(relativePath.toFileResponse()?.uri!!)
            result2 = !FileAccess.isDirectory(relativePath.toFileResponse()?.uri!!)
            result3 = !FileAccess.isDirectory(relativePath.toFileResponse()?.uri!!)
        }
        MToast.showLong("isFile:$result1,$result2,$result3")
    }

    fun writeFile(view: View) {
        if (pathAccessMode) {
            FileAccess.write(relativePath, "writeFile写入".toByteArray())
            FileAccess.write(relativePath, "，writeFile追加".toByteArray(), true)
            FileAccess.write(relativePath2, "writeFile写入".toByteArray())
            FileAccess.write(relativePath2, "writeFile追加".toByteArray(), true)
        } else {
            FileAccess.write(
                File(absolutePath(relativePath)).toUri(), "writeFile写入".toByteArray()
            )
            FileAccess.write(
                File(absolutePath(relativePath)).toUri(), "writeFile追加".toByteArray(), true
            )
            FileAccess.write(relativePath2, "writeFile写入".toByteArray())
            FileAccess.write(relativePath2, "writeFile追加".toByteArray(), true)
        }
    }

    fun deleteFile(view: View) {
        val result = if (pathAccessMode) {
            FileAccess.delete(Environment.DIRECTORY_DOCUMENTS)
        } else {
            FileAccess.delete(
                absolutePath(
                    Environment.getExternalStorageDirectory().absolutePath + "/" + Environment.DIRECTORY_DOCUMENTS
                )
            )
        }
        MToast.showLong("删除文件:$result")
    }

    fun getAllFiles(view: View) {
        if (pathAccessMode) {
            FileAccess.listFiles(Environment.DIRECTORY_DOCUMENTS)?.forEach { MLog.d(it) }
        } else {
            val uri = relativePath3.toFileResponse()!!.uri
            FileAccess.listFiles(uri)?.forEach { MLog.d(it) }
        }
    }

    fun rename(view: View) {
        val result = if (pathAccessMode) {
            FileAccess.renameTo(relativePath2, "rename.txt")
        } else {
            FileAccess.renameTo(relativePath2.toFileResponse()!!.uri, "rename.txt")
        }
        MToast.showLong("重命名：$result")
    }

    fun copyFile(view: View) {
        val result = if (pathAccessMode) {
            FileAccess.copyTo(relativePath2, "rename.txt")
        } else {
            FileAccess.copyTo(relativePath2.toFileResponse()!!.uri, "rename.txt")
        }
        MToast.showLong("复制：$result")
    }

    fun moveFile(view: View) {
        val result = if (pathAccessMode) {
            FileAccess.moveTo(
                relativePath, Environment.DIRECTORY_DOWNLOADS + "/move.txt"
            )
        } else {
            FileAccess.moveTo(
                relativePath.toFileResponse()!!.uri, Environment.DIRECTORY_DOWNLOADS + "/move.txt"
            )
        }
        MToast.showLong("移动：$result")
    }

    private fun absolutePath(path: String): String {
        return Environment.getExternalStorageDirectory().absolutePath + "/" + path
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.scoped_storage, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.uriAccess -> pathAccessMode = false
            R.id.pathAccess -> pathAccessMode = true
        }
        return super.onOptionsItemSelected(item)
    }
}