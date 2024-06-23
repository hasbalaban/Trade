package com.finance.trade_learn.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.DataForHomePage
import com.finance.trade_learn.utils.ConvertOperation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class ViewModeHomePage : BaseViewModel() {
    private var disposable: CompositeDisposable = CompositeDisposable()
    var isLoading = MutableLiveData<Boolean>(false)
    var listOfCrypto = MutableLiveData<ArrayList<CoinsHome>>()
    var listOfCryptoForPopular = MutableLiveData<ArrayList<CoinsHome>>()
    private var lastCrypoList = MutableLiveData<List<CoinsHome>>()

    fun getAllCryptoFromApi(page : Int) {
        isLoading.value = true
        viewModelScope.launch {
                cryptoService().getCoinList(2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<CoinDetail>>() {
                        override fun onSuccess(t: List<CoinDetail>) {
                            isLoading.value = false
                            try {
                                val data = convertCryptoList(t)
                                if (data.ListOfCrypto.isNotEmpty()){
                                    listOfCrypto.value = data.ListOfCrypto
                                    lastCrypoList.value = data.lastCrypoList
                                    listOfCryptoForPopular.value = convertPopularCoinList(data.ListOfCrypto)
                                }

                            } catch (_: Exception) {
                                isLoading.value = false
                            }
                        }

                        override fun onError(e: Throwable) {
                            isLoading.value = false
                        }
                    })
        }
    }

    fun getAllCryptoFromLocalApi(page : Int) {
        isLoading.value = true
        viewModelScope.launch {
                cryptoService().getCoinList(2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<CoinDetail>>() {
                        override fun onSuccess(t: List<CoinDetail>) {
                            isLoading.value = false
                            try {
                                val data = convertCryptoList(t)
                                if (data.ListOfCrypto.isNotEmpty()){
                                    listOfCrypto.value = data.ListOfCrypto
                                    lastCrypoList.value = data.lastCrypoList
                                    listOfCryptoForPopular.value = convertPopularCoinList(data.ListOfCrypto)
                                }

                            } catch (e: Exception) {
                                println(e.localizedMessage)
                                isLoading.value = false
                            }
                        }

                        override fun onError(e: Throwable) {
                            isLoading.value = false
                        }
                    })
        }
    }

    fun convertCryptoList(t: List<CoinDetail>): DataForHomePage {
        return ConvertOperation(t, lastCrypoList).convertDataToUse()
    }

    private fun convertPopularCoinList(list: ArrayList<CoinsHome>?): ArrayList<CoinsHome>? {
        val popList = arrayListOf<CoinsHome>()
        val populerlist = mutableListOf("bit", "bnb", "eth", "sol", "gate", "avax")
        list?.let{
            for (i in list) {
                if (popList.size == 3) return@let
                if (populerlist.contains(i.CoinName.subSequence(0, 3).toString().lowercase())) {
                    popList.add(i)
                    populerlist.remove(i.CoinName.subSequence(0, 3))
                }
            }
        }
        return popList
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }


}