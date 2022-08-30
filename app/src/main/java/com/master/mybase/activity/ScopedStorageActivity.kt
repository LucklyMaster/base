package com.master.mybase.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.net.toUri
import com.master.lib.widget.MToast
import com.master.lib.ext.toFileResponse
import com.master.lib.log.MLog
import com.master.lib.sandbox.MediaAccess
import com.master.mybase.R
import com.master.mybase.databinding.ActivityScopedStorageBinding
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * 分区存储测试
 * @author: MasterChan
 * @date: 2022-6-12 1:01
 */
class ScopedStorageActivity : MyBaseActivity<ActivityScopedStorageBinding>() {

    private val relativePath = "${Environment.DIRECTORY_DOCUMENTS}/MyBase8"
    private val relativePath2 = "${Environment.DIRECTORY_DOCUMENTS}/MyBase9"
    private val relativePath3 = "${Environment.DIRECTORY_DOCUMENTS}/MyBase10"
    private val relativePath4 = "${Environment.DIRECTORY_DCIM}/MyBase11"
    private val relativePath5 = "${Environment.DIRECTORY_ALARMS}/MyBase11"

    private val file1 = "${relativePath}/a.txt"
    private val file2 = "${relativePath}/b.txt"
    private val file3 = "${relativePath2}/c.txt"
    private val file4 = "${relativePath3}/d.log"
    private val file5 = "${relativePath4}/img.png"
    private val file6 = "${relativePath5}/tts.mp3"

