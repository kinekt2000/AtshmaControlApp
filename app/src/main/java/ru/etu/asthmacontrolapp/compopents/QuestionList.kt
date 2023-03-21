package ru.etu.asthmacontrolapp.compopents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme

@Composable
fun QuestionList(
    question: String,
    answers: List<String> = listOf<String>(
        "Answer 1", "Answer 2", "Answer 3", "Answer 4", "Answer 5"
    ),
    fontSize: Float = 20.0f,
    selected: Int = -1,
    onSelect: (value: Int) -> Unit
) {
    fun selectValue(value: Int) {
        onSelect(value)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
        ) {
            Text(
                text = question,
                fontSize = (fontSize * 1.2f).sp,
                modifier = Modifier.padding(5.dp)
            )
        }
        Column() {
            answers.forEachIndexed { index, s ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(selected = (index == selected), onClick = { selectValue(index) })
                    Text(s, fontSize = (fontSize).sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionPreview() {
    val (value, setValue) = remember { mutableStateOf(-1) }

    AsthmaControlAppTheme {
        Text(text = value.toString())
        QuestionList(
            question = "How's your mood?",
            answers = listOf("good", "bad"),
            onSelect = { v -> setValue(v) },
            selected = value
        )
    }
}

