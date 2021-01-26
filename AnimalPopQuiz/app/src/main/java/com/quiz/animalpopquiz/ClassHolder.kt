package com.quiz.animalpopquiz


class RawAPIDData(
    val results :List<JsonAPIResult>,
    val unused_response_code : Int
)


class JsonAPIResult(
    val category:String,
    val type: String,
    val difficulty: String,
    val question:String,
    val correct_answer: String,
    val incorrect_answers: ArrayList<String>
)

class CompletedReorganizedList(
    val allQuestions: ArrayList<String>,
    val allAnswers: ArrayList<ArrayList<String>>,
    val allCorrectAnswers: ArrayList<String>
)

class EndGameResults(

    val attemptedQuestionNumber:String,
    val correctNumberofAnswer:String,
    val incorrectNumberofAnswers:String
)