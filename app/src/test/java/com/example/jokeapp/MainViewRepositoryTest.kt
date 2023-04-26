package com.example.jokeapp

import com.example.jokeapp.data.Error
import com.example.jokeapp.data.Repository
import com.example.jokeapp.data.ResultCallback
import com.example.jokeapp.presentation.JokeUi
import com.example.jokeapp.presentation.MainViewModel
import com.example.jokeapp.presentation.JokeUiCallback
import junit.framework.TestCase.assertEquals
import org.junit.Test


class MainViewRepositoryTest {

//    @Test
//    fun test_success() {
//        val model = com.example.jokeapp.data.FakeRepository()
//        model.returnSuccess = true
//        val viewModel = MainViewModel(model)
//        viewModel.init(object : JokeUiCallback {
//            override fun provideText(text: String) {
//                assertEquals("fake joke 1" + "\n" + "punchline", text)
//            }
//        })
//        viewModel.getJoke()
//    }
//
//    @Test
//    fun test_error() {
//        val model = com.example.jokeapp.data.FakeRepository()
//        model.returnSuccess = false
//        val viewModel = MainViewModel(model)
//        viewModel.init(object : JokeUiCallback {
//            override fun provideText(text: String) {
//                assertEquals("fake error message", text)
//            }
//        })
//        viewModel.getJoke()
//    }
//}

//private class FakeRepository : Repository<JokeUi, Error> {
//
//    var returnSuccess = true
//
//    private var callback: ResultCallback<JokeUi, Error>? = null
//    override fun fetch() {
//        if (returnSuccess) {
//            callback?.provideSuccess(JokeUi("fake joke 1", "punchline"))
//        } else {
//            callback?.provideError(FakeError())
//        }
//    }
//
//    override fun clear() {
//        callback = null
//    }
//
//    override fun init(resultCallback: ResultCallback<JokeUi, Error>) {
//        callback = resultCallback
//    }
}

private class FakeError : Error {
    override fun message(): String {
        return "fake error message"
    }
}