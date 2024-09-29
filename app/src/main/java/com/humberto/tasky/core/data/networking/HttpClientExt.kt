package com.humberto.tasky.core.data.networking

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.Result
import kotlinx.serialization.SerializationException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <reified T> safeCall(execute: () -> Response<T>): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch (e: IOException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.SERIALIZATION)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        return Result.Error(DataError.Network.UNKNOWN)
    }

    return responseToResult(response)
}

inline fun <reified T> responseToResult(response: Response<T>): Result<T, DataError.Network> {
    return when {
        response.isSuccessful -> {
            response.body()?.let {
                Result.Success(it)
            } ?: Result.Error(DataError.Network.UNKNOWN)
        }
        response.code() == 401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        response.code() == 408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        response.code() == 409 -> Result.Error(DataError.Network.CONFLICT)
        response.code() == 413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        response.code() == 429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        response.code() in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}