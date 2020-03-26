package com.gmail.maystruks08.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "runners")
data class RunnerTable(
    @PrimaryKey
    val id: String,
    val number: Int,
    val fullName: String,
    val city: String,
    val dateOfBirthday: Date,
    val type: Int,
    val needToSync: Boolean = false
)




