package com.finance.trade_learn.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.finance.trade_learn.database.dataBaseEntities.myCoins
import com.finance.trade_learn.database.dataBaseService
import com.finance.trade_learn.utils.sharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelUtils() : ViewModel() {


    fun isOneEntering(context: Context): Boolean {
        val sharedManager = sharedPreferencesManager(context)
        val firstOpen = sharedManager.getSharedPreferencesBoolen("firstOpen")
        return if (firstOpen ) {
            true.also {
                sharedManager.addSharedPreferencesBoolen("firstOpen", false)
                addOneTimeDollars(1000.0,context)
            }
        } else false
    }

    private fun addOneTimeDollars(dollars: Double, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val databaseDao = dataBaseService.invoke(context).databaseDao()
            val myCoins = myCoins("TETHER", dollars)
            databaseDao.addCoin(myCoins)
        }
    }


}