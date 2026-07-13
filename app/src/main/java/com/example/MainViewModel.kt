package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Stack

data class ProcessInfo(
    val pid: String,
    val name: String,
    val initialCpu: Double,
    val initialRam: Double,
    val status: String,
    val category: String, // CPU, RAM, Battery, Network
    var currentCpu: Double = initialCpu,
    var currentRam: Double = initialRam,
    val lastUsed: Long = 0L
)

data class SystemAlert(
    val id: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val type: String, // CPU, Battery, RAM
    val severity: String, // Critical, Medium
    val initialValue: Int,
    var value: Int = initialValue
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.HomeDashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _cpuLoad = MutableStateFlow(0)
    val cpuLoad = _cpuLoad.asStateFlow()

    private val _ramLoad = MutableStateFlow(0)
    val ramLoad = _ramLoad.asStateFlow()

    private val _pwrLoad = MutableStateFlow(0)
    val pwrLoad = _pwrLoad.asStateFlow()

    // System Dashboard Values
    private val _sysCpu = MutableStateFlow(0)
    val sysCpu = _sysCpu.asStateFlow()

    private val _sysMemory = MutableStateFlow(0)
    val sysMemory = _sysMemory.asStateFlow()

    private val _sysBattery = MutableStateFlow(0)
    val sysBattery = _sysBattery.asStateFlow()

    // Network speeds
    private val _downloadSpeed = MutableStateFlow(0.0)
    val downloadSpeed = _downloadSpeed.asStateFlow()

    private val _uploadSpeed = MutableStateFlow(0.0)
    val uploadSpeed = _uploadSpeed.asStateFlow()

    // Screen navigation stack to support push_back transition correctly
    private val navigationStack = Stack<Screen>()

    private val _usageAccessGranted = MutableStateFlow(true)
    val usageAccessGranted = _usageAccessGranted.asStateFlow()

    // Filters for Live Monitor
    private val _activeFilter = MutableStateFlow("CPU")
    val activeFilter = _activeFilter.asStateFlow()

    // Process List
    private val _processes = MutableStateFlow(
        listOf<ProcessInfo>()
    )
    val processes = _processes.asStateFlow()

        // Historical telemetry sequences for drawing actual working charts
    private val _cpuHistory = MutableStateFlow<List<Int>>(emptyList())
    val cpuHistory = _cpuHistory.asStateFlow()

    private val _pwrHistory = MutableStateFlow<List<Int>>(emptyList())
    val pwrHistory = _pwrHistory.asStateFlow()

    private val _netHistory = MutableStateFlow<List<Double>>(emptyList())
    val netHistory = _netHistory.asStateFlow()

    private val _selectedProcess = MutableStateFlow<ProcessInfo?>(null)
    val selectedProcess = _selectedProcess.asStateFlow()

    fun selectProcess(proc: ProcessInfo) {
        _selectedProcess.value = proc
        navigateTo(Screen.AppDetailView)
    }

    // Active diagnostic alerts
    private val _alerts = MutableStateFlow(
        listOf<SystemAlert>()
    )
    val alerts = _alerts.asStateFlow()

    fun completeOnboarding() {
        val sharedPref = getApplication<Application>().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("onboarding_completed", true).apply()
    }

    init {
        _currentScreen.value = Screen.HomeDashboard
        
        // Live system updates subscription natively in ViewModel to ensure data delivery without Service constraints
        viewModelScope.launch(Dispatchers.IO) {
            var lastRxBytes = android.net.TrafficStats.getTotalRxBytes()
            var lastTxBytes = android.net.TrafficStats.getTotalTxBytes()
            var lastTime = System.currentTimeMillis()
            val am = getApplication<Application>().getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val bm = getApplication<Application>().getSystemService(android.content.Context.BATTERY_SERVICE) as android.os.BatteryManager
            
            while (true) {
                // CPU
                try {
                    val cpu = CpuTracker.getCpuUsage()
                    _sysCpu.value = cpu
                    _cpuLoad.value = cpu
                } catch (e: Exception) { e.printStackTrace() }
                
                // Memory
                try {
                    val mi = android.app.ActivityManager.MemoryInfo()
                    am.getMemoryInfo(mi)
                    if (mi.totalMem > 0) {
                        val mem = (((mi.totalMem - mi.availMem).toDouble() / mi.totalMem) * 100).toInt()
                        _sysMemory.value = mem.coerceIn(1, 100)
                        _ramLoad.value = mem.coerceIn(1, 100)
                    }
                } catch (e: Exception) { e.printStackTrace() }
                
                // Battery
                try {
                    val bat = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    if (bat > 0) {
                        _sysBattery.value = bat
                        _pwrLoad.value = bat
                    } else {
                        val filter = android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
                        val batteryIntent = getApplication<Application>().registerReceiver(null, filter)
                        val level = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
                        val scale = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
                        if (level != -1 && scale != -1) {
                            val batteryPct = (level.toFloat() / scale.toFloat() * 100).toInt()
                            _sysBattery.value = batteryPct
                            _pwrLoad.value = batteryPct
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
                
                // Network
                try {
                    val rxBytes = android.net.TrafficStats.getTotalRxBytes()
                    val txBytes = android.net.TrafficStats.getTotalTxBytes()
                    
                    val fallbackRx = android.net.TrafficStats.getUidRxBytes(android.os.Process.myUid())
                    val fallbackTx = android.net.TrafficStats.getUidTxBytes(android.os.Process.myUid())
                    
                    val realRx = if (rxBytes == android.net.TrafficStats.UNSUPPORTED.toLong()) fallbackRx else rxBytes
                    val realTx = if (txBytes == android.net.TrafficStats.UNSUPPORTED.toLong()) fallbackTx else txBytes

                    val currentTime = System.currentTimeMillis()
                    val timeDelta = (currentTime - lastTime) / 1000.0
                    
                    if (timeDelta > 0 && lastTime > 0) {
                        val rxDiff = if (realRx >= lastRxBytes) realRx - lastRxBytes else 0
                        val txDiff = if (realTx >= lastTxBytes) realTx - lastTxBytes else 0
                        val dl = rxDiff / 1024.0 / timeDelta
                        val ul = txDiff / 1024.0 / timeDelta
                        _downloadSpeed.value = if (dl.isNaN() || dl.isInfinite()) 0.0 else dl
                        _uploadSpeed.value = if (ul.isNaN() || ul.isInfinite()) 0.0 else ul
                    }
                    lastRxBytes = realRx.coerceAtLeast(0)
                    lastTxBytes = realTx.coerceAtLeast(0)
                    lastTime = currentTime
                } catch (e: Exception) { e.printStackTrace() }
                
                delay(1000)
            }
        }

        // Fixed-interval history collection
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(1000)
                
                val currentCpuList = _cpuHistory.value.toMutableList()
                if (currentCpuList.size >= 60) currentCpuList.removeAt(0)
                currentCpuList.add(_sysCpu.value.coerceAtLeast(0))
                _cpuHistory.value = currentCpuList

                val currentPwrList = _pwrHistory.value.toMutableList()
                if (currentPwrList.size >= 60) currentPwrList.removeAt(0)
                currentPwrList.add(_sysBattery.value.coerceAtLeast(0))
                _pwrHistory.value = currentPwrList

                val currentNetList = _netHistory.value.toMutableList()
                if (currentNetList.size >= 60) currentNetList.removeAt(0)
                currentNetList.add(_downloadSpeed.value.coerceAtLeast(0.0))
                _netHistory.value = currentNetList
            }
        }

        // Start periodic process update scan collecting real on-device apps dynamically
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    refreshProcesses(getApplication())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(3000)
            }
        }
    }

    fun setFilter(filter: String) {
        _activeFilter.value = filter
    }

    fun hasUsageStatsPermission(): Boolean {
        val appOps = getApplication<Application>().getSystemService(
            android.content.Context.APP_OPS_SERVICE
        ) as android.app.AppOpsManager
        val mode = appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            getApplication<Application>().packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    fun hasBatteryOptimizationsIgnorePermission(context: android.content.Context): Boolean {
        val pm = context.getSystemService(android.content.Context.POWER_SERVICE) as android.os.PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun hasNetworkPermission(context: android.content.Context): Boolean {
        val cm = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        return cm.activeNetwork != null
    }

    fun navigateTab(screen: Screen) {
        if (_currentScreen.value == screen) return
        navigationStack.clear()
        _currentScreen.value = screen
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value == screen) return
        navigationStack.push(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (!navigationStack.isEmpty()) {
            _currentScreen.value = navigationStack.pop()
        } else {
            if (_currentScreen.value != Screen.HomeDashboard) {
                _currentScreen.value = Screen.HomeDashboard
            }
        }
    }

    private var cachedInstalledApps: List<android.content.pm.ApplicationInfo>? = null
    private var lastAppCacheTime: Long = 0

    fun refreshProcesses(context: android.content.Context) {
        val am = context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val pm = context.packageManager
        
        val runningApps = am.runningAppProcesses ?: emptyList()
        val updatedList = mutableListOf<ProcessInfo>()
        val pidsToPackage = mutableMapOf<String, Int>()
        
        for (appProcess in runningApps) {
            val pid = appProcess.pid
            val packageName = appProcess.processName.split(":").first()
            pidsToPackage[packageName] = pid
            
            // Get Real Memory
            val memInfo = am.getProcessMemoryInfo(intArrayOf(pid))
            val ramUsageMB = if (memInfo.isNotEmpty()) {
                memInfo[0].totalPss / 1024.0
            } else {
                0.0
            }
            
            val appInfo = try {
                pm.getApplicationInfo(packageName, 0)
            } catch (e: Exception) { null }
            
            val isSystem = (appInfo?.flags ?: 0) and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0
            val cat = if (isSystem) "System" else "User App"
            
            val rName = appInfo?.let { pm.getApplicationLabel(it).toString() } ?: packageName
            val currentProcessCpu = if (packageName == context.packageName) _cpuLoad.value.toDouble() else (ramUsageMB / 10).coerceIn(1.0, 15.0)

            updatedList.add(
                ProcessInfo(
                    pid = packageName, // using packageName as id for killing
                    name = rName,
                    initialCpu = currentProcessCpu,
                    initialRam = ramUsageMB,
                    status = "Active",
                    category = cat,
                    lastUsed = System.currentTimeMillis(),
                    currentCpu = currentProcessCpu,
                    currentRam = ramUsageMB
                )
            )
        }
        
        var addedFromUsage = false
        if (hasUsageStatsPermission()) {
            val usm = context.getSystemService(android.content.Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
            val time = System.currentTimeMillis()
            val stats = usm.queryUsageStats(android.app.usage.UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60 * 60 * 24, time)
            if (stats != null) {
                val recentStats = stats.filter { it.lastTimeUsed > 0 && !pidsToPackage.containsKey(it.packageName) }
                    .sortedByDescending { it.lastTimeUsed }
                    .distinctBy { it.packageName }
                    .take(20)
                
                for (usage in recentStats) {
                    val packageName = usage.packageName
                    val appInfo = try { pm.getApplicationInfo(packageName, 0) } catch (e: Exception) { null } ?: continue
                    val isSystem = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                    val rName = pm.getApplicationLabel(appInfo).toString()
                    addedFromUsage = true
                    
                    val rx = android.net.TrafficStats.getUidRxBytes(appInfo.uid)
                    val tx = android.net.TrafficStats.getUidTxBytes(appInfo.uid)
                    val networkMb = ((if (rx > 0) rx else 0) + (if (tx > 0) tx else 0)) / (1024.0 * 1024.0)
                    
                    // Map real foreground app usage to a meaningful percentage
                    val usageMinutes = usage.totalTimeInForeground / (1000.0 * 60)
                    val usagePercentage = (usageMinutes / (60 * 4) * 100).coerceIn(0.1, 80.0) // 4 hours active = 100%

                    updatedList.add(
                        ProcessInfo(
                            pid = packageName,
                            name = rName,
                            initialCpu = usagePercentage,
                            initialRam = networkMb,
                            status = "In-Background",
                            category = if (isSystem) "System" else "User App",
                            lastUsed = usage.lastTimeUsed,
                            currentCpu = usagePercentage,
                            currentRam = networkMb
                        )
                    )
                }
            }
        }

        // Fallback: Show top apps by network usage if UsageStats misses them or lacks permission
        if (!addedFromUsage) {
            val currentTime = System.currentTimeMillis()
            if (cachedInstalledApps == null || currentTime - lastAppCacheTime > 60000) {
                 cachedInstalledApps = try { pm.getInstalledApplications(0) } catch (e: Exception) { emptyList() }
                 lastAppCacheTime = currentTime
            }
            val installedApps = cachedInstalledApps ?: emptyList()
            val appUsage = installedApps.mapNotNull { appInfo ->
                if (pidsToPackage.containsKey(appInfo.packageName)) return@mapNotNull null
                val rx = android.net.TrafficStats.getUidRxBytes(appInfo.uid)
                val tx = android.net.TrafficStats.getUidTxBytes(appInfo.uid)
                val totalBytes = (if (rx > 0) rx else 0) + (if (tx > 0) tx else 0)
                if (totalBytes > 0) Pair(appInfo, totalBytes) else null
            }.sortedByDescending { it.second }.take(15)

            for ((appInfo, bytes) in appUsage) {
                val isSystem = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                val rName = pm.getApplicationLabel(appInfo).toString()
                val mb = bytes / (1024.0 * 1024.0)
                updatedList.add(
                    ProcessInfo(
                        pid = appInfo.packageName,
                        name = rName,
                        initialCpu = (mb / 5).coerceIn(0.0, 45.0),
                        initialRam = mb,
                        status = "Network Active",
                        category = if (isSystem) "System" else "User App",
                        lastUsed = System.currentTimeMillis() - 10000,
                        currentCpu = (mb / 5).coerceIn(0.0, 45.0),
                        currentRam = mb
                    )
                )
            }
        }

        _processes.value = updatedList.sortedByDescending { it.currentCpu + it.currentRam }
    }

    fun killProcess(context: android.content.Context, packageName: String) {
        try {
            val am = context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            am.killBackgroundProcesses(packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _processes.value = _processes.value.filter { it.pid != packageName }
    }

    fun terminateAlert(alertId: String) {
        _alerts.value = _alerts.value.filter { it.id != alertId }
    }

    fun purgeCache() {
        // Since we can't actually free RAM easily without root unless we kill processes,
        // we'll leave this empty or rely on process kill. 
        _alerts.value = _alerts.value.filter { it.type != "RAM" }
    }

    fun optimizeAlert(alertId: String) {
        _alerts.value = _alerts.value.map { alert ->
            if (alert.id == alertId) {
                alert.copy(value = alert.value - 10)
            } else {
                alert
            }
        }.filter { it.value > 0 }
    }
}
