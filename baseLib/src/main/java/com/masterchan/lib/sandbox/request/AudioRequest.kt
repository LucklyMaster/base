package com.masterchan.lib.sandbox.request

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * API29及以上音频文件访问
 * @author: MasterChan
 * @date: 2022-06-12 00:38
 */
@RequiresApi(Build.VERSION_CODES.Q)
class AudioRequest : FileRequest(), IAudioRequest