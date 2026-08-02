package com.ajantha.msc.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {

    @Serializable
    data object Dashboard : Destination

    @Serializable
    data object AppList : Destination

    @Serializable
    data class AppDetail(
        val packageName: String
    ) : Destination
}

