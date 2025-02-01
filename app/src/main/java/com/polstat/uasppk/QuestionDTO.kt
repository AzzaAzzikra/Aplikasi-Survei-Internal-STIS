package com.polstat.uasppk

import com.google.gson.annotations.SerializedName

data class QuestionDTO(
    @SerializedName("id") val id: Long,
    @SerializedName("text") val text: String,
    @SerializedName("choices") val choices: List<String>,
    @SerializedName("responses") val responses: List<ResponseDTO>,
    @SerializedName("questionType") val type: QuestionType
)