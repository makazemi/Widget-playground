package com.makazemi.bitcoinwidget.network


import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

internal class ApiResponseCall<T>(
    private val delegate: Call<T>
) : Call<GenericApiResponse<T>> {
    override fun enqueue(realCallback: Callback<GenericApiResponse<T>>) {
        delegate.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, error: Throwable) {
                // we always succeed
                realCallback.onResponse(
                    this@ApiResponseCall,
                    Response.success(GenericApiResponse.create(error))
                )
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                realCallback.onResponse(
                    this@ApiResponseCall, Response.success(
                        GenericApiResponse.create(response)
                    )
                )
            }

        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun clone(): Call<GenericApiResponse<T>> = ApiResponseCall(delegate)

    override fun isCanceled() = delegate.isCanceled

    override fun cancel() = delegate.cancel()

    override fun execute(): Response<GenericApiResponse<T>> {
        return Response.success(GenericApiResponse.create(delegate.execute()))
    }

    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout {
        return Timeout().timeout(10000, TimeUnit.MILLISECONDS)
    }
}


class ApiResponseCallAdapter<R>(
    private val bodyType: Type,
    private val delegate: CallAdapter<R, Call<R>>
) : CallAdapter<R, Call<GenericApiResponse<R>>> {
    override fun adapt(original: Call<R>): Call<GenericApiResponse<R>> {
        return ApiResponseCall(delegate.adapt(original))
    }

    override fun responseType(): Type = bodyType
}

class ApiResponseCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val parameterizedReturn = returnType as? ParameterizedType ?: return null
        if (parameterizedReturn.rawType != Call::class.java) {
            return null
        }
        val parameterizedApiResponse =
            parameterizedReturn.actualTypeArguments.firstOrNull() as? ParameterizedType
                ?: return null
        val bodyType = parameterizedApiResponse.actualTypeArguments.firstOrNull() ?: return null
        val callBody = OneArgParameterizedType(Call::class.java, arrayOf(bodyType))
        val delegate = retrofit.callAdapter(callBody, annotations) ?: return null
        @Suppress("UNCHECKED_CAST")
        return ApiResponseCallAdapter(bodyType, delegate as CallAdapter<Any, Call<Any>>)
    }
}

open class OneArgParameterizedType(
    private val myRawType: Type,
    private val myTypeArgs: Array<Type>
) : ParameterizedType {
    override fun getRawType() = myRawType

    override fun getOwnerType() = null

    override fun getActualTypeArguments() = myTypeArgs
}