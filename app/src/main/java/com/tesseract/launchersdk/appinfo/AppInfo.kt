package com.tesseract.launchersdk.appinfo

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String?,
    val packageName: String?,
    val icon: Drawable?,
    val versionCode: Int?,
    val versionName: String,
    val activityName: String?
)