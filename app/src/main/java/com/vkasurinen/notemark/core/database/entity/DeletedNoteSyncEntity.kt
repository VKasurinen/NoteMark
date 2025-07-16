package com.vkasurinen.notemark.core.database.entity

import androidx.room.PrimaryKey

data class DeletedNoteSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val noteId: String,
)
