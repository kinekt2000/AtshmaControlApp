package ru.etu.asthmacontrolapp.compopents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.etu.asthmacontrolapp.db.record.AudioRecord

@Composable
fun AudioAnalysis(audioRecord: AudioRecord, onBack: () -> Unit) {
    BackHandler { onBack() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan)
    )
    {
        Column {
            Text(text = "AUDIO ANALYSIS")
            Text(text = audioRecord.recordName)
            Text(text = audioRecord.filePath)
            Text(text = audioRecord.duration.toString())
            Text(text = audioRecord.timestamp.toString())
        }

    }
}