package com.ajantha.apsa.ui.appdetail.util

import com.ajantha.apsa.model.AppUiModel

fun buildAppSummaryPrompt(model: AppUiModel): String {
    return """
            You are an Android privacy and security assistant.

            Analyze this app and give a short privacy/security summary for a normal user.

            App name: ${model.app.appName}
            Package name: ${model.app.packageName}
            Installer: ${model.app.installSource}
            Target SDK: ${model.app.targetSdk}
            Min SDK: ${model.app.minSdk}
            System app: ${model.app.isSystemApp}
            Debuggable: ${model.app.isDebuggable}
            Allow backup: ${model.app.allowBackup}
            Cleartext traffic: ${model.app.usesCleartextTraffic}

            Risk score: ${model.riskPercent}
            Risk level: ${model.riskLevel}

            Permission count: ${model.app.permissionCount}
            Dangerous permission count: ${model.app.dangerousPermissionCount}
            Exported activities count: ${model.app.exportedActivityCount}
            Exported services count: ${model.app.exportedServiceCount}
            Exported receivers count: ${model.app.exportedReceiverCount}
            Exported providers count: ${model.app.exportedProviderCount}

            Permissions:
            ${model.app.permissions.joinToString("\n")}

            Dangerous permissions:
            ${model.app.dangerousPermissions.joinToString("\n")}

            Give:
            1. Summary
            2. Main risks
            3. What is normal for this app
            4. Practical recommendations

            Keep the answer simple and not too long.
        """.trimIndent()
}

fun buildPermissionPrompt(appName: String, permission: String): String {
    return """
            You are an Android privacy assistant.

            Explain this permission in simple language for a normal user.

            App name: $appName
            Permission: $permission

            Answer in this structure:
            1. What it does
            2. Why the app may need it
            3. Privacy risk
            4. Recommendation

            If the permission is common for this kind of app, say so clearly.
        """.trimIndent()
}

fun buildComponentPrompt(
    appName: String,
    componentType: String,
    componentName: String
): String {
    return """
            You are an Android security assistant.

            Explain this exported Android component for a normal user.

            App name: $appName
            Component type: $componentType
            Component name: $componentName

            Answer in this structure:
            1. What this component is
            2. Why exported components can be risky
            3. Whether this is a concern
            4. Recommendation

            Keep it short and clear.
        """.trimIndent()
}