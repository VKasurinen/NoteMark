package com.vkasurinen.notemark.core.database.dao
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.vkasurinen.notemark.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Transaction
    @Upsert
    suspend fun upsertNote(note: NoteEntity)

    @Transaction
    @Upsert
    suspend fun upsertNotes(notes: List<NoteEntity>)

    @Query("SELECT * FROM note ORDER BY lastEditedAt DESC")
    fun getNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note ORDER BY lastEditedAt DESC")
    suspend fun getNotesOnce(): List<NoteEntity>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteNote(id: String)

    @Query("DELETE FROM note")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM note ORDER BY lastEditedAt DESC")
    fun observeNotes(): Flow<List<NoteEntity>>
}