package com.polstat.uasppk

import com.google.gson.annotations.SerializedName

data class ResponseDTO(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("userEmail") val userEmail: String,
    @SerializedName("selectedChoices") val selectedChoices: List<String>?,
    @SerializedName("openEndedAnswer") val openEndedAnswer: String?,
    @SerializedName("question") val question: Question
)