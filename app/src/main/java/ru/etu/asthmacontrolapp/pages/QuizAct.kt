package ru.etu.asthmacontrolapp.pages

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.etu.asthmacontrolapp.classes.AnswersStorage

import ru.etu.asthmacontrolapp.classes.Question
import ru.etu.asthmacontrolapp.compopents.MultiPageQuiz
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme

private val questionList = listOf(
    Question(
        "Как часто за последние 4 недели астма мешала Вам выполнять обычный объем работы в " + "учебном заведении, на работе или дома?",
        listOf("Все время", "Очень часто", "Иногда", "Редко", "Никогда")
    ), Question(
        "Как часто за последние 4 недели Вы отмечали у себя затрудненное дыхание?", listOf(
            "Чаще, чем раз в день",
            "1 раз в день",
            "От 3 до 6 раз в неделю",
            "1 или два раза в неделю",
            "Ни разу"
        )
    ), Question(
        "Как часто за последние 4 недели Вы просыпались ночью или раньше, чем обычно, из-за " + "симптомов астмы (свистящего дыхания, кашля, затрудненного дыхания, чувства стеснения или " + "боли в груди)?",
        listOf(
            "4 ночи в неделю или чаще",
            "2-3 ночи в неделю",
            "1 раз в неделю",
            "1 или 2 раза",
            "Ни разу"
        )
    ), Question(
        "Как часто за последние 4 недели Вы использовали быстродействующий ингалятор (например, " + "Вентолин, Беродуал, Амровен, Сальбутамол) или небулайзер (аэрозольный аппарат) с лекарством " + "(например, Беротек, Беродуал, Вентолин небулы)?",
        listOf(
            "3 раза в день или чаще",
            "1 или 2 раза в день",
            "2 или 3 раза в неделю",
            "1 раз в неделю или реже",
            "Ни разу"
        )
    ), Question(
        "Как бы Вы оценили, насколько Вам удавалось контролировать астму за последние 4 недели?",
        listOf(
            "Совсем не удавалось контролировать",
            "Плохо удавалось контролировать",
            "В некоторой степени удавалось контролировать",
            "Хорошо удавалось контролировать",
            "Полностью удавалось контролировать"
        )
    )
)

@Composable
fun QuizAct() {
    @SuppressLint("MutableCollectionMutableState") // reference to actual list is more valuable than the content
    var answers by remember { mutableStateOf(MutableList(questionList.size) { 0 }) }
    var finished by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val answersStorage by remember {
        derivedStateOf {
            AnswersStorage(
                context,
                "act"
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
fun QuizActPreview() {
    AsthmaControlAppTheme {
        QuizAct()
    }
}