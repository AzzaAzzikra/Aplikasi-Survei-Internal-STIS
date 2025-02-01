package com.polstat.uasppk

data class Question(
    val id: Long,
    val text: String,
    val questionType: QuestionType,
    val surveyId: Long,
    val choices: List<String>,
    val responses: List<Response>
)