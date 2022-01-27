package com.makazemi.bitcoinwidget.repository


import com.makazemi.bitcoinwidget.network.ApiErrorResponse
import com.makazemi.bitcoinwidget.network.ErrorBody
import com.makazemi.bitcoinwidget.network.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.makazemi.bitcoinwidget.network.ErrorHandling.Companion.ERROR_UNKNOWN
import com.makazemi.bitcoinwidget.network.ErrorHandling.Companion.NETWORK_ERROR
import com.makazemi.bitcoinwidget.network.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.makazemi.bitcoinwidget.network.GenericApiResponse
import com.makazemi.bitcoinwidget.util.Constant.NETWORK_TIMEOUT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    isNetworkAvailable: Boolean,
    apiCall: suspend () -> GenericApiResponse<T>

): GenericApiResponse<T> {
    if (!isNetworkAvailable) {
        return ApiErrorResponse(ErrorBody(message = UNABLE_TODO_OPERATION_WO_INTERNET))
    }
    return withContext(Dispatchers.IO) {
        try {
            withTimeout(NETWORK_TIMEOUT) {
                apiCall.invoke()
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = "408" // timeout error code
                    ApiErrorResponse(ErrorBody(code, ERROR_CHECK_NETWORK_CONNECTION))
                }
                is IOException -> {
                    ApiErrorResponse(ErrorBody(message = NETWORK_ERROR))
                }
                is HttpException -> {
                    val code = throwable.code().toString()
                    val errorResponse =
                        ErrorBody.convertToObject(convertErrorBody(throwable)).error
                    ApiErrorResponse(ErrorBody(code, errorResponse))
                }
                else -> {
                    ApiErrorResponse(ErrorBody(message = ERROR_UNKNOWN))

                }
            }
        }
    }
}


fun <ResultType> buildError(
    message: String,
    code: String? = null
): DataState<ResultType> {
    return DataState.error(
        ErrorBody(code, message)
    )


}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ERROR_UNKNOWN
    }
}























