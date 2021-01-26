package com.quiz.animalpopquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_result.*
import kotlin.system.exitProcess

class Result : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val questionNumber = intent.getIntExtra("questionNumber", 0)
        val correctAnswerNum = intent.getIntExtra("correctAnswerNum", 0)
        //val incorrectAnswerNum = intent.getIntExtra("incorrectAnswerNum", 0)
        val percent = 100*(correctAnswerNum.toDouble()/questionNumber.toInt())
        var percenttostring = String.format("%.2f", percent)
        if (percenttostring == "100.00"){
            percenttostring = "100"
        }
        scorepercent.text = "$percenttostring%"
        if (percent >= 75){
            scorefeedback.text= getString(R.string.top75)
            animalpic.setImageResource(R.drawable.koala)
        }else if(percent >25 && percent <75){
            scorefeedback.text= getString(R.string.mid50)
            animalpic.setImageResource(R.drawable.whale2)
        }else if (percent <=26){
            scorefeedback.text= getString(R.string.low25)
            animalpic.setImageResource(R.drawable.nothappypuppy)
        }

        play_button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        exit_button.setOnClickListener{
            finish();


        }
    }
}
