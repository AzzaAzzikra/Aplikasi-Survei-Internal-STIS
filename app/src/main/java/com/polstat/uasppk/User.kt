package com.polstat.uasppk

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long? = null,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)