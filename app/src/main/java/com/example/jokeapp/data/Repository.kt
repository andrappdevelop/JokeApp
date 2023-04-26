package com.example.jokeapp.data

import com.example.jokeapp.presentation.JokeUi

interface Repository<S, E> {

    fun fetch()
    fun clear()
    fun init(resultCallback: ResultCallback<S, E>)
    fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, Error>)
    fun chooseFavorites(favorites: Boolean)
}

interface ResultCallback<S, E> {

    fun provideSuccess(data: S)

    fun provideError(error: E)
}