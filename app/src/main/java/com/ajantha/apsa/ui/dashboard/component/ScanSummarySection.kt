package com.ajantha.apsa.ui.dashboard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ajantha.apsa.R
import com.ajantha.apsa.model.RiskLevel

@Composable
fun ScanSummarySection(
    highRiskApps: Int,
    mediumRiskApps: Int,
    safeApps: Int,
) {

    val totalApps = highRiskApps + mediumRiskApps + safeApps

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Analytics,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.scan_summary),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50)
            ) {

                Text(
                    text = "$totalApps Apps",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 2.dp
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SummaryCard(
                modifier = Modifier.weight(1f),
                riskLevel = RiskLevel.HIGH,
                title = stringResource(R.string.high_risk),
                value = highRiskApps.toString()
            )

            SummaryCard(
                modifier = Modifier.weight(1f),
                riskLevel = RiskLevel.MEDIUM,
                title = stringResource(R.string.medium_risk),
                value = mediumRiskApps.toString()
            )

            SummaryCard(
                modifier = Modifier.weight(1f),
                riskLevel = RiskLevel.LOW,
                title = stringResource(R.string.low_risk),
                value = safeApps.toString()
            )
        }
    }
}