package com.example.jokeapp.data.cache

import com.example.jokeapp.data.Error
import com.example.jokeapp.data.Joke
import com.example.jokeapp.data.ToBaseUi
import com.example.jokeapp.data.ToCache
import com.example.jokeapp.data.ToFavoriteUi
import com.example.jokeapp.presentation.JokeUi
import com.example.jokeapp.presentation.ManageResources
import io.realm.Realm

interface CacheDataSource : DataSource {

    fun addOrRemove(id: Int, joke: Joke): JokeUi

    class Base(
        private val realm: ProvideRealm,
        manageResources: ManageResources,
        private val error: Error = Error.NoFavoriteJoke(manageResources),
        private val mapper: Joke.Mapper<JokeCache> = ToCache(),
        private val toFavorite: Joke.Mapper<JokeUi> = ToFavoriteUi(),
        private val toBaseUi: Joke.Mapper<JokeUi> = ToBaseUi()
    ) : CacheDataSource {

        override fun addOrRemove(id: Int, joke: Joke): JokeUi {
            realm.provideRealm().let {
                val jokeCached = it.where(JokeCache::class.java).equalTo("id", id).findFirst()
                if (jokeCached == null) {
                    it.executeTransaction { realm ->
                        val jokeCache = joke.map(mapper)
                        realm.insert(jokeCache)
                    }
                    return joke.map(toFavorite)
                } else {
                    it.executeTransaction {
                        jokeCached.deleteFromRealm()
                    }
                    return joke.map(toBaseUi)
                }
            }
        }

        override fun fetch(jokeCallback: JokeCallback) {
            realm.provideRealm().let {
                val jokes = it.where(JokeCache::class.java).findAll()
                if (jokes.isEmpty()) {
                    jokeCallback.provideError(error)
                } else {
                    val jokeCached = jokes.random()
                    jokeCallback.provideJoke(it.copyFromRealm(jokeCached))
                }
            }
        }
    }

    class Fake(manageResources: ManageResources) : CacheDataSource {

        private val error by lazy {
            Error.NoFavoriteJoke(manageResources)
        }

        private val map = mutableMapOf<Int, Joke>()

        override fun addOrRemove(id: Int, joke: Joke): JokeUi {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.map(ToBaseUi())
            } else {
                map[id] = joke
                joke.map(ToFavoriteUi())
            }
        }

        private var count = 0

        override fun fetch(jokeCallback: JokeCallback) {
            if (map.isEmpty())
                jokeCallback.provideError(error)
            else {
                count++
                if (count == map.size) count = 0
                jokeCallback.provideJoke(map.toList()[count].second)
            }
        }
    }
}

interface DataSource {
    fun fetch(jokeCallback: JokeCallback)
}

interface JokeCallback : ProvideError {
    fun provideJoke(joke: Joke)
}

interface ProvideError {
    fun provideError(error: Error)
}

interface ProvideRealm {
    fun provideRealm(): Realm
}