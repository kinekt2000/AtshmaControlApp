package ru.etu.asthmacontrolapp.classes

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.properties.ReadOnlyProperty


class AnswersStorage(
    private val context: Context,
    quizName: String,
    patientId: Long
) {
    companion object {
        @JvmStatic
        private var storageMap: MutableMap<String, ReadOnlyProperty<Context, DataStore<Preferences>>?> =
            mutableMapOf()

        protected fun finalize() {
            storageMap.clear()
        }
    }

    private var storage: ReadOnlyProperty<Context, DataStore<Preferences>>? = null
    private val storageName = "${quizName}_${patientId}"
    private val saveDirectory = File(context.getExternalFilesDir(".quiz"), quizName)
    private val saveFile = File(saveDirectory, "$patientId.txt")

    init {
        if (storageMap.getOrDefault(storageName, null) == null) {
            storageMap[storageName] = preferencesDataStore(storageName)
        }
        storage = storageMap[storageName]
    }

    suspend fun saveAnswers(answers: List<Int>) {
        withContext(Dispatchers.IO) {
            try {
                saveDirectory.mkdirs()
                saveFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                saveFile.printWriter().use { out ->
                    answers.forEachIndexed { index, i ->
                        this@AnswersStorage.saveAnswer(index, i)
                        out.println(i)
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
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
        delay(2000L)

        if (saveFile.exists()) {
            saveFile.readLines().map(String::toInt).forEachIndexed { index, i ->
                saveAnswer(index, i)
                Log.d("ANSWERS", "$index -> $i")
            }
        }

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

    suspend fun destroy() {
        Log.d("ANSWERS", "CALLED DESTROY")
        Log.d("ANSWERS", saveFile.absolutePath)
        val storageLock = storage
        storageLock?.getValue(context, AnswersStorage::storageName)?.edit {
            it.clear()
        }
    }
}