package com.ajantha.apsa.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.ajantha.apsa.model.InstalledApp

class AppScanner(context: Context) {

    private val pm = context.packageManager

    fun getInstalledApps(): List<InstalledApp> {
        val packages = pm.getInstalledPackages(
            PackageManager.GET_PERMISSIONS or PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES or PackageManager.GET_RECEIVERS or PackageManager.GET_PROVIDERS
        )
        return packages
            .filter { pkg ->
                val appInfo = pkg.applicationInfo ?: return@filter false

                val isSystem =
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                            (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

                !isSystem
            }
            .map { pkg ->
                val appInfo = pkg.applicationInfo
                val permissions = pm.getPermissions(pkg.packageName)
                val dangerous = permissions.dangerous()
                val activities =
                    pkg.activities?.filter { it.exported }?.map { it.name } ?: emptyList()
                val services = pkg.services?.filter { it.exported }?.map { it.name } ?: emptyList()
                val receivers =
                    pkg.receivers?.filter { it.exported }?.map { it.name } ?: emptyList()
                val providers =
                    pkg.providers?.filter { it.exported }?.map { it.name } ?: emptyList()
                val iconDrawable = appInfo?.loadIcon(pm)

                InstalledApp(
                    packageName = pkg.packageName,
                    appName = appInfo?.loadLabel(pm).toString(),
                    apkPath = appInfo?.sourceDir ?: "",
                    icon = iconDrawable,
                    targetSdk = appInfo?.targetSdkVersion ?: -1,
                    minSdk = appInfo?.minSdkVersion ?: -1,
                    isDebuggable = appInfo?.isDebuggable() ?: false,
                    allowBackup = appInfo?.isBackupEnabled() ?: false,
                    isSystemApp = appInfo?.isSystemApp() ?: false,
                    usesCleartextTraffic = appInfo?.usesCleartext() ?: false,
                    installSource = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        pm.getInstallSourceInfo(pkg.packageName).installingPackageName ?: ""
                    } else {
                        @Suppress("DEPRECATION")
                        pm.getInstallerPackageName(pkg.packageName) ?: ""
                    },
                    permissions = permissions,
                    dangerousPermissions = dangerous,
                    exportedActivities = activities,
                    exportedServices = services,
                    exportedReceivers = receivers,
                    exportedProviders = providers,
                    permissionCount = permissions.size,
                    dangerousPermissionCount = dangerous.size,
                    exportedActivityCount = activities.size,
                    exportedServiceCount = services.size,
                    exportedReceiverCount = receivers.size,
                    exportedProviderCount = providers.size
                )
            }
    }
}