package com.example.waddle

import android.content.res.AssetManager
import org.json.JSONObject
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class Dictionary {

    var contenders: List<String> = listOf()
    var valids: List<String> = listOf()

    private var explanation:String = "Oops - apparently we can't access the Oxford English Dictionary"

    fun initialize(assets: AssetManager){

        try {

            var bufferReader = assets.open("contenders.txt").bufferedReader()
            contenders = bufferReader.readLines()

            bufferReader = assets.open("valids.txt").bufferedReader()
            valids = bufferReader.readLines()
        }
        catch (cause: Exception){

            println("Failure loading dictionary resouces: $cause")
        }
    }

    fun getExplanation():String {
        return explanation
    }

    fun showWords() {

        for(word in valids)
            println(word)

        for(word2 in contenders)
            println(word2)
    }

    fun randomWord():String {

        val random = Random()

        return contenders[random.nextInt(contenders.size)].uppercase()
    }

    fun isValidWord(word:String):Boolean {

        for(w in contenders){

            if(w.uppercase() == word)
                return true
        }

        for(w in valids){

            if(w.uppercase() == word)
                return true
        }

        return false
    }

    fun queryWord(word:String) {

        Thread(Runnable {
            val url = URL("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/$word?fields=definitions")
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

            // Set Headers
            conn.setRequestProperty("app_id", "927bae55")
            conn.setRequestProperty("app_key", "1e016901498290db22722b07e4f78fff")

            try {

                var responseCode:Int = conn.getResponseCode()

                // Request not successful
                if (responseCode != HttpURLConnection.HTTP_OK) {

                    explanation = "Opps - could not access the Oxford English Dictionary: Response code $responseCode"

                    println(explanation)
                    return@Runnable
                }

                val data = conn.inputStream.bufferedReader().readText()
                var json = JSONObject(data)
                var results = json.getJSONArray("results")
                var result = JSONObject(results[0].toString())
                var lexicals = result.getJSONArray("lexicalEntries")

                var descriptions = StringBuilder()

                for(l in 0 until lexicals.length()){

                    var lexical = JSONObject(lexicals[l].toString())
                    var entries = lexical.getJSONArray("entries")
                    var entry = JSONObject(entries[0].toString())
                    var senses:JSONArray = entry.getJSONArray("senses")

                    var category = lexical.getJSONObject("lexicalCategory")
                    var type = category.getString("id")
                    var count = 1

                    descriptions.append(word.uppercase() + " - ($type)\n\n")

                    for (s in 0 until senses.length()){

                        var sense = JSONObject(senses[s].toString())
                        var definitions = sense.getJSONArray("definitions")

                        for (d in 0 until definitions.length()){

                            var definition = definitions[d].toString()
                            descriptions.append("$count. $definition\n\n")

                            count++
                        }
                    }
                }

                explanation = descriptions.toString()

            }catch (e: Exception){

                explanation = "$e"
                return@Runnable
            }

            conn.disconnect()

        }).start()
    }
}

