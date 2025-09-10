package dev.zt64.sdrmapper.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zt64.sdrmapper.NativeSdr
import dev.zt64.sdrmapper.ui.generated.resources.*
import dev.zt64.sdrmapper.ui.viewmodel.HomeViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen() {
    val vm = koinViewModel<HomeViewModel>()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    label = { Text(stringResource(Res.string.home)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(Res.string.home)
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    label = { Text(stringResource(Res.string.recordings)) },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(Res.string.recordings)
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    label = { Text(stringResource(Res.string.settings)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(Res.string.settings)
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "SDR name here",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "VID",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row {
                        FilledTonalIconButton(
                            onClick = { }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            Surface {
                Button(
                    onClick = {
                        println("Rust: ${NativeSdr.test()}")
                    }
                ) {
                    Text("Test Native")
                }
            }

            Surface {
                Button(
                    onClick = {
                        vm.requestPermissions()
                    }
                ) {
                    Text("Request Permissions")
                }
            }
        }
    }
}