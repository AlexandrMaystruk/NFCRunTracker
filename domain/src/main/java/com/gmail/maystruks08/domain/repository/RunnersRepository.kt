package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType

interface RunnersRepository {

    suspend fun getAllRunners(): ResultOfTask<Exception, List<Runner>>

    suspend fun updateRunnersCache(onResult: (ResultOfTask<Exception, List<Runner>>) -> Unit)

    suspend fun getRunnerById(cardId: String): Runner?

    suspend fun updateRunnerData(runner: Runner): Runner?

    suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint

}