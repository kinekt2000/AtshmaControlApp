package ru.etu.asthmacontrolapp.compopents

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun Spikes(
    amplitudes: List<Int>,
    msElapsedTime: Long = 0,
    msPerSample: Long = 70,
    msPerFrame: Long = 5000
) {
    val numOfBars = (msPerFrame.toDouble() / msPerSample).toInt()
    var barsArray = amplitudes.takeLast(numOfBars / 2)
    barsArray = barsArray + List(numOfBars - barsArray.size) { 0 }

    var msOffset = msElapsedTime.toDouble()

    if (msOffset > msPerFrame / 2.0) {
        msOffset %= msPerSample
        msOffset += msPerFrame / 2.0 - msPerSample
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .height(100.dp), onDraw = {
        val barWidth = this.size.width / numOfBars
        val minBarHeight = this.size.height * 0.05f
        val barCenter = this.size.height / 2
        val pixelsPerSecond = this.size.width / msPerFrame * 1000f
        val pixelOffset = pixelsPerSecond * msOffset / 1000f

        val middleLineDashCount = (this.size.height / 15f).roundToInt()
        val middleDashSize = if (middleLineDashCount % 2 == 0) {
            this.size.height / (middleLineDashCount - 1)
        } else {
            this.size.height / middleLineDashCount
        }

        barsArray.forEachIndexed { index, item ->
            val barHeight = max(this.size.height / 10000f * item, minBarHeight)
            this.drawRect(
                color = Color.Black,
                topLeft = Offset(
                    (index + 0.1f) * barWidth + this.size.width / 2 - pixelOffset.toFloat(),
                    barCenter - barHeight / 2
                ),
                size = Size(barWidth * 0.8f, barHeight)
            )
        }

        this.drawLine(
            color = Color.Red,
            start = Offset(this.size.width / 2, 0f),
            end = Offset(this.size.width / 2, this.size.height),
            strokeWidth = 5f,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(middleDashSize, middleDashSize),
                0f
            )
        )
    })
}

@Preview(showBackground = true)
@Composable
fun SpikesPreview() {
    var timeMs by remember { mutableStateOf(0L) }
    val amplitudes = remember { mutableStateListOf<Int>() }
    val msPerSample = 70L

    LaunchedEffect(timeMs) {
        delay(msPerSample / 10)
        timeMs += msPerSample / 10
        if (timeMs % msPerSample == 0L) {
            amplitudes.add((0..20000).random())
        }
    }

    AsthmaControlAppTheme {
        Spikes(
            amplitudes,
            timeMs,
            msPerSample,
            5000
        )
    }
}