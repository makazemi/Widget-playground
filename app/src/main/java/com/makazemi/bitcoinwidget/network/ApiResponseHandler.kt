package com.makazemi.bitcoinwidget.network

import com.makazemi.bitcoinwidget.repository.DataState
import com.makazemi.bitcoinwidget.repository.buildError


abstract class ApiResponseHandler<ResultType, Data>(
    private val response: GenericApiResponse<Data>
) {


    suspend fun getResult(): DataState<ResultType> {

        return when (response) {
            is ApiSuccessResponse -> {
                handleSuccess(resultObj = response.body)
            }
            is ApiErrorResponse -> {

                buildError(
                    response.error.message ?: ErrorHandling.ERROR_UNKNOWN,
                    response.error.code.toString()
                )

            }
            is ApiEmptyResponse -> {
                buildError(
                    code = "204",
                    message = "HTTP 204. Returned NOTHING."
                )
            }
        }
    }

    abstract suspend fun handleSuccess(resultObj: Data): DataState<ResultType>

}