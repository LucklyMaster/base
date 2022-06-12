package com.masterchan.lib.sandbox.request

import android.annotation.SuppressLint
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.masterchan.lib.ext.application
import com.masterchan.lib.ext.create
import com.masterchan.lib.ext.createDirs
import com.masterchan.lib.ext.orNull
import com.masterchan.lib.sandbox.FileResponse
import java.io.File

/**
 * 文件访问的一些通用实现；不再处理异常，需要调用者自行处理
 * @author: MasterChan
 * @date: 2022-06-09 16:27
 */
abstract class AbsFileRequest : IFileRequest {

    protected val resolver = application.contentResolver!!
    protected val dbUri = MediaStore.Files.getContentUri("external")

    override fun exist(uri: Uri): Boolean {
        return DocumentFile.fromSingleUri(application, uri)?.exists() == true
    }

    override fun exist(filePath: String): Boolean {
        return File(obtainPath(filePath)).exists()
    }

    override fun isFile(uri: Uri): Boolean {
        return !isDirectory(uri)
    }

    override fun isFile(filePath: String): Boolean {
        return !isDirectory(filePath)
    }

    override fun isDirectory(uri: Uri): Boolean {
        return DocumentFile.fromSingleUri(application, uri)?.isDirectory == true
    }

    override fun isDirectory(filePath: String): Boolean {
        return File(obtainPath(filePath)).isDirectory
    }

    override fun lastModified(uri: Uri): Long {
        return DocumentFile.fromSingleUri(application, uri)?.lastModified() ?: -1L
    }

    override fun lastModified(filePath: String): Long {
        return File(obtainPath(filePath)).lastModified()
    }

    override fun length(uri: Uri): Long {
        return DocumentFile.fromSingleUri(application, uri)?.length() ?: -1L
    }

    override fun length(filePath: String): Long {
        return File(obtainPath(filePath)).length()
    }

    override fun createDir(filePath: String): Boolean {
        return File(obtainPath(filePath)).createDirs()
    }

    override fun write(uri: Uri, data: ByteArray?, append: Boolean) {
        data?.let { resolver.openOutputStream(uri, if (append) "wa" else "w")?.write(it) }
    }

    override fun write(relativePath: String, data: ByteArray?, append: Boolean) {
        val file = File(obtainPath(relativePath))
        file.parentFile?.createDirs()
        write(file.toUri(), data, append)
    }

    override fun read(uri: Uri): ByteArray? {
        return resolver.openInputStream(uri)?.readBytes()
    }

    override fun read(relativePath: String): ByteArray? {
        val file = File(obtainPath(relativePath)).orNull { exists() } ?: return null
        return read(file.toUri())
    }

    override fun delete(uri: Uri): Boolean {
        resolver.delete(uri, null, null)
        return true
        // return DocumentFile.fromTreeUri(application, uri)?.delete() == true
    }

    override fun delete(relativePath: String): Boolean {
        val file = File(obtainPath(relativePath))
        if (!file.exists()) {
            return true
        }
        return DocumentFile.fromFile(file).delete()
    }

    override fun renameTo(relativePath: String, destName: String): Boolean {
        val file = File(obtainPath(relativePath))
        if (!file.exists()) {
            return false
        }
        return DocumentFile.fromFile(file).renameTo(destName)
    }

    override fun copyTo(uri: Uri, destUri: Uri): Boolean {
        return try {
            write(destUri, read(uri))
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    override fun copyTo(
        relativePath: String,
        destRelativePath: String
    ): Boolean {
        val srcFile = File(obtainPath(relativePath))
        if (!srcFile.exists()) {
            return false
        }
        return try {
            val destFile = File(obtainPath(destRelativePath)).apply { create() }
            resolver.openInputStream(srcFile.toUri())?.readBytes().let {
                resolver.openOutputStream(destFile.toUri())?.write(it)
            }
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    override fun moveTo(uri: Uri, destUri: Uri): Boolean {
        return if (copyTo(uri, destUri)) delete(uri) else false
    }

    override fun moveTo(
        relativePath: String,
        destRelativePath: String
    ): Boolean {
        return if (copyTo(relativePath, destRelativePath)) {
            delete(relativePath)
        } else {
            false
        }
    }

    protected fun obtainPath(relativePath: String): String {
        val rootPath = Environment.getExternalStorageDirectory().absolutePath
        return if (relativePath.startsWith(rootPath)) {
            relativePath
        } else {
            "$rootPath/$relativePath"
        }
    }

    @SuppressLint("Range")
    protected fun query(selection: String?, args: Array<String>?): List<FileResponse>? {
        val projection = mutableListOf<String>()
        projection.add(MediaStore.MediaColumns._ID)
        projection.add(MediaStore.MediaColumns.DATA)
        projection.add(MediaStore.MediaColumns.DATE_ADDED)
        projection.add(MediaStore.MediaColumns.WIDTH)
        projection.add(MediaStore.MediaColumns.HEIGHT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection.add(MediaStore.MediaColumns.DURATION)
        }
        val cursor = resolver.query(
            dbUri, projection.toTypedArray(), selection, args, null
        ) ?: return null
        val list = mutableListOf<FileResponse>()
        cursor.moveToFirst()
        (0 until cursor.count).forEach { _ ->
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
            val date = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED))
            val width = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
            val height = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
            val duration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION))
            } else {
                FileResponse.NOT_SUPPORT
            }
            val uri = ContentUris.withAppendedId(dbUri, id)
            list.add(FileResponse(uri, File(path), date, duration, width, height))
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }
}