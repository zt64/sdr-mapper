package dev.zt64.sdrmapper.ui.viewmodel

import androidx.lifecycle.ViewModel
import dev.sargunv.maplibrecompose.core.source.GeoJsonData
import dev.sargunv.maplibrecompose.core.source.GeoJsonOptions
import dev.sargunv.maplibrecompose.core.source.GeoJsonSource

class MapViewModel : ViewModel() {
    val circleSourceId = "custom-circle"
    val source = GeoJsonSource(
        id = circleSourceId,
        data = GeoJsonData.Uri("https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_10m_ports.geojson"),
        options = GeoJsonOptions()
    )
}