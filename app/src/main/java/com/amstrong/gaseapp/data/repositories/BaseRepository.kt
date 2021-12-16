package com.amstrong.gaseapp.data.repositories

import android.content.ContentValues
import android.util.Log
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        Log.e(ContentValues.TAG, "safeApiCall: ", throwable)
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }

    suspend fun logout(api: AuthApi) = safeApiCall {
        api.logout()
    }
}