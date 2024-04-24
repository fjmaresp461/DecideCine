package com.example.quevemoshoy.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("providers") val providers: List<Providers>
): Serializable
data class SimpleMovie(
    val id: Int,
    val title: String
)

data class MovieResponse(
    @SerializedName("results") val movies: List<Movie>
) : Serializable
data class Genre(
    @SerializedName("name")
    val name: String
) : Serializable
data class Providers(
    @SerializedName("logo_path") val logoPath: String,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String
) : Serializable

data class ProvidersResponse(
    @SerializedName("results") val results: Results
) : Serializable

data class Results(
    @SerializedName("ES") val es: CountryProviders
) : Serializable

data class CountryProviders(
    @SerializedName("flatrate") val providers: List<Providers>
) : Serializable



