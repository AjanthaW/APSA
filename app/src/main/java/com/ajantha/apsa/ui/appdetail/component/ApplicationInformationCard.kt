package com.ajantha.apsa.ui.appdetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.PublicOff
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.SystemUpdateAlt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ajantha.apsa.model.InstalledApp

@Composable
fun ApplicationInformationCard(
    modifier: Modifier = Modifier,
    app: InstalledApp
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Outlined.Android,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "Application Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

            }

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                InfoTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.DeveloperMode,
                    title = "Target SDK",
                    value = "API ${app.targetSdk}"
                )

                InfoTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.SystemUpdateAlt,
                    title = "Minimum SDK",
                    value = "API ${app.minSdk}"
                )

            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                InfoTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.PublicOff,
                    title = "Cleartext",
                    value = if (app.usesCleartextTraffic) "Allowed" else "Disabled"
                )

                InfoTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.BugReport,
                    title = "Debuggable",
                    value = if (app.isDebuggable) "Enabled" else "Disabled"
                )

                InfoTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Backup,
                    title = "Backup",
                    value = if (app.allowBackup) "Enabled" else "Disabled"
                )

            }

            Spacer(Modifier.height(12.dp))

            InfoTile(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Store,
                title = "Source",
                value = app.installSource
            )


        }

    }

}