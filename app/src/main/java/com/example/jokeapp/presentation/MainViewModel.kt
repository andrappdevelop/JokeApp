package com.example.jokeapp.presentation

import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokeapp.data.Error
import com.example.jokeapp.data.Joke
import com.example.jokeapp.data.Repository
import com.example.jokeapp.data.ToBaseUi
import com.example.jokeapp.data.ToFavoriteUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val communication: JokeCommunication,
    private val repository: Repository<JokeUi, Error>,
    private val toFavorite: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUi> = ToBaseUi(),
    dispatcherList: DispatcherList = DispatcherList.Base()
) : BaseViewModel(dispatcherList), Observe<JokeUi> {

    private val blockUi: suspend (JokeUi) -> Unit = {
        communication.map(it)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<JokeUi>) {
        communication.observe(owner, observer)
    }

    fun getJoke() {
        handle({
            val result = repository.fetch()
            if (result.isSuccessful())
                result.map(if (result.toFavorite()) toFavorite else toBaseUi)
            else
                JokeUi.Failed(result.errorMessage())
        }, blockUi)
    }

    fun chooseFavorite(favorites: Boolean) {
        repository.chooseFavorites(favorites)
    }

    fun changeJokeStatus() =
        handle({
            repository.changeJokeStatus()
        }, blockUi)
}

interface Observe<T : Any> {
    fun observe(owner: LifecycleOwner, observer: Observer<T>) = Unit
}

interface Communication<T : Any> : Observe<T> {

    fun map(data: T)

    abstract class Abstract<T : Any>(
        private val liveData: MutableLiveData<T> = MutableLiveData()
    ) : Communication<T> {

        override fun map(data: T) {
            liveData.value = data
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
            liveData.observe(owner, observer)
        }
    }
}

interface JokeCommunication : Communication<JokeUi> {
    class Base() : Communication.Abstract<JokeUi>(), JokeCommunication
}

interface JokeUiCallback {

    fun provideText(text: String)

    fun provideIconResId(@DrawableRes iconResId: Int)
}

interface DispatcherList {

    fun io(): CoroutineDispatcher
    fun ui(): CoroutineDispatcher

    class Base() : DispatcherList {

        override fun io(): CoroutineDispatcher = Dispatchers.IO

        override fun ui(): CoroutineDispatcher = Dispatchers.Main
    }
}

abstract class BaseViewModel(
    private val dispatcherList: DispatcherList
) : ViewModel() {

    fun <T> handle(
        blockIo: suspend () -> T,
        blockUi: suspend (T) -> Unit
    ) = viewModelScope.launch(dispatcherList.io()) {
        val result = blockIo.invoke()
        withContext(dispatcherList.ui()) {
            blockUi.invoke(result)
        }
    }
}