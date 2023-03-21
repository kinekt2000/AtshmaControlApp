package ru.etu.asthmacontrolapp.pages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.etu.asthmacontrolapp.classes.AnswersStorage
import ru.etu.asthmacontrolapp.classes.Question
import ru.etu.asthmacontrolapp.compopents.MultiPageQuiz
import ru.etu.asthmacontrolapp.compopents.QuizResult


private val questionList = listOf(
    Question(
        "За последние 4 недели отмечались дневные симптомы чаще, чем 2 раза в неделю?",
        listOf("Нет", "Да")
    ),
    Question(
        "За последние 4 недели отмечались ночные пробуждения из-аз бронхиальной астмы?",
        listOf("Нет", "Да")
    ),
    Question(
        "За последние 4 недели отмечалась потребность в использовании препаратов для купирования синдромов чаще, чем 2 раза в неделю?",
        listOf("Нет", "Да")
    ),
    Question(
        "За последние 4 недели отмечалось любое ограничение активности из-за бронхиальной астмы?",
        listOf("Нет", "Да")
    )
)

private fun answersKey(answers: List<Int>): String {
    val sum = answers.sum() - questionList.size

    if (sum == 0) {
        return "За последние 4 недели отмечается ХОРОШО контролируемое течение бронхиальной астмы"
    }

    if (sum < 3) {
        return "За последние 4 недели отмечается Частично контролируемое течение бронхиальной астмы"
    }

    return "За последние 4 недели отмечается НЕКОНТРОЛИРУЕМОЕ течение бронхиальной астмы"
}

@Composable
fun QuizGina(onExit: () -> Unit = {}) {
    @SuppressLint("MutableCollectionMutableState") // reference to actual list is more valuable than the content
    var answers by remember { mutableStateOf(MutableList(questionList.size) { 0 }) }
    var finished by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val answersStorage by remember {
        derivedStateOf {
            AnswersStorage(
                context,
                "gina"
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
                finished = true
            }
        }
    }

    if (finished) {
        QuizResult(
            heading = answersKey(answers),
            questions = questionList,
            answers = answers,
            onExit = { onExit() })
    } else {
        MultiPageQuiz(
            questionList = questionList,
            initialAnswers = answers,
            onAnswer = { questionIndex, answerIndex -> answerEvent(questionIndex, answerIndex) },
            onFinish = { lAnswers -> finishEvent(lAnswers) },
            onExit = { onExit() }
        )
    }
}