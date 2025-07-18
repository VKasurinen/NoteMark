package com.vkasurinen.notemark.notes.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkasurinen.notemark.core.database.dao.NotePendingSyncDao
import com.vkasurinen.notemark.core.domain.notes.LocalDataSource
import com.vkasurinen.notemark.notes.network.RemoteNoteDataSource
import com.vkasurinen.notemark.core.domain.util.Result
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class DeleteNoteWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val remoteNoteDataSource: RemoteNoteDataSource by inject()
    private val pendingSyncDao: NotePendingSyncDao by inject()
    private val localDataSource: LocalDataSource by inject()

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        val noteId = inputData.getString(NOTE_ID) ?: return Result.failure()

        return try {
            // 1. Delete from remote first
            when (val result = remoteNoteDataSource.deleteNote(noteId)) {
                is com.vkasurinen.notemark.core.domain.util.Result.Success -> {
                    // 2. Only delete locally after successful remote deletion
                    localDataSource.deleteNote(noteId)
                    // 3. Remove from sync queue
                    pendingSyncDao.deletePendingSyncNote(noteId)
                    pendingSyncDao.deleteDeletedNote(noteId)
                    Result.success()
                }
                is com.vkasurinen.notemark.core.domain.util.Result.Error -> {
                    Timber.e("Delete failed for $noteId: ${result.message}")
                    Result.retry()
                }
                is com.vkasurinen.notemark.core.domain.util.Result.Loading -> Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in DeleteNoteWorker")
            Result.retry()
        }
    }

    companion object {
        const val NOTE_ID = "NOTE_ID"
    }
}
