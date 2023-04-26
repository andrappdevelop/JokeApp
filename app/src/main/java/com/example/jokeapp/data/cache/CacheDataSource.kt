package com.example.jokeapp.data.cache

import com.example.jokeapp.data.Error
import com.example.jokeapp.data.cloud.JokeCloud
import com.example.jokeapp.presentation.JokeUi
import com.example.jokeapp.presentation.ManageResources

interface CacheDataSource {

    fun addOrRemove(id: Int, joke: JokeCloud): JokeUi
    fun fetch(jokeCacheCallback: JokeCacheCallback)

    class Fake(manageResources: ManageResources) : CacheDataSource {

        private val error by lazy {
            Error.NoFavoriteJoke(manageResources)
        }

        private val map = mutableMapOf<Int, JokeCloud>()

        override fun addOrRemove(id: Int, joke: JokeCloud): JokeUi {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.toUi()
            } else {
                map[id] = joke
                joke.toFavoriteUi()
            }
        }

        private var count = 0

        override fun fetch(jokeCacheCallback: JokeCacheCallback) {
            if (map.isEmpty())
                jokeCacheCallback.provideError(error)
            else {
                count++
                if (count == map.size) count = 0
                jokeCacheCallback.provideJoke(map.toList()[count].second)
            }
        }
    }
}

interface JokeCacheCallback : ProvideError {
    fun provideJoke(joke: JokeCloud)
}

interface ProvideError {
    fun provideError(error: Error)
}