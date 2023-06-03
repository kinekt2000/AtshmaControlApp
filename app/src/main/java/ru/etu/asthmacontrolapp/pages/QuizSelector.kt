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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme

@Composable
fun QuizSelector(patientId: Long, onBack: () -> Unit) {
    val navController = rememberNavController()

    fun goBack() {
        navController.navigateUp()
    }

    NavHost(
        navController = navController,
        startDestination = "quiz_selector",
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        composable("quiz_selector") {
            Box(modifier = Modifier.fillMaxSize()) {
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
                        onClick = { navController.navigate("act") }) {
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
                        onClick = { navController.navigate("acq5") }) {
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
                        onClick = { navController.navigate("gina") }) {
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
        composable("act") {
            QuizAct(patientId, onExit = { goBack() })
        }
        composable("acq5") {
            QuizAcq5(patientId, onExit = { goBack() })
        }
        composable("gina") {
            QuizGina(patientId, onExit = { goBack() })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuizSelectorPreview() {
    AsthmaControlAppTheme {
        QuizSelector(0L, onBack = {})
    }
}