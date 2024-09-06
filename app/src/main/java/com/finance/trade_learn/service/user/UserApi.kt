package com.finance.trade_learn.service.user

import com.finance.trade_learn.database.dataBaseEntities.UserTransactions
import com.finance.trade_learn.database.dataBaseEntities.UserTransactionsRequest
import com.finance.trade_learn.models.ErrorResponse
import com.finance.trade_learn.models.NewUserRequest
import com.finance.trade_learn.models.ResetPasswordRequest
import com.finance.trade_learn.models.UserBalance
import com.finance.trade_learn.models.UserInfo
import com.finance.trade_learn.models.UserLoginRequest
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.watchList.WatchListItem
import com.finance.trade_learn.models.watchList.WatchListRequestItem
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.util.concurrent.TimeUnit

class AppInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
            .newBuilder()
            .build()

        var response: okhttp3.Response? = null

        try {
            response = chain.proceed(request)
        } catch (exception: Exception) {
            if (exception is ConnectException) {
                println("exception" + "ConnectionError -->> url ->>   ${request.url}  ")
            } else {
                println("exception" + "GenericResponseError -->> url ->>  ${request.url} ")
            }
            println(exception.printStackTrace())
        }


        if (response != null) {
            try {
                val obj = response.body?.string()
                val request = chain.request().newBuilder().build()

                val buffer = okio.Buffer()
                request.body?.writeTo(buffer)

                val contentType = response.body?.contentType()
                if (contentType.toString().contains("application/json")) {
                    return response.newBuilder()
                        .body(ResponseBody.create(contentType, obj ?: ""))
                        .build()
                }
            } catch (e: Exception) {
                println(e)
            }
        }

        val errorWrappedResponse = WrapResponse<Any>()
        errorWrappedResponse.message = "Response Error"
        errorWrappedResponse.error =  ErrorResponse(
            message = "Response Error",
            name = "",
            code = -100
        )


        val body = Gson().toJson(errorWrappedResponse).toResponseBody(null)

        return okhttp3.Response.Builder()
            .code(200)
            .body(body)
            .protocol(Protocol.HTTP_2)
            .message("ERROR_MESSAGE_UNKNOWN")
            .request(chain.request())
            .build()

    }
}

val client = OkHttpClient().newBuilder().addInterceptor(
    AppInterceptor()
).callTimeout(40, TimeUnit.SECONDS)
    .connectTimeout(40, TimeUnit.SECONDS)
    .writeTimeout(40, TimeUnit.SECONDS)
    .readTimeout(40, TimeUnit.SECONDS)
    .build()


class UserApi {

    //val localBaseUrl = "http://10.0.2.2:8080"
    val localBaseUrl = "https://learn-trade-d43b9356970c.herokuapp.com"
    var userService = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(localBaseUrl)
        .build()
        .create(UserService::class.java)

    suspend fun createNewUser(newUserRequest: NewUserRequest): Response<WrapResponse<UserInfo>> {
        return userService.createNewUser(newUserRequest = newUserRequest)
    }

    suspend fun login(loginRequest: UserLoginRequest): Response<WrapResponse<UserInfo>> {
        return userService.login(loginRequest = loginRequest)
    }

    suspend fun deleteAccount(email: String): Response<WrapResponse<String>> {
        return userService.deleteAccount(email = email)
    }

    suspend fun sendResetPasswordCode(email: String): Response<WrapResponse<String>>  {
        return userService.sendResetPasswordCode(email = email)
    }

    suspend fun resetPassword(resetPasswordRequest : ResetPasswordRequest): Response<WrapResponse<String>>  {
        return userService.resetPassword(resetPasswordRequest = resetPasswordRequest)
    }

    suspend fun getTransactionHistory(email : String): Response<WrapResponse<List<UserTransactions>>> {
        return userService.getTransactionHistory(email = email)
    }

    suspend fun addTransactionHistory(transaction: UserTransactionsRequest): Response<WrapResponse<List<UserBalance>>>  {
        return userService.addTransactionHistory(userTransactions = transaction)
    }
    suspend fun getUserInfo(email: String): Response<WrapResponse<UserInfo>>  {
        return userService.getUserInfo(email = email)
    }

    suspend fun addOrRemoveWatchListItem(watchListRequestItem : WatchListRequestItem): Response<WrapResponse<List<WatchListItem>>> {
        return userService.addOrRemoveWatchListItem(watchListRequestItem = watchListRequestItem)
    }

}