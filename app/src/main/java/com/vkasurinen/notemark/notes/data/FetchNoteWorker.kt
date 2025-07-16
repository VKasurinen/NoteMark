package com.vkasurinen.notemark.notes.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.core.database.mappers.toEntity
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.network.RemoteNoteDataSource
import timber.log.Timber

class FetchNoteWorker(
    context: Context,
    params: WorkerParameters,
    private val remoteNoteDataSource: RemoteNoteDataSource,
    private val noteDao: NoteDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        return when (val result = remoteNoteDataSource.getNotes(1, 50)) {
            is com.vkasurinen.notemark.core.domain.util.Result.Error -> {
                Timber.e(result.message, "Failed to fetch notes remotely")
                Result.retry()
            }
            is com.vkasurinen.notemark.core.domain.util.Result.Success -> {
                result.data?.let { noteDao.upsertNotes(it.map { it.toEntity() }) }
                Result.success()
            }
            is com.vkasurinen.notemark.core.domain.util.Result.Loading -> {
                Timber.d("FetchNoteWorker is loading")
                Result.retry()
            }
        }
    }
}