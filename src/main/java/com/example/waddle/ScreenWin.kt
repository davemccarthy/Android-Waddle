package com.example.waddle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ScreenWin(navController: NavController, answer: String?){

    //  Answer with initial value
    var explanation by remember { mutableStateOf("") }

    explanation = dictionary.getExplanation()

    AppScaffold(
        "PLAY AGAIN",
        onClick = {navController.navigate("game")},enabled = true){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "\uD83D\uDE03️",
                fontSize = 96.sp
            )
            Box(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Congratulations !!!",
                    fontSize = 32.sp
                )
            }
            Box(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = explanation,
                    fontSize = 18.sp
                )
            }
        }
    }
}