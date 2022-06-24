package com.master.lib.ext

import android.net.Uri
import com.master.lib.sandbox.MediaAccess
import com.master.lib.sandbox.FileResponse

fun String.toFileResponse(): FileResponse? {
    return MediaAccess.getResponse(this)
}

fun Uri.toFileResponse(): FileResponse? {
    return MediaAccess.getResponse(this)
}