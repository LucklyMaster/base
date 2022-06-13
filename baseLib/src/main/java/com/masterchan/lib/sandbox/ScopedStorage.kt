package com.masterchan.lib.sandbox

import android.os.Environment
import com.masterchan.lib.ext.isScopedStorage
import com.masterchan.lib.sandbox.request.*

/**
 * API29及以上分区存储文件访问框架，API28需要设置分区存储不生效；
 * 所有参数中的路径，可以是"sdcard/xxx/xxx.txt"，也可以是[Environment.DIRECTORY_DOWNLOADS]/xxx/xxx.txt，
 * 调用时会自动组装
 * @author: MasterChan
 * @date: 2022-06-09 11:55
 */
object ScopedStorage {

    /*val file: FileAccess
        get() = if (isScopedStorage) FileAccess(FileRequest()) else FileAccess(
            (FileRequestApi28Impl())
        )

    val image: ImageAccess
        get() = if (isScopedStorage) ImageAccess(ImageRequest()) else ImageAccess(
            (ImageRequestApi28Impl())
        )

    val audio: AudioAccess
        get() = if (isScopedStorage) AudioAccess(AudioRequest()) else AudioAccess(
            (AudioRequestApi28Impl())
        )

    val video: VideoAccess
        get() = if (isScopedStorage) VideoAccess(VideoRequest()) else VideoAccess(
            (VideoRequestApi28Impl())
        )*/
}