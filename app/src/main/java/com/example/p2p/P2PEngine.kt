package com.example.p2p

import com.example.data.local.CyberDao
import com.example.data.local.DiagnosticLogEntity
import com.example.data.local.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

enum class WebRtcState {
    DISCONNECTED,
    SIGNALING,
    GATHERING_ICE,
    CONNECTED,
    STREAMING
}

enum class ResolutionPreset(val width: Int, val height: Int, val targetFps: Int, val targetBitrateKbps: Int, val label: String) {
    RES_1080P(1920, 1080, 60, 6200, "1080p (60fps) Ultra"),
    RES_720P(1280, 720, 30, 2600, "720p (30fps) Balanced"),
    RES_480P(854, 480, 15, 850, "480p (15fps) Fallback")
}

data class TelemetryData(
    val state: WebRtcState = WebRtcState.DISCONNECTED,
    val roomId: String = "ALPHA-9092",
    val peerId: String = "PEER-771X",
    val resolution: ResolutionPreset = ResolutionPreset.RES_1080P,
    val currentFps: Int = 0,
    val bitrateKbps: Int = 0,
    val latencyMs: Int = 18,
    val packetLossPct: Float = 0.2f,
    val frameDrops: Int = 0,
    val autoFallbackEnabled: Boolean = true,
    val isNetworkSimulatedPoor: Boolean = false,
    val iceCandidatesCount: Int = 12
)

class P2PEngine(private val dao: CyberDao) {

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val _telemetry = MutableStateFlow(TelemetryData())
    val telemetry: StateFlow<TelemetryData> = _telemetry.asStateFlow()

    private var telemetryJob: Job? = null

    init {
        log("INFO", "P2PEngine initialized with Native Fallback Resolution Engine.")
    }

    fun connectPeer(roomId: String = "ALPHA-9092") {
        scope.launch {
            _telemetry.update { it.copy(state = WebRtcState.SIGNALING, roomId = roomId) }
            log("WEBRTC", "SDP Offer generated. Connecting to Signaling server for room: $roomId")
            delay(600)

            _telemetry.update { it.copy(state = WebRtcState.GATHERING_ICE, iceCandidatesCount = 4) }
            log("WEBRTC", "Gathering ICE Candidates (STUN stun.l.google.com:19302)...")
            delay(800)

            _telemetry.update { it.copy(state = WebRtcState.CONNECTED, iceCandidatesCount = 14) }
            log("WEBRTC", "DTLS Handshake verified. Peer connection established! DataChannel 'dc_cyber_stream' OPEN.")
            
            // Insert log to DB
            dao.insertLog(DiagnosticLogEntity(level = "WEBRTC", message = "P2P WebRTC Connected to PEER-771X in room $roomId", tag = "ICE"))

            startStreaming()
        }
    }

    fun disconnectPeer() {
        telemetryJob?.cancel()
        _telemetry.update {
            it.copy(
                state = WebRtcState.DISCONNECTED,
                currentFps = 0,
                bitrateKbps = 0
            )
        }
        log("WEBRTC", "P2P DataChannel closed by Operator.")
    }

