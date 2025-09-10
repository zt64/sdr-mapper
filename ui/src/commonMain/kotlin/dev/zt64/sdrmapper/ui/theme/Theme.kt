package dev.zt64.sdrmapper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun Theme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }
    ) {
        content()
        // CompositionLocalProvider(
        //     LocalContextMenuRepresentation provides DefaultContextMenuRepresentation(
        //         backgroundColor = MaterialTheme.colorScheme.surface,
        //         textColor = MaterialTheme.colorScheme.onSurface,
        //         itemHoverColor = MaterialTheme.colorScheme.inverseOnSurface
        //     ),
        //     LocalScrollbarStyle provides ScrollbarStyle(
        //         minimalHeight = 16.dp,
        //         thickness = 8.dp,
        //         shape = MaterialTheme.shapes.small,
        //         hoverDurationMillis = 300,
        //         unhoverColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
        //         hoverColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.50f)
        //     ),
        //     content = content
        // )
    }
}