package com.finance.trade_learn.repository

import com.finance.trade_learn.database.DatabaseDao
import com.finance.trade_learn.database.dataBaseEntities.SaveCoin
import com.finance.trade_learn.database.dataBaseEntities.myCoins
import java.util.*
import javax.inject.Inject

interface UserLocalWalletDatabase{
    suspend fun getAllItems() :  List<myCoins>

    suspend fun getSelectedItemDetail(selectedItem : String) :  myCoins?

    suspend fun updateSelectedItem(item : myCoins) :  Unit

    suspend fun addProgressToTradeHistory(item : SaveCoin) :  Unit

    suspend fun buyNewItem(item : myCoins) :  Unit

    suspend fun getFilteredItems(filter : String) :  List<myCoins>

}


// lowerCase will be delete here until 1 july
class CoinDetailRepositoryImp @Inject constructor(
    private val dataBaseService : DatabaseDao
    ) : UserLocalWalletDatabase {
    override suspend fun getAllItems(): List<myCoins> {
        return dataBaseService.getAllCoins().map {
            it.copy(CoinName = it.CoinName.lowercase(Locale.getDefault()))
        }
    }

    override suspend fun getSelectedItemDetail(selectedItem : String): myCoins? {
        return  dataBaseService.getSelectedCoinInfo(selectedItem.lowercase(Locale.getDefault())) ?:  dataBaseService.getSelectedCoinInfo(selectedItem.uppercase(Locale.getDefault()))?.apply {
            CoinName = this.CoinName.lowercase(Locale.getDefault())
        }
    }

    override suspend fun updateSelectedItem(item : myCoins) {
        dataBaseService.updateCoin(item.copy(CoinName = item.CoinName.lowercase(
            Locale.getDefault())))
        dataBaseService.updateCoin(item)
    }

    override suspend fun addProgressToTradeHistory(item: SaveCoin) {
        val itemNameLowercase = item.coinName.lowercase(Locale.getDefault())
        dataBaseService.addTrade(item.copy(
            coinName = itemNameLowercase
        ))
    }

    override suspend fun buyNewItem(item: myCoins) {
        dataBaseService.addCoin(item)
    }

    override suspend fun getFilteredItems(filter: String): List<myCoins> {
        return dataBaseService.getFilteredItems(filter.lowercase(Locale.getDefault()))
    }

}