package com.finance.trade_learn.view.trade

import com.finance.trade_learn.base.BaseViewModel

data class TradePageUiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
)


data class BuySellScreenUiState(
    val isLoading: Boolean = false,
    val data: BuySellScreenData = BuySellScreenData()
)

data class BuySellScreenData(
    private var _isLogin: Boolean = BaseViewModel.isLogin.value,
    private var _currentPrice: Double = 0.0,
    private var _dailyPercentChange: Double = 0.0,
    private var _balance: Double = 0.0,
    private var _ownedShares: Double = 0.0,
    private var _transactionAmount: Double = 0.0, // Private field
) {
    var isLogin: Boolean
        get() = BaseViewModel.isLogin.value
        set(value) {
            _isLogin = value
        }
    var currentPrice: Double
        get() = _currentPrice.formatToDecimals(6)
        set(value) {
            _currentPrice = value // Set the value with 4 decimal precision
        }
    var dailyPercentChange: Double
        get() = _dailyPercentChange.formatToDecimals(2)
        set(value) {
            _dailyPercentChange = value // Set the value with 4 decimal precision
        }
    var balance: Double
        get() = _balance.formatToDecimals(2)
        set(value) {
            _balance = value // Set the value with 4 decimal precision
        }
    var ownedAmount: Double
        get() = _ownedShares.formatToDecimals(6)
        set(value) {
            _ownedShares = value // Set the value with 4 decimal precision
        }
    var transactionAmount: Double
        get() = _transactionAmount.formatToDecimals(8)
        set(value) {
            _transactionAmount = value // Set the value with 4 decimal precision
        }
    val totalTransactionCost: Double
        get() = (currentPrice * transactionAmount).formatToDecimals(4)


    val isBuyEnabled : Boolean
        get() = balance >= totalTransactionCost
    val isSellEnabled : Boolean
        get() = ownedAmount >= transactionAmount


}


fun Double.formatToDecimals(decimals: Int): Double {
    return "%.${decimals}f".format(this).toDouble()
}
