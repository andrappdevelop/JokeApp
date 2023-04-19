package com.example.jokeapp

class MainViewModel(
    private val model: Model<Joke, Error>
) {

    private var textCallback: TextCallback = TextCallback.Empty()

    fun getJoke() {
        model.fetch()
    }

    fun init(textCallback: TextCallback) {
        this.textCallback = textCallback
        model.init(object : ResultCallback<Joke, Error> {
            override fun provideSuccess(data: Joke) = textCallback.provideText(data.toUi())
            override fun provideError(error: Error) = textCallback.provideText(error.message())
        })
    }

    fun clear() {
        textCallback = TextCallback.Empty()
        model.clear()
    }
}

interface TextCallback {

    fun provideText(text: String)

    class Empty : TextCallback {
        override fun provideText(text: String) = Unit
    }
}