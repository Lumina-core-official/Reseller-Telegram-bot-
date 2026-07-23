package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true,
    val type: String = "P2P" // "P2P", "AI", "SYSTEM"
)

@Entity(tableName = "diagnostic_logs")
data class DiagnosticLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val level: String, // "INFO", "WARN", "ERROR", "WEBRTC", "STREAM"
    val message: String,
    val tag: String = "CyberOps"
)
