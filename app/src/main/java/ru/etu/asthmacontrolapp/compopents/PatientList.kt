package ru.etu.asthmacontrolapp.compopents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ru.etu.asthmacontrolapp.db.patient.Patient
import ru.etu.asthmacontrolapp.ui.theme.AsthmaControlAppTheme
import com.slaviboy.composeunits.dw


@Composable
fun PatientList(
    patientList: List<Patient>,
    onSelect: (Patient) -> Unit = {},
    onExit: () -> Unit = {}
) {
    var input by remember { mutableStateOf("") }
    var selected: Patient? by remember { mutableStateOf(null) }

    BackHandler {
        onExit()
    }
    Dialog(onDismissRequest = { onExit() }) {
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(0.5.dw, 0.8.dw)
        ) {
            Column {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "search"
                        )
                    },
                    placeholder = { Text(text = "поиск") }
                )

                LazyColumn(modifier = Modifier.weight(1.0f))
                {
                    items(
                        patientList.filter { it.patientName.contains(input) },
                        key = { message -> message.patientId },
                        itemContent = {
                            Card(
                                modifier = Modifier
                                    .padding(8.dp, 8.dp, 8.dp, 0.dp)
                                    .clickable { selected = it },
                                elevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(30.dp, 30.dp),
                                        contentAlignment = Alignment.Center,
                                    )
                                    {
                                        if (it == selected) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = "checked"
                                            )
                                        }
                                    }
                                    Text(text = it.patientName)
                                }
                            }
                        })
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
                        shape = RectangleShape,
                        onClick = { onExit() },
                    ) {
                        Text(
                            text = "Назад",
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 18.sp
                        )
                    }
                    OutlinedButton(
                        enabled = selected != null,
                        border = BorderStroke(
                            1.dp,
                            if (selected != null) MaterialTheme.colors.onSurface else Color.LightGray
                        ),
                        shape = RectangleShape,
                        onClick = { onSelect(selected!!) },
                    ) {
                        Text(
                            text = "Выбрать",
                            color = if (selected != null) MaterialTheme.colors.onSurface else Color.LightGray,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatientListPreview() {
    AsthmaControlAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        )
        PatientList(
            listOf(
                Patient("First"),
                Patient("Second"),
                Patient("Third"),
                Patient("Third"),
                Patient("Third")

            )
        )
    }
}
