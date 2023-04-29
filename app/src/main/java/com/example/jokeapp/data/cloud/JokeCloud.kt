package com.example.jokeapp.data.cloud

import com.example.jokeapp.data.Joke
import com.google.gson.annotations.SerializedName

data class JokeCloud(
    @SerializedName("type")
    private val type: String,
    @SerializedName("setup")
    private val mainText: String,
    @SerializedName("punchline")
    private val punchline: String,
    @SerializedName("id")
    private val id: Int
) : Joke {

    override suspend fun <T> map(mapper: Joke.Mapper<T>): T = mapper.map(type, mainText, punchline, id)
}

