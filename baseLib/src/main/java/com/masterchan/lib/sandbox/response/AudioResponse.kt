package com.masterchan.lib.sandbox.response

import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File

/**
 * API29以下音频访问结果
 * @author: MasterChan
 * @date: 2022-06-10 15:30
 */
open class AudioResponse(
    uri: Uri,
    file: File,
    path: String,
    length: Long,
    override var addDate: Long
) : FileResponse(uri, file, path, length) {

    open var duration = -1L
        get() = if (field == -1L) calculateDuration().also { field = it } else field

    private fun calculateDuration(): Long {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: -1
    }
}