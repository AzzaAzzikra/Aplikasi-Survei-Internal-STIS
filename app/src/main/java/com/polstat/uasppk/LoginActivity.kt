package com.polstat.uasppk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var errorMessage: TextView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        errorMessage = findViewById(R.id.errorMessage)

        apiService = ApiClient.instance

        loginButton.setOnClickListener { loginUser() }

        registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.text = "Please enter email and password"
            errorMessage.visibility = View.VISIBLE
            return
        }

        val request = AuthRequest(email, password)
        val call = apiService.login(request)

        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        saveToken(authResponse.accessToken)

                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    errorMessage.text = "Invalid email or password"
                    errorMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                errorMessage.text = "Network error, please try again!"
                errorMessage.visibility = View.VISIBLE
            }
        })
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", token)
        editor.apply()
    }
}