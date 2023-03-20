package ru.etu.asthmacontrolapp.pages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.etu.asthmacontrolapp.classes.AnswersStorage

import ru.etu.asthmacontrolapp.classes.Question
import ru.etu.asthmacontrolapp.compopents.MultiPageQuiz
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme

private val questionList: List<Question> = listOf(
    Question(
        "В среднем, как часто за последнюю неделю Вы просыпались ночью из-за астмы?",
        listOf(
            "Никогда",
            "Очень редко",
            "Редко",
            "Несколько раз",
            "Много раз",
            "Очень много раз",
            "Не мог(-ла) спать из-за астмы"
        )
    ), Question(
        "В среднем, насколько сильны были симптомы астмы, когда Вы просыпались утром в течение последней недели?",
        listOf(
            "Симптомов не было",
            "Очень слабые симптомы",
            "Слабые симптомы",
            "Умеренные симптомы",
            "Довольно сильные симптомы",
            "Сильные симптомы",
            "Очень сильные симптомы"
        )
    ), Question(
        "В целом, насколько Вы были ограниченны в своих профессиональных и повседневных занятиях из-за астмы в течение последней недели?",
        listOf(
            "Совсем не ограничен(-а)",
            "Чуть-чуть ограничен(-а)",
            "Немного ограничен(-а)",
            "Умеренно ограничен(-а)",
            "Очень ограничен(-а)",
            "Чрезвычайно ограничен(-а)",
            "Полностью ограничен(-а)"
        )
    ), Question(
        "В целом, какую часть времени в течение недели у Вас были хрипы в груди?",
        listOf(
            "Никогда",
            "Очень редко",
            "Редко",
            "Иногда",
            "Значительную часть времени",
            "Подавляющую часть времени",
            "Все время"
        )
    ), Question(
        "В целом, была ли у вас отдышка из-за астмы в течение последней недели?",
        listOf(
            "Отдышки не было",
            "Очень небольшая",
            "Небольшая",
            "Умеренная",
            "Довольно сильная",
            "Сильная",
            "Очень сильная"
        )
    )
)

@Composable
fun QuizAcq5() {
    @SuppressLint("MutableCollectionMutableState") // reference to actual list is more valuable than the content
    var answers by remember { mutableStateOf(MutableList(questionList.size) { 0 }) }
    var finished by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val answersStorage by remember {
        derivedStateOf {
            AnswersStorage(
                context,
                "acq5"
            )
        }
    }

    LaunchedEffect(Unit) {
        answersStorage.readAnswers { readAnswers ->
            answers = List(answers.size) { index ->
                readAnswers.getOrElse(index) { 0 }
            }.toMutableList()
            finished = readAnswers.size == questionList.size
            loaded = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("QuizAc15", "Destroy Composable")
        }
    }

    fun answerEvent(questionIndex: Int, answerIndex: Int) {
        runBlocking {
            launch {
                answersStorage.saveAnswer(questionIndex, answerIndex)
                answers[questionIndex] = answerIndex
            }
        }
    }

    fun finishEvent(lAnswers: List<Int>) {
        runBlocking {
            launch {
                answersStorage.saveAnswers(lAnswers)
                answers = lAnswers.toMutableList()
            }
        }
    }

    MultiPageQuiz(
        questionList = questionList,
        initialAnswers = answers,
        onAnswer = { questionIndex, answerIndex -> answerEvent(questionIndex, answerIndex) },
        onFinish = { lAnswers -> finishEvent(lAnswers) }
    )
}

@Preview(showBackground = true)
@Composable
fun QuizAcq5Preview() {
    AsthmaControlAppTheme {
        QuizAcq5()
    }
}