package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Distance
import kotlinx.coroutines.flow.Flow

interface DistanceRepository {

    suspend fun observeDistanceDataFlow(raceId: String)

    suspend fun getDistanceListFlow(raceId: String): Flow<List<Distance>>

}