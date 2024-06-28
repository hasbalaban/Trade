package com.finance.trade_learn.base


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.models.DataForHomePage
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.utils.ConvertOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {
    private var baseDisposable: CompositeDisposable = CompositeDisposable()

    val coinListDetail = MutableLiveData<List<CoinInfoList>>()

    var isLoading = MutableLiveData<Boolean>(false)

    var currentItemsLiveData = MutableLiveData<List<CoinsHome>>()
    var listOfCryptoForPopular = MutableLiveData<List<CoinsHome>>()

    fun getCoinList(){
        CoroutineScope(Dispatchers.IO).launch {
            baseDisposable.add(
                cryptoService().getCoinList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<CoinInfoList>>() {
                        override fun onSuccess(t: List<CoinInfoList>) {
                            coinListDetail.value = t
                            try {
                            } catch (_: Exception) { }
                        }

                        override fun onError(e: Throwable) {
                        }
                    })
            )
        }
    }

    fun getAllCrypto(page : Int) {
        isLoading.value = true
        viewModelScope.launch {
            cryptoService().getCoinList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<CoinDetail>>() {
                    override fun onSuccess(t: List<CoinDetail>) {
                        isLoading.value = false
                        try {

                            val data = convertCryptoList(t)
                            if (data.ListOfCrypto.isNotEmpty()){
                                currentItemsLiveData.value = data.ListOfCrypto

                                listOfCryptoForPopular.value = convertPopularCoinList(data.ListOfCrypto)

                                currentItems = data.ListOfCrypto
                                lastItems = data.lastCrypoList
                            }

                        } catch (_: Exception) {
                            isLoading.value = false

                        }
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false

                        currentItemsLiveData.value = currentItems
                        listOfCryptoForPopular.value = convertPopularCoinList(currentItems)
                    }
                })
        }
    }

    fun convertCryptoList(t: List<CoinDetail>): DataForHomePage {
        return ConvertOperation(t, lastItems).convertDataToUse()
    }


    private fun convertPopularCoinListShortByTotalVolume(list: ArrayList<CoinsHome>?): List<CoinsHome>? {
        return list?.sortedBy {
            it.total_volume
        }?.take(3)
    }

    private fun convertPopularCoinList(list: List<CoinsHome>?): ArrayList<CoinsHome> {
        val popList = arrayListOf<CoinsHome>()
        val populerlist = mutableListOf("bit", "bnb", "eth", "sol", "gate", "avax")
        list?.let{
            for (i in list){
                if (popList.size == 3) break
                if (populerlist.contains(i.CoinName.subSequence(0, 3).toString().lowercase())) {
                    popList.add(i)
                    populerlist.remove(i.CoinName.subSequence(0, 3))
                }
            }

            for (i in list.sortedBy { it.total_volume }) {
                if (!popList.contains(i)) popList.add(i)
            }
        }
        return popList
    }



    override fun onCleared() {
        super.onCleared()
        baseDisposable.clear()
    }

    companion object {
        var cachedData : List<CoinDetail> = emptyList()
        var currentItems : List<CoinsHome> = emptyList()
        var lastItems : List<CoinsHome> = emptyList()
    }

}