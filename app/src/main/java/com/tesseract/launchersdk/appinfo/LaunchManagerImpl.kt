package com.tesseract.launchersdk.appinfo

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
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
        val rawAppsList: List<ResolveInfo> =
            packageManager.queryIntentActivities(
                getLauncherIntent(),
                DEFAULT_FLAG
            )
        return getAppsList(rawAppsList)
    }

    private fun getAppsList(rawAppsList: List<ResolveInfo>): List<AppInfo> {
        return rawAppsList.map { app ->
            AppInfo(
                name = app.activityInfo.loadLabel(packageManager).toString(),
                packageName = app.activityInfo.packageName,
                icon = app.activityInfo.loadIcon(packageManager)
            )
        }
    }

    private fun getLauncherIntent(): Intent {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        return intent
    }

    companion object {
        private const val DEFAULT_FLAG = 0
    }
}