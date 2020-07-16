package com.young.weexbase.model

import com.google.gson.Gson
import kotlinx.coroutines.CancellableContinuation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.coroutines.resumeWithException

class CoroutineCallback<T>(private val continuation: CancellableContinuation<T>) : Callback<T> {
    override fun onFailure(call: Call<T>, throwable: Throwable) {
        if (continuation.isCancelled) return
        continuation.resumeWithException(throwable)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        continuation.resumeWith(runCatching {
            if (response.isSuccessful) {
                response.body() ?: throw NullPointerException("Response body is null: $response")
            } else {
                val errorStr = response.errorBody()?.string()
                if (errorStr.isNullOrEmpty()) {
                    throw ErrorResult()
                } else {
                    val errorResult = try {
                        Gson().fromJson(errorStr, ErrorResult::class.java)
                    } catch (e: Exception) {
                        ErrorResult()
                    }
                    throw errorResult
                }
            }
        })
    }
}