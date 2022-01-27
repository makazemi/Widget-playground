package com.makazemi.bitcoinwidget.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.makazemi.bitcoinwidget.model.BitcoinModel
import com.makazemi.bitcoinwidget.repository.RepositoryFake
import com.makazemi.bitcoinwidget.ui.MainViewModel
import com.makazemi.bitcoinwidget.util.MainCoroutineRule
import com.makazemi.bitcoinwidget.util.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    private lateinit var repository: RepositoryFake

    private lateinit var viewModel: MainViewModel

    @Before
    fun init() {
        repository = RepositoryFake()
        viewModel = MainViewModel(repository)
    }


    @Test
    fun currencyToBitcoin_successState() {

        assertThat(
            viewModel.response.getOrAwaitValue().loading.isLoading,
            `is`(true)
        )

        assertThat(
            viewModel.response.getOrAwaitValue().loading.isLoading,
            `is`(false)
        )

        assertThat(
            viewModel.response.getOrAwaitValue().data?.peekContent(),
            `is`(BitcoinModel(value = 2.2f))
        )
    }

    @Test
    fun currencyToBitcoin_failState() {
        repository.setReturnError(true)
        assertThat(
            viewModel.response.getOrAwaitValue().loading.isLoading,
            `is`(false)
        )
        assertThat(viewModel.response.getOrAwaitValue().data?.peekContent(), nullValue())
        assertThat(
            viewModel.response.getOrAwaitValue().error?.peekContent()?.message,
            `is`("SERVER ERROR")
        )

    }
}