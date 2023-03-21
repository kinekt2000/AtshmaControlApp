package ru.etu.asthmacontrolapp.compopents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.etu.asthmacontrolapp.classes.Question
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme

@Composable
fun QuizResult(
    heading: String = "",
    questions: List<Question>,
    answers: List<Int>,
    onExit: () -> Unit = {}
) {
    var showResult by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        if (heading.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 8.dp
            ) {
                Text(text = heading, fontSize = 18.sp, modifier = Modifier.padding(5.dp))
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (showResult) {
                Column(
                    modifier = Modifier
                        .padding(0.dp, 12.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    questions.forEachIndexed { index, question ->
                        if (index != 0) {
                            Divider()
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 8.dp)
                        ) {
                            Text(text = "${index + 1}. ${question.question}")
                            Text(
                                text = question.answers[answers[index] - 1],
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { showResult = true },
                    modifier = Modifier.align(Alignment.Center),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colors.onSurface,
                    ),
                    border = BorderStroke(
                        ButtonDefaults.OutlinedBorderSize,
                        MaterialTheme.colors.onSurface
                    ),
                ) {
                    Text(text = "Показать ответы", fontSize = 18.sp)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = { onExit() },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.onSurface,
                ),
                border = BorderStroke(
                    ButtonDefaults.OutlinedBorderSize,
                    MaterialTheme.colors.onSurface
                ),
            ) {
                Text(text = "К меню")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun QuizResultPreview() {
    AsthmaControlAppTheme {
        QuizResult(
            heading = "Everything is good",
            questions = listOf(
                Question("question 1?", listOf("Answer 11", "Answer 12", "Answer 13", "Answer 14")),
                Question("question 2?", listOf("Answer 21", "Answer 22", "Answer 23", "Answer 24")),
                Question("question 3?", listOf("Answer 31", "Answer 32", "Answer 33", "Answer 34")),
                Question("question 4?", listOf("Answer 41", "Answer 42", "Answer 43", "Answer 44")),
                Question("question 5?", listOf("Answer 51", "Answer 52", "Answer 53", "Answer 54")),
            ),
            answers = listOf(1, 3, 2, 1, 4)
        )
    }
}