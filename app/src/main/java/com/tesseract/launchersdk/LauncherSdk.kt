package com.tesseract.launchersdk

import android.content.Context
import com.tesseract.launchersdk.appinfo.LaunchManagerImpl
import com.tesseract.launchersdk.appinfo.LauncherManager

object LauncherSdk {

    private lateinit var launcherManager: LauncherManager

    /**
     * Returns an instance of LauncherManager
     */
    fun getInstance(context: Context): LauncherManager {
        if (!::launcherManager.isInitialized) {
            launcherManager = LaunchManagerImpl(context.packageManager)
        }
        return launcherManager
    }
}