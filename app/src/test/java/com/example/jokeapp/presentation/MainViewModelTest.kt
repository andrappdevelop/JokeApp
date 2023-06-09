package com.example.jokeapp.presentation

import com.example.jokeapp.data.Error
import com.example.jokeapp.data.Joke
import com.example.jokeapp.data.Repository
import com.example.jokeapp.data.cache.JokeResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: MainViewModel
    private lateinit var toFavoriteMapper: FakeMapper
    private lateinit var toBaseMapper: FakeMapper
    private lateinit var jokeUiCallback: FakeJokeUiCallback
    private lateinit var communication: FakeCommunication

    @Before
    fun setUp() {
        repository = FakeRepository()
        toFavoriteMapper = FakeMapper(true)
        toBaseMapper = FakeMapper(false)
        jokeUiCallback = FakeJokeUiCallback()
        communication = FakeCommunication()

        viewModel = MainViewModel(
            communication,
            repository,
            toFavoriteMapper,
            toBaseMapper,
            FakeDispatchers()
        )
    }

    @Test
    fun text_successful_not_favorite() {
        repository.returnFetchJokeResult =
            FakeJokeResult(
                FakeJoke("testType", "fakeText", "testPunchline", 12),
                false,
                true,
                ""
            )

        viewModel.getJoke()
        val expected = FakeJokeUi("fakeText", "testPunchline", 12, false)
        val actual = communication.data

        assertEquals(expected, actual)
    }

    @Test
    fun test_successful_favorite() {
        repository.returnFetchJokeResult =
            FakeJokeResult(
                FakeJoke("testType", "fakeText", "testPunchline", 15),
                true,
                true,
                ""
            )

        viewModel.getJoke()
        val expected = FakeJokeUi("fakeText", "testPunchline", 15, true)
        val actual = communication.data

        assertEquals(expected, actual)
    }

    @Test
    fun test_not_successful() {
        repository.returnFetchJokeResult =
            FakeJokeResult(
                FakeJoke("testType", "fakeText", "testPunchline", 15),
                true,
                false,
                "testErrorMessage"
            )

        viewModel.getJoke()
        val expected = JokeUi.Failed("testErrorMessage")
        val actual = communication.data

        assertEquals(expected, actual)
    }

    @Test
    fun test_change_joke_status() {
        repository.returnChangeJokeStatus =
            FakeJokeUi("testText", "testPunchline", 15, false)
        viewModel.changeJokeStatus()

        val expected = FakeJokeUi("testText", "testPunchline", 15, false)
        val actual = communication.data

        assertEquals(expected, actual)
    }

    @Test
    fun test_choose_favorite() {
        viewModel.chooseFavorite(true)
        assertEquals(true, repository.chooseFavoritesList[0])
        assertEquals(1, repository.chooseFavoritesList.size)

        viewModel.chooseFavorite(false)
        assertEquals(false, repository.chooseFavoritesList[1])
        assertEquals(2, repository.chooseFavoritesList.size)
    }
}

private class FakeJokeUiCallback : JokeUiCallback {

    val provideTextList = mutableListOf<String>()

    override fun provideText(text: String) {
        provideTextList.add(text)
    }

    val provideIconResIdList = mutableListOf<Int>()

    override fun provideIconResId(iconResId: Int) {
        provideIconResIdList.add(iconResId)
    }
}

private class FakeDispatchers : DispatcherList {

    private val dispatcher = TestCoroutineDispatcher()

    override fun io(): CoroutineDispatcher = dispatcher

    override fun ui(): CoroutineDispatcher = dispatcher
}

private class FakeMapper(
    private val toFavorite: Boolean
) : Joke.Mapper<JokeUi> {

    override suspend fun map(type: String, mainText: String, punchline: String, id: Int): JokeUi {
        return FakeJokeUi(mainText, punchline, id, toFavorite)
    }
}

private data class FakeJokeUi(
    private val text: String,
    private val punchline: String,
    private val id: Int,
    private val toFavorite: Boolean
) : JokeUi {

    override fun show(jokeUiCallback: JokeUiCallback) = with(jokeUiCallback) {
        provideText(text + "_" + punchline)
        provideIconResId(if (toFavorite) id + 1 else id)
    }
}

private data class FakeJoke(
    private val type: String,
    private val mainText: String,
    private val punchline: String,
    private val id: Int
) : Joke {
    override suspend fun <T> map(mapper: Joke.Mapper<T>): T {
        return mapper.map(type, mainText, punchline, id)
    }
}

private data class FakeJokeResult(
    val joke: Joke,
    private val toFavorite: Boolean,
    private val successful: Boolean,
    private val errorMessage: String
) : JokeResult {

    override suspend fun <T> map(mapper: Joke.Mapper<T>): T = joke.map(mapper)

    override fun toFavorite(): Boolean = toFavorite

    override fun isSuccessful(): Boolean = successful

    override fun errorMessage(): String = errorMessage
}

private class FakeRepository : Repository<JokeUi, Error> {

    var returnFetchJokeResult: JokeResult? = null

    override suspend fun fetch(): JokeResult {
        return returnFetchJokeResult!!
    }

    var returnChangeJokeStatus: JokeUi? = null

    override suspend fun changeJokeStatus(): JokeUi {
        return returnChangeJokeStatus!!
    }

    var chooseFavoritesList = mutableListOf<Boolean>()

    override fun chooseFavorites(favorites: Boolean) {
        chooseFavoritesList.add(favorites)
    }
}

private class FakeCommunication : JokeCommunication {

    lateinit var data: JokeUi

    override fun map(data: JokeUi) {
        this.data = data
    }
}