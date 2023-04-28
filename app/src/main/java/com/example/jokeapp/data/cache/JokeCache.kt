package com.example.jokeapp.data.cache

import com.example.jokeapp.data.Joke
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class JokeCache : RealmObject(), Joke {
    @PrimaryKey
    var id: Int = -1
    var text: String = ""
    var punchline: String = ""
    var type: String = ""

    override fun <T> map(mapper: Joke.Mapper<T>): T = mapper.map(type, text, punchline, id)
}