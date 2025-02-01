package com.polstat.uasppk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class QuestionAdapter(private val recyclerView: RecyclerView) : ListAdapter<QuestionDTO, QuestionAdapter.QuestionViewHolder>(QuestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.question_card, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = getItem(position)
        holder.bind(question)
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val answerEditText: EditText = itemView.findViewById(R.id.answerEditText)
        private val answerRadioGroup: RadioGroup = itemView.findViewById(R.id.answerRadioGroup)
        private val answerCheckBoxGroup: ViewGroup = itemView.findViewById(R.id.answerCheckBoxGroup)

        fun bind(question: QuestionDTO) {
            questionText.text = question.text

            // Hide all input types first
            answerEditText.visibility = View.GONE
            answerRadioGroup.visibility = View.GONE
            answerCheckBoxGroup.visibility = View.GONE

            when (question.type) {
                QuestionType.OPEN_ENDED -> {
                    answerEditText.visibility = View.VISIBLE
                }
                QuestionType.MULTIPLE_CHOICE_SINGLE -> {
                    answerRadioGroup.visibility = View.VISIBLE
                    answerRadioGroup.removeAllViews()
                    question.choices.forEach { choice ->
                        val radioButton = RadioButton(itemView.context)
                        radioButton.text = choice
                        answerRadioGroup.addView(radioButton)
                    }
                }
                QuestionType.MULTIPLE_CHOICE_MULTIPLE -> {
                    answerCheckBoxGroup.visibility = View.VISIBLE
                    answerCheckBoxGroup.removeAllViews()
                    question.choices.forEach { choice ->
                        val checkBox = CheckBox(itemView.context)
                        checkBox.text = choice
                        answerCheckBoxGroup.addView(checkBox)
                    }
                }
            }
        }

        fun getResponse(): String {
            return when {
                answerEditText.visibility == View.VISIBLE -> {
                    answerEditText.text.toString()
                }
                answerRadioGroup.visibility == View.VISIBLE -> {
                    val selectedRadioButtonId = answerRadioGroup.checkedRadioButtonId
                    val selectedRadioButton = itemView.findViewById<RadioButton>(selectedRadioButtonId)
                    selectedRadioButton?.text.toString()
                }
                answerCheckBoxGroup.visibility == View.VISIBLE -> {
                    val selectedChoices = mutableListOf<String>()
                    for (i in 0 until answerCheckBoxGroup.childCount) {
                        val checkBox = answerCheckBoxGroup.getChildAt(i) as CheckBox
                        if (checkBox.isChecked) {
                            selectedChoices.add(checkBox.text.toString())
                        }
                    }
                    selectedChoices.joinToString(", ")
                }
                else -> ""
            }
        }
    }

    class QuestionDiffCallback : DiffUtil.ItemCallback<QuestionDTO>() {
        override fun areItemsTheSame(oldItem: QuestionDTO, newItem: QuestionDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QuestionDTO, newItem: QuestionDTO): Boolean {
            return oldItem == newItem
        }
    }

    // Update the getResponses method
    fun getResponses(): List<String> {
        val responses = mutableListOf<String>()

        // Iterate over the list of questions and get responses
        for (i in currentList.indices) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? QuestionViewHolder
            val response = viewHolder?.getResponse() ?: ""
            responses.add(response)
        }

        return responses
    }
}