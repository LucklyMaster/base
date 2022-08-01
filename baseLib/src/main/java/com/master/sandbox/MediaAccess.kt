package com.master.sandbox

import android.os.Environment
import com.master.sandbox.request.IFileRequest

/**
 * API29及以上媒体库访问框架，需要设置[android:requestLegacyExternalStorage="true"]；
 * 所有参数中的路径，可以是"sdcard/xxx/xxx.txt"，也可以是[Environment.DIRECTORY_DOWNLOADS]/xxx/xxx.txt，
 * 调用时会自动组装；
 * 自定义的实现可以通过[Delegate]
 * @author: MasterChan
 * @date: 2022-06-09 16:24
 */
object MediaAccess : IFileRequest by Delegate.get()