package ru.etu.asthmacontrolapp.db.record

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioRecordDao {
    @Query("SELECT * FROM audioRecords WHERE patientId = :patientId")
    fun getAll(patientId: Long): Flow<List<AudioRecord>>

    @Insert
    suspend fun insert(vararg audioRecord: AudioRecord)

    @Delete
    suspend fun delete(audioRecord: AudioRecord)

    @Update
    suspend fun update(audioRecord: AudioRecord)
}