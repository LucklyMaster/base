package com.masterchan.lib.sandbox.request

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * API29及以上视频文件访问
 * @author: MasterChan
 * @date: 2022-06-12 00:42
 */
@RequiresApi(Build.VERSION_CODES.Q)
class VideoRequest : FileRequest(), IVideoRequest