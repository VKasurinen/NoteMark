package com.vkasurinen.notemark.notes.data

import com.vkasurinen.notemark.core.database.mappers.toDomain
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.core.database.dao.NotePendingSyncDao
import com.vkasurinen.notemark.core.database.entity.NotePendingSyncEntity
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.network.RemoteNoteDataSource
import timber.log.Timber

class CreateNoteWorker(
    context: Context,
    params: WorkerParameters,
    private val remoteNoteDataSource: RemoteNoteDataSource,
    private val pendingSyncDao: NotePendingSyncDao,
    private val noteDao: NoteDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        val noteId = inputData.getString(NOTE_ID) ?: return Result.failure()
        val noteEntity = noteDao.getNoteById(noteId) ?: return Result.failure()

        return when (val result = remoteNoteDataSource.postNote(noteEntity.toDomain())) {
            is com.vkasurinen.notemark.core.domain.util.Result.Success -> {
                pendingSyncDao.deletePendingSyncNote(noteId)
                Result.success()
            }
            is com.vkasurinen.notemark.core.domain.util.Result.Error -> {
                Timber.e(result.message, "Failed to create note remotely")
                pendingSyncDao.upsertPendingSyncNote(
                    NotePendingSyncEntity(
                        noteId = noteId,
                        syncType = NotePendingSyncEntity.SyncType.CREATE,
                        lastAttempt = System.currentTimeMillis()
                    )
                )
                Result.retry()
            }
            is com.vkasurinen.notemark.core.domain.util.Result.Loading -> {
                Timber.d("Note creation in progress, retrying...")
                Result.retry()
            }
        }
    }

    companion object {
        const val NOTE_ID = "NOTE_ID"
    }
}