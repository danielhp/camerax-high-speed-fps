package com.sample.cameraxhighspeedfps.ui

import android.app.Application
import androidx.camera.core.CameraInfo
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.sample.cameraxhighspeedfps.camera.HighSpeedCameraManager
import com.sample.cameraxhighspeedfps.camera.HighSpeedConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HighSpeedViewModel(application: Application) : AndroidViewModel(application) {
    private val cameraManager = HighSpeedCameraManager(application)

    val configs = cameraManager.configs
    val isRecording = cameraManager.isRecording

    private val _selectedConfig = MutableStateFlow<HighSpeedConfig?>(null)
    val selectedConfig = _selectedConfig.asStateFlow()

    private val _slowMotionEnabled = MutableStateFlow(false)
    val slowMotionEnabled = _slowMotionEnabled.asStateFlow()

    fun discoverCapabilities(cameraInfo: CameraInfo) {
        cameraManager.discoverCapabilities(cameraInfo)
        // Automatically select the first config if none is selected
        viewModelScope.launch {
            cameraManager.configs.collect { configs ->
                if (_selectedConfig.value == null && configs.isNotEmpty()) {
                    _selectedConfig.value = configs.first()
                }
            }
        }
    }

    fun selectConfig(config: HighSpeedConfig) {
        _selectedConfig.value = config
    }

    fun toggleSlowMotion(enabled: Boolean) {
        _slowMotionEnabled.value = enabled
    }

    fun bindCamera(
        cameraProvider: ProcessCameraProvider,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        config: HighSpeedConfig,
        slowMotion: Boolean
    ) {
        viewModelScope.launch {
            cameraManager.bindCamera(cameraProvider, lifecycleOwner, previewView, config, slowMotion)
        }
    }

    fun toggleRecording() {
        cameraManager.toggleRecording()
    }
}
