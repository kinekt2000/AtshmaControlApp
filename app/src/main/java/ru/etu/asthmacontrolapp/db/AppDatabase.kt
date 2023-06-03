package ru.etu.asthmacontrolapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.etu.asthmacontrolapp.db.patient.Patient
import ru.etu.asthmacontrolapp.db.patient.PatientDao
import ru.etu.asthmacontrolapp.db.record.AudioRecord
import ru.etu.asthmacontrolapp.db.record.AudioRecordDao

@Database(entities = [AudioRecord::class, Patient::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audioRecordDao(): AudioRecordDao
    abstract fun patientsDao(): PatientDao
}