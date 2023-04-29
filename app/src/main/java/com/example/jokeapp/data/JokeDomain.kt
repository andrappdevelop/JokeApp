package com.example.jokeapp.data

import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.JokeCache
import com.example.jokeapp.presentation.JokeUi

interface Joke {
    suspend fun <T> map(mapper: Mapper<T>): T

    interface Mapper<T> {
        suspend fun map(
            type: String,
            mainText: String,
            punchline: String,
            id: Int
        ): T
    }
}

data class JokeDomain(
    private val type: String,
    private val mainText: String,
    private val punchline: String,
    private val id: Int
) : Joke {

    override suspend fun <T> map(mapper: Joke.Mapper<T>): T =
        mapper.map(type, mainText, punchline, id)
}

class ToCache : Joke.Mapper<JokeCache> {
    override suspend fun map(
        type: String, mainText: String, punchline: String, id: Int
    ): JokeCache {
        val jokeCache = JokeCache()
        jokeCache.id = id
        jokeCache.text = mainText
        jokeCache.punchline = punchline
        jokeCache.type = type
        return jokeCache
    }
}

class ToBaseUi : Joke.Mapper<JokeUi> {

    override suspend fun map(type: String, mainText: String, punchline: String, id: Int): JokeUi {
        return JokeUi.Base(mainText, punchline)
    }
}

class ToFavoriteUi : Joke.Mapper<JokeUi> {

    override suspend fun map(type: String, mainText: String, punchline: String, id: Int): JokeUi {
        return JokeUi.Favorite(mainText, punchline)
    }
}

class Change(
    private val cacheDataSource: CacheDataSource,
    private val toDomain: Joke.Mapper<JokeDomain> = ToDomain()
) : Joke.Mapper<JokeUi> {

    override suspend fun map(type: String, mainText: String, punchline: String, id: Int): JokeUi {
        return cacheDataSource.addOrRemove(id, toDomain.map(type, mainText, punchline, id))
    }
}

class ToDomain : Joke.Mapper<JokeDomain> {

    override suspend fun map(
        type: String,
        mainText: String,
        punchline: String,
        id: Int
    ): JokeDomain {
        return JokeDomain(type, mainText, punchline, id)
    }
}