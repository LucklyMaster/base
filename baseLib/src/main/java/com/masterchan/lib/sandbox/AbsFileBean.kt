package com.masterchan.lib.sandbox

import android.net.Uri
import java.io.File

/**
 * 沙盒文件读取结果
 * @author: MasterChan
 * @date: 2022-06-09 14:50
 */
abstract class AbsFileBean {
    abstract var uri: Uri
    abstract var path: String
    abstract var file: File
    abstract var size: Long
}