package ru.etu.asthmacontrolapp.models

import ru.etu.asthmacontrolapp.db.record.AudioRecord

sealed interface ApplicationEvent {
    object SaveAudioRecord : ApplicationEvent
    data class SavePatient(val patientName: String) : ApplicationEvent
    data class SetPatientId(val patientId: Long) : ApplicationEvent
    data class SetRecordName(val recordName: String) : ApplicationEvent
    data class SetFilePath(val filePath: String) : ApplicationEvent
    data class SetRecordTimestamp(val timestamp: Long) : ApplicationEvent
    data class SetRecordDuration(val duration: Long) : ApplicationEvent
    data class DeleteRecord(val record: AudioRecord) : ApplicationEvent
}