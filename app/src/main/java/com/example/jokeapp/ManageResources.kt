package com.example.jokeapp

import android.content.Context
import androidx.annotation.StringRes

interface ManageResources {

    fun string(@StringRes resourceId: Int): String

    class Base(private val context: Context) : ManageResources {

        override fun string(resourceId: Int): String = context.getString(resourceId)
    }
}