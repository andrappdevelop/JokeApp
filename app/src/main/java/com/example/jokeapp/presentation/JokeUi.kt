package com.example.jokeapp.presentation

import androidx.annotation.DrawableRes
import com.example.jokeapp.R

abstract class JokeUi(
    private val text: String,
    private val punchline: String,
    @DrawableRes private val iconResId: Int
) {

    fun show(jokeUiCallback: JokeUiCallback) = with(jokeUiCallback) {
        provideText("$text\n$punchline")
        provideIconResId(iconResId)
    }

    class Base(text: String, punchline: String) :
        JokeUi(text, punchline, R.drawable.ic_favorite_empty_48)

    class Favorite(text: String, punchline: String) :
        JokeUi(text, punchline, R.drawable.ic_favorite_filled_48)

    class Failed(text: String) : JokeUi(text, "", 0)
}