package ru.etu.asthmacontrolapp.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.etu.asthmacontrolapp.db.patient.Patient
import ru.etu.asthmacontrolapp.db.patient.PatientDao
import ru.etu.asthmacontrolapp.db.record.AudioRecord
import ru.etu.asthmacontrolapp.db.record.AudioRecordDao

@OptIn(ExperimentalCoroutinesApi::class)
class ApplicationModel(
    private val audioRecordDao: AudioRecordDao,
    private val patientDao: PatientDao
) : ViewModel() {
    private val _state = MutableStateFlow(ApplicationState())
    private val _patientId = MutableStateFlow<Long?>(null)
    private val _records = _patientId.flatMapLatest { patientId ->
        audioRecordDao.getAll(patientId ?: 0L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _patients = patientDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val state =
        combine(_state, _records, _patientId, _patients) { state, records, patientId, patients ->
            state.copy(records = records, patients = patients, patientId = patientId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApplicationState())

    fun onEvent(event: ApplicationEvent) {
        when (event) {
            is ApplicationEvent.DeleteRecord -> {
                viewModelScope.launch {
                    audioRecordDao.delete(event.record)
                }
            }

            is ApplicationEvent.SetRecordDuration -> {
                _state.update { it.copy(duration = event.duration) }
            }

            is ApplicationEvent.SetRecordName -> {
                _state.update { it.copy(recordName = event.recordName) }
            }

            is ApplicationEvent.SetFilePath -> {
                _state.update { it.copy(filePath = event.filePath) }
            }

            is ApplicationEvent.SetRecordTimestamp -> {
                _state.update { it.copy(timestamp = event.timestamp) }
            }

            is ApplicationEvent.SetPatientId -> {
                _patientId.value = event.patientId
            }

            is ApplicationEvent.SavePatient -> {
                val patientName = event.patientName
                val patient = Patient(patientName = patientName)
                viewModelScope.launch {
                    val id = patientDao.insert(patient)
                    onEvent(ApplicationEvent.SetPatientId(id))
                }
            }

            ApplicationEvent.SaveAudioRecord -> {
                val recordName = state.value.recordName
                val filePath = state.value.filePath
                val timestamp = state.value.timestamp
                val duration = state.value.duration
                val patientId = state.value.patientId

                if (filePath.isBlank() || duration == 0L || timestamp == 0L) {
                    return
                }

                val record = AudioRecord(
                    patientId = patientId!!,
                    recordName = recordName,
                    filePath = filePath,
                    timestamp = timestamp,
                    duration = duration
                )

                viewModelScope.launch { audioRecordDao.insert(record) }
                _state.update {
                    it.copy(
                        recordName = "",
                        filePath = "",
                        timestamp = 0L,
                        duration = 0L
                    )
                }
            }
        }
    }
}