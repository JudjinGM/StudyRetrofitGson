package com.example.studyretrofitgson

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TranslationApi {
    @POST("/translate/yoda.json")
    fun translateToYoda(@Body request: TranslationRequest): Call<TranslationResponse>

    @POST("/translate/morse.json")
    fun translateToMorse(@Body request: TranslationRequest): Call<TranslationResponse>
}