package com.finance.trade_learn.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


object RemoteConfigsConst{
    const val SHOULD_BE_LOCAL_REQUEST = "SHOULD_BE_LOCAL_REQUEST"
    const val SHOULD_ADVERTISEMENT = "SHOULD_ADVERTISEMENT"
}


object RemoteConfigs {
    var SHOULD_BE_LOCAL_REQUEST = true
    var SHOULD_ADVERTISEMENT= true
}