package com.finance.trade_learn.viewModel

import com.finance.trade_learn.models.BaseModelCrypto
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.returnDataForHomePage
import com.finance.trade_learn.utils.ConverOperation
import com.finance.trade_learn.utils.converOperation1
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class ViewModeHomePage : ViewModel() {
    private var disposable: CompositeDisposable = CompositeDisposable()
    var isLoading = MutableLiveData<Boolean>()
    var listOfCrypto = MutableLiveData<ArrayList<CoinsHome>>()
    var listOfCryptoForPopular = MutableLiveData<ArrayList<CoinsHome>>()
    private var listOfCryptoforCompare = MutableLiveData<List<CoinsHome>>()
    private var change = enumPriceChange.notr

    fun getAllCryptoFromApi() {
        isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            disposable.add(
                cryptoService().getCoinGecko(null, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<CoinDetail>>() {
                        override fun onSuccess(t: List<CoinDetail>) {
                            try {
                                val data = convert1(t)
                                isLoading.value = false
                                change = data.change
                                listOfCrypto = data.ListOfCrypto
                                listOfCryptoforCompare = data.ListOfCryptoForCompare
                                listOfCryptoForPopular.value = convertPopularCoinList(data.ListOfCrypto.value)
                            } catch (e: Exception) {
                                println(e.localizedMessage)

                            }
                        }

                        override fun onError(e: Throwable) { isLoading.value = false }
                    })
            )
        }


    }

    fun convert(t: List<BaseModelCrypto>): returnDataForHomePage {
        return ConverOperation(t, listOfCryptoforCompare).convertDataToUse()
    }

    fun convert1(t: List<CoinDetail>): returnDataForHomePage {
        return converOperation1(t, listOfCryptoforCompare).convertDataToUse()
    }

    private fun convertPopularCoinList(list: ArrayList<CoinsHome>?): ArrayList<CoinsHome>? {
        val popList = arrayListOf<CoinsHome>()
        val populerlist = mutableListOf("bit", "bnb", "eth", "sol", "gate", "avax")
        return if (list != null) {
            for (i in list) {
                if (populerlist.contains(i.CoinName.subSequence(0, 3).toString().lowercase())) {
                    popList.add(i)
                    populerlist.remove(i.CoinName.subSequence(0, 3))
                if (popList.size == 3) return popList
                } }
            popList
        } else null

    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }


}