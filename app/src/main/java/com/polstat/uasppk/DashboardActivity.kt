package com.polstat.uasppk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {
    private lateinit var welcomeMessage: TextView
    private lateinit var logoutButton: Button
    private lateinit var searchInput: EditText
    private lateinit var surveysRecyclerView: RecyclerView
    private lateinit var surveyAdapter: SurveyAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        welcomeMessage = findViewById(R.id.welcomeMessage)
        logoutButton = findViewById(R.id.logoutButton)
        searchInput = findViewById(R.id.searchInput)
        surveysRecyclerView = findViewById(R.id.surveysRecyclerView)

        apiService = ApiClient.instance

        surveysRecyclerView.layoutManager = LinearLayoutManager(this)
        surveyAdapter = SurveyAdapter()
        surveysRecyclerView.adapter = surveyAdapter

        loadSurveys()

        searchInput.addTextChangedListener {
            filterSurveys(it.toString())
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        val userProfile: TextView = findViewById(R.id.userProfileText)
        userProfile.setOnClickListener {
            navigateToUserProfile()
        }

        val createSurveyButton: Button = findViewById(R.id.createSurveyButton)
        createSurveyButton.setOnClickListener {
            val intent = Intent(this, CreateSurveyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadSurveys() {
        apiService.getAllSurveys().enqueue(object : Callback<List<Survey>> {
            override fun onResponse(call: Call<List<Survey>>, response: Response<List<Survey>>) {
                if (response.isSuccessful) {
                    val surveys = response.body() ?: emptyList()
                    surveyAdapter.submitList(surveys)
                } else {
                    Log.e("DashboardActivity", "Failed to load surveys: ${response.errorBody()?.string()}")
                    Toast.makeText(this@DashboardActivity, "Failed to load surveys", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Survey>>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Network error, try again!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterSurveys(query: String) {
        val filteredSurveys = surveyAdapter.currentList.filter {
            it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
        }
        surveyAdapter.submitList(filteredSurveys)
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        sharedPreferences.edit().remove("JWT_TOKEN").apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToUserProfile() {
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }
}