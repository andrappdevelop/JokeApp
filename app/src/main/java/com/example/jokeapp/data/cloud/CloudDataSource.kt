package com.example.jokeapp.data.cloud

import com.example.jokeapp.data.Error
import com.example.jokeapp.data.cache.DataSource
import com.example.jokeapp.data.cache.JokeCallback
import com.example.jokeapp.presentation.ManageResources
import retrofit2.Call
import retrofit2.Response
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

        override fun fetch(jokeCallback: JokeCallback) {
            jokeService.joke().enqueue(object : retrofit2.Callback<JokeCloud> {
                override fun onResponse(call: Call<JokeCloud>, response: Response<JokeCloud>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null)
                            jokeCallback.provideError(serviceError)
                        else
                            jokeCallback.provideJoke(body)
                    } else
                        jokeCallback.provideError(serviceError)
                }

                override fun onFailure(call: Call<JokeCloud>, t: Throwable) {
                    jokeCallback.provideError(
                        if (t is UnknownHostException || t is java.net.ConnectException)
                            noConnection
                        else
                            serviceError
                    )
                }
            })
        }
    }
}