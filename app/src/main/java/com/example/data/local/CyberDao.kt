package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CyberDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE type = :type ORDER BY timestamp ASC")
    fun getMessagesByType(type: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE isSynced = 0")
    suspend fun getPendingUnsyncedMessages(): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE messages SET isSynced = 1 WHERE id = :id")
    suspend fun markMessageSynced(id: Long)

    @Query("SELECT * FROM diagnostic_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentLogs(): Flow<List<DiagnosticLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DiagnosticLogEntity)

    @Query("DELETE FROM diagnostic_logs")
    suspend fun clearLogs()

    @Query("DELETE FROM messages")
    suspend fun clearMessages()
}
