package com.livelock.app.media

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File

object VideoTrimmer {

    fun trim(
        context: Context,
        inputUri: Uri,
        outputFile: File,
        maxDurationMs: Long = 15_000,
        onComplete: (Boolean) -> Unit
    ) {
        try {
            val mediaItem = MediaItem.fromUri(inputUri)
            val edited = EditedMediaItem.Builder(mediaItem)
                .setRemoveAudio(false)
                .build()
            val transformer = Transformer.Builder(context)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, result: ExportResult) {
                        onComplete(true)
                    }
                    override fun onError(
                        composition: Composition,
                        result: ExportResult,
                        exception: ExportException
                    ) {
                        onComplete(false)
                    }
                })
                .build()
            transformer.start(edited, outputFile.absolutePath)
        } catch (e: Exception) {
            onComplete(false)
        }
    }
}
