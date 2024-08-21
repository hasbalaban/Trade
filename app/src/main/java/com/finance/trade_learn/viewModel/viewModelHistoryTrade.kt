package com.finance.trade_learn.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions
import com.finance.trade_learn.database.dataBaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelHistoryTrade : BaseViewModel() {
    val listOfTrade = MutableLiveData<ArrayList<UserTransactions>>()

    fun getDataFromDatabase(context: Context) {
        val dao = dataBaseService.invoke(context).databaseDao()
        CoroutineScope(Dispatchers.Main).launch {
            val list = dao.getAllTrades()
            convertListForAdapter(list)
        }
    }

    private fun convertListForAdapter(list: List<UserTransactions>) {

        val newModel = ArrayList<UserTransactions>()
        for (i in list) {
            val name = i.transactionItemName
            val amount = i.amount.toBigDecimal()
            val price = i.price.toBigDecimal()
            val total = i.transactionTotalPrice.toBigDecimal()
            val date = i.date
            val state = i.transactionType
            val itemOfHistory = UserTransactions(
                i.id, name, amount.toString(),
                price.toString(), total.toString(), date, state
            )
            newModel.add(itemOfHistory)

        }
        listOfTrade.value = newModel
    }
}