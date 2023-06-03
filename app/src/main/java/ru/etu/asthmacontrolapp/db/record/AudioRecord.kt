package ru.etu.asthmacontrolapp.db.record

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audioRecords")
data class AudioRecord(
    var patientId: Long,
    var recordName: String,
    var filePath: String,
    var timestamp: Long,
    var duration: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}