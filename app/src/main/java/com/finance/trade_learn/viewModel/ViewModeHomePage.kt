package com.finance.trade_learn.viewModel

import com.finance.trade_learn.models.BaseModelCrypto
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.returnDataForHomePage
import com.finance.trade_learn.utils.converOperation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class ViewModeHomePage : ViewModel() {
    var isInitialize = MutableLiveData(false)
    private var disposable: CompositeDisposable = CompositeDisposable()
    var state = MutableLiveData<Boolean>()
    var listOfCrypto = MutableLiveData<ArrayList<CoinsHome>>()
    var listOfCryptoForPopular = MutableLiveData<ArrayList<CoinsHome>>()
    private var listOfCryptoforCompare = MutableLiveData<List<CoinsHome>>()
    private var change = enumPriceChange.notr

    fun getAllCryptoFromApi() {
        state.value = false
        CoroutineScope(Dispatchers.IO).launch {
            disposable.add(
                cryptoService().AllCrypto()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<BaseModelCrypto>>() {
                        override fun onSuccess(t: List<BaseModelCrypto>) {
                            try {
                                val data = convert(t.filter { it.day1 != null })
                                state.value = true
                                isInitialize.value = true
                                change = data.change
                                listOfCrypto = data.ListOfCrypto
                                listOfCryptoforCompare = data.ListOfCryptoForCompare
                                listOfCryptoForPopular.value = convertPopularCoinList(data.ListOfCrypto.value)
                            } catch (e: Exception) { }
                        }

                        override fun onError(e: Throwable) { state.value = false }
                    })
            )
        }


    }

    fun convert(t: List<BaseModelCrypto>): returnDataForHomePage {
        return converOperation(t, listOfCryptoforCompare).convertDataToUse()
    }

    private fun convertPopularCoinList(list: ArrayList<CoinsHome>?): ArrayList<CoinsHome>? {
        val popList = arrayListOf<CoinsHome>()
        return if (list != null) {
            for (i in list) {
                if (i.CoinName.subSequence(0, 3) == "BTC" || i.CoinName.subSequence(0, 3) == "BNB" || i.CoinName.subSequence(0, 3) == "ETH") {
                    popList.add(i) } }
            popList
        } else null

    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }


}