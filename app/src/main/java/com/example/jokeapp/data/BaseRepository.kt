package com.example.jokeapp.data

import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.JokeResult
import com.example.jokeapp.data.cloud.CloudDataSource
import com.example.jokeapp.presentation.JokeUi

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
    private val change: Joke.Mapper<JokeUi> = Change(cacheDataSource)
) : Repository<JokeUi, Error> {

    private var jokeTemporary: Joke? = null

    override suspend fun fetch(): JokeResult {
        val jokeResult = if (getJokeFromCache)
            cacheDataSource.fetch()
        else
            cloudDataSource.fetch()
        jokeTemporary = if (jokeResult.isSuccessful())
            jokeResult.map(ToDomain())
        else
            null
        return jokeResult
    }

    override suspend fun changeJokeStatus(): JokeUi = jokeTemporary!!.map(change)

    private var getJokeFromCache = false

    override fun chooseFavorites(favorites: Boolean) {
        getJokeFromCache = favorites
    }
}