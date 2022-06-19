package com.masterchan.lib.ext

import android.net.Uri
import com.masterchan.lib.sandbox.MediaAccess
import com.masterchan.lib.sandbox.FileResponse

fun String.toFileResponse(): FileResponse? {
    return MediaAccess.getResponse(this)
}

fun Uri.toFileResponse(): FileResponse? {
    return MediaAccess.getResponse(this)
}