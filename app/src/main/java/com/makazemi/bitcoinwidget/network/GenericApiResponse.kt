package com.makazemi.bitcoinwidget.network


import com.makazemi.bitcoinwidget.network.ErrorHandling.Companion.ERROR_SERVER_CONNECTION
import retrofit2.Response


@Suppress("unused") // T is used in extending classes
sealed class GenericApiResponse<T> {

    companion object {


        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(
                ErrorBody(message = error.message ?: "unknown error")
            )
        }

        fun <T> create(response: Response<T>): GenericApiResponse<T> {
            if (response.isSuccessful) {
                val body = response.body()
                return if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else if (response.code() == 401) {
                    ApiErrorResponse(
                        ErrorBody(
                            response.code().toString(),
                            "401 Unauthorized. Token may be invalid."
                        )
                    )
                } else {
                    ApiSuccessResponse(body = body)
                }
            } else {

                val msg = response.errorBody()?.string()
                val userMessage = ErrorBody.convertToObject(msg).message
                val errorMsg = if (userMessage.isNullOrEmpty()) {
                    ERROR_SERVER_CONNECTION
                } else {
                    userMessage
                }
                return ApiErrorResponse(
                    ErrorBody(response.code().toString(), errorMsg)
                )
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : GenericApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T) : GenericApiResponse<T>()

data class ApiErrorResponse<T>(val error: ErrorBody) : GenericApiResponse<T>()

