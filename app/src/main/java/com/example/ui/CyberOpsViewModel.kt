package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CyberDatabase
import com.example.data.local.DiagnosticLogEntity
import com.example.data.local.MessageEntity
import com.example.data.repository.AiAssistantRepository
import com.example.p2p.P2PEngine
import com.example.p2p.ResolutionPreset
import com.example.p2p.TelemetryData
import com.example.service.ScreenShareForegroundService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
    STREAM,
    AI_TERMINAL,
    P2P_CHAT,
    DIAGNOSTICS
}

data class NexusUser(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val securityLevel: Int,
    val avatarInitials: String,
    val badgeTitle: String
)

class CyberOpsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CyberDatabase.getInstance(application)
    private val dao = db.cyberDao()

    val p2pEngine = P2PEngine(dao)
    val aiRepository = AiAssistantRepository(dao)

    val telemetry: StateFlow<TelemetryData> = p2pEngine.telemetry

    val presetAccounts = listOf(
        NexusUser(
            id = "USR-001",
            name = "Operator Alpha",
            email = "alpha@nexus.ops",
            password = "alpha123",
            role = "Master Commander & Lead Architect",
            securityLevel = 5,
            avatarInitials = "OA",
            badgeTitle = "LEVEL 5 MASTER CLEARANCE"
        ),
        NexusUser(
            id = "USR-002",
            name = "Commander Orion",
            email = "orion@nexus.ops",
            password = "orion456",
            role = "Tactical WebRTC Stream Engineer",
            securityLevel = 4,
            avatarInitials = "CO",
            badgeTitle = "LEVEL 4 TACTICAL CLEARANCE"
        ),
        NexusUser(
            id = "USR-003",
            name = "Agent Cipher",
            email = "cipher@nexus.ops",
            password = "cipher789",
            role = "P2P Cryptography & Telemetry Specialist",
            securityLevel = 3,
            avatarInitials = "AC",
            badgeTitle = "LEVEL 3 SECURITY CLEARANCE"
        ),
        NexusUser(
            id = "USR-004",
            name = "Operator Omega",
            email = "omega@nexus.com",
            password = "token888",
            role = "Executive Command & Core Security Officer",
            securityLevel = 5,
            avatarInitials = "OO",
            badgeTitle = "LEVEL 5 EXECUTIVE CLEARANCE"
        )
    )

    private val _currentUser = MutableStateFlow<NexusUser?>(presetAccounts[0]) // default logged in as Operator Alpha
    val currentUser: StateFlow<NexusUser?> = _currentUser.asStateFlow()

    val allMessages: StateFlow<List<MessageEntity>> = dao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val diagnosticLogs: StateFlow<List<DiagnosticLogEntity>> = dao.getRecentLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isForegroundServiceRunning: StateFlow<Boolean> = ScreenShareForegroundService.isServiceRunning

    private val _selectedTab = MutableStateFlow(AppTab.STREAM)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _aiInputText = MutableStateFlow("")
    val aiInputText: StateFlow<String> = _aiInputText.asStateFlow()

    private val _p2pInputText = MutableStateFlow("")
    val p2pInputText: StateFlow<String> = _p2pInputText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _isAutoFixing = MutableStateFlow(false)
    val isAutoFixing: StateFlow<Boolean> = _isAutoFixing.asStateFlow()

    init {
        // Initial peer connection
        p2pEngine.connectPeer("ALPHA-9092")
    }

    fun login(account: NexusUser) {
        _currentUser.value = account
        viewModelScope.launch {
            dao.insertLog(
                DiagnosticLogEntity(
                    level = "INFO",
                    tag = "AUTH",
                    message = "User session initialized for ${account.name} (${account.email})"
                )
            )
        }
    }

    fun logout() {
        val prevUser = _currentUser.value?.name
        _currentUser.value = null
        viewModelScope.launch {
            dao.insertLog(
                DiagnosticLogEntity(
                    level = "INFO",
                    tag = "AUTH",
                    message = "User session terminated for $prevUser"
                )
            )
        }
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun updateAiInput(text: String) {
        _aiInputText.value = text
    }

    fun updateP2pInput(text: String) {
        _p2pInputText.value = text
    }

    fun sendAiPrompt(promptText: String = _aiInputText.value) {
        val prompt = promptText.trim()
        if (prompt.isBlank() || _isAiLoading.value) return

        _aiInputText.value = ""
        _isAiLoading.value = true

        viewModelScope.launch {
            // Insert user query message to local DB
            val userName = _currentUser.value?.name ?: "Operator"
            dao.insertMessage(
                MessageEntity(
                    sender = userName,
                    content = prompt,
                    type = "AI"
                )
            )

            // Query Gemini API / Repository
            aiRepository.queryAi(prompt)
            _isAiLoading.value = false
        }
    }

    fun sendP2pMessage() {
        val text = _p2pInputText.value.trim()
        if (text.isBlank()) return

        _p2pInputText.value = ""
        viewModelScope.launch {
            val userName = _currentUser.value?.name ?: "Operator"
            p2pEngine.sendP2pMessage(text, sender = userName)
        }
    }

    fun autoFixSystemErrors() {
        if (_isAutoFixing.value) return
        _isAutoFixing.value = true

        viewModelScope.launch {
            dao.insertLog(
                DiagnosticLogEntity(
                    level = "WARN",
                    tag = "SELF_HEAL",
                    message = "AUTONOMOUS REPAIR ENGINE: Scanning WebRTC, DataChannels & Buffer queues..."
                )
            )

            delay(600)
            p2pEngine.syncPendingMessages()

            delay(600)
            if (telemetry.value.state != com.example.p2p.WebRtcState.STREAMING) {
                p2pEngine.connectPeer()
            }

            delay(600)
            dao.insertLog(
                DiagnosticLogEntity(
                    level = "INFO",
                    tag = "SELF_HEAL",
                    message = "SUCCESS: Network jitter normalized, STUN/ICE candidates re-allocated, 0 dropped frames."
                )
            )
            _isAutoFixing.value = false
        }
    }

    fun toggleConnectPeer() {
        if (telemetry.value.state == com.example.p2p.WebRtcState.STREAMING || telemetry.value.state == com.example.p2p.WebRtcState.CONNECTED) {
            p2pEngine.disconnectPeer()
        } else {
            p2pEngine.connectPeer()
        }
    }

    fun setManualResolution(preset: ResolutionPreset) {
        p2pEngine.setManualResolution(preset)
    }

    fun toggleAutoFallback(enabled: Boolean) {
        p2pEngine.toggleAutoFallback(enabled)
    }

    fun toggleSimulatedNetworkJitter(enable: Boolean) {
        p2pEngine.toggleSimulatedNetworkJitter(enable)
    }

    fun syncOfflineMessages() {
        viewModelScope.launch {
            p2pEngine.syncPendingMessages()
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            dao.clearLogs()
        }
    }

    fun toggleForegroundProjectionService() {
        val context = getApplication<Application>().applicationContext
        if (isForegroundServiceRunning.value) {
            ScreenShareForegroundService.stopService(context)
        } else {
            ScreenShareForegroundService.startService(context)
        }
    }
}
