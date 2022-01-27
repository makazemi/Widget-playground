package com.makazemi.bitcoinwidget.network

import org.json.JSONObject

class ErrorBody(
    var code: String? = "",
    var message: String?,
    var error: String = ""
) {


    companion object {
        fun convertToObject(errorBody: String?): ErrorBody {
            val error = ErrorBody("", "", "")
            try {
                val jsonError = JSONObject(errorBody!!)
                val errors = jsonError.optString("errors")
                val jsonErrorInner = JSONObject(errors)
                val message = jsonErrorInner.optString("message")
                val errorStr = jsonErrorInner.optString("error")
                error.message = message
                error.error = errorStr

            } catch (ex: Exception) {
            }
            return error
        }


    }

    override fun toString(): String {
        return "code: " + code + "\n" +
                "message: " + message + "\n" +
                "userMessage: " + error
    }


}