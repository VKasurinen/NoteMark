package com.vkasurinen.notemark.core.domain.notes

import com.vkasurinen.notemark.notes.domain.Note


interface SyncRunScheduler {
    suspend fun scheduleSync(type: SyncType)
    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data object FetchNote : SyncType
        data class DeleteNote(val noteId: String) : SyncType
        data class CreateNote(val note: Note) : SyncType
    }
}