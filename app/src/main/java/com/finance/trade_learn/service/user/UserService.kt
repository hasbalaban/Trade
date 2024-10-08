package com.finance.trade_learn.service.user

import com.finance.trade_learn.database.dataBaseEntities.TableRow
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions
import com.finance.trade_learn.database.dataBaseEntities.UserTransactionsRequest
import com.finance.trade_learn.models.NewUserRequest
import com.finance.trade_learn.models.ResetPasswordRequest
import com.finance.trade_learn.models.UserBalance
import com.finance.trade_learn.models.UserInfo
import com.finance.trade_learn.models.UserLoginRequest
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.watchList.WatchListItem
import com.finance.trade_learn.models.watchList.WatchListRequestItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @POST("/createNewUser")
    suspend fun createNewUser(@Body newUserRequest : NewUserRequest): Response<WrapResponse<UserInfo>>

    @POST("/login")
    suspend fun login(@Body loginRequest : UserLoginRequest): Response<WrapResponse<UserInfo>>

    @DELETE("/deleteUserById")
    suspend fun deleteAccount(
        @Query("email") email: String
    ): Response<WrapResponse<String>>

    @POST("/code/request-password-reset")
    suspend fun sendResetPasswordCode(@Query("email") email: String): Response<WrapResponse<String>>

    @POST("/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest : ResetPasswordRequest): Response<WrapResponse<String>>

    @GET("/transaction")
    suspend fun getTransactionHistory(
        @Query("email") email: String
    ): Response<WrapResponse<List<UserTransactions>>>

    @POST("/transaction")
    suspend fun addTransactionHistory(
        @Body userTransactions: UserTransactionsRequest
    ): Response<WrapResponse<List<UserBalance>>>

    @GET("/userInfo")
    suspend fun getUserInfo(
        @Query("email") email: String
    ): Response<WrapResponse<UserInfo>>


    @POST("/watchList")
    suspend fun addOrRemoveWatchListItem(
        @Body watchListRequestItem : WatchListRequestItem
    ): Response<WrapResponse<List<WatchListItem>>>




    @GET("/currencies")
    suspend fun getAllCurrencies(): Response<WrapResponse<List<TableRow>>>


}