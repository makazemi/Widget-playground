package com.makazemi.bitcoinwidget.repository

import com.makazemi.bitcoinwidget.model.BitcoinModel
import com.makazemi.bitcoinwidget.model.CurrencyModel
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun getConvertedCurrencyToBitcoin(): Flow<DataState<BitcoinModel>>
}