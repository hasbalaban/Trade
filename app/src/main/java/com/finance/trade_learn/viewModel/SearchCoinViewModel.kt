package com.finance.trade_learn.viewModel

import androidx.lifecycle.MutableLiveData
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchCoinViewModel: BaseViewModel() {
    private var disposable: CompositeDisposable = CompositeDisposable()
    val coinListDetail = MutableLiveData<List<CoinInfoList>>()

    fun getCoinList(){
        CoroutineScope(Dispatchers.IO).launch {
            disposable.add(
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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}