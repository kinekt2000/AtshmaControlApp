package ru.etu.asthmacontrolapp.classes

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlin.properties.ReadOnlyProperty


class AnswersStorage(private val context: Context, private val storageName: String) {
    companion object {
        @JvmStatic
        private var storage: ReadOnlyProperty<Context, DataStore<Preferences>>? = null
    }

    init {
        if (storage == null) {
            storage = preferencesDataStore(storageName)
        }
    }

    protected fun finalize() {
        storage = null
    }

    suspend fun saveAnswers(answers: List<Int>) {
        answers.forEachIndexed { index, i ->
            saveAnswer(index, i)
        }
    }

    suspend fun saveAnswer(questionIndex: Int, answerIndex: Int) {
        val storageLock = storage
        if (storageLock != null) {
            val dataStoreKey = intPreferencesKey(questionIndex.toString())
            storageLock.getValue(context, AnswersStorage::storageName).edit { storage ->
                storage[dataStoreKey] = answerIndex
            }
        }
    }

    suspend fun readAnswers(callback: (answers: List<Int>) -> Unit) {
        val storageLock = storage
        if (storageLock != null) {
            val answers: List<Int> = storageLock.getValue(context, AnswersStorage::storageName).data
                .first()
                .asMap()
                .entries
                .sortedBy { it.key.name }
                .map { (_, value) -> value.toString().toInt() }
            callback(answers)
        }
    }
}