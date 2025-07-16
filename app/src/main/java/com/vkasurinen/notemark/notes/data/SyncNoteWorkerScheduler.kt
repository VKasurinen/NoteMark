package com.vkasurinen.notemark.notes.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkasurinen.notemark.core.database.dao.NotePendingSyncDao
import com.vkasurinen.notemark.core.database.entity.NotePendingSyncEntity
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.network.RemoteNoteDataSource


//class CreateRunWorker(
//    context: Context,
//    private val params: WorkerParameters,
//    private val remoteRunDataSource: RemoteNoteDataSource,
//    private val pendingSyncDao: NotePendingSyncDao
//): CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        if (runAttemptCount >= 5) {
//            return Result.failure()
//        }
//
//        val pendingRunId = params.inputData.getString(RUN_ID) ?: return Result.failure()
//        val pendingRunEntity = pendingSyncDao.getRunPendingSyncEntity(pendingRunId)
//            ?: return Result.failure()
//
//        val run = pendingRunEntity.run.toRun()
//        return when(val result = remoteRunDataSource.postRun(run, pendingRunEntity.mapPictureBytes)) {
//            is com.vkasurinen.notemark.core.domain.util.Result.Error -> {
//                result.error.toWorkerResult()
//            }
//            is com.example.core.domain.util.Result.Success -> {
//                pendingSyncDao.deleteRunPendingSyncEntity(pendingRunId)
//                Result.success()
//            }
//        }
//
//    }
//
//    companion object {
//        const val NOTE_ID = "NOTE_ID"
//    }
//}