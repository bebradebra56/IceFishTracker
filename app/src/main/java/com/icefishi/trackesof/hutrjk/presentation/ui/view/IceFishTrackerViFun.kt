package com.icefishi.trackesof.hutrjk.presentation.ui.view

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IceFishTrackerViFun(private val context: Context) {
    fun iceFishTrackerSavePhoto() : Uri {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val df = sdf.format(Date())
        val dir = context.filesDir.absoluteFile
        if (!dir.exists()) {
            dir.mkdir()
        }
        return FileProvider.getUriForFile(
            context,
            "com.icefishi.trackesoffffffffff",
            File(dir, "/$df.jpg")
        )
    }

}