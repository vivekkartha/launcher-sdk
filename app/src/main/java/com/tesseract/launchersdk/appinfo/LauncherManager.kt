package com.tesseract.launchersdk.appinfo

interface LauncherManager {
    fun getInstalledApps(onAppsListLoaded: (appsList: List<AppInfo>) -> Unit)
}