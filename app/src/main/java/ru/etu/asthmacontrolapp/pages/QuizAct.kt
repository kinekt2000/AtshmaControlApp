package ru.etu.asthmacontrolapp.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.etu.asthmacontrolapp.compopents.FiveRatingQuestion
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme

@Composable
fun QuizAct() {
    var index by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        FiveRatingQuestion(
            question = "How's your mood",
            fontSize = 25f,
            onSelect = { i -> index = i }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuizActPreview() {
    AsthmaControlAppTheme {
        QuizAct()
    }
}