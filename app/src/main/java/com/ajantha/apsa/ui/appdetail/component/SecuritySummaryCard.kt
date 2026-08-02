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
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WifiTethering
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
fun SecuritySummaryCard(
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
                    Icons.Outlined.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Security Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

            }

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                SecurityStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Key,
                    title = "Permissions",
                    value = app.permissionCount.toString()
                )

                SecurityStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.WarningAmber,
                    title = "Dangerous",
                    value = app.dangerousPermissionCount.toString(),
                    highlight = app.dangerousPermissionCount > 0
                )

            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                SecurityStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Apps,
                    title = "Activities",
                    value = app.exportedActivityCount.toString()
                )

                SecurityStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Settings,
                    title = "Services",
                    value = app.exportedServiceCount.toString()
                )

            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                SecurityStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.WifiTethering,
                    title = "Receivers",
                    value = app.exportedReceiverCount.toString()
                )

                SecurityStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Storage,
                    title = "Providers",
                    value = app.exportedProviderCount.toString()
                )

            }

        }

    }

}