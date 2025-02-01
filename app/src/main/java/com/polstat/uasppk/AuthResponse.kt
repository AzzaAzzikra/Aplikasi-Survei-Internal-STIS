package com.polstat.uasppk

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("email") val email: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("redirectURL") val redirectURL: String
)