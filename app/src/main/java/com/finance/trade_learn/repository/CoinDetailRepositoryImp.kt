package com.finance.trade_learn.repository

import androidx.lifecycle.LiveData
import com.finance.trade_learn.database.DatabaseDao
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import java.util.*
import javax.inject.Inject

interface UserLocalWalletDatabase{
    suspend fun getAllItems() :  List<MyCoins>

    fun getAllCoinsAsLiveData() :  LiveData<List<MyCoins>>

    fun getSelectedItemDetail(selectedItem : String) :  LiveData<MyCoins?>

    suspend fun updateSelectedItem(item : MyCoins) :  Unit

    suspend fun addProgressToTradeHistory(item : UserTransactions) :  Unit

    suspend fun buyNewItem(item : MyCoins) :  Unit
}


// lowerCase will be delete here until 1 july
class CoinDetailRepositoryImp @Inject constructor(
    private val dataBaseService : DatabaseDao
    ) : UserLocalWalletDatabase {
    override suspend fun getAllItems(): List<MyCoins> {
        return dataBaseService.getAllCoins().map {
            it.copy(CoinName = it.CoinName.lowercase(Locale.getDefault()))
        }
    }

    override fun getAllCoinsAsLiveData(): LiveData<List<MyCoins>> {
        return dataBaseService.getAllCoinsAsLiveData()
    }

    override fun getSelectedItemDetail(selectedItem : String): LiveData<MyCoins?> {
        return  dataBaseService.getSelectedCoinInfo(selectedItem.lowercase(Locale.getDefault()))
    }

    override suspend fun updateSelectedItem(item : MyCoins) {
        dataBaseService.updateCoin(item)
    }

    override suspend fun addProgressToTradeHistory(item: UserTransactions) {
        dataBaseService.addTrade(item)
    }

    override suspend fun buyNewItem(item: MyCoins) {
        dataBaseService.addCoin(item)
    }
}