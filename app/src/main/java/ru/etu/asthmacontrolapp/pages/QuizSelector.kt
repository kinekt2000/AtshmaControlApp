package ru.etu.asthmacontrolapp.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme

@Composable
fun QuizSelector(onSelect: (value: String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .align(
                    Alignment.TopCenter
                )
                .width(intrinsicSize = IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.onSurface,
                ),
                border = BorderStroke(
                    ButtonDefaults.OutlinedBorderSize,
                    MaterialTheme.colors.onSurface
                ),
                onClick = { onSelect("act") }) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Анкета Act",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.onSurface,
                ),
                border = BorderStroke(
                    ButtonDefaults.OutlinedBorderSize,
                    MaterialTheme.colors.onSurface
                ),
                onClick = { onSelect("acq5") }) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Анкета Acq5",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.onSurface,
                ),
                border = BorderStroke(
                    ButtonDefaults.OutlinedBorderSize,
                    MaterialTheme.colors.onSurface
                ),
                onClick = { onSelect("gina") }) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Анкета GINA",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    var page by remember { mutableStateOf("none") }

    AsthmaControlAppTheme {
        Text(text = page)
        QuizSelector(onSelect = { v -> page = v })
    }
}