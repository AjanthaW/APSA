package com.ajantha.apsa.util

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ajantha.apsa.R
import com.ajantha.apsa.model.RiskLevel
import com.ajantha.msc.constants.RiskColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val dangerousPermissionsSet = setOf(
    Manifest.permission.READ_SMS,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.READ_CONTACTS,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA
)

fun PackageManager.getPermissions(packageName: String): List<String> {
    val pkg = getPackageInfo(
        packageName,
        PackageManager.GET_PERMISSIONS
    )
    return pkg.requestedPermissions?.toList() ?: emptyList()
}

fun List<String>.dangerous(): List<String> {
    return filter { dangerousPermissionsSet.contains(it) }
}

fun ApplicationInfo.isDebuggable(): Boolean = (flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

fun ApplicationInfo.isSystemApp(): Boolean = (flags and ApplicationInfo.FLAG_SYSTEM) != 0

fun ApplicationInfo.isBackupEnabled(): Boolean = (flags and ApplicationInfo.FLAG_ALLOW_BACKUP) != 0

fun ApplicationInfo.usesCleartext(): Boolean =
    (flags and ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC) != 0

fun Boolean.toInt() = if (this) 1 else 0

@Composable
fun RiskLevel.label(): String {
    return when (this) {
        RiskLevel.HIGH -> stringResource(R.string.high_risk)
        RiskLevel.MEDIUM -> stringResource(R.string.medium_risk)
        RiskLevel.LOW -> stringResource(R.string.low_risk)
    }
}

@Composable
fun RiskLevel.color(): Color {
    return when (this) {
        RiskLevel.HIGH -> RiskColors.High
        RiskLevel.MEDIUM -> RiskColors.Medium
        RiskLevel.LOW -> RiskColors.Low
    }
}

fun Long.toScanTime(): String {
    val now = Calendar.getInstance()
    val scan = Calendar.getInstance().apply {
        timeInMillis = this@toScanTime
    }

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    return when {
        now.get(Calendar.YEAR) == scan.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == scan.get(Calendar.DAY_OF_YEAR) ->
            "Today • ${timeFormat.format(Date(this))}"

        now.get(Calendar.YEAR) == scan.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - scan.get(Calendar.DAY_OF_YEAR) == 1 ->
            "Yesterday • ${timeFormat.format(Date(this))}"

        else ->
            "${dateFormat.format(Date(this))} • ${timeFormat.format(Date(this))}"
    }
}

fun String.capitalizeFirst(): String =
    replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

fun String.removeMarkdown(): String {
    return this
        .replace(Regex("""^#{1,6}\s*""", RegexOption.MULTILINE), "")
        .replace("**", "")
        .replace("*", "")
        .replace("`", "")
        .replace(Regex("""^- """, RegexOption.MULTILINE), "")
        .replace(Regex("""^\* """, RegexOption.MULTILINE), "")
        .trim()
}