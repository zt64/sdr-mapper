package dev.zt64.sdrmapper.domain.manager

expect class SdrUsbManager {
    fun requestPermissions()
    fun refreshDevices()
    fun addDevice(
        vid: Int,
        pid: Int,
        name: String
    )
}