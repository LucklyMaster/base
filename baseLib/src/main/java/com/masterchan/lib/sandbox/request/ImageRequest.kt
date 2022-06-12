package com.masterchan.lib.sandbox.request

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * 图片文件访问实现
 * @author: MasterChan
 * @date: 2022-06-12 00:30
 */
@RequiresApi(Build.VERSION_CODES.Q)
class ImageRequest : FileRequest(), IImageRequest