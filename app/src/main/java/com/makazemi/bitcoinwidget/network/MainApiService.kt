package com.makazemi.bitcoinwidget.network

import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface MainApiService {

    @GET("tobtc")
    suspend fun getConvertedCurrencyToBitcoin(
        @Query("currency") currency: String="USD",
        @Query("value") value:Float=1f
    ): GenericApiResponse<Float>
}