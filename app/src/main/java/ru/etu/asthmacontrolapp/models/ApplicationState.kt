package ru.etu.asthmacontrolapp.models

import ru.etu.asthmacontrolapp.db.patient.Patient
import ru.etu.asthmacontrolapp.db.record.AudioRecord

data class ApplicationState(
    val records: List<AudioRecord> = emptyList(),
    val patients: List<Patient> = emptyList(),
    var recordName: String = "",
    var filePath: String = "",
    var timestamp: Long = 0,
    var duration: Long = 0,
    var patientId: Long? = null,
)