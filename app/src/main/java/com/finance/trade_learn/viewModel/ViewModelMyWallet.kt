package com.finance.trade_learn.viewModel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.Adapters.solveCoinName
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
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
    private val myCoinsDatabaseModel = MutableLiveData<List<MyCoins>>()
    val myCoinsNewModel = MutableLiveData<ArrayList<NewModelForItemHistory>>()
    val myBaseModelOneCryptoModel = MutableLiveData<List<CoinDetail>>()
    var disposable = CompositeDisposable()
    var totalValue = MutableLiveData<BigDecimal>()

    // this function fot get coins that i have
    fun getMyCoinsDetails(constraint: String? = null) {

        CoroutineScope(Dispatchers.Main).launch {
            if (constraint == null) {
                myCoinsDatabaseModel.value = coinDetailRepositoryImp.getAllItems()
                checkDatabaseData(myCoinsDatabaseModel)
                return@launch
            }
            myCoinsDatabaseModel.value = coinDetailRepositoryImp.getFilteredItems(constraint)
            checkDatabaseData(myCoinsDatabaseModel)
        }
    }

    private fun checkDatabaseData(myCoinsDatabaseModel: MutableLiveData<List<MyCoins>>) {

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
        if (coinQuery.isBlank()) return
        disposable.add(
            cryptoService().getSelectedCoinToTradeCoinGecko(coinQuery.lowercase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<CoinDetail>>() {

                    override fun onSuccess(t: List<CoinDetail>) {
                        myBaseModelOneCryptoModel.value = t
                        createNewModel(itemList = t)
                    }

                    override fun onError(e: Throwable) {

                        val cachedItems = cachedData.filter {
                            val id = solveCoinName(it.id)
                            coinQuery.split(",").contains(id)
                        }
                        viewModelScope.launch(Dispatchers.Main){
                            myBaseModelOneCryptoModel.value = cachedItems
                            createNewModel(itemList = cachedItems)
                        }
                    }

                })
        )
    }


    fun createNewModel(itemList: List<CoinDetail>) {

        var total = BigDecimal.ZERO
        val newModelForCoins = ArrayList<NewModelForItemHistory>()

        if (myCoinsDatabaseModel.value?.isNotEmpty() == true) {


            CoroutineScope(Dispatchers.IO).launch {
                var j = 0
                    for (i in itemList){
                        myCoinsDatabaseModel.value?.let {
                            for (z in myCoinsDatabaseModel.value!!) {
                                if (i.id.lowercase() == z.CoinName.lowercase()) {
                                    val name = i.id.lowercase(Locale.getDefault())
                                    val price = i.current_price?.toBigDecimal() ?: BigDecimal.ZERO

                                    val amount =
                                        coinDetailRepositoryImp.getSelectedItemDetail(i.id.lowercase(Locale.getDefault()))?.CoinAmount?.toBigDecimal() ?: coinDetailRepositoryImp.getSelectedItemDetail(i.id.uppercase(Locale.getDefault()))?.CoinAmount?.toBigDecimal() ?: BigDecimal.ZERO
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

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}