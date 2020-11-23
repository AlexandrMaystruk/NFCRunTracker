package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException

interface RunnersRepository {

    suspend fun getRunners(type: RunnerType, onlyFinishers: Boolean = false, initSize: Int? = null): List<Runner>

    suspend fun getRunnerByCardId(cardId: String): Runner?

    suspend fun getRunnerByNumber(runnerNumber: Int): Runner?

    suspend fun getRunnerTeamMembers(currentRunnerNumber: Int, teamName: String): List<Runner>?

    @Throws(SaveRunnerDataException::class, SyncWithServerException::class)
    suspend fun updateRunnerData(runner: Runner): Runner

    suspend fun getCheckpoints(type: RunnerType): List<Checkpoint>

    suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint

}