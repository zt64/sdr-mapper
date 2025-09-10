package dev.zt64.sdrmapper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.zt64.sdrmapper.ui.Destination
import dev.zt64.sdrmapper.ui.screen.*
import dev.zt64.sdrmapper.ui.theme.Theme

@Composable
fun App() {
    Theme(
        isDarkTheme = isSystemInDarkTheme()
    ) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Destination.Home
        ) {
            composable<Destination.Intro> {
                IntroScreen()
            }
            composable<Destination.Home> {
                HomeScreen()
            }
            composable<Destination.HeatMap> {
                val heatmap = it.toRoute<Destination.HeatMap>()
                HeatMapScreen(heatmap.id)
            }
            composable<Destination.Settings> {
                SettingsScreen()
            }
        }
    }
}