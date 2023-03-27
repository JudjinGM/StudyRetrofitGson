package com.example.studyretrofitgson

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val forecaBaseUrl = "https://pfa.foreca.com"

    private var token = ""

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(forecaBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val forecaService = retrofit.create(ForecaApi::class.java)

    private val locations = ArrayList<ForecastLocation>()
    private val adapter = LocationsAdapter {
        showWeather(it)
    }

    private lateinit var searchButton: Button
    private lateinit var queryInput: EditText
    private lateinit var placeHolderMessage: TextView
    private lateinit var locationsList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchButton = findViewById(R.id.searchButton)
        queryInput = findViewById(R.id.queryInput)
        placeHolderMessage = findViewById(R.id.placeholderMessage)
        locationsList = findViewById(R.id.locations)

        adapter.locations = locations

        locationsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        locationsList.adapter = adapter

        searchButton.setOnClickListener {
            if (queryInput.text.isNotEmpty()) {
                if (token.isEmpty()) {
                    authenticate()
                } else {
                    search()
                }
            }
        }
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeHolderMessage.visibility = View.VISIBLE
            locations.clear()
            adapter.notifyDataSetChanged()
            placeHolderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            } else {
                placeHolderMessage.visibility = View.GONE
            }
        }
    }

    private fun authenticate() {
        forecaService.authenticate(ForecaAuthRequest("judjingm", "8c3BJ17KNdAe"))
            .enqueue(object : Callback<ForecaAuthResponse> {
                override fun onResponse(
                    call: Call<ForecaAuthResponse>,
                    response: Response<ForecaAuthResponse>
                ) {
                    if (response.code() == 200) {
                        token = response.body()?.token.toString()
                        search()
                    } else {
                        showMessage(
                            getString(R.string.something_went_wrong),
                            response.code().toString()
                        )
                    }
                }

                override fun onFailure(call: Call<ForecaAuthResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                }
            })
    }

    private fun search() {
        forecaService.getLocations("Bearer $token", queryInput.text.toString())
            .enqueue(object : Callback<LocationResponse> {
                override fun onResponse(
                    call: Call<LocationResponse>,
                    response: Response<LocationResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.locations?.isNotEmpty() == true) {
                                locations.clear()
                                locations.addAll(response.body()?.locations!!)
                                adapter.notifyDataSetChanged()
                                showMessage("", "")
                            } else {
                                showMessage(getString(R.string.nothing_found), "")
                            }
                        }
                        401 -> authenticate()
                        else -> showMessage(
                            getString(R.string.something_went_wrong),
                            response.code().toString()
                        )
                    }

                }

                override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                }
            })
    }

    private fun showWeather(locations: ForecastLocation) {
        forecaService.getForecast("Bearer $token", locations.id)
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>
                ) {
                    if (response.body()?.current != null) {
                        val message =
                            "${locations.name} t: ${response.body()?.current?.temperature}\nFeels like: ${response.body()?.current?.feelsLikeTemp}"
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()

                }
            })
    }


}