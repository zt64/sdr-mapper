package dev.zt64.sdrmapper.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.zt64.sdrmapper.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen() {
    val vm = viewModel<SettingsViewModel>()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}