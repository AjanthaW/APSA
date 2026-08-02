package com.ajantha.apsa.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ajantha.apsa.model.RiskLevel
import com.ajantha.apsa.util.color
import com.ajantha.apsa.util.label

@Composable
fun RiskBadge(
    riskLevel: RiskLevel
) {

    Surface(
        shape = RoundedCornerShape(50),
        color = riskLevel.color().copy(alpha = 0.15f)
    ) {

        Text(
            text = riskLevel.label(),
            color = riskLevel.color(),
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            style = MaterialTheme.typography.labelMedium
        )
    }
}