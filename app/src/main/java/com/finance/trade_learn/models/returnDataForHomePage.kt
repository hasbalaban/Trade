package com.finance.trade_learn.models

import androidx.lifecycle.MutableLiveData
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.models.modelsConvector.CoinsHome

open class returnDataForHomePage (
    val ListOfCryptoForCompare: MutableLiveData<List<CoinsHome>>,
    var ListOfCrypto : MutableLiveData<ArrayList<CoinsHome>>,
    var change :enumPriceChange

    )