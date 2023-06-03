package ru.etu.asthmacontrolapp.compopents

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.etu.asthmacontrolapp.db.AppDatabase
import ru.etu.asthmacontrolapp.db.record.AudioRecord
import ru.etu.asthmacontrolapp.models.ApplicationEvent
import ru.etu.asthmacontrolapp.models.ApplicationModel
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme
import java.lang.IllegalStateException
import java.text.DateFormat
import java.util.Date

@Composable
fun RecordItem(
    audioRecord: AudioRecord,
    playBack: Boolean = false,
    onPlayPress: () -> Unit = {},
    onPlayBackFinish: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val ms = audioRecord.duration % 1000
    val totalS = audioRecord.duration / 1000
    val s = totalS % 60
    val m = totalS / 60

    var paused by remember { mutableStateOf(!playBack) }
    var currentPosition by remember { mutableStateOf(0.0f) }
    var job: Job? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "audioRecords").build()
    }

    val applicationModel = viewModel<ApplicationModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ApplicationModel(db.audioRecordDao(), db.patientsDao()) as T
            }
        }
    )

    val mediaPlayer: MediaPlayer? = remember {
        try {
            MediaPlayer().apply {
                setDataSource(audioRecord.filePath)
                setOnCompletionListener {
                    onPlayBackFinish()
                    currentPosition = 0.0f
                    paused = true
                    it.stop()
                    it.prepare()
                    job?.cancel()
                }
                prepare()
            }
        } catch (e: IllegalStateException) {
            Log.d("RecordItem", e.message.toString())
            null
        }
    }

    fun startPlayBack() {
        mediaPlayer?.start()
        paused = false

        if (!playBack) {
            onPlayPress()
        }
        job = scope.launch {
            while (isActive && mediaPlayer != null) {
                delay(20)
                currentPosition = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration
            }
        }
    }

    fun pausePlayBack() {
        mediaPlayer?.pause()
        paused = true
        job?.cancel()
    }

    fun stopPlayBack() {
        mediaPlayer?.stop()
        mediaPlayer?.prepare()
        paused = true
        job?.cancel()
    }

    fun changeCurrentPosition(value: Float) {
        currentPosition = value
        mediaPlayer?.seekTo((mediaPlayer.duration * value).toInt())
    }


    if (!playBack) {
        stopPlayBack()
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (paused) {
                    IconButton(
                        modifier = Modifier.size(60.dp),
                        onClick = { startPlayBack() }) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "play record"
                        )
                    }
                } else {
                    IconButton(
                        modifier = Modifier.size(60.dp),
                        onClick = { pausePlayBack() }) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Filled.Pause,
                            contentDescription = "pause record"
                        )
                    }
                }
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(text = audioRecord.recordName, fontSize = 20.sp)
                    Row {
                        Text(
                            text = "%02d:%02d.%02d".format(m, s, ms / 10),
                            modifier = Modifier.alpha(0.7f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = DateFormat.getDateInstance().format(Date(audioRecord.timestamp)),
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
                IconButton(
                    modifier = Modifier.size(60.dp),
                    onClick = { applicationModel.onEvent(ApplicationEvent.DeleteRecord(audioRecord)) }
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete record")
                }
            }
            if (playBack) {
                Slider(
                    value = currentPosition,
                    onValueChange = ::changeCurrentPosition,
                    modifier = Modifier.padding(0.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecorderItemPreview() {
    AsthmaControlAppTheme {
        RecordItem(AudioRecord(0, "Record Item Name", "...", Date().time, 1243129))
    }
}