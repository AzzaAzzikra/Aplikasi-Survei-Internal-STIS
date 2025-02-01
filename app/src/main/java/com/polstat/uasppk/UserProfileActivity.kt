package com.polstat.uasppk

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button
    private lateinit var deleteAccountButton: Button
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        saveButton = findViewById(R.id.saveButton)
        logoutButton = findViewById(R.id.logoutButton)
        deleteAccountButton = findViewById(R.id.deleteAccountButton)

        apiService = ApiClient.instance
        sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)

        loadUserDetails()

        saveButton.setOnClickListener { saveUserDetails() }
        logoutButton.setOnClickListener { logout() }
        deleteAccountButton.setOnClickListener { confirmDeleteAccount() }
    }

    fun decodeJwtToken(token: String): Claims? {
        return try {
            Jwts.parser()
                .setSigningKey("DcNQhIzHJwSk7UvaB1oGUOPVepPkxQb3nJ1yWYeM9vChCFbxmChN85alI4/Xr1r8AxAi2kYkh5mtMEqfsHAu6g==")
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
    }

    private fun loadUserDetails() {
        val username = "AzzaAzzikra"
        val email = "222212894@stis.ac.id"
        val password = "password12345"

        usernameEditText.setText(username)
        emailEditText.setText(email)
        passwordEditText.setText(password)
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUserDetails() {
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val id: Long = 13

        val userDTO = UserDTO(id, username, email, password)

        apiService.updateEmailAndUsername(userDTO).enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UserProfileActivity, "Email and Username updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Go back to the previous activity
                } else {
                    Toast.makeText(this@UserProfileActivity, "Failed to update Email and Username", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                Toast.makeText(this@UserProfileActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        if (password.isNotEmpty()) {
            apiService.updatePassword(userDTO).enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UserProfileActivity, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        finish() // Go back to the previous activity
                    } else {
                        Toast.makeText(this@UserProfileActivity, "Failed to update Password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Toast.makeText(this@UserProfileActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun logout() {
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Yes") { _, _ -> deleteAccount() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteAccount() {
        apiService.deleteAccount().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UserProfileActivity, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                    logout()
                } else {
                    Toast.makeText(this@UserProfileActivity, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@UserProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}