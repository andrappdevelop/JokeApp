package com.example.jokeapp.data.cloud

import com.example.jokeapp.data.Error
import com.example.jokeapp.data.cache.DataSource
import com.example.jokeapp.data.cache.JokeResult
import com.example.jokeapp.presentation.ManageResources
import java.net.UnknownHostException

interface CloudDataSource : DataSource {

    class Base(
        private val jokeService: JokeService,
        private val manageResources: ManageResources
    ) : CloudDataSource {

        private val noConnection by lazy {
            Error.NoConnection(manageResources)
        }

        private val serviceError by lazy {
            Error.ServiceUnavailable(manageResources)
        }

        override fun fetch(): JokeResult = try {
            val response = jokeService.joke().execute()
            JokeResult.Success(response.body()!!, false)
        } catch (e: Exception) {
            JokeResult.Failure(
                if (e is UnknownHostException || e is java.net.ConnectException)
                    noConnection
                else
                    serviceError
            )
        }
    }
}