package com.example.jokeapp

import junit.framework.TestCase.assertEquals
import org.junit.Test


class MainViewModelTest {

    @Test
    fun test_success() {
        val model = FakeModel()
        model.returnSuccess = true
        val viewModel = MainViewModel(model)
        viewModel.init(object : TextCallback {
            override fun provideText(text: String) {
                assertEquals("fake joke 1" + "\n" + "punchline", text)
            }
        })
        viewModel.getJoke()
    }

    @Test
    fun test_error() {
        val model = FakeModel()
        model.returnSuccess = false
        val viewModel = MainViewModel(model)
        viewModel.init(object : TextCallback {
            override fun provideText(text: String) {
                assertEquals("fake error message", text)
            }
        })
        viewModel.getJoke()
    }
}

private class FakeModel : Model<Joke, Error> {

    var returnSuccess = true

    private var callback: ResultCallback<Joke, Error>? = null
    override fun fetch() {
        if (returnSuccess) {
            callback?.provideSuccess(Joke("fake joke 1", "punchline"))
        } else {
            callback?.provideError(FakeError())
        }
    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<Joke, Error>) {
        callback = resultCallback
    }
}

private class FakeError : Error {
    override fun message(): String {
        return "fake error message"
    }
}