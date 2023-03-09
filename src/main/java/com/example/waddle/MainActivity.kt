package com.example.waddle

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.waddle.ui.theme.DarkGreen
import com.example.waddle.ui.theme.Orange
import com.example.waddle.ui.theme.WaddleTheme

//  Globals
var dictionary = Dictionary()

var record:Int = 0
var streak:Int = 0

//  Match states
enum class LetterMatch{

    MATCH_PENDING,
    MATCH_EXACT,
    MATCH_PARTIAL,
    MATCH_NONE
}

//  Foreground - background
val letterColoring = arrayOf(

    LetterColors(Color.Black, Color.White),     //  MATCH_PENDING
    LetterColors(Color.White, DarkGreen),       //  MATCH_EXACT
    LetterColors(Color.White, Orange),          //  MATCH_PARTIAL
    LetterColors(Color.White, Color.LightGray)  //  MATCH_EXACT
)

//  Generic letter attribs
data class Letter(var character: Char = ' ', var match: LetterMatch = LetterMatch.MATCH_PENDING)

//  Letter colors
data class LetterColors(val foreground: Color, val background: Color)

@Composable
fun BlockLetter(letter:Char, match:LetterMatch){

    val configuration = LocalConfiguration.current
    val sideLength = configuration.screenHeightDp.dp / 12

    //  Fade in animation
    val foreground by animateColorAsState(
        targetValue = letterColoring[match.ordinal].foreground,
        animationSpec = tween(
            durationMillis = 2000,
            delayMillis = 40,
            easing = LinearOutSlowInEasing
        )
    )

    //  Fade in animation
    val background by animateColorAsState(
        targetValue = letterColoring[match.ordinal].background,
        animationSpec = tween(
            durationMillis = 2000,
            delayMillis = 40,
            easing = LinearOutSlowInEasing
        )
    )

    Surface(
        color = background,
        border = BorderStroke(1.dp, Color.DarkGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(sideLength)
            .height(sideLength)
            .padding(2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ){
            Text(
                text = letter.toString(),
                fontSize = 28.sp,
                color = foreground
            )
        }
    }
}

//  Letter (as seen on keyboard)
@Composable
fun BoardLetter(letter:Char, match:LetterMatch, onClick: () -> Unit = {}) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val foreground by animateColorAsState(
        targetValue = letterColoring[match.ordinal].foreground,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 40,
            easing = LinearOutSlowInEasing
        )
    )

    val background by animateColorAsState(
        targetValue = letterColoring[match.ordinal].background,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 40,
            easing = LinearOutSlowInEasing
        )
    )

    Button(
        modifier = Modifier
            .padding(1.dp)
            .size(width = screenWidth / 11, height = screenHeight / 16),
        colors = ButtonDefaults.buttonColors(backgroundColor = background),
        border = BorderStroke(1.dp, Color.Gray),
        onClick = onClick

    ) {
        Text(
            text = letter.toString(),
            textAlign = TextAlign.Center,
            color = foreground,
            fontSize = 16.sp
        )
    }
}

/**
 * Persistent stats
 */
@Composable
fun StatBox(title:String, value:Int){

    Box(
        modifier = Modifier.padding(15.dp,10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = MaterialTheme.colors.surface,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = value.toString(),
                color = MaterialTheme.colors.surface,
                style = MaterialTheme.typography.h6
            )
        }
    }
}

/**
 * Top pane
 */
@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        Row {

            StatBox(title = "Streak", value = streak)

            Box(modifier = Modifier
                .padding(15.dp, 10.dp)
                .weight(1f),
                contentAlignment = Alignment.Center
            ){

                Text(
                    text = "Waddle",//stringResource(R.string.app_name),
                    color = MaterialTheme.colors.surface,
                    style = MaterialTheme.typography.h4
                )
            }
            StatBox(title = "Record", value = record)
        }
    }
}

/**
* Bottom pane
*/
@Composable
fun BottomAppBar(ButtonMessage:String,enabled:Boolean,onClick: () -> Unit) {

    Box(modifier = Modifier
        //.fillMaxWidth()
        .background(color = MaterialTheme.colors.primary),
    ) {

        Button(
            enabled = enabled,
            modifier = Modifier
                .padding(10.dp,10.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary),
            border = BorderStroke(1.dp, Color.DarkGray),
            onClick = onClick) {
            Text(
                text = ButtonMessage,
                style = MaterialTheme.typography.h6)
        }
    }
}

/**
 *  Harness for app screens
 */
@Composable
fun AppScaffold(
    ButtonMessage:String,
    onClick: () -> Unit,
    enabled:Boolean = true,
    content: @Composable () -> Unit) {

    Scaffold(
        topBar = {TopAppBar()},
        bottomBar = {BottomAppBar(ButtonMessage,enabled,onClick)},
        drawerContent = {Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }}
    ){padding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

/**
 *  Entry point
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Let dic load data
        dictionary.initialize(application.assets)

        setContent {

            WaddleTheme {

                val navController = rememberNavController()

                NavHost(navController, startDestination = "intro") {
                    composable(route = "intro") {
                        ScreenIntro(navController)
                    }
                    composable(route = "game") {
                        ScreenGame(navController)
                    }
                    composable(route = "win/{answer}") {
                        ScreenWin(navController, it.arguments?.getString("answer"))
                    }
                    composable(route = "fail/{answer}") {
                        ScreenFail(navController, it.arguments?.getString("answer"))
                    }
                }
            }
        }
    }

    //  Load params
    override fun onResume() {
        super.onResume()

        val sharedPreferences: SharedPreferences = application.getSharedPreferences("Wordy", ComponentActivity.MODE_PRIVATE)

        streak = sharedPreferences.getInt("streak", 0)
        record = sharedPreferences.getInt("record", 0)
    }

    //  Save param
    override fun onPause() {
        super.onPause()

        val sharedPreferences: SharedPreferences = getSharedPreferences("Wordy", ComponentActivity.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()

        myEdit.putInt("streak", streak)
        myEdit.putInt("record", record)

        myEdit.apply()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WaddleTheme {

        val navController = rememberNavController()

        ScreenIntro(navController)
    }
}