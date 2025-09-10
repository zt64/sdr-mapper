package dev.zt64.sdrmapper.domain.manager

import android.app.Application
import android.widget.Toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class NotificationManager actual constructor() : KoinComponent {
    private val application by inject<Application>()

    actual fun notifyInApp(message: String) {
    }

    actual fun notifySystem(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_LONG).show()
    }
}