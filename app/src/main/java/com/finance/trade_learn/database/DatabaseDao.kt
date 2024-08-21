package com.finance.trade_learn.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions

@Dao
interface DatabaseDao {

    // ------ operations of sell and buy
    @Insert
    suspend fun addCoin(myCoins: MyCoins)


    @Query("select * from myCoins where CoinAmount>0.0")
    suspend fun getAllCoins(): List<MyCoins>

    // this fun will return that it  constraint
    @Query("select * from myCoins where CoinName LIKE '%' || :firstName || '%'")
    suspend fun getFilteredItems(firstName: String): List<MyCoins>


    @Query("select * from myCoins where CoinName=:coinName ")
    fun getSelectedCoinInfo(coinName: String): LiveData<MyCoins?>


    @Update
    suspend fun updateCoin(myCoins: MyCoins)


    // ------ operations of save of trade
    @Insert
    suspend fun addTrade(trade: UserTransactions)

    @Query("select * from SaveCoin")
    suspend fun getAllTrades(): List<UserTransactions>


}