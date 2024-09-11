package com.finance.trade_learn.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


object RemoteConfigsConst{
    const val SHOULD_BE_LOCAL_REQUEST = "SHOULD_BE_LOCAL_REQUEST"
    const val SHOULD_SHOW_ADVERTISEMENT = "SHOULD_SHOW_ADVERTISEMENT"
}


object RemoteConfigs {

    private val _isRemoteConfigFetchingCompleted = MutableStateFlow<Boolean>(false)
    val isRemoteConfigFetchingCompleted : StateFlow<Boolean> get() = _isRemoteConfigFetchingCompleted

    fun setRemoteConfigStatus(isCompleted : Boolean){
        _isRemoteConfigFetchingCompleted.value = isCompleted
    }



    var SHOULD_BE_LOCAL_REQUEST = true
    var SHOULD_SHOW_ADVERTISEMENT= false
}