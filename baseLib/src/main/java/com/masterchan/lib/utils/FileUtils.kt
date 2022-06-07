package com.masterchan.lib.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.*
import java.util.*

/**
 * 文件操作工具类
 * @author MasterChan
 * @date 2021-12-01 17:52
 */
object FileUtils {

    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @param charsetName 字符集
     */
    @JvmStatic
    fun read2String(filePath: String, charsetName: String = "UTF-8"): String? {
        val file = File(filePath)
        val sb = StringBuilder()
        if (!file.isFile) {
            return null
        }
        val reader: BufferedReader
        try {
            var line: String?
            reader = BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
            while (reader.readLine().also { line = it } != null) {
                if ("" != sb.toString()) {
                    sb.append("\r\n")
                }
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    /**
     * 读取文件
     * @param filePath String
     * @return ByteArrayOutputStream?
     */
    @JvmStatic
    fun read2OutputStream(filePath: String): ByteArrayOutputStream? {
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val bos = ByteArrayOutputStream(file.length().toInt())
        var bis: BufferedInputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(file))
            val buffSize = 1024
            val buffer = ByteArray(buffSize)
            var len: Int
            while (-1 != bis.read(buffer, 0, buffSize).also { len = it }) {
                bos.write(buffer, 0, len)
            }
            return bos
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bis?.close()
                bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 文件转InputStream
     * @param filePath String
     * @return InputStream?
     */
    @JvmStatic
    fun read2InputStream(filePath: String): InputStream? {
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val inputStream: InputStream
        return try {
            inputStream = BufferedInputStream(FileInputStream(file))
            inputStream
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 读取文件为byte数组
     * @param filePath String?
     * @return ByteArray?
     */
    @JvmStatic
    fun read2ByteArray(filePath: String): ByteArray? {
        val stream = read2OutputStream(filePath)
        return stream?.toByteArray()
    }

    /**
     * 如果不存在文件，创建文件
     * @param filePath 文件路径
     * @param content 文本内容
     * @return 是否创建成功
     */
    @JvmStatic
    fun writeFile(filePath: String, content: String, append: Boolean = false): Boolean {
        if (content.isEmpty()) {
            return false
        }
        var fileWriter: FileWriter? = null
        return try {
            createFile(filePath)
            fileWriter = FileWriter(filePath, append)
            fileWriter.write(content)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("IOException occurred. ", e)
        } finally {
            try {
                fileWriter?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 将byte数据写到文件
     * @param filePath 文件路径
     * @param data byte数组
     */
    @JvmStatic
    fun writeFile(filePath: String, data: ByteArray) {
        var bos: BufferedOutputStream? = null
        var fos: FileOutputStream? = null
        val file: File
        try {
            file = File(filePath)
            if (!createFile(filePath)) {
                return
            }
            fos = FileOutputStream(file)
            bos = BufferedOutputStream(fos)
            bos.write(data)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 将steam写入文件
     * @param file 需要写到的文件
     * @param steam InputStream
     * @param append 是否追加
     */
    @JvmStatic
    fun writeFile(file: File, steam: InputStream, append: Boolean = false): Boolean {
        var os: OutputStream? = null
        try {
            createFile(file.absolutePath)
            os = FileOutputStream(file, append)
            val data = ByteArray(1024)
            var length: Int
            while (steam.read(data).also { length = it } != -1) {
                os.write(data, 0, length)
            }
            os.flush()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
                steam.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 创建文件
     * @param filePath 文件
     * @return 是否创建成功
     */
    @JvmStatic
    fun createFile(filePath: String): Boolean {
        return createFile(File(filePath))
    }

    /**
     * 如果不存在文件，创建文件
     * @param file 文件
     * @return 是否创建成功
     */
    @JvmStatic
    fun createFile(file: File): Boolean {
        if (!file.exists()) {
            val parentFile = file.parentFile
            if (parentFile != null && !parentFile.exists()) {
                if (!createDir(parentFile.path)) {
                    return false
                }
            }
            return file.createNewFile()
        }
        return true
    }

    /**
     * 创建文件夹
     * @param filePath 文件夹路径
     */
    @JvmStatic
    fun createDir(filePath: String): Boolean {
        return File(filePath).mkdirs()
    }

    /**
     * 删除文件夹和文件
     * @param path 文件夹/文件路径
     */
    @JvmStatic
    fun deleteFile(path: String): Boolean {
        if (path.isEmpty()) {
            return true
        }
        val file = File(path)
        if (!file.exists()) {
            return true
        }
        if (file.isFile) {
            return file.delete()
        }
        if (!file.isDirectory) {
            return false
        }
        if (file.isDirectory) {
            val listFiles = file.listFiles()
            if (!listFiles.isNullOrEmpty()) {
                for (f in listFiles) {
                    if (f.isFile) {
                        f.delete()
                    } else if (f.isDirectory) {
                        deleteFile(f.absolutePath)
                    }
                }
            }
        }
        return file.delete()
    }

    /**
     * 获取文件名字
     * @param filePath 文件路径
     */
    @JvmStatic
    fun getFileName(filePath: String): String {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val fp = filePath.lastIndexOf(File.separator)
        return if (fp == -1) filePath else filePath.substring(fp + 1)
    }

    /**
     * 获取文件夹名字
     * @param filePath 文件路径
     */
    @JvmStatic
    fun getFolderName(filePath: String): String {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val fp = filePath.lastIndexOf(File.separator)
        return if (fp == -1) "" else filePath.substring(0, fp)
    }

    /**
     * 获取文件后缀
     * @param filePath 文件路径
     */
    @JvmStatic
    fun getFileSuffix(filePath: String): String {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val suffix = filePath.lastIndexOf(".")
        val fp = filePath.lastIndexOf(File.separator)
        if (suffix == -1) {
            return ""
        }
        return if (fp >= suffix) "" else filePath.substring(suffix + 1)
    }

    /**
     * 获取不带后缀的文件名
     * @param filePath 文件路径
     */
    @JvmStatic
    fun getFileNameWithoutSuffix(filePath: String): String {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val suffix = filePath.lastIndexOf(".")
        val fp = filePath.lastIndexOf(File.separator)
        if (fp == -1) {
            return if (suffix == -1) filePath else filePath.substring(0, suffix)
        }
        if (suffix == -1) {
            return filePath.substring(fp + 1)
        }
        return if (fp < suffix) filePath.substring(fp + 1, suffix) else filePath.substring(fp + 1)
    }

    /**
     * 文件是否存在
     * @param filePath 文件路径
     */
    @JvmStatic
    fun isFileExist(filePath: String): Boolean {
        if (filePath.isEmpty()) {
            return false
        }
        return File(filePath).exists()
    }

    /**
     * 修改SD卡上的文件或目录名 oldFilePath 旧文件或文件夹路径 newFilePath 新文件或文件夹路径
     * @param oldFilePath String
     * @param newFilePath String
     * @return Boolean
     */
    @JvmStatic
    fun renameFile(oldFilePath: String, newFilePath: String): Boolean {
        val oldFile = File(oldFilePath)
        val newFile = File(newFilePath)
        return oldFile.renameTo(newFile)
    }

    @JvmStatic
    fun moveFile(srcFilePath: String, destFilePath: String) {
        if (srcFilePath.isNotEmpty() && destFilePath.isNotEmpty()) {
            moveFile(File(srcFilePath), File(destFilePath))
        }
    }

    @JvmStatic
    fun moveFile(srcFile: File, destFile: File) {
        val rename = srcFile.renameTo(destFile)
        if (!rename) {
            copyFile(srcFile.absolutePath, destFile.absolutePath)
            deleteFile(srcFile.absolutePath)
        }
    }

    @JvmStatic
    fun copyFile(srcFilePath: String, destFilePath: String): Boolean {
        val inputStream: InputStream = FileInputStream(srcFilePath)
        return writeFile(File(destFilePath), inputStream)
    }

    /**
     * 获取文件大小
     * @param path 文件路径
     */
    @JvmStatic
    fun getFileSize(path: String): Long {
        if (TextUtils.isEmpty(path)) {
            return -1
        }
        val file = File(path)
        return if (file.exists() && file.isFile) file.length() else -1
    }

    /**
     * 按文件时间排序
     * @param filePath File
     * @param desc 是否降序排列
     * @return Array<File?>?
     */
    @JvmStatic
    fun orderByDate(filePath: File, desc: Boolean): Array<File>? {
        val fs = filePath.listFiles() ?: return null
        Arrays.sort(fs) { f1, f2 ->
            val diff = f1.lastModified() - f2.lastModified()
            when {
                diff > 0 -> 1
                diff == 0L -> 0
                else -> -1
            }
        }
        return if (desc) {
            val list = mutableListOf<File>()
            for (i in list.size - 1 downTo 0) {
                list.add(fs[i])
            }
            list.toTypedArray()
        } else {
            fs
        }
    }

    /**
     * 获取文件夹中的所有文件，并根据名称排序
     * @param filePath 文件夹路径
     * @param desc 降序
     * @param careUpCase 是否关心大小写
     * @return 文件夹中的所有文件
     */
    @JvmStatic
    fun orderByName(filePath: File, desc: Boolean, careUpCase: Boolean = false): Array<File>? {
        val fs = filePath.listFiles() ?: return null
        Arrays.sort(fs) { o1, o2 ->
            when {
                o1.isDirectory && o2.isFile -> -1
                o1.isFile && o2.isDirectory -> 1
                else -> o1.name.compareTo(o2.name, careUpCase)
            }
        }
        return if (desc) {
            val list = mutableListOf<File>()
            for (i in list.size - 1 downTo 0) {
                list.add(fs[i])
            }
            list.toTypedArray()
        } else {
            fs
        }
    }

    /**
     * 按照文件大小排序
     * @param filePath File
     * @param desc Boolean
     * @return Array<File?>
     */
    @JvmStatic
    fun orderByLength(filePath: File, desc: Boolean): Array<File>? {
        val files = filePath.listFiles() ?: return null
        Arrays.sort(files) { o1, o2 ->
            val diff = o1.length() - o2.length()
            when {
                diff > 0 -> 1
                diff == 0L -> 0
                else -> -1
            }
        }
        return if (desc) {
            val list = mutableListOf<File>()
            for (i in list.size - 1 downTo 0) {
                list.add(files[i])
            }
            list.toTypedArray()
        } else {
            files
        }
    }

    /**
     * 文件筛选
     * @param files Array<File>?
     * @param filter String?
     * @return List<File>
     */
    @JvmStatic
    fun filter(files: Array<File>?, filter: String?): List<File> {
        val fileList: MutableList<File> = ArrayList()
        files?.let {
            for (file in it) {
                if (file.name.contains(filter!!)) {
                    fileList.add(file)
                }
            }
        }
        return fileList
    }

    /**
     * 文件筛选
     * @param file File
     * @param filterName String?
     * @return Array<File>?
     */
    @JvmStatic
    fun fileNameFilter(file: File, filterName: String?): Array<File>? {
        return if (!file.isDirectory) {
            null
        } else file.listFiles { pathname ->
            pathname.name.contains(filterName!!)
        }
    }

    /**
     * 获取文件列表
     * @param fileDir String
     * @return Array<File>?
     */
    @JvmStatic
    fun getFiles(fileDir: String): Array<File>? {
        return getFiles(File(fileDir))
    }

    /**
     * 获取文件列表
     * @param fileDir File
     * @return Array<File>?
     */
    @JvmStatic
    fun getFiles(fileDir: File): Array<File>? {
        return if (!fileDir.isDirectory) null else fileDir.listFiles()
    }

    /**
     * 打开文件
     * @param context Context
     * @param file File?
     */
    @JvmStatic
    fun openFile(context: Context, file: File?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        //7.0以上需要
        if (Build.VERSION.SDK_INT >= 24) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val uri =
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file!!)
            intent.setDataAndType(uri, mimeType)
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimeType)
        }
        context.startActivity(intent)
    }
}