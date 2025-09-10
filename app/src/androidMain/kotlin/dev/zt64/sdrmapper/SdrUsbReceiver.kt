package dev.zt64.sdrmapper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import dev.zt64.sdrmapper.domain.manager.ACTION_USB_PERMISSION
import dev.zt64.sdrmapper.domain.manager.SdrUsbManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SdrUsbReceiver : BroadcastReceiver(), KoinComponent {
    private val sdrUsbManager: SdrUsbManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_USB_PERMISSION == intent.action) {
            synchronized(this) {
                val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    device?.apply {
                        sdrUsbManager.addDevice(vendorId, productId, deviceName)
                    }
                } else {
                    Log.d("SDRMapper", "permission denied for device $device")
                }
            }
        }
    }
}