package com.example.waddle

import android.os.Handler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waddle.ui.theme.*

@Composable
fun ScreenGame(navController: NavController) {

    //  Answer with initial value
    val answer by remember { mutableStateOf(dictionary.randomWord()) }

    //  Coordinates
    var cursorX by remember { mutableStateOf(0) }
    var cursorY by remember { mutableStateOf(0) }

    //  Informative button
    var message by remember { mutableStateOf("SUBMIT") }

    //  Guess entries
    val grid = remember { mutableStateListOf(
        mutableStateListOf(Letter(),Letter(),Letter(),Letter(),Letter()),
        mutableStateListOf(Letter(),Letter(),Letter(),Letter(),Letter()),
        mutableStateListOf(Letter(),Letter(),Letter(),Letter(),Letter()),
        mutableStateListOf(Letter(),Letter(),Letter(),Letter(),Letter()),
        mutableStateListOf(Letter(),Letter(),Letter(),Letter(),Letter()),
        mutableStateListOf(Letter(),Letter(),Letter(),Letter(),Letter())
    )}

    //  keyboard keys
    val keyboard = remember { mutableStateListOf(
        mutableStateListOf(Letter('Q'),Letter('W'),Letter('E'),Letter('R'),Letter('T'),Letter('Y'),Letter('U'),Letter('I'),Letter('O'),Letter('P')),
        mutableStateListOf(Letter('A'),Letter('S'),Letter('D'),Letter('F'),Letter('G'),Letter('H'),Letter('J'),Letter('K'),Letter('L')),
        mutableStateListOf(Letter('Z'),Letter('X'),Letter('C'),Letter('V'),Letter('B'),Letter('N'),Letter('M'),Letter('⌫'))
    )}

    AppScaffold(
        message,
        onClick = {

            //  Build guess word
            var guess = ""

            for(letter in grid[cursorY])
                guess += letter.character

            //  Submission - verify word
            if(!dictionary.isValidWord(guess)){

                message = "Invalid word: $guess"
                return@AppScaffold
            }

            //  Check each individual letter
            for(index in 0..4){

                val character = grid[cursorY][index].character
                var match = LetterMatch.MATCH_PARTIAL

                if(character == answer[index]){
                    match = LetterMatch.MATCH_EXACT
                }
                else if(!answer.contains(character))
                    match = LetterMatch.MATCH_NONE

                //  Assign match status
                grid[cursorY][index] = Letter(character, match = match)

                //  Find character on keyboard
                for(row in keyboard.indices){
                    for(key in keyboard[row].indices)
                        if(keyboard[row][key].character == character && keyboard[row][key].match != LetterMatch.MATCH_EXACT)
                            keyboard[row][key] = Letter(character, match = match)
                }
            }

            println("Answer: $answer")

            //  Move on
            cursorX = 0
            cursorY++

            //  Check for correct answer
            if(guess == answer){

                dictionary.queryWord(answer)

                if(++streak > record)
                    record = streak

                val delayedHandler = Handler()
                delayedHandler.postDelayed({
                    navController.navigate("win/$answer")
                }, 3000)

                return@AppScaffold
            }

            if(cursorY > 5){

                streak = 0

                val delayedHandler = Handler()
                delayedHandler.postDelayed({
                    navController.navigate("fail/$answer")
                }, 2000)
            }
        },
        enabled = cursorX == 5){

        //  Guess matrix
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Card(

                backgroundColor = Opaque,
                shape = RoundedCornerShape(5.dp),
                elevation = 10.dp)
            {
                //  Components
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //  Visual letter grid
                    grid.forEach {
                        Row(
                            Modifier.padding(1.dp),
                        ) {
                            it.forEach {
                                BlockLetter(letter = it.character, match = it.match)
                            }
                        }
                    }
                }
            }

            //  Small gap
            Spacer(modifier = Modifier.height(10.dp))

            //  Visual keyboard
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                keyboard.forEach {

                    Card(
                        backgroundColor = Opaque,
                        shape = RoundedCornerShape(5.dp),
                        elevation = 10.dp)
                    {
                        Row(
                            Modifier.padding(1.dp),
                        ) {
                            it.forEach {
                                BoardLetter(letter = it.character, match = it.match, onClick = {

                                    //  Check for backspace
                                    if(it.character == '⌫'){
                                        if(cursorX != 0){
                                            cursorX--
                                            grid[cursorY][cursorX] = Letter(character = ' ')
                                        }
                                        return@BoardLetter
                                    }

                                    //  Check for end of guess
                                    if(cursorX >= 5)
                                        return@BoardLetter

                                    //  Add letter and move on
                                    grid[cursorY][cursorX] = Letter(character = it.character)
                                    cursorX++

                                    //  Revert message back regardless
                                    message = "SUBMIT"
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {

    val navController = rememberNavController()

    WaddleTheme {
        ScreenGame(navController)
    }
}