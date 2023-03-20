package ru.etu.asthmacontrolapp.compopents

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.etu.asthmacontrolapp.classes.Question
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme
import kotlin.math.max
import kotlin.math.min

@Composable
fun MultiPageQuiz(
    questionList: List<Question>,
    initialAnswers: List<Int> = List(questionList.size) { 0 },
    onAnswer: (Int, Int) -> Unit = { _, _ -> },
    onFinish: (List<Int>) -> Unit = { _ -> },
    onExit: () -> Unit = {}
) {
    var currentQuestion by remember { mutableStateOf(0) }
    var isLastQuestion by remember { mutableStateOf(false) }
    val answers = remember { MutableList(questionList.size) { 0 }.toMutableStateList() }
    val enableNextButton = remember { derivedStateOf { answers[currentQuestion] != 0 } }

    LaunchedEffect(initialAnswers) {
        initialAnswers.forEachIndexed { index, i ->
            answers[index] = i
        }
    }

    fun nextButtonAction() {
        currentQuestion = min(currentQuestion + 1, questionList.size - 1)
        isLastQuestion = (currentQuestion == questionList.size - 1)
        if (isLastQuestion) {
            onFinish(answers)
        } else {
            onAnswer(currentQuestion - 1, answers[currentQuestion - 1])
        }
    }

    fun prevButtonAction() {
        currentQuestion = max(currentQuestion - 1, 0)
        isLastQuestion = (currentQuestion == questionList.size - 1)
        if (currentQuestion == 0) {
            onExit()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            QuestionList(question = questionList[currentQuestion].question,
                answers = questionList[currentQuestion].answers,
                fontSize = 18f,
                selected = answers[currentQuestion] - 1,
                onSelect = { i -> answers[currentQuestion] = i + 1 })
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
                    shape = RectangleShape,
                    onClick = { prevButtonAction() },
                ) {
                    Text(
                        text = if (currentQuestion == 0) "Выйти" else "Назад",
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 18.sp
                    )
                }
                OutlinedButton(
                    enabled = enableNextButton.value,
                    border = BorderStroke(
                        1.dp,
                        if (enableNextButton.value) MaterialTheme.colors.onSurface else Color.LightGray
                    ),
                    shape = RectangleShape,
                    onClick = { nextButtonAction() },
                ) {
                    Text(
                        text = if (isLastQuestion) "Закончить" else "Далее",
                        color = if (enableNextButton.value) MaterialTheme.colors.onSurface else Color.LightGray,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MultiPageQuizPreview() {
    AsthmaControlAppTheme {
        MultiPageQuiz(
            listOf(
                Question("question 1?", listOf("Answer 11", "Answer 12", "Answer 13", "Answer 14")),
                Question("question 2?", listOf("Answer 21", "Answer 22", "Answer 23", "Answer 24")),
                Question("question 3?", listOf("Answer 31", "Answer 32", "Answer 33", "Answer 34")),
                Question("question 4?", listOf("Answer 41", "Answer 42", "Answer 43", "Answer 44")),
                Question("question 5?", listOf("Answer 51", "Answer 52", "Answer 53", "Answer 54")),
            )
        )
    }
}