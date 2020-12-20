package com.tesseract.launchersdk.appinfo

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LaunchManagerImpl(private val packageManager: PackageManager) : LauncherManager {
    /**
     * Returns AppInfo for all installed apps
     */
    override fun getInstalledApps(onAppsListLoaded: (appsList: List<AppInfo>) -> Unit) {
        // Query installed apps in a worker thread
        GlobalScope.launch {
            val appInfoList = getInstalledAppList()?.let { apps ->
                getAppsInfoList(apps)
            }
            withContext(Main) {
                appInfoList?.let { apps -> onAppsListLoaded(apps) }
            }
        }
    }

    private fun getInstalledAppList(): List<ResolveInfo>? {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, 0)
    }

    private fun getAppsInfoList(rawAppsList: List<ResolveInfo>): List<AppInfo> {
        return rawAppsList.map { app ->
            with(app.activityInfo) {
                AppInfo(
                    name = app.loadLabel(packageManager).toString(),
                    packageName = packageName,
                    icon = loadIcon(packageManager),
                    versionCode = getVersionCode(packageName),
                    versionName = getVersionName(packageName),
                    activityName = getLauncherIntent(packageName)?.component?.className
                )
            }
        }.sortedBy { it.name?.toLowerCase(Locale.getDefault()) }
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