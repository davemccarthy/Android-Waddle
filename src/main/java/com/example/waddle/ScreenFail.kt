package com.example.waddle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ScreenFail(navController: NavController, answer: String?){

    AppScaffold(
        "PLAY AGAIN",
        onClick = {navController.navigate("game")},enabled = true) {

        Text(
            text = "☹️",
            fontSize = 128.sp
        )
        Box(modifier = Modifier.padding(50.dp)) {
            Text(
                text = "Sorry, you've exhausted the number of attempts. The correct answer was $answer",
                fontSize = 24.sp
            )
        }
    }
}

