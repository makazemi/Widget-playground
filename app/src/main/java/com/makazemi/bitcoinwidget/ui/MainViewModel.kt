package com.makazemi.bitcoinwidget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.makazemi.bitcoinwidget.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    val response = repository.getConvertedCurrencyToBitcoin().asLiveData()
}