package com.vkasurinen.notemark.notes.data

import com.vkasurinen.notemark.core.database.mappers.toDomain
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.core.database.dao.NotePendingSyncDao
import com.vkasurinen.notemark.core.database.entity.NotePendingSyncEntity
import com.vkasurinen.notemark.core.domain.notes.LocalDataSource
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.network.RemoteNoteDataSource
import timber.log.Timber
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class CreateNoteWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val remoteNoteDataSource: RemoteNoteDataSource by inject()
    private val pendingSyncDao: NotePendingSyncDao by inject()
    private val noteDao: NoteDao by inject()
    private val localDataSource: LocalDataSource by inject()

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        val noteId = inputData.getString(NOTE_ID) ?: return Result.failure()

        val note = when (val result = localDataSource.getNoteById(noteId)) {
            is com.vkasurinen.notemark.core.domain.util.Result.Success -> result.data ?: return Result.failure()
            is com.vkasurinen.notemark.core.domain.util.Result.Error -> return Result.retry()
            is com.vkasurinen.notemark.core.domain.util.Result.Loading -> return Result.retry()
        }

        return when (val result = remoteNoteDataSource.postNote(note)) {
            is com.vkasurinen.notemark.core.domain.util.Result.Success -> {
                pendingSyncDao.deletePendingSyncNote(noteId)
                Result.success()
            }
            is com.vkasurinen.notemark.core.domain.util.Result.Error -> {
                Timber.e(result.message, "Failed to create note remotely")
                if (result.message?.contains("not found", ignoreCase = true) == true) {
                    pendingSyncDao.deletePendingSyncNote(noteId)
                    Timber.e("Note not found on server, removed from sync queue")
                } else {
                    pendingSyncDao.upsertPendingSyncNote(
                        NotePendingSyncEntity(
                            noteId = noteId,
                            syncType = NotePendingSyncEntity.SyncType.CREATE,
                            lastAttempt = System.currentTimeMillis()
                        )
                    )
                }
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
