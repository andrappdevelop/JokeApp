package com.example.jokeapp.data

import androidx.annotation.StringRes
import com.example.jokeapp.presentation.ManageResources
import com.example.jokeapp.R

interface Error {

    fun message(): String

    abstract class Abstract(
        private val manageResources: ManageResources, @StringRes private val messageId: Int
    ) : Error {

        override fun message() = manageResources.string(messageId)
    }

    class NoConnection(manageResources: ManageResources) :
        Abstract(manageResources, R.string.no_connection_message)

    class ServiceUnavailable(manageResources: ManageResources) :
        Abstract(manageResources, R.string.service_unavailable_message)

    class NoFavoriteJoke(manageResources: ManageResources) :
            Abstract(manageResources, R.string.no_favorite_jokes)
}