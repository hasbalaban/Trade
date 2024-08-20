package com.finance.trade_learn.service.user

import com.finance.trade_learn.models.NewUserRequest
import com.finance.trade_learn.models.ResetPasswordRequest
import com.finance.trade_learn.models.User
import com.finance.trade_learn.models.UserLoginRequest
import com.finance.trade_learn.models.WrapResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @POST("/createNewUser")
    suspend fun createNewUser(@Body newUserRequest : NewUserRequest): Response<WrapResponse<User?>>

    @POST("/login")
    suspend fun login(@Body loginRequest : UserLoginRequest): Response<WrapResponse<User?>>

    @DELETE("/deleteUserById")
    suspend fun deleteAccount(
        @Query("email") email: String
    ): Response<WrapResponse<String>>

    @POST("/code/request-password-reset")
    suspend fun sendResetPasswordCode(@Query("email") email: String): Response<WrapResponse<String>>
    @POST("/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest : ResetPasswordRequest): Response<WrapResponse<String>>


}