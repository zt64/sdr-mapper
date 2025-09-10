package dev.zt64.sdrmapper.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.zt64.sdrmapper.ui.viewmodel.IntroViewModel

@Composable
fun IntroScreen() {
    val vm = viewModel<IntroViewModel>()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "SDR Mapper")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 14.dp)
        ) {
            Text(
                text = "TODO",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.weight(1f))

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                }
            ) {
                Text("Next")
            }
        }
    }
}