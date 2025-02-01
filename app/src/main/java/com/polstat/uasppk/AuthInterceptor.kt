package com.polstat.uasppk

import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("ACCESS_TOKEN", null)

        val request = chain.request()

        if (token != null && !request.url().toString().contains("login") && !request.url().toString().contains("register")) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }
}