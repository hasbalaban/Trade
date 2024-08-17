package com.finance.trade_learn.service.user

import com.finance.trade_learn.models.NewUserRequest
import com.finance.trade_learn.models.User
import com.finance.trade_learn.models.UserLoginRequest
import com.finance.trade_learn.models.WrapResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("/createNewUser")
    suspend fun createNewUser(@Body newUserRequest : NewUserRequest): Response<WrapResponse<User?>>

    @POST("/login")
    suspend fun login(@Body loginRequest : UserLoginRequest): Response<WrapResponse<User?>>


}