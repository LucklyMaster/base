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

    private val relativePath = "${Environment.DIRECTORY_DOCUMENTS}/MyBase4"
    private val relativePath2 = "${Environment.DIRECTORY_DOCUMENTS}/MyBase5"
    private val relativePath3 = "${Environment.DIRECTORY_DOCUMENTS}/MyBase6"
    private val relativePath4 = "${Environment.DIRECTORY_DCIM}/MyBase7"
    private val relativePath5 = "${Environment.DIRECTORY_ALARMS}/MyBase7"

    private val file1 = "${relativePath}/a.txt"
    private val file2 = "${relativePath}/b.txt"
    private val file3 = "${relativePath2}/c.txt"
    private val file4 = "${relativePath3}/d.log"
    private val file5 = "${relativePath4}/img.png"
    private val file6 = "${relativePath5}/tts.mp3"

    private var pathAccessMode = true

    override fun onCreated(savedInstanceState: Bundle?) {
    }

    fun createFile(view: View) {
        val result1 = FileAccess.createFile(
            Environment.getExternalStorageDirectory().absolutePath + "/" + relativePath, "a.txt"
        )
        val result2 = FileAccess.createFile(relativePath, "b.txt")
        val result3 = FileAccess.createFile(relativePath2, "c.txt", "OK".toByteArray())
        val result4 = FileAccess.createFile(relativePath3, "d.log", "OK".toByteArray()) {
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        }
        val os = ByteArrayOutputStream()
        BitmapFactory.decodeResource(resources, R.mipmap.img)
            .compress(Bitmap.CompressFormat.PNG, 100, os)
        val result5 = FileAccess.createFile(relativePath4, "img", os.toByteArray()) {
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        }
        val result6 = FileAccess.createFile(
            relativePath5, "tts.mp3", resources.openRawResource(R.raw.tts).readBytes()
        )
        val result7 = FileAccess.createFile(relativePath3, "noExtension", os.toByteArray())
        MToast.showLong("创建文件:$result1,$result2,$result3,$result4,$result5,$result6,$result7")
    }

    fun exist(view: View) {
        val result1: Boolean
        val result2: Boolean
        val result3: Boolean
        if (pathAccessMode) {
            result1 = FileAccess.exist(file1)
            result2 = FileAccess.exist(file5)
            result3 = FileAccess.exist(file6)
        } else {
            result1 = FileAccess.exist(file1.toFileResponse()?.uri!!)
            result2 = FileAccess.exist(file5.toFileResponse()?.uri!!)
            result3 = FileAccess.exist(file6.toFileResponse()?.uri!!)
        }
        MToast.showLong("exist:$result1,$result2,$result3")
    }

    fun isFile(view: View) {
        val result1: Boolean
        val result2: Boolean
        val result3: Boolean
        if (pathAccessMode) {
            result1 = !FileAccess.isDirectory(file1)
            result2 = !FileAccess.isDirectory(file5)
            result3 = !FileAccess.isDirectory(file6)
        } else {
            result1 = !FileAccess.isDirectory(file1.toFileResponse()?.uri!!)
            result2 = !FileAccess.isDirectory(file5.toFileResponse()?.uri!!)
            result3 = !FileAccess.isDirectory(file6.toFileResponse()?.uri!!)
        }
        MToast.showLong("isFile:$result1,$result2,$result3")
    }

    fun writeFile(view: View) {
        if (pathAccessMode) {
            FileAccess.write(file1, "writeFile写入".toByteArray())
            FileAccess.write(file2, "，writeFile追加".toByteArray(), true)
            FileAccess.write(file3, "writeFile写入".toByteArray())
            FileAccess.write(file4, "writeFile追加".toByteArray(), true)
            FileAccess.write(
                "${Environment.DIRECTORY_DOWNLOADS}/test/load.txt", "正在测试写入文件是否加入到媒体库".toByteArray()
            )
        } else {
            FileAccess.write(
                File(absolutePath(file1)).toUri(), "writeFile写入".toByteArray()
            )
            FileAccess.write(
                File(absolutePath(file2)).toUri(), "writeFile追加".toByteArray(), true
            )
            FileAccess.write(file3, "writeFile写入".toByteArray())
            FileAccess.write(file4, "writeFile追加".toByteArray(), true)
            FileAccess.write(
                "${Environment.DIRECTORY_DOWNLOADS}/test/load.txt".toFileResponse()!!.uri,
                "正在测试写入文件是否加入到媒体库2".toByteArray(), true
            )
        }
    }

    fun deleteFile(view: View) {
        val result = if (pathAccessMode) {
            FileAccess.delete(Environment.DIRECTORY_DOCUMENTS)
        } else {
            file3.toFileResponse()?.uri?.let {
                FileAccess.delete(it)
            }
        }
        val result2 = FileAccess.delete(Environment.DIRECTORY_DOWNLOADS)
        val result3 = FileAccess.delete(Environment.DIRECTORY_DCIM)
        MToast.showLong("删除文件:$result,$result2,$result3")
    }

    fun getAllFiles(view: View) {
        FileAccess.listFiles(Environment.DIRECTORY_DOCUMENTS)?.forEach { MLog.d(it) }
        FileAccess.listFiles(Environment.DIRECTORY_DOWNLOADS)?.forEach { MLog.d(it) }
        FileAccess.listFiles(Environment.DIRECTORY_DCIM)?.forEach { MLog.d(it) }
    }

    fun rename(view: View) {
        val result = if (pathAccessMode) {
            FileAccess.renameTo(file1, "rename.txt")
        } else {
            FileAccess.renameTo(file2.toFileResponse()!!.uri, "rename.txt")
        }
        MToast.showLong("重命名：$result")
    }

    fun copyFile(view: View) {
        val result = if (pathAccessMode) {
            FileAccess.copyTo(file1, Environment.DIRECTORY_DOWNLOADS + "/copy.txt")
        } else {
            FileAccess.copyTo(
                file2.toFileResponse()!!.uri, Environment.DIRECTORY_DOWNLOADS + "/copy.txt"
            )
        }
        MToast.showLong("复制：$result")
    }

    fun moveFile(view: View) {
        val result = if (pathAccessMode) {
            FileAccess.moveTo(
                file5, Environment.DIRECTORY_DOWNLOADS + "/move.jpg"
            )
        } else {
            FileAccess.moveTo(
                file5.toFileResponse()!!.uri, Environment.DIRECTORY_DOWNLOADS + "/move.jpg"
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