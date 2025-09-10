package dev.zt64.sdrmapper.ui.viewmodel

import androidx.lifecycle.ViewModel
import dev.zt64.sdrmapper.domain.manager.SdrUsbManager

class HomeViewModel(private val sdrUsbManager: SdrUsbManager) : ViewModel() {
    fun requestPermissions() {
        sdrUsbManager.requestPermissions()
    }
}