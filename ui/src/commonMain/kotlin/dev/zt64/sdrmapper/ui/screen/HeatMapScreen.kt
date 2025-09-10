package dev.zt64.sdrmapper.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.layer.CircleLayer
import dev.sargunv.maplibrecompose.compose.source.getBaseSource
import dev.sargunv.maplibrecompose.core.BaseStyle
import dev.sargunv.maplibrecompose.expressions.dsl.const
import dev.zt64.sdrmapper.ui.viewmodel.MapViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HeatMapScreen(recordingId: String) {
    val vm = koinViewModel<MapViewModel>()

    MaplibreMap(baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")) {
        getBaseSource(id = "openmaptiles")?.let { tiles ->
            CircleLayer(id = "example", source = tiles, sourceLayer = "poi")
        }

        CircleLayer(
            id = "circle-overlay",
            source = vm.source,
            radius = const(10.dp),
            maxZoom = 50f
        )
    }
}