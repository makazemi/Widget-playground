package com.makazemi.bitcoinwidget.repository

import com.makazemi.bitcoinwidget.network.ErrorBody


data class DataState<T>(
    var error: Event<ErrorBody>? = null,
    var loading: Loading = Loading(false),
    var data: Event<T>? = null
) {

    companion object {

        fun <T> error(
            message: ErrorBody
        ): DataState<T> {
            return DataState(
                error = Event(message),
                loading = Loading(false),
                data = null
            )
        }

        fun <T> loading(
            isLoading: Boolean,
            cachedData: T? = null
        ): DataState<T> {
            return DataState(
                error = null,
                loading = Loading(isLoading),
                data = Event.dataEvent(cachedData)
            )
        }

        fun <T> data(
            data: T? = null
        ): DataState<T> {
            return DataState(
                error = null,
                loading = Loading(false),
                data = Event.dataEvent(data)
            )
        }
    }

    override fun toString(): String {
        return "isloading=${loading.isLoading}, error=$error"
    }
}

enum class TypeError {
    DIALOG, TOAST, SNACKBAR
}