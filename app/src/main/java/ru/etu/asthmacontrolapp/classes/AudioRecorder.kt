package ru.etu.asthmacontrolapp.classes

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.*

class AudioRecorder(
    private val context: Context
) {
    enum class State {
        INITIAL,
        PREPARED,
        STARTED,
        STOPPED
    }

    private val temporaryRecordingFile = File(context.filesDir, "temporaryAudioRecord.mp3")
    private var recorder: MediaRecorder? = null
    private var onErrorListener: MediaRecorder.OnErrorListener? = null
    private var recording = State.INITIAL

    @Suppress("DEPRECATION")
    private fun createMediaRecorder(): MediaRecorder {
        val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()

        mediaRecorder.setOnErrorListener { mr, what, extra ->
            recording = State.INITIAL
            onErrorListener?.onError(mr, what, extra)
        }

        return mediaRecorder
    }

    fun setOnMediaRecorderError(onError: MediaRecorder.OnErrorListener) {
        onErrorListener = onError
    }

    fun prepare() {
        createMediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(temporaryRecordingFile).fd)

            prepare()
            recorder = this
            recording = State.PREPARED
        }
    }

    fun start() {
        recorder?.start()
        recording = State.STARTED
    }

    fun isRecording(): State {
        return recording
    }

    fun stop() {
        recorder?.stop()
        recorder?.release()
        recording = State.STOPPED
    }

    val maxAmplitude: Int
        get() {
            return recorder?.maxAmplitude ?: 0
        }

    fun save(filename: String): String {
        val destinationFile =
            File(
                context.getExternalFilesDir(".data"),
                "${filename.replace(' ', '_')}.mp3"
            )
        Files.copy(temporaryRecordingFile.toPath(), destinationFile.toPath())
        Toast.makeText(context, "сохранено", Toast.LENGTH_SHORT).show()
        return destinationFile.toString()
    }

    fun restore() {
        temporaryRecordingFile.delete()
        recorder = null
        recording = State.INITIAL
    }
}