package com.vkasurinen.notemark.core.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_note_sync")
data class DeletedNoteSyncEntity(
    @PrimaryKey
    val noteId: String,
    val deletedAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val maxRetries: Int = 5
)
