package com.finance.trade_learn.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.database.dataBaseService
import com.finance.trade_learn.utils.Secrets
import com.finance.trade_learn.utils.SharedPreferencesManager
import com.finance.trade_learn.utils.notificationWorkManager
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ViewModelUtils() : ViewModel() {


    fun isOneEntering(context: Context): Boolean {
        val sharedManager = SharedPreferencesManager(context)
        val hasGift = sharedManager.getSharedPreferencesBoolen("hasGift")
        val removedWorkManagers = sharedManager.getSharedPreferencesBoolen("removedWorkManagers")


        if (hasGift && !removedWorkManagers){
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWork()
            notificationWorkManager(1, TimeUnit.DAYS, context)
            sharedManager.addSharedPreferencesBoolen("removedWorkManagers", true)
            // : remove this for next release
        }


        if(!hasGift){
            sharedManager.addSharedPreferencesBoolen("hasGift", true)
            addOneTimeDollars(context)
        }

        return !hasGift
    }

    private fun addOneTimeDollars(context: Context) {
        viewModelScope.launch {
            val databaseDao = dataBaseService.invoke(context).databaseDao()
            val myCoins = MyCoins("tether", Secrets.GIFT_AMOUNT)
            databaseDao.addCoin(myCoins)
        }
    }


}