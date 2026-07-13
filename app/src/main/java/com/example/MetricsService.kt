package com.example

import android.app.*
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*
import android.os.BatteryManager
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.net.TrafficStats
import java.io.File
import androidx.core.app.NotificationCompat

class MetricsService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastRxBytes = TrafficStats.getTotalRxBytes()
    private var lastTxBytes = TrafficStats.getTotalTxBytes()
    private var lastTime = System.currentTimeMillis()
    private var batteryLevel = 0

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        // Attempt to register receiver and pull sticky battery intent immediately
        val batteryIntent = registerReceiver(null, filter)
        batteryIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level != -1 && scale != -1) {
                batteryLevel = (level.toFloat() / scale * 100).toInt()
            }
        }
        
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(batteryReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(batteryReceiver, filter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        if (batteryLevel <= 0) {
            val bm = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(1, 100)
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level != -1 && scale != -1) {
                batteryLevel = (level.toFloat() / scale.toFloat() * 100).toInt()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            startForeground(1, createNotification(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, createNotification())
        }
        serviceScope.launch {
            while (isActive) {
                try {
                    updateMetrics()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(1000)
            }
        }
        return START_STICKY
    }

    private suspend fun updateMetrics() {
        val cpu = CpuTracker.getCpuUsage()
        val mem = calculateMemoryUsage()
        
        val rxBytes = TrafficStats.getTotalRxBytes()
        val txBytes = TrafficStats.getTotalTxBytes()
        val currentTime = System.currentTimeMillis()
        
        val timeDelta = (currentTime - lastTime) / 1000.0
        val downloadSpeed = if (timeDelta > 0) (rxBytes - lastRxBytes) / 1024.0 / timeDelta else 0.0
        val uploadSpeed = if (timeDelta > 0) (txBytes - lastTxBytes) / 1024.0 / timeDelta else 0.0
        
        lastRxBytes = rxBytes
        lastTxBytes = txBytes
        lastTime = currentTime
        
        DataRepository.updateMetrics(cpu, mem, downloadSpeed, uploadSpeed, batteryLevel)
    }

    private fun calculateMemoryUsage(): Int {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return (((mi.totalMem - mi.availMem).toDouble() / mi.totalMem) * 100).toInt()
    }

    private fun createNotification(): Notification {
        val channelId = "metrics_service"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Metrics Service", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Metrics Tracker")
            .setContentText("Monitoring system metrics")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        try {
            unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {}
        serviceScope.cancel()
        super.onDestroy()
    }
}
