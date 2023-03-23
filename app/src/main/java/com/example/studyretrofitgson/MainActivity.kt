package com.example.studyretrofitgson

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val translateBaseUrl = "https://api.funtranslations.com"




        val retrofitTranslate = Retrofit.Builder().baseUrl(translateBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val translationService = retrofitTranslate.create(TranslationApi::class.java)

        fun yodaTranslate(text: String) {
            translationService
                .translateToYoda(TranslationRequest(text))
                .enqueue(object : Callback<TranslationResponse> {
                    override fun onResponse(
                        call: Call<TranslationResponse>,
                        response: Response<TranslationResponse>
                    ) {
                        Log.d(
                            "TRANSLATION_LOG",
                            "Yoda says: ${response.body()?.contents?.translated}\\n${response.body()?.contents?.translation}"
                        )
                    }

                    override fun onFailure(call: Call<TranslationResponse>, t: Throwable) {
                    }

                })
        }

        fun morseTranslate(text: String) {
            translationService
                .translateToMorse(TranslationRequest(text))
                .enqueue(object : Callback<TranslationResponse> {
                    override fun onResponse(
                        call: Call<TranslationResponse>,
                        response: Response<TranslationResponse>
                    ) {
                        Log.d(
                            "TRANSLATION_LOG",
                            "Morse says: ${response.body()?.contents?.translated}\n${response.body()?.contents?.translation}"
                        )
                    }

                    override fun onFailure(call: Call<TranslationResponse>, t: Throwable) {
                    }

                })
        }

        val testText = "Creating Android applications is very easy and interesting!"
        yodaTranslate(testText)
        morseTranslate(testText)
    }
}