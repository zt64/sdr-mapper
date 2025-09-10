package dev.zt64.sdrmapper.ui

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    object Intro : Destination

    @Serializable
    data object Home : Destination

    @Serializable
    data class HeatMap(val id: String) : Destination

    @Serializable
    object Settings : Destination
}