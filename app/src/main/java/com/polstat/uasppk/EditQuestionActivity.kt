package com.polstat.uasppk

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditQuestionActivity : AppCompatActivity() {

    private lateinit var questionSpinner: Spinner
    private lateinit var questionEditText: EditText
    private lateinit var questionTypeSpinner: Spinner
    private lateinit var answerChoicesLayout: LinearLayout
    private lateinit var addChoiceButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var deleteButton: Button

    private lateinit var apiService: ApiService
    private var surveyId: Long? = null
    private var questionId: Long? = null

    private val answerChoices = mutableListOf<String>()
    private val questionsList = mutableListOf<QuestionDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_question)

        apiService = ApiClient.instance
        surveyId = intent.getLongExtra("SURVEY_ID", -1)

        if (surveyId == -1L) {
            Toast.makeText(this, "Invalid Survey ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        questionSpinner = findViewById(R.id.selectQuestionSpinner)
        questionEditText = findViewById(R.id.questionEditText)
        questionTypeSpinner = findViewById(R.id.questionTypeSpinner)
        answerChoicesLayout = findViewById(R.id.answerChoicesLayout)
        addChoiceButton = findViewById(R.id.addChoiceButton)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        deleteButton = findViewById(R.id.deleteButton)

        setupQuestionTypeSpinner()
        fetchQuestions()

        questionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    clearFields()
                } else {
                    loadQuestionDetails(questionsList[position - 1])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        addChoiceButton.setOnClickListener {
            val choiceText = EditText(this)
            choiceText.hint = "Enter choice"
            answerChoicesLayout.addView(choiceText)
            answerChoices.add("")
            choiceText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    answerChoices[answerChoicesLayout.indexOfChild(choiceText)] = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        questionTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = QuestionType.values()[position]

                if (selectedType == QuestionType.MULTIPLE_CHOICE_SINGLE || selectedType == QuestionType.MULTIPLE_CHOICE_MULTIPLE) {
                    addChoiceButton.visibility = View.VISIBLE
                    answerChoicesLayout.visibility = View.VISIBLE
                } else {
                    addChoiceButton.visibility = View.GONE
                    answerChoicesLayout.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        saveButton.setOnClickListener { saveQuestion() }
        cancelButton.setOnClickListener { finish() }
        deleteButton.setOnClickListener { deleteQuestion() }
    }

    private fun setupQuestionTypeSpinner() {
        val questionTypes = QuestionType.values().map { it.name }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, questionTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionTypeSpinner.adapter = spinnerAdapter
    }

    private fun deleteQuestion() {
        apiService.deleteQuestion(surveyId!!, questionId!!).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditQuestionActivity, "Question deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditQuestionActivity, "Failed to delete question", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EditQuestionActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchQuestions() {
        apiService.getQuestionsBySurveyId(surveyId!!).enqueue(object : Callback<List<QuestionDTO>> {
            override fun onResponse(call: Call<List<QuestionDTO>>, response: Response<List<QuestionDTO>>) {
                if (response.isSuccessful) {
                    questionsList.clear()
                    questionsList.addAll(response.body() ?: emptyList())
                    updateQuestionSpinner()
                }
            }

            override fun onFailure(call: Call<List<QuestionDTO>>, t: Throwable) {
                Toast.makeText(this@EditQuestionActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateQuestionSpinner() {
        val questionTitles = mutableListOf("Create New Question")
        questionTitles.addAll(questionsList.map { it.text })
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, questionTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionSpinner.adapter = adapter
    }

    private fun loadQuestionDetails(question: QuestionDTO) {
        questionId = question.id
        questionEditText.setText(question.text)
        questionTypeSpinner.setSelection(QuestionType.valueOf(question.type.name).ordinal)
        answerChoices.clear()
        answerChoices.addAll(question.choices)
        updateChoiceLayout()
    }

    private fun clearFields() {
        questionId = null
        questionEditText.text.clear()
        questionTypeSpinner.setSelection(0)
        answerChoices.clear()
        updateChoiceLayout()
    }

    private fun updateChoiceLayout() {
        answerChoicesLayout.removeAllViews()
        answerChoices.forEachIndexed { index, choice ->
            val choiceEditText = EditText(this)
            choiceEditText.setText(choice)
            answerChoicesLayout.addView(choiceEditText)
            choiceEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    answerChoices[index] = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun saveQuestion() {
        val questionText = questionEditText.text.toString()
        val questionType = QuestionType.valueOf(questionTypeSpinner.selectedItem.toString())

        val question = Question(
            id = questionId ?: 0L,
            text = questionText,
            questionType = questionType,
            surveyId = surveyId!!,
            choices = if (questionType == QuestionType.MULTIPLE_CHOICE_SINGLE || questionType == QuestionType.MULTIPLE_CHOICE_MULTIPLE) answerChoices else emptyList(),
            responses = emptyList()
        )

        if (questionId == null) {
            addNewQuestion(question)
        } else {
            updateQuestion(question)
        }
    }

    private fun addNewQuestion(question: Question) {
        apiService.addQuestionToSurvey(surveyId!!, question).enqueue(object : Callback<Question> {
            override fun onResponse(call: Call<Question>, response: Response<Question>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditQuestionActivity, "Question added", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditQuestionActivity, "Failed to add question", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Question>, t: Throwable) {
                Toast.makeText(this@EditQuestionActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateQuestion(question: Question) {
        apiService.updateQuestion(surveyId!!, questionId!!, question).enqueue(object : Callback<Question> {
            override fun onResponse(call: Call<Question>, response: Response<Question>) {
                Toast.makeText(this@EditQuestionActivity, "Question updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            override fun onFailure(call: Call<Question>, t: Throwable) {
                Toast.makeText(this@EditQuestionActivity, "Failed to update question", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
