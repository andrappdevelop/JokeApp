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
            val realm = realm.provideRealm()
            val jokeCached = realm.where(JokeCache::class.java).equalTo("id", id).findFirst()
            return if (jokeCached == null) {
                realm.executeTransaction {
                    val jokeCache = joke.map(mapper)
                    it.insert(jokeCache)
                }
                joke.map(toFavorite)
            } else {
                realm.executeTransaction {
                    jokeCached.deleteFromRealm()
                }
                joke.map(toBaseUi)
            }
        }

        override fun fetch(): JokeResult {
            val realm = realm.provideRealm()
            val jokes = realm.where(JokeCache::class.java).findAll()
            return if (jokes.isEmpty())
                JokeResult.Failure(error)
            else
                JokeResult.Success(realm.copyFromRealm(jokes.random()), true)
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

        override fun fetch(): JokeResult {
            return if (map.isEmpty())
                JokeResult.Failure(error)
            else {
                count++
                if (count == map.size) count = 0
                JokeResult.Success(map.toList()[count].second, true)
            }
        }
    }
}

interface DataSource {
    fun fetch(): JokeResult
}

interface JokeResult : Joke {

    fun toFavorite(): Boolean
    fun isSuccessful(): Boolean
    fun errorMessage(): String

    class Success(
        private val joke: Joke,
        private val toFavorite: Boolean
    ) : JokeResult {
        override fun toFavorite(): Boolean = toFavorite
        override fun isSuccessful(): Boolean = true
        override fun errorMessage(): String = ""
        override fun <T> map(mapper: Joke.Mapper<T>): T = joke.map(mapper)
    }

    class Failure(private val error: Error) : JokeResult {
        override fun toFavorite(): Boolean = false
        override fun isSuccessful(): Boolean = false
        override fun errorMessage(): String = error.message()
        override fun <T> map(mapper: Joke.Mapper<T>): T = throw IllegalStateException()
    }
}

interface ProvideRealm {
    fun provideRealm(): Realm
}