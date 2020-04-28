package com.gmail.maystruks08.data.local.dao

import androidx.room.*
import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable
import com.gmail.maystruks08.data.local.pojo.RunnerTableView

@Dao
interface RunnerDao :
    BaseDao<RunnerTable> {

    @Transaction
    suspend fun insertOrReplaceRunner(runner: RunnerTable, results: List<ResultTable>) {
        insertOrReplace(runner)
        insertAllOrReplaceCheckpoint(results)
    }

    @Transaction
    suspend fun insertRunner(runner: RunnerTable, results: List<ResultTable>) {
        insert(runner)
        insertAllCheckpoints(results)
    }

    @Transaction
    suspend fun updateRunner(runner: RunnerTable, results: List<ResultTable>) {
        update(runner)
        results.forEach { update(it) }
    }

    @Query("SELECT * FROM runners WHERE type =:type")
    suspend fun getRunners(type: Int): List<RunnerTableView>

    @Query("SELECT * FROM runners WHERE totalResult IS NOT NULL")
    suspend fun getRunnersFinishers(): List<RunnerTableView>

    @Query("UPDATE runners SET needToSync = :needToSync WHERE id = :runnerId")
    suspend fun markAsNeedToSync(runnerId: String, needToSync: Boolean)

    @Query("DELETE FROM runners WHERE number =:number ")
    suspend fun delete(number: Int)

    @Query("DELETE FROM runners")
    suspend fun dropTable()


    /**
     * CHECKPOINTS
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOrReplaceCheckpoint(obj: List<ResultTable>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllCheckpoints(obj: List<ResultTable>)

    @Update
    suspend fun update(obj: ResultTable)

}

