package com.finance.trade_learn.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.models.BaseModelCrypto
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.utils.ConverOperation
import com.finance.trade_learn.utils.ConverOperation1
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
class ViewModelMarket @Inject constructor() : BaseViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()
    var listOfCrypto = MutableLiveData<ArrayList<CoinsHome>>()
    private var listOfCryptoforCompare = MutableLiveData<List<CoinsHome>>()
    private var change = enumPriceChange.notr
    var isLoading = MutableLiveData<Boolean>(false)


    fun runGetAllCryptoFromApi() {
        isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            disposable.add(
                cryptoService().getCoinGecko(null, page = 2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<CoinDetail>>() {
                        override fun onSuccess(t: List<CoinDetail>) {
                            isLoading.value = false
                            try {
                                convert(t)
                            } catch (e: Exception) {
                                Log.i("hata", "hata")
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.i("messages", e.message!!)
                            isLoading.value = false
                        }
                    })
            )
        }


    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun convert(t: List<CoinDetail>) {
        val data = ConverOperation1(t, listOfCryptoforCompare).convertDataToUse()
        listOfCrypto.value = data.ListOfCrypto
    }


}