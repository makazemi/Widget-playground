package com.makazemi.bitcoinwidget.repository

import com.makazemi.bitcoinwidget.model.BitcoinModel
import com.makazemi.bitcoinwidget.network.ApiResponseHandler
import com.makazemi.bitcoinwidget.network.CheckConnection
import com.makazemi.bitcoinwidget.network.MainApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Singleton

@Singleton
class MainRepositoryImp constructor(
    private val apiService: MainApiService,
    private val checkConnection: CheckConnection
) : MainRepository {
    override fun getConvertedCurrencyToBitcoin(): Flow<DataState<BitcoinModel>> =
        flow {
            emit(DataState.loading(true))
            val apiResult = safeApiCall(
                checkConnection.isConnectedToTheInternet()
            ) {
                apiService.getConvertedCurrencyToBitcoin()
            }
            emit(
                object : ApiResponseHandler<BitcoinModel, Float>(
                    response = apiResult
                ) {
                    override suspend fun handleSuccess(resultObj: Float): DataState<BitcoinModel> {
                        val value = if (resultObj != 0f) 1 / resultObj else 0f
                        return DataState.data(BitcoinModel(value))
                    }


                }.getResult()
            )
        }
}