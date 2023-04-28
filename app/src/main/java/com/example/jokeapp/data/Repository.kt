package com.example.jokeapp.data

import com.example.jokeapp.data.cache.JokeResult
import com.example.jokeapp.presentation.JokeUi

interface Repository<S, E> {

    fun fetch(): JokeResult
    fun changeJokeStatus(): JokeUi
    fun chooseFavorites(favorites: Boolean)
}