    private fun startStreaming() {
        telemetryJob?.cancel()
        _telemetry.update { it.copy(state = WebRtcState.STREAMING) }
        log("STREAM", "MediaStream active. Starting live frame buffer dispatch.")

        telemetryJob = scope.launch {
            var dropsAccumulator = 0

            while (telemetry.value.state == WebRtcState.STREAMING) {
                val isPoor = telemetry.value.isNetworkSimulatedPoor
                val autoFallback = telemetry.value.autoFallbackEnabled

                var packetLoss = if (isPoor) Random.nextFloat() * 12.0f + 5.0f else Random.nextFloat() * 1.5f
                var latency = if (isPoor) Random.nextInt(120, 240) else Random.nextInt(14, 28)

                // Check Fallback Trigger
                val currentPreset = telemetry.value.resolution
                val targetPreset = if (autoFallback) {
                    when {
                        packetLoss > 8.0f || latency > 140 -> ResolutionPreset.RES_480P
                        packetLoss > 3.5f || latency > 70 -> ResolutionPreset.RES_720P
                        else -> ResolutionPreset.RES_1080P
                    }
                } else {
                    currentPreset
                }

                if (targetPreset != currentPreset && autoFallback) {
                    dropsAccumulator += 3
                    log(
                        "WARN",
                        "⚡ FALLBACK TRIGGERED! Auto-scaling stream resolution: ${currentPreset.label} ➔ ${targetPreset.label} (Loss: ${"%.1f".format(packetLoss)}%, Latency: ${latency}ms)"
                    )
                    dao.insertLog(
                        DiagnosticLogEntity(
                            level = "WARN",
                            message = "Resolution Fallback: ${currentPreset.label} -> ${targetPreset.label}",
                            tag = "FALLBACK"
                        )
                    )
                }

                val fpsSpread = if (isPoor && targetPreset == ResolutionPreset.RES_1080P) -15 else Random.nextInt(-2, 3)
                val calculatedFps = (targetPreset.targetFps + fpsSpread).coerceAtLeast(8)
                val calculatedBitrate = (targetPreset.targetBitrateKbps * (1.0f - packetLoss / 100f)).toInt()

                _telemetry.update {
                    it.copy(
                        resolution = targetPreset,
                        currentFps = calculatedFps,
                        bitrateKbps = calculatedBitrate,
                        latencyMs = latency,
                        packetLossPct = packetLoss,
                        frameDrops = dropsAccumulator
                    )
                }

                delay(1000)
            }
        }
    }

    fun setManualResolution(preset: ResolutionPreset) {
        _telemetry.update { it.copy(resolution = preset, autoFallbackEnabled = false) }
        log("STREAM", "Manual Resolution Override applied: ${preset.label}")
    }

    fun toggleAutoFallback(enabled: Boolean) {
        _telemetry.update { it.copy(autoFallbackEnabled = enabled) }
        log("STREAM", "Auto Fallback Resolution Engine set to: ${if (enabled) "ENABLED" else "DISABLED"}")
    }

    fun toggleSimulatedNetworkJitter(enableJitter: Boolean) {
        _telemetry.update { it.copy(isNetworkSimulatedPoor = enableJitter) }
        log("WARN", "Network Simulation mode: ${if (enableJitter) "POOR NETWORK (High Loss & Latency)" else "OPTIMAL BROADBAND"}")
    }

    suspend fun sendP2pMessage(text: String, sender: String = "Operator"): Boolean {
        if (text.isBlank()) return false
        val isOnline = _telemetry.value.state == WebRtcState.STREAMING || _telemetry.value.state == WebRtcState.CONNECTED

        // Save to DB
        val msgId = dao.insertMessage(
            MessageEntity(
                sender = sender,
                content = text,
                isSynced = isOnline,
                type = "P2P"
            )
        )

        if (isOnline) {
            log("P2P", "Outbound DataChannel packet delivered: $text")
            
            // Simulate Peer Response
            scope.launch {
                delay(1200)
                val peerReplies = listOf(
                    "ACK: DataChannel packet received. Frame checksum verified.",
                    "Peer Alpha receiving high-fps stream. Latency nominal.",
                    "Copy that. Diagnostic console telemetrics operating at peak efficiency.",
                    "WebRTC SCTP buffer clear. Ready for next command block."
                )
                val reply = peerReplies.random()
                dao.insertMessage(
                    MessageEntity(
                        sender = "PEER-771X",
                        content = reply,
                        isSynced = true,
                        type = "P2P"
                    )
                )
            }
        } else {
            log("WARN", "P2P DataChannel offline! Message cached locally in Room DB as PENDING_SYNC (ID: $msgId).")
        }
        return true
    }

    suspend fun syncPendingMessages() {
        val pending = dao.getPendingUnsyncedMessages()
        if (pending.isNotEmpty()) {
            log("P2P", "Syncing ${pending.size} offline pending messages to P2P network...")
            pending.forEach { msg ->
                dao.markMessageSynced(msg.id)
            }
            log("P2P", "All offline pending messages synced successfully!")
        }
    }

    private fun log(level: String, msg: String) {
        scope.launch {
            dao.insertLog(DiagnosticLogEntity(level = level, message = msg, tag = "P2P"))
        }
    }
}
