package com.vkasurinen.notemark.core.database.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vkasurinen.notemark.core.database.entity.NotePendingSyncEntity
import com.vkasurinen.notemark.core.database.entity.DeletedNoteSyncEntity

@Dao
interface NotePendingSyncDao {
    // Pending sync operations
    @Query("SELECT * FROM note_pending_sync")
    suspend fun getAllPendingSyncNotes(): List<NotePendingSyncEntity>

    @Query("SELECT * FROM note_pending_sync WHERE noteId = :noteId")
    suspend fun getPendingSyncNote(noteId: String): NotePendingSyncEntity?

    @Upsert
    suspend fun upsertPendingSyncNote(entity: NotePendingSyncEntity)

    @Query("DELETE FROM note_pending_sync WHERE noteId = :noteId")
    suspend fun deletePendingSyncNote(noteId: String)

    // Deleted notes tracking
    @Query("SELECT * FROM deleted_note_sync")
    suspend fun getAllDeletedNotes(): List<DeletedNoteSyncEntity>

    @Upsert
    suspend fun upsertDeletedNote(deletedNote: DeletedNoteSyncEntity)

    @Query("DELETE FROM deleted_note_sync WHERE noteId = :noteId")
    suspend fun deleteDeletedNote(noteId: String)
}