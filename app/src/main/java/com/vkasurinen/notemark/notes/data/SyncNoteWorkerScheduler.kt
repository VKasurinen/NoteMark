package com.vkasurinen.notemark.notes.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import androidx.work.workDataOf
import com.vkasurinen.notemark.core.domain.notes.SyncRunScheduler
import com.vkasurinen.notemark.notes.domain.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SyncNoteWorkerScheduler(
    private val context: Context,
    private val applicationScope: CoroutineScope
): SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(type: SyncRunScheduler.SyncType) {
        when(type) {
            is SyncRunScheduler.SyncType.FetchNote -> scheduleFetchNotesWorker()
            is SyncRunScheduler.SyncType.DeleteNote -> scheduleDeleteNoteWorker(type.noteId)
            is SyncRunScheduler.SyncType.CreateNote -> scheduleCreateNoteWorker(type.note)
        }
    }

    private suspend fun scheduleDeleteNoteWorker(noteId: String) {
        val workRequest = OneTimeWorkRequestBuilder<DeleteNoteWorker>()
            .setInputData(workDataOf(DeleteNoteWorker.NOTE_ID to noteId))
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .build()

        applicationScope.launch {
            workManager.enqueueUniqueWork(
                "DeleteNote_$noteId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            ).await()
        }
    }

    private suspend fun scheduleCreateNoteWorker(note: Note) {
        val workRequest = OneTimeWorkRequestBuilder<CreateNoteWorker>()
            .setInputData(workDataOf(CreateNoteWorker.NOTE_ID to note.id))
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .build()

        applicationScope.launch {
            workManager.enqueueUniqueWork(
                "CreateNote_${note.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            ).await()
        }
    }

    private suspend fun scheduleFetchNotesWorker() {
        val workRequest = OneTimeWorkRequestBuilder<FetchNoteWorker>()
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            "FetchNotes",
            ExistingWorkPolicy.KEEP,
            workRequest
        ).await()
    }

    override suspend fun cancelAllSyncs() {
        workManager.cancelAllWork().await()
    }
}