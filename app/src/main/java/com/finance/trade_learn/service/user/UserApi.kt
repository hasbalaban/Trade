package com.finance.trade_learn.service.user

import android.provider.ContactsContract.CommonDataKinds.Email
import com.finance.trade_learn.models.NewUserRequest
import com.finance.trade_learn.models.User
import com.finance.trade_learn.models.UserLoginRequest
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.handleResponse
import com.finance.trade_learn.service.ctryptoApi.CryptoOperationInterface
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class UserApi {


    val localBaseUrl = "http://10.0.2.2:8080"
    //val localBaseUrl = "https://learn-trade-d43b9356970c.herokuapp.com"
    var userService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(localBaseUrl)
        .build()
        .create(UserService::class.java)

    suspend fun createNewUser(newUserRequest: NewUserRequest): Response<WrapResponse<User?>> {
        return userService.createNewUser(newUserRequest = newUserRequest)
    }

    suspend fun login(loginRequest: UserLoginRequest): Response<WrapResponse<User?>> {
        return userService.login(loginRequest = loginRequest)
    }

    suspend fun deleteAccount(email: String): Response<WrapResponse<String>> {
        return userService.deleteAccount(email = email)
    }

    suspend fun sendResetPasswordCode(email: String): Response<WrapResponse<String>>  {
        return userService.sendResetPasswordCode(email = email)
    }

}