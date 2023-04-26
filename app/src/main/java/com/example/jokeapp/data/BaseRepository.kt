package com.example.jokeapp.data

import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.JokeCacheCallback
import com.example.jokeapp.data.cloud.CloudDataSource
import com.example.jokeapp.data.cloud.JokeCloud
import com.example.jokeapp.data.cloud.JokeCloudCallback
import com.example.jokeapp.presentation.JokeUi

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource
) : Repository<JokeUi, Error> {

    private var callback: ResultCallback<JokeUi, Error>? = null

    private var jokeCloudTemporary: JokeCloud? = null

    override fun fetch() {
        if (getJokeFromCache)
            cacheDataSource.fetch(object : JokeCacheCallback {
                override fun provideJoke(joke: JokeCloud) {
                    callback?.provideSuccess(joke.toFavoriteUi())
                }

                override fun provideError(error: Error) {
                    callback?.provideError(error)
                }
            })
        else
            cloudDataSource.fetch(object : JokeCloudCallback {
                override fun provideJokeCloud(jokeCloud: JokeCloud) {
                    jokeCloudTemporary = jokeCloud
                    callback?.provideSuccess(jokeCloud.toUi())
                }

                override fun provideError(error: Error) {
                    jokeCloudTemporary = null
                    callback?.provideError(error)
                }
            })
    }

    override fun clear() {
        callback = null
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, Error>) {
        jokeCloudTemporary?.let {
            resultCallback.provideSuccess(it.change(cacheDataSource))
        }
    }

    override fun init(resultCallback: ResultCallback<JokeUi, Error>) {
        callback = resultCallback
    }

    private var getJokeFromCache = false

    override fun chooseFavorites(favorites: Boolean) {
        getJokeFromCache = favorites
    }
}