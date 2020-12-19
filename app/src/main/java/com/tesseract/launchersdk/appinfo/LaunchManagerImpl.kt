package com.tesseract.launchersdk.appinfo

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LaunchManagerImpl(private val packageManager: PackageManager) : LauncherManager {
    private val _uninstallLiveData = MutableLiveData<AppInfo>()

    val uninstallLiveData: LiveData<AppInfo>
        get() = _uninstallLiveData

    /**
     * Returns AppInfo for all installed apps
     */
    override fun getInstalledApps(): List<AppInfo> {
        val rawAppsList: List<ApplicationInfo> =
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return getAppsList(rawAppsList)
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
        }
    }

    private fun getVersionName(packageName: String): String =
        packageManager.getPackageInfo(packageName, DEFAULT_FLAG).versionName

    private fun getVersionCode(packageName: String): Int =
        packageManager.getPackageInfo(packageName, DEFAULT_FLAG).versionCode

    private fun getLauncherIntent(packageName: String): Intent? =
        packageManager.getLaunchIntentForPackage(packageName)

    companion object {
        private const val DEFAULT_FLAG = 0
    }
}