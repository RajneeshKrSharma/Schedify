package com.unique.schedify.core.presentation.base_composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unique.schedify.core.ConnectivityChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(
    connectivityChecker: ConnectivityChecker
) : ViewModel() {
    private val _isConnected = MutableStateFlow(true) // Use MutableStateFlow to hold connectivity state
    val isConnected: StateFlow<Boolean> = _isConnected // Expose StateFlow for composables to observe

    // This represents whether the strip should be shown
    private val _showStrip = MutableStateFlow(false)
    val showStrip: StateFlow<Boolean> get() = _showStrip

    init {
        // Collect from the ConnectivityChecker and update the state
        viewModelScope.launch {
            connectivityChecker.isConnected.collect { isConnected ->
                _isConnected.value = isConnected // Update state when network status changes
                updateConnectionStatus(isConnected)
            }
        }
    }

    private fun updateConnectionStatus(connected: Boolean) {
        viewModelScope.launch {
            _isConnected.value = connected
            if (connected) {
                _showStrip.value = true
                // Wait for 2 seconds before hiding the strip
                delay(2000)
                _showStrip.value = false
            } else {
                _showStrip.value = true
            }
        }
    }
}