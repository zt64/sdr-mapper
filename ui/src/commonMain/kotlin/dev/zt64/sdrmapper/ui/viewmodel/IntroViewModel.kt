package dev.zt64.sdrmapper.ui.viewmodel

import androidx.lifecycle.ViewModel
import dev.zt64.sdrmapper.domain.manager.PreferencesManager

class IntroViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {
    fun checkPermissions(): Boolean {
        TODO()
    }
}