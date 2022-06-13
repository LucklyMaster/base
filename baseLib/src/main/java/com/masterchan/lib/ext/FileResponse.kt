package com.masterchan.lib.ext

import android.net.Uri
import com.masterchan.lib.sandbox.FileAccess
import com.masterchan.lib.sandbox.FileResponse

fun String.toFileResponse(): FileResponse? {
    return FileAccess.getResponse(this)
}

fun Uri.toFileResponse(): FileResponse? {
    return FileAccess.getResponse(this)
}