package com.makazemi.bitcoinwidget.repository


import com.makazemi.bitcoinwidget.model.BitcoinModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryFake() : MainRepository {

    private var shouldReturnError = false


    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun getConvertedCurrencyToBitcoin(): Flow<DataState<BitcoinModel>> = flow {
        if (shouldReturnError) {
            emit(buildError(message = "SERVER ERROR"))
            return@flow
        }
        emit(DataState.loading(true))
        emit(DataState.data(BitcoinModel(value = 2.2f)))
    }

}