package ru.etu.asthmacontrolapp.db.patient

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    var patientName: String
) {
    @PrimaryKey(autoGenerate = true)
    var patientId: Long = 0
}