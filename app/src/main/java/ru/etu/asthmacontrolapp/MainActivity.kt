package ru.etu.asthmacontrolapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.etu.asthmacontrolapp.pages.QuizAcq5
import ru.etu.asthmacontrolapp.pages.QuizAct
import ru.etu.asthmacontrolapp.pages.QuizSelector

import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AsthmaControlAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Main(rememberNavController())
                }
            }
        }
    }
}

@Composable
fun Main(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "selector",
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        composable("selector") {
            QuizSelector(onSelect = { page -> navController.navigate(page) })
        }
        composable("act") {
            QuizAct()
        }
        composable("acq5") {
            QuizAcq5()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AsthmaControlAppTheme {
        Main(rememberNavController())
    }
}