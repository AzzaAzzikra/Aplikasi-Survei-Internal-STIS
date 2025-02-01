package com.polstat.uasppk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurveyDetailActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private var surveyId: Long? = null
    private lateinit var questionsRecyclerView: RecyclerView
    private lateinit var submitButton: Button
    private lateinit var backButton: Button
    private lateinit var editButton: Button // Corrected the declaration
    private lateinit var questionsAdapter: QuestionAdapter
    private lateinit var surveyTitleTextView: TextView
    private lateinit var surveyDescriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_detail)

        apiService = ApiClient.instance

        surveyId = intent.getLongExtra("SURVEY_ID", -1)

        if (surveyId == -1L) {
            Toast.makeText(this, "Invalid Survey ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        questionsRecyclerView = findViewById(R.id.questionsRecyclerView)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.backButton)
        editButton = findViewById(R.id.editButton) // Initialize editButton
        surveyTitleTextView = findViewById(R.id.surveyTitleTextView)
        surveyDescriptionTextView = findViewById(R.id.surveyDescriptionTextView)

        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsAdapter = QuestionAdapter(questionsRecyclerView)
        questionsRecyclerView.adapter = questionsAdapter

        fetchSurveyDetails()

        fetchSurveyQuestions()

        backButton.setOnClickListener {
            finish()
        }

        submitButton.setOnClickListener {
            submitSurveyResponse()
        }

        editButton.setOnClickListener {
            val intent = Intent(this, EditQuestionActivity::class.java)
            intent.putExtra("SURVEY_ID", surveyId)
            startActivity(intent)
        }
    }

    private fun fetchSurveyDetails() {
        val id = surveyId ?: run {
            Toast.makeText(this@SurveyDetailActivity, "Invalid Survey ID", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.getSurveyById(id).enqueue(object : Callback<Survey> {
            override fun onResponse(call: Call<Survey>, response: Response<Survey>) {
                if (response.isSuccessful) {
                    val survey = response.body()
                    survey?.let {
                        surveyTitleTextView.text = it.title
                        surveyDescriptionTextView.text = it.description
                    }
                } else {
                    Toast.makeText(this@SurveyDetailActivity, "Failed to load survey details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Survey>, t: Throwable) {
                Toast.makeText(this@SurveyDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchSurveyQuestions() {
        val id = surveyId ?: run {
            Toast.makeText(this@SurveyDetailActivity, "Invalid Survey ID", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.getQuestionsBySurveyId(id).enqueue(object : Callback<List<QuestionDTO>> {
            override fun onResponse(call: Call<List<QuestionDTO>>, response: Response<List<QuestionDTO>>) {
                if (response.isSuccessful) {
                    val questions = response.body() ?: emptyList()
                    questionsAdapter.submitList(questions)
                } else {
                    Toast.makeText(this@SurveyDetailActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<QuestionDTO>>, t: Throwable) {
                Toast.makeText(this@SurveyDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun submitSurveyResponse() {
        val responses = questionsAdapter.getResponses()

        if (responses.isEmpty()) {
            Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = "222212894@stis.ac.id"

        for ((index, question) in questionsAdapter.currentList.withIndex()) {
            val response = responses.getOrNull(index)

            if (response == null || question.id == null) {
                Toast.makeText(this, "Question ID is missing for Question ${question.text}", Toast.LENGTH_SHORT).show()
                continue
            }

            val answer = when (question.type) {
                QuestionType.OPEN_ENDED -> response
                QuestionType.MULTIPLE_CHOICE_SINGLE, QuestionType.MULTIPLE_CHOICE_MULTIPLE -> response?.split(",")
                else -> null
            }

            fun convertToQuestion(questionDTO: QuestionDTO): Question {
                return Question(
                    id = questionDTO.id,
                    text = questionDTO.text,
                    questionType = questionDTO.type,
                    surveyId = 0L,  // This would need to be set appropriately
                    choices = questionDTO.choices,
                    responses = listOf()  // If needed, you can handle this as an empty list or fetch it from somewhere else
                )
            }

            val responseDTO = ResponseDTO(
                userEmail = userEmail,
                selectedChoices = if (question.type != QuestionType.OPEN_ENDED) answer as? List<String> else null,
                openEndedAnswer = if (question.type == QuestionType.OPEN_ENDED) answer as? String else null,
                question = convertToQuestion(question)
            )

            apiService.submitResponse(surveyId ?: return, responseDTO).enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SurveyDetailActivity, "Response submitted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SurveyDetailActivity, "Failed to submit response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Toast.makeText(this@SurveyDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
