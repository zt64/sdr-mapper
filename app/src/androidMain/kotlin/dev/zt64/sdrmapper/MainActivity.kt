package dev.zt64.sdrmapper

import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.zt64.sdrmapper.domain.manager.ACTION_USB_PERMISSION

class MainActivity : ComponentActivity() {
    private val usbReceiver = SdrUsbReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()

        super.onCreate(savedInstanceState)

        System.loadLibrary("sdrmapper_rs")

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbReceiver, filter, RECEIVER_EXPORTED)
        }

        setContent {
            App()
        }
    }
}