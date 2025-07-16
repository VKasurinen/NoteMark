package com.vkasurinen.notemark.notes.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkasurinen.notemark.core.database.dao.NotePendingSyncDao
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.network.RemoteNoteDataSource
import timber.log.Timber

class DeleteNoteWorker(
    context: Context,
    params: WorkerParameters,
    private val remoteNoteDataSource: RemoteNoteDataSource,
    private val pendingSyncDao: NotePendingSyncDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        val noteId = inputData.getString(NOTE_ID) ?: return Result.failure()
        return when (val result = remoteNoteDataSource.deleteNote(noteId)) {
            is com.vkasurinen.notemark.core.domain.util.Result.Success -> {
                // Use the correct method name from NotePendingSyncDao
                pendingSyncDao.deleteDeletedNote(noteId)
                Result.success()
            }
            is com.vkasurinen.notemark.core.domain.util.Result.Error -> {
                Timber.e(result.message, "Failed to delete note remotely")
                Result.retry()
            }
            is com.vkasurinen.notemark.core.domain.util.Result.Loading -> {
                Timber.d("DeleteNoteWorker is loading")
                Result.retry()
            }
        }
    }

    companion object {
        const val NOTE_ID = "NOTE_ID"
    }
}