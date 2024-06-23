package com.finance.trade_learn.viewModel

import com.finance.trade_learn.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class ViewModelMarket @Inject constructor() : BaseViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

}