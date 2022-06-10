package com.masterchan.lib.sandbox.access

import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import com.masterchan.lib.ext.create
import com.masterchan.lib.sandbox.response.FileResponse
import java.io.File

/**
 * API29以下文件访问
 * @author: MasterChan
 * @date: 2022-06-10 16:25
 */
class FileRequest : AbsFileRequest() {

    override fun createFile(path: String): Boolean {
        return uri.toFile().create()
    }

    override fun createFile(
        publicDir: String,
        filePath: String,
        vararg args: Pair<String, String>
    ): Boolean {
        return File(obtainPath(publicDir, filePath)).create()
    }

    override fun insert(publicDir: String, fileName: String, data: ByteArray?): Boolean {
        val path = "${Environment.getExternalStorageDirectory().absolutePath}/$publicDir/$fileName"
        val file = File(path)
        if (file.create()) {
            try {
                data?.let { file.writeBytes(it) }
                return true
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        return false
    }

    override fun insert(
        publicDir: String,
        fileName: String,
        data: ByteArray?,
        args: ContentValues.() -> Unit
    ): Boolean {
        return insert(publicDir, fileName, data)
    }

    override fun write(uri: Uri, data: ByteArray?) {
        data?.let { uri.toFile().writeBytes(it) }
    }

    override fun write(publicDir: String, fileName: String, data: ByteArray?) {
        data?.let { File(obtainPath(publicDir, fileName)).writeBytes(it) }
    }

    override fun read(uri: Uri): ByteArray? {
        return uri.toFile().readBytes()
    }

    override fun read(publicDir: String, fileName: String): ByteArray? {

    }

    override fun delete(uri: Uri) {

    }

    override fun delete(publicDir: String, fileName: String) {

    }

    override fun queryByName(publicDir: String, fileName: String): List<FileResponse> {

    }

    override fun queryByExtension(publicDir: String, extension: String): List<FileResponse> {

    }

    override fun renameTo(uri: Uri, name: String) {

    }

    override fun renameTo(publicDir: String, fileName: String, destName: String) {

    }

    override fun copyTo(uri: Uri, destUri: Uri) {

    }

    override fun copyTo(publicDir: String, fileName: String, destDir: String, destName: String) {

    }

    override fun moveTo(uri: Uri, destUri: Uri) {

    }

    override fun moveTo(publicDir: String, fileName: String, destDir: String, destName: String) {

    }

    override fun listFile(publicDir: String): FileResponse? {

    }
}