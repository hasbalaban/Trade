package com.finance.trade_learn.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.view.home.homeViewStates.SearchBarViewState
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    private val _searchBarViewState = MutableStateFlow<SearchBarViewState>(SearchBarViewState())
    val searchBarViewState : StateFlow<SearchBarViewState> get() = _searchBarViewState


    fun updateSearchBarViewState (searchBarViewState : SearchBarViewState){
        _searchBarViewState.value = searchBarViewState
    }


    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }





}