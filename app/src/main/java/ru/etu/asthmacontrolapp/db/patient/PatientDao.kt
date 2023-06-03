package ru.etu.asthmacontrolapp.db.patient

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients")
    fun getAll(): Flow<List<Patient>>

    @Insert
    suspend fun insert(patient: Patient): Long
}