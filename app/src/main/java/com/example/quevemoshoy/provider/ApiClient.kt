package com.example.quevemoshoy.provider

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * `ApiClient` es un objeto que proporciona un cliente Retrofit para hacer llamadas a la API de películas.
 *
 * Este objeto configura y construye un cliente Retrofit con la URL base de la API de películas y un convertidor Gson.
 */
object ApiClient {
    private const val BASE_URL = "https://api.themoviedb.org"
    const val API_KEY = "2bb0ba9a57e9cdae9dd4f957fd27140a"

    /**
     * El cliente Retrofit para hacer llamadas a la API de películas.
     */
    val retrofit: Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

}
