package com.mc.lib.ext

import android.net.Uri
import com.mc.lib.sandbox.MediaAccess
import com.mc.lib.sandbox.FileResponse

fun String.toFileResponse(): FileResponse? {
    return MediaAccess.getResponse(this)
}

fun Uri.toFileResponse(): FileResponse? {
    return MediaAccess.getResponse(this)
}