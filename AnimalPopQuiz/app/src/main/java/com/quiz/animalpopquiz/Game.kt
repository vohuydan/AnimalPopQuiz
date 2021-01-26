package com.quiz.animalpopquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.ads.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException

class Game : AppCompatActivity() {
    private lateinit var mAdView: AdView
    companion object{
        val completedReorganizedList : ArrayList<CompletedReorganizedList> =ArrayList()
        val questionHolder : ArrayList<String> =ArrayList()
        val allAnswersHolder: ArrayList<ArrayList<String>> = ArrayList()
        val allCorrectAnswersHolder: ArrayList<String> = ArrayList()
        //var gameQuestionNumber = 0
        var isCorrect = 0
        var isFailed = 0
        var questNum = 10
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        questNum = intent.getIntExtra("questNum",10)
        Log.d("RECIEVED from Main", "$questNum")

        if(completedReorganizedList.isEmpty()) {
            val job = GlobalScope.launch(Dispatchers.Default) {
                fetchJson("https://opentdb.com/api.php?amount=50&category=27&type=multiple")
            }

            runBlocking {
                //delay(1000)
                while (Game.questionHolder.size != 50 && Game.allAnswersHolder.size != 50&& Game.allCorrectAnswersHolder.size != 50){
                    //do nothing
                }
                job.join()
            }
            Log.d("Past Getting API", "REORGANIZING LIST")
            completedReorganizedList.add(
                CompletedReorganizedList(
                    allQuestions = questionHolder,
                    allAnswers = allAnswersHolder,
                    allCorrectAnswers = allCorrectAnswersHolder
                )

            )
        }






        Log.d("Creating ADS", "ADSSSSSSSSSSSSSSSSSSSSSSS")
        MobileAds.initialize(this)
        bannerAd()
        Log.d("Creating ADS", "BANNER VREATED")
        isCorrect = 0
        playQuiz()
    }




    private fun playQuiz() {
        Log.d("Insed PLay quiz", "START GAME")
        val randomList: ArrayList<Int> = getRandom()
        Log.d("RANDOM LIST ", "$randomList")
        /*
        for (question in completedReorganizedList[0].allQuestions){
            println("$question")
        }

         */

        //display First question and number
        var questionNumber = 0
        questionNumber++
        question_number_text.text = "$questionNumber / ${questNum}"// change question number text
        val currentQuestion =
            completedReorganizedList[0].allQuestions[randomList[0]] //-1 for 0 index
        question_text.text = currentQuestion
        //set answers into list view
        val answersToCurrentQuestion = completedReorganizedList[0].allAnswers[randomList[0]]
        for (answers in answersToCurrentQuestion.withIndex()) {
            //put value in adapter to put into list view
            answer_container.adapter = AnswerAdapter(this, answersToCurrentQuestion)
        }


        answer_container.setOnItemClickListener { _, _, _, id ->
            val clickedId = id.toInt()
            if (completedReorganizedList[0].allCorrectAnswers[randomList[0]] == completedReorganizedList[0].allAnswers[randomList[0]][clickedId]) { // if the clicked choice is the same as the correct answer
                isCorrect++
            } else {
                isFailed++
            }
            Log.d("RANDOM LIST ", "$randomList")
            //if last question reached
            if (questionNumber == questNum) {//gameQuestionNumber == completedReorganizedList[0].allQuestions.count() - 1
                val intent = Intent(this, Result::class.java)
                intent.putExtra("questionNumber", questNum)
                intent.putExtra("correctAnswerNum", isCorrect)
                intent.putExtra("incorrectAnswerNum", isFailed)
                startActivity(intent)
                finish()



            } else {
                questionNumber++
                //gameQuestionNumber++
                randomList.removeAt(0)

            }
            //change to next question
            question_number_text.text = "$questionNumber / $questNum"
            question_text.text = completedReorganizedList[0].allQuestions[randomList[0]]
            val newanswersToCurrentQuestion =
                completedReorganizedList[0].allAnswers[randomList[0]]
            for (newanswer in newanswersToCurrentQuestion.withIndex()) {
                //put value in adapter to put into list view
                answer_container.adapter = AnswerAdapter(this, newanswersToCurrentQuestion)
            }

        }
    }

    private fun getRandom(): ArrayList<Int> {
        val returnList:ArrayList<Int> = ArrayList(questNum)
        for(i in 1..questNum){

            var rand = (0..49).random()

            if (returnList.contains(rand)){
                do{
                    rand = (0..49).random()
                }while(returnList.contains(rand))
            }
            returnList.add(rand)
        }
        return returnList
    }



    private fun bannerAd(){
        mAdView = findViewById(R.id.adview)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun fetchJson(url :String){
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val gson = GsonBuilder().create()
                val rawData =gson.fromJson(body,RawAPIDData::class.java)
                for ((index) in rawData.results.withIndex()){ //basic for loop going through each "result". Each index "result" is a package of questions/ answers-->for each result in length/size of rawData
                    //println ("$index , $value")
                    val rawDataCopy = rawData.results
                    var question = rawDataCopy[index].question //Accessing JsonAPIResult
                    question = parseString(question)
                    questionHolder.add(question) //add question to holder

                    val allAnswers = rawDataCopy[index].incorrect_answers
                    val parseAllAnswers:ArrayList<String> = ArrayList()
                    allAnswers.add((0..3).random(),rawDataCopy[index].correct_answer) // add in the correct answer and randomize the order
                    for (answerin in allAnswers){
                        val passedString= parseString(answerin)
                        //Log.d("After Parse", "$passedString")
                        parseAllAnswers.add(passedString)
                    }
                    allAnswersHolder.add(parseAllAnswers)

                    var correctAnswer = rawDataCopy[index].correct_answer
                    correctAnswer = parseString(correctAnswer)
                    allCorrectAnswersHolder.add(correctAnswer)
                }
            }
        })
    }
    private fun parseString(string: String):String {
        var newString = string

        //Log.d("Before Parse","$newString")
        if (newString.contains("&quot;")) { //remove from string
            newString = newString.replace("&quot;", "")
        }
        //Log.d("Before Parse","$newString")
        if (newString.contains("&#039;")) {
            newString = newString.replace("&#039;", "\'")
        }
        if (newString.contains("&eacute;")) {
            newString = newString.replace("&eacute;", "e")
        }

        return newString
    }
}
