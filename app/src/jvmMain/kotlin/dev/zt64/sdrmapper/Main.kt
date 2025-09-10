package dev.zt64.sdrmapper

import androidx.compose.material3.Text
import androidx.compose.ui.window.singleWindowApplication
import dev.sargunv.maplibrecompose.compose.KcefProvider
import dev.sargunv.maplibrecompose.compose.MaplibreContextProvider

fun main() {
    singleWindowApplication(
        title = "SDR Mapper"
    ) {
        KcefProvider(
            loading = { Text("Performing first time setup ...") },
            content = {
                MaplibreContextProvider {
                    App()
                }
            }
        )
    }
}