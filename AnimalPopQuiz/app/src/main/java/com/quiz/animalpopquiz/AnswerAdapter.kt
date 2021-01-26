package com.quiz.animalpopquiz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AnswerAdapter(val context: Context, val answerList : ArrayList<String>) : BaseAdapter() {
    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val singleAnswerRow: View = layoutInflater.inflate(R.layout.single_answer, viewGroup,false)
        val singleAnswerNumber = position+1 // 0 index
        singleAnswerRow.findViewById<TextView>(R.id.answer_num).text = "$singleAnswerNumber"
        singleAnswerRow.findViewById<TextView>(R.id.answer_text).text = "${answerList[position]}"
        return singleAnswerRow

    }

    override fun getItem(position: Int): Any {
        return position.toLong()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return answerList.count()
    }
}