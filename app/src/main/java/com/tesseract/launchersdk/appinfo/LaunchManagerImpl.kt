package com.tesseract.launchersdk.appinfo

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LaunchManagerImpl(private val packageManager: PackageManager) : LauncherManager {
    /**
     * Returns AppInfo for all installed apps
     */
    override fun getInstalledApps(onAppsListLoaded: (appsList: List<AppInfo>) -> Unit) {
        // Query installed apps in a worker thread
        GlobalScope.launch {
            val rawAppsList: List<ApplicationInfo> =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            withContext(Main) {
                onAppsListLoaded(getAppsList(rawAppsList))
            }
        }
    }

    private fun getAppsList(rawAppsList: List<ApplicationInfo>): List<AppInfo> {
        return rawAppsList.map { app ->
            AppInfo(
                name = app.loadLabel(packageManager).toString(),
                packageName = app.packageName,
                icon = app.loadIcon(packageManager),
                versionCode = getVersionCode(app.packageName),
                versionName = getVersionName(app.packageName),
                activityName = getLauncherIntent(app.packageName)?.component?.className
            )
        }.sortedBy { it.name }
    }

    private fun getVersionName(packageName: String): String? =
        packageManager.getPackageInfo(packageName, DEFAULT_FLAG).versionName ?: ""

    private fun getVersionCode(packageName: String): Int? =
        packageManager.getPackageInfo(packageName, DEFAULT_FLAG).versionCode ?: 0

    private fun getLauncherIntent(packageName: String): Intent? =
        packageManager.getLaunchIntentForPackage(packageName)

    companion object {
        private const val DEFAULT_FLAG = 0
    }
}