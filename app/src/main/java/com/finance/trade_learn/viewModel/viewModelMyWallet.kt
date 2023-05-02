package com.finance.trade_learn.viewModel

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

import androidx.lifecycle.MutableLiveData
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.database.dataBaseEntities.myCoins
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ViewModelMyWallet @Inject constructor(
    private val coinDetailRepositoryImp : CoinDetailRepositoryImp
    ) : BaseViewModel() {
    private val myCoinsDatabaseModel = MutableLiveData<List<myCoins>>()
    val myCoinsNewModel = MutableLiveData<ArrayList<NewModelForItemHistory>>()
    val myBaseModelOneCryptoModel = MutableLiveData<List<CoinDetail>>()
    var disposable = CompositeDisposable()
    var totalValue = MutableLiveData<BigDecimal>()

    // this function fot get coins that i have
    fun getMyCoinsDetails(constrait: String? = null) {

        CoroutineScope(Dispatchers.Main).launch {
            if (constrait == null) {
                myCoinsDatabaseModel.value = coinDetailRepositoryImp.getAllItems()
                checkDatabaseData(myCoinsDatabaseModel)
            } else {
                myCoinsDatabaseModel.value = coinDetailRepositoryImp.getFilteredItems(constrait)
                checkDatabaseData(myCoinsDatabaseModel)
            }


        }


    }

    fun checkDatabaseData(myCoinsDatabaseModel: MutableLiveData<List<myCoins>>) {

        myCoinsDatabaseModel.let {
            var coinQuery = ""
            for (i in myCoinsDatabaseModel.value!!) {
                coinQuery += i.CoinName.lowercase() + ","
            }
            val ids = coinQuery.dropLast(1)
            getDataFromApi(ids)
        }
    }


    private fun getDataFromApi(coinQuery: String) {
        disposable.add(
            cryptoService().getSelectedCoinToTradeCoinGecko(coinQuery.lowercase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<CoinDetail>>() {

                    override fun onSuccess(t: List<CoinDetail>) {
                        myBaseModelOneCryptoModel.value = t
                        createNewModel()
                    }

                    override fun onError(e: Throwable) {
                        Log.i("hatahataprice", e.message.toString())
                    }

                })
        )
    }


    fun createNewModel() {

        var total = BigDecimal.ZERO
        val newModelForCoins = ArrayList<NewModelForItemHistory>()

        if (myCoinsDatabaseModel.value?.isNotEmpty() == true) {


            CoroutineScope(Dispatchers.IO).launch {
                var j = 0
                myBaseModelOneCryptoModel.value?.let {
                    for (i in it){
                        myCoinsDatabaseModel.value?.let {
                            for (z in myCoinsDatabaseModel.value!!) {
                                if (i.id.lowercase() == z.CoinName.lowercase()) {
                                    val name = i.id.lowercase(Locale.getDefault())
                                    val price = i.current_price.toBigDecimal()

                                    val amount =
                                        coinDetailRepositoryImp.getSelectedItemDetail(i.id.lowercase(Locale.getDefault()))?.CoinAmount?.toBigDecimal() ?:
                                        coinDetailRepositoryImp.getSelectedItemDetail(i.id.uppercase(Locale.getDefault()))?.CoinAmount?.toBigDecimal() ?:
                                        BigDecimal.ZERO
                                    val image = i.image

                                    total += (price * amount)

                                    newModelForCoins.add(
                                        NewModelForItemHistory(
                                            name, amount.toString(),
                                            (amount * price).toString(), image
                                        )
                                    )
                                    j++
                                    break
                                }


                            }
                        }


                        withContext(Dispatchers.Main) {
                            totalValue.value = total
                            myCoinsNewModel.value = newModelForCoins
                        }

                    }
                }
            }
        }


    }


    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}