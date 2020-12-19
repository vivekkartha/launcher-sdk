package com.tesseract.launchersdk.appinfo

import androidx.lifecycle.LiveData

interface LauncherManager {
    fun getInstalledApps(): List<AppInfo>
}