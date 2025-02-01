package com.polstat.uasppk

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Authentication
    @POST("/api/auth/login")
    fun login(@Body request: AuthRequest): Call<AuthResponse>

    @POST("/api/auth/register")
    fun register(@Body request: UserDTO): Call<Map<String, String>>

    @GET("/api/auth/accounts")
    fun getAllAccounts(): Call<List<UserDTO>>

    @PUT("/api/auth/update-email-username")
    fun updateEmailAndUsername(@Body request: UserDTO): Call<UserDTO>

    @PUT("/api/auth/update-password")
    fun updatePassword(@Body request: UserDTO): Call<UserDTO>

    @DELETE("/api/auth/deleteAccount")
    fun deleteAccount(): Call<String>

    // Surveys
    @POST("/api/surveys")
    fun createSurvey(@Body survey: Survey): Call<Survey>

    @GET("/api/surveys")
    fun getAllSurveys(): Call<List<Survey>>

    @GET("/api/surveys/{id}")
    fun getSurveyById(@Path("id") id: Long): Call<Survey>

    @PUT("/api/surveys/{id}")
    fun updateSurvey(@Path("id") id: Long, @Body survey: Survey): Call<Survey>

    @DELETE("/api/surveys/{id}")
    fun deleteSurvey(@Path("id") id: Long): Call<Void>

    // Questions
    @GET("/api/surveys/{surveyId}/questions")
    fun getQuestionsBySurveyId(@Path("surveyId") surveyId: Long): Call<List<QuestionDTO>>

    @POST("/api/surveys/{surveyId}/questions")
    fun addQuestionToSurvey(@Path("surveyId") surveyId: Long, @Body question: Question): Call<Question>

    @PUT("/api/surveys/{surveyId}/questions/{questionId}")
    fun updateQuestion(@Path("surveyId") surveyId: Long, @Path("questionId") questionId: Long, @Body question: Question): Call<Question>

    @DELETE("/api/surveys/{surveyId}/questions/{questionId}")
    fun deleteQuestion(@Path("surveyId") surveyId: Long, @Path("questionId") questionId: Long): Call<Void>

    @POST("/api/surveys/{surveyId}/responses")
    fun submitResponse(@Path("surveyId") surveyId: Long, @Body responseDTO: ResponseDTO): Call<ResponseDTO>
}
