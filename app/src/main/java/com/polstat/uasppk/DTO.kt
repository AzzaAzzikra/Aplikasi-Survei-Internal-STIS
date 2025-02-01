package com.polstat.uasppk

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
