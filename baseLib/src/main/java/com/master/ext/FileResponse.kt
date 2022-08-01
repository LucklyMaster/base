package com.master.ext

import android.net.Uri
import com.master.sandbox.MediaAccess
import com.master.sandbox.FileResponse

fun String.toFileResponse(): FileResponse? {
    return MediaAccess.getResponse(this)
}

fun Uri.toFileResponse(): FileResponse? {
    return MediaAccess.getResponse(this)
}