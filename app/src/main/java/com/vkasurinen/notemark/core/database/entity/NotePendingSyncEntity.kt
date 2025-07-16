package com.vkasurinen.notemark.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "note_pending_sync")
data class NotePendingSyncEntity(
    @PrimaryKey
    val noteId: String,
    val syncType: SyncType,
    val lastAttempt: Long = 0
) {
    enum class SyncType {
        CREATE, UPDATE
    }
}
