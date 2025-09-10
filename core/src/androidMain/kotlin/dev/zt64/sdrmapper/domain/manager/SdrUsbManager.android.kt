package dev.zt64.sdrmapper.domain.manager

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbManager
import androidx.core.content.getSystemService

const val ACTION_USB_PERMISSION = "dev.zt64.sdrmapper.USB_PERMISSION"

actual class SdrUsbManager(private val application: Application, private val notificationManager: NotificationManager) {
    actual fun requestPermissions() {
        val usbManager = application.getSystemService<UsbManager>()!!

        val deviceList = usbManager.deviceList
        if (deviceList.isEmpty()) {
            notificationManager.notifySystem("No USB devices found")
        } else {
            val device = deviceList.values.first()
            if (!usbManager.hasPermission(device)) {
                val intent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    Intent(ACTION_USB_PERMISSION).apply {
                        `package` = application.packageName
                    }
                } else {
                    Intent(ACTION_USB_PERMISSION)
                }
                val permissionIntent = PendingIntent.getBroadcast(
                    application,
                    0,
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )

                usbManager.requestPermission(device, permissionIntent)
            } else {
                notificationManager.notifySystem("Permission already granted for device: ${device.deviceName}")
            }
        }
    }

    actual fun refreshDevices() {
    }

    actual fun addDevice(
        vid: Int,
        pid: Int,
        name: String
    ) {
        notificationManager.notifySystem("Added device: $name (VID: $vid, PID: $pid)")
    }
}