package com.masterchan.lib.sandbox.access

import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.*
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import com.masterchan.lib.ext.application
import java.io.File

/**
 *
 * @author: MasterChan
 * @date: 2022-06-09 16:27
 */
abstract class AbsFileRequest : IFileRequest {

    protected val resolver = application.contentResolver

    private val publicDirs: Array<String> by lazy { getPublicDirList().toTypedArray() }

    override fun exist(uri: Uri): Boolean {
        return DocumentFile.fromSingleUri(application, uri)?.exists() == true
    }

    override fun isFile(uri: Uri): Boolean {
        return DocumentFile.fromSingleUri(application, uri)?.isFile == true
    }

    override fun isDirectory(uri: Uri): Boolean {
        return DocumentFile.fromSingleUri(application, uri)?.isDirectory == true
    }

    override fun lastModified(uri: Uri): Long {
        return DocumentFile.fromSingleUri(application, uri)?.lastModified() ?: -1L
    }

    override fun length(uri: Uri): Long {
        return DocumentFile.fromSingleUri(application, uri)?.length() ?: -1L
    }

    override fun createDir(path: String): Boolean {
        return File(path).mkdirs()
    }

    override fun createDir(publicDir: String, dirName: String): Boolean {
        return createDir(obtainPath(publicDir, dirName))
    }

    override fun createFile(path: String): Boolean {
        /*val path =
            Environment.getExternalStorageDirectory().absolutePath + "/" + Environment.DIRECTORY_DOWNLOADS + "/Test/MC/a.txt"
        val file = File(path)
        file.parentFile?.mkdirs()
        val df = DocumentFile.fromFile(file.parentFile!!)

        // df.uri.toFile().parentFile?.mkdirs()
        Log.d("TAG", "setLockBg: ${DocumentFile.fromFile(file).exists()}")
        df.createFile(
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)!!,
            file.nameWithoutExtension
        )*/
    }

    protected open fun checkPublicDir(dir: String?) {
        if (!dir.isNullOrEmpty() && !isPublicDir(dir)) {
            throw ArithmeticException("the dir is not a public dir")
        }
    }

    protected open fun isPublicDir(dir: String?): Boolean {
        return publicDirs.contains(dir)
    }

    protected fun obtainPath(publicDir: String, filePath: String): String {
        return "${Environment.getExternalStorageDirectory().absolutePath}/$publicDir/$filePath"
    }

    private fun getPublicDirList(): List<String> {
        val list = mutableListOf(
            DIRECTORY_MUSIC, DIRECTORY_PODCASTS, DIRECTORY_ALARMS, DIRECTORY_RINGTONES,
            DIRECTORY_NOTIFICATIONS, DIRECTORY_PICTURES, DIRECTORY_MOVIES, DIRECTORY_DOWNLOADS,
            DIRECTORY_DCIM, DIRECTORY_DOCUMENTS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            list.add(DIRECTORY_AUDIOBOOKS)
        }
        return list
    }
}