    private var pathAccessMode = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
    }

    fun createFile(view: View) {
        val result1 = MediaAccess.createFile(
            Environment.getExternalStorageDirectory().absolutePath + "/" + relativePath, "a.txt"
        )
        val result2 = MediaAccess.createFile(relativePath, "b.txt")
        val result3 = MediaAccess.createFile(relativePath2, "c.log", "OK".toByteArray())
        val result4 = MediaAccess.createFile(relativePath3, "d.log", "OK".toByteArray()) {
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        }
        val os = ByteArrayOutputStream()
        BitmapFactory.decodeResource(resources, R.mipmap.img)
            .compress(Bitmap.CompressFormat.PNG, 100, os)
        val result5 = MediaAccess.createFile(relativePath4, "img", os.toByteArray()) {
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        }
        val result6 = MediaAccess.createFile(
            relativePath5, "tts.mp3", resources.openRawResource(R.raw.tts).readBytes()
        )
        val result7 = MediaAccess.createFile(relativePath3, "noExtension", os.toByteArray())

        val result8 = MediaAccess.createFile(
            relativePath4, "test.mp4", resources.openRawResource(R.raw.temp).readBytes()
        )
        MToast.showLong(
            "创建文件:$result1,$result2,$result3,$result4,$result5,$result6,$result7,$result8"
        )
    }

    fun exist(view: View) {
        val result1: Boolean
        val result2: Boolean
        val result3: Boolean
        if (pathAccessMode) {
            result1 = MediaAccess.exist(file1)
            result2 = MediaAccess.exist(file5)
            result3 = MediaAccess.exist(file6)
        } else {
            result1 = MediaAccess.exist(file1.toFileResponse()?.uri!!)
            result2 = MediaAccess.exist(file5.toFileResponse()?.uri!!)
            result3 = MediaAccess.exist(file6.toFileResponse()?.uri!!)
        }
        MToast.showLong("exist:$result1,$result2,$result3")
    }

    fun isFile(view: View) {
        val result1: Boolean
        val result2: Boolean
        val result3: Boolean
        if (pathAccessMode) {
            result1 = !MediaAccess.isDirectory(file1)
            result2 = !MediaAccess.isDirectory(file5)
            result3 = !MediaAccess.isDirectory(file6)
        } else {
            result1 = !MediaAccess.isDirectory(file1.toFileResponse()?.uri!!)
            result2 = !MediaAccess.isDirectory(file5.toFileResponse()?.uri!!)
            result3 = !MediaAccess.isDirectory(file6.toFileResponse()?.uri!!)
        }
        MToast.showLong("isFile:$result1,$result2,$result3")
    }

    fun writeFile(view: View) {
        if (pathAccessMode) {
            MediaAccess.write(file1, "writeFile写入".toByteArray())
            MediaAccess.write(file2, "，writeFile追加".toByteArray(), true)
            MediaAccess.write(file3, "writeFile写入".toByteArray())
            MediaAccess.write(file4, "writeFile追加".toByteArray(), true)
            MediaAccess.write(
                "${Environment.DIRECTORY_DOWNLOADS}/test/load.txt", "正在测试写入文件是否加入到媒体库".toByteArray()
            )
        } else {
            MediaAccess.write(
                File(absolutePath(file1)).toUri(), "writeFile写入".toByteArray()
            )
            MediaAccess.write(
                File(absolutePath(file2)).toUri(), "writeFile追加".toByteArray(), true
            )
            MediaAccess.write(file3, "writeFile写入".toByteArray())
            MediaAccess.write(file4, "writeFile追加".toByteArray(), true)
            MediaAccess.write(
                "${Environment.DIRECTORY_DOWNLOADS}/test/load.txt".toFileResponse()!!.uri,
                "正在测试写入文件是否加入到媒体库2".toByteArray(), true
            )
        }
    }

    fun deleteFile(view: View) {
        val result = if (pathAccessMode) {
            MediaAccess.delete(Environment.DIRECTORY_DOCUMENTS)
        } else {
            file3.toFileResponse()?.uri?.let {
                MediaAccess.delete(it)
            }
        }
        val result1 = MediaAccess.delete(relativePath)
        val result2 = MediaAccess.delete(relativePath2)
        val result3 = MediaAccess.delete(relativePath3)
        val result4 = MediaAccess.delete(relativePath4)
        val result5 = MediaAccess.delete(relativePath5)
        MToast.showLong("删除文件:$result1,$result2,$result3,$result4,$result5")
    }

    fun getAllFiles(view: View) {
        MediaAccess.listFiles(Environment.DIRECTORY_DOCUMENTS)?.forEach {
            MLog.d(it)
        }
        MediaAccess.listFiles(Environment.DIRECTORY_DOWNLOADS)?.forEach {
            MLog.d(it)
        }
        MediaAccess.listFiles(Environment.DIRECTORY_DCIM)?.forEach {
            MLog.d(it)
        }
    }

    fun rename(view: View) {
        val result = if (pathAccessMode) {
            MediaAccess.renameTo(file1, "rename.txt")
        } else {
            MediaAccess.renameTo(file2.toFileResponse()!!.uri, "rename.txt")
        }
        MToast.showLong("重命名：$result")
    }

    fun copyFile(view: View) {
        val result = if (pathAccessMode) {
            MediaAccess.copyTo(file1, Environment.DIRECTORY_DOWNLOADS + "/copy.txt")
        } else {
            MediaAccess.copyTo(
                file2.toFileResponse()!!.uri, Environment.DIRECTORY_DOWNLOADS + "/copy.txt"
            )
        }
        MToast.showLong("复制：$result")
    }

    fun moveFile(view: View) {
        val result = if (pathAccessMode) {
            MediaAccess.moveTo(
                file5, Environment.DIRECTORY_DOWNLOADS + "/move.jpg"
            )
        } else {
            MediaAccess.moveTo(
                file5.toFileResponse()!!.uri, Environment.DIRECTORY_DOWNLOADS + "/move.jpg"
            )
        }
        MToast.showLong("移动：$result")
    }

    private fun absolutePath(path: String): String {
        return Environment.getExternalStorageDirectory().absolutePath + "/" + path
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.scoped_storage, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.uriAccess -> pathAccessMode = false
            R.id.pathAccess -> pathAccessMode = true
        }
        return super.onOptionsItemSelected(item)
    }
}