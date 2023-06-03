package ru.etu.asthmacontrolapp.pages

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import kotlinx.coroutines.launch
import ru.etu.asthmacontrolapp.R
import ru.etu.asthmacontrolapp.classes.AudioRecorder
import ru.etu.asthmacontrolapp.compopents.AudioAnalysis
import ru.etu.asthmacontrolapp.models.TimerModel
import ru.etu.asthmacontrolapp.compopents.RecordItem
import ru.etu.asthmacontrolapp.compopents.Spikes
import ru.etu.asthmacontrolapp.db.AppDatabase
import ru.etu.asthmacontrolapp.db.record.AudioRecord
import ru.etu.asthmacontrolapp.models.ApplicationEvent
import ru.etu.asthmacontrolapp.models.ApplicationModel
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme
import java.util.Date

const val MS_PER_SAMPLE = 170L
const val MS_PER_DELAY = 17L

@Composable
fun RecordingScreen(amplitudes: List<Int>, msElapsedTime: Long, onStopRecording: () -> Unit) {
    val ms = msElapsedTime % 1000
    val totalS = msElapsedTime / 1000
    val s = totalS % 60
    val m = totalS / 60

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        Text(
            text = "%02d:%02d.%02d".format(m, s, ms / 10),
            fontSize = 70.sp,
            modifier = Modifier
                .padding(20.dp)
                .weight(0.2f)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .padding(10.dp)
        ) {
            Spikes(
                amplitudes = amplitudes,
                msElapsedTime = msElapsedTime,
                msPerSample = MS_PER_SAMPLE,
                msPerFrame = 5000
            )
        }
        Spacer(modifier = Modifier.weight(0.2f))
        OutlinedButton(
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colors.onSurface,
            ),
            border = BorderStroke(
                ButtonDefaults.OutlinedBorderSize,
                MaterialTheme.colors.onSurface
            ),
            onClick = { onStopRecording() },
        ) {
            Text(
                text = "Закончить запись",
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}


@Composable
fun RecordsList(
    records: List<AudioRecord>,
    onStartRecording: () -> Unit,
    onBack: () -> Unit,
    onSelect: (AudioRecord) -> Unit = {}
) {
    var playbackId: Int? by remember { mutableStateOf(null) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.onSurface,
                ),
                border = BorderStroke(
                    ButtonDefaults.OutlinedBorderSize,
                    MaterialTheme.colors.onSurface
                ),
                onClick = { onBack() },
            ) {
                Text(
                    text = "Назад",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        LazyColumn(modifier = Modifier.weight(1.0f)) {
            items(records) { audioRecord ->
                Box(modifier = Modifier.padding(8.dp))
                {
                    RecordItem(
                        audioRecord,
                        playBack = playbackId == audioRecord.id,
                        onPlayPress = { playbackId = audioRecord.id },
                        onPlayBackFinish = { playbackId = -1 },
                        onClick = { onSelect(audioRecord) }
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { onStartRecording() }) {
                Icon(
                    painter = painterResource(id = R.drawable.record),
                    contentDescription = "Record"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Recorder(applicationModel: ApplicationModel, onBack: () -> Unit) {
    val context = LocalContext.current

    val audioRecorder = remember { AudioRecorder(context) }
    val amplitudes = remember { mutableStateListOf<Int>() }
    var selectedRecord: AudioRecord? by remember { mutableStateOf(null) }

    val timerModel = viewModel<TimerModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TimerModel(MS_PER_DELAY) as T
            }
        }
    )

    val applicationState by applicationModel.state.collectAsState()
    val timerState by timerModel.state.collectAsState()
    val msElapsedTime by timerModel.msElapsedTime.collectAsState()

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            permissionGranted = it
            if (permissionGranted) {
                Toast.makeText(context, "Нажмите запись, чтоб начать", Toast.LENGTH_SHORT).show()
            }
        }

    if (msElapsedTime % MS_PER_SAMPLE == 0L && timerState == TimerModel.State.RUNNING) {
        amplitudes.add(audioRecorder.maxAmplitude)
    }

    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()


    fun startRecording() {
        if (permissionGranted) {
            audioRecorder.prepare()
            audioRecorder.start()
            timerModel.stop()
            timerModel.start()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopRecording() {
        timerModel.pause()
        audioRecorder.stop()

        val timestamp = Date().time

        applicationModel.onEvent(ApplicationEvent.SetRecordTimestamp(timestamp))
        applicationModel.onEvent(ApplicationEvent.SetRecordDuration(msElapsedTime))
        scope.launch { sheetState.expand() }
    }

    fun finishRecordingProcess() {
        audioRecorder.restore()
        scope.launch { sheetState.collapse() }
    }

    fun saveAudioFile() {
        val filePath =
            audioRecorder.save("${applicationState.timestamp}_${applicationState.recordName}")
        finishRecordingProcess()

        applicationModel.onEvent(ApplicationEvent.SetFilePath(filePath))
        applicationModel.onEvent(ApplicationEvent.SaveAudioRecord)
    }

    BackHandler(enabled = sheetState.isExpanded) { finishRecordingProcess() }
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(32.dp, 32.dp, 4.dp, 4.dp),
        sheetPeekHeight = 0.dp,
        sheetGesturesEnabled = false,
        drawerScrimColor = DrawerDefaults.scrimColor,
        sheetElevation = 100.dp,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = applicationState.recordName,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(text = "Название записи") },
                        onValueChange = { applicationModel.onEvent(ApplicationEvent.SetRecordName(it)) })
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { finishRecordingProcess() },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colors.onSurface,
                            ),
                            border = BorderStroke(
                                ButtonDefaults.OutlinedBorderSize,
                                MaterialTheme.colors.onSurface
                            ),
                        ) {
                            Text(text = "Отмена", fontSize = 18.sp)
                        }
                        OutlinedButton(
                            onClick = { saveAudioFile() },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colors.onSurface,
                            ),
                            border = BorderStroke(
                                ButtonDefaults.OutlinedBorderSize,
                                MaterialTheme.colors.onSurface
                            ),
                        ) {
                            Text(text = "Сохранить", fontSize = 18.sp)
                        }
                    }
                }
            }
        }) {

        if (audioRecorder.isRecording() == AudioRecorder.State.INITIAL) {
            if (selectedRecord == null) {
                RecordsList(
                    applicationState.records,
                    onStartRecording = { startRecording() },
//                    onSelect = { selectedRecord = it },
                    onBack = { onBack() }
                )
            } else {
                AudioAnalysis(selectedRecord!!, onBack = { selectedRecord = null })
            }
        } else {
            BackHandler(enabled = timerState == TimerModel.State.RUNNING) { stopRecording() }
            RecordingScreen(
                amplitudes = amplitudes,
                msElapsedTime = msElapsedTime,
                onStopRecording = { stopRecording() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecorderPreview() {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "audioRecords").build()
    }

    val applicationModel = viewModel<ApplicationModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ApplicationModel(db.audioRecordDao(), db.patientsDao()) as T
            }
        }
    )

    AsthmaControlAppTheme {
        Recorder(applicationModel = applicationModel, onBack = {})
    }
}