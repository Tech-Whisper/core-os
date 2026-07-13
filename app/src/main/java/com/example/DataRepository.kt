package com.example

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DataRepository {
    private val _cpuUsage = MutableStateFlow(-1)
    val cpuUsage: StateFlow<Int> = _cpuUsage

    private val _memoryUsage = MutableStateFlow(-1)
    val memoryUsage: StateFlow<Int> = _memoryUsage

    private val _networkDownloadSpeed = MutableStateFlow(-1.0)
    val networkDownloadSpeed: StateFlow<Double> = _networkDownloadSpeed

    private val _networkUploadSpeed = MutableStateFlow(-1.0)
    val networkUploadSpeed: StateFlow<Double> = _networkUploadSpeed

    private val _batteryLevel = MutableStateFlow(-1)
    val batteryLevel: StateFlow<Int> = _batteryLevel

    fun updateMetrics(
        cpu: Int,
        memory: Int,
        download: Double,
        upload: Double,
        battery: Int
    ) {
        _cpuUsage.value = cpu
        _memoryUsage.value = memory
        _networkDownloadSpeed.value = download
        _networkUploadSpeed.value = upload
        _batteryLevel.value = battery
    }
}
