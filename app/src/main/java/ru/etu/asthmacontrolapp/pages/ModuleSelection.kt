package ru.etu.asthmacontrolapp.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import kotlinx.coroutines.launch
import ru.etu.asthmacontrolapp.compopents.PatientList
import ru.etu.asthmacontrolapp.db.AppDatabase
import ru.etu.asthmacontrolapp.models.ApplicationEvent
import ru.etu.asthmacontrolapp.models.ApplicationModel
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme


@ExperimentalMaterialApi
@Composable
fun ModuleSelector() {
    val context = LocalContext.current
    val navController = rememberNavController()

    var openPatientSelector by remember { mutableStateOf(false) }
    var enteredPatientName by remember { mutableStateOf("") }

    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "audioRecords").build()
    }

    val applicationModel = viewModel<ApplicationModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ApplicationModel(db.audioRecordDao(), db.patientsDao()) as T
            }
        }
    )

    val applicationState by applicationModel.state.collectAsState()
    val patient by remember {
        derivedStateOf {
            applicationState.patients.firstOrNull { it.patientId == applicationState.patientId }
        }
    }

    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    if (openPatientSelector) {
        PatientList(
            patientList = applicationState.patients,
            onExit = { openPatientSelector = false },
            onSelect = {
                applicationModel.onEvent(ApplicationEvent.SetPatientId(it.patientId))
                openPatientSelector = false
            })
    }
    BackHandler(sheetState.isExpanded) {
        scope.launch {
            sheetState.collapse()
        }
    }
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            {
                OutlinedTextField(
                    value = enteredPatientName,
                    onValueChange = { enteredPatientName = it },
                    placeholder = { Text(text = "Имя пациента") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Row {
                    Spacer(modifier = Modifier.weight(1.0f))
                    OutlinedButton(
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colors.onSurface
                        ),
                        shape = RectangleShape,
                        onClick = {
                            scope.launch {
                                sheetState.collapse()
                            }
                            applicationModel.onEvent(ApplicationEvent.SavePatient(enteredPatientName))
                        },
                    ) {
                        Text(
                            text = "Добавить",
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            NavHost(
                navController = navController,
                startDestination = "module_selector",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
            ) {
                composable("module_selector") {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(intrinsicSize = IntrinsicSize.Max)
                                .weight(1.0f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedButton(
                                enabled = applicationState.patientId != null,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (applicationState.patientId != null) MaterialTheme.colors.onSurface else Color.LightGray,
                                ),
                                border = BorderStroke(
                                    ButtonDefaults.OutlinedBorderSize,
                                    if (applicationState.patientId != null) MaterialTheme.colors.onSurface else Color.LightGray
                                ),
                                onClick = { navController.navigate("quiz_selector") }) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Анкеты",
                                    textAlign = TextAlign.Center,
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                enabled = applicationState.patientId != null,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (applicationState.patientId != null) MaterialTheme.colors.onSurface else Color.LightGray,
                                ),
                                border = BorderStroke(
                                    ButtonDefaults.OutlinedBorderSize,
                                    if (applicationState.patientId != null) MaterialTheme.colors.onSurface else Color.LightGray
                                ),
                                onClick = { navController.navigate("audio_recorder") }) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Запись кашля",
                                    textAlign = TextAlign.Center,
                                    fontSize = 24.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                modifier = Modifier.size(60.dp),
                                onClick = { scope.launch { sheetState.expand() } }) {
                                Icon(
                                    modifier = Modifier.size(40.dp),
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "add patient"
                                )
                            }
                            Text(
                                patient?.patientName ?: "Пациент не выбран",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1.0f),
                            )
                            IconButton(
                                modifier = Modifier.size(60.dp),
                                onClick = {
                                    openPatientSelector = true
                                }) {
                                Icon(
                                    modifier = Modifier.size(40.dp),
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "select patient"
                                )
                            }
                        }
                    }
                }
                composable("quiz_selector") {
                    QuizSelector(
                        applicationState.patientId ?: 0,
                        onBack = { navController.navigateUp() })
                }
                composable("audio_recorder") {
                    Recorder(applicationModel, onBack = { navController.navigateUp() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun ModuleSelectorPreview() {
    AsthmaControlAppTheme {
        ModuleSelector()
    }
}
