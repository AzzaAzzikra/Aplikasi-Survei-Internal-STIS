package com.polstat.uasppk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var errorMessage: TextView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameInput = findViewById(R.id.usernameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        registerButton = findViewById(R.id.registerButton)
        errorMessage = findViewById(R.id.errorMessage)

        apiService = ApiClient.instance

        registerButton.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val username = usernameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage.text = "Please fill in all fields"
            errorMessage.visibility = View.VISIBLE
            return
        }

        if (password != confirmPassword) {
            errorMessage.text = "Passwords do not match"
            errorMessage.visibility = View.VISIBLE
            return
        }

        val request = UserDTO(username = username, email = email, password = password)
        val call = apiService.register(request)

        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    errorMessage.text = "Registration failed"
                    errorMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("RegisterActivity", "Network error: ${t.message}")
                errorMessage.text = "Network error, please try again!"
                errorMessage.visibility = View.VISIBLE
            }
        })
    }
}
