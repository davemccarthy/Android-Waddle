package com.example.waddle

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

//  Sample colorized letter
@Composable
fun Example(letter:Char, match:LetterMatch, explanation:String){

    val configuration = LocalConfiguration.current
    val sideLength = configuration.screenHeightDp.dp / 12

    Box(modifier = Modifier.padding(35.dp,5.dp)){

        Row(){

            BlockLetter(letter = letter, match = match)
            Text(
                text = explanation,
                modifier = Modifier
                    .padding(10.dp,0.dp)
                    .height(sideLength)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                style = MaterialTheme.typography.subtitle1,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ScreenIntro(navController: NavController){

    AppScaffold(
        "START GAME",
        onClick = {navController.navigate("game")},enabled = true) {

        //  Nice comment
        Box(modifier = Modifier.padding(20.dp)) {
            Text(text = "A homage to the great WORDLE game. Easy to play. You have 6 chances to guess the correct 5 letter word. There are glues in the coloring of your guesses.", style = MaterialTheme.typography.h6)
        }

        Box {
            Column {

                Example(letter = 'A', LetterMatch.MATCH_EXACT, explanation = "Correct letter in correct position")
                Example(letter = 'B', LetterMatch.MATCH_PARTIAL, explanation = "Correct letter in wrong position")
                Example(letter = 'C', LetterMatch.MATCH_NONE, explanation = "Letter not in word")
            }
        }

        //  Nice comment
        Box(modifier = Modifier.padding(20.dp)) {
            Text(text = "And as a little bonus, if you make a correct guess, you'll be supplied with the definition from the Oxford Dictionary", style = MaterialTheme.typography.h6)
        }
    }
}