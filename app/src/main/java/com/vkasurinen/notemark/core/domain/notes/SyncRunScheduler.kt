package com.vkasurinen.notemark.core.domain.notes

import com.vkasurinen.notemark.notes.domain.Note


interface SyncRunScheduler {
    suspend fun scheduleSync(type: SyncType)
    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data object FetchRuns : SyncType
        data class DeleteRun(val noteId: String) : SyncType
        data class CreateRun(val note: Note) : SyncType
    }
}