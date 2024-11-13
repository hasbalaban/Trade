package com.finance.trade_learn.view.trade

import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.view.wallet.format

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
    var selectedItemId : String = "",
) {
    var isLogin: Boolean
        get() = BaseViewModel.isLogin.value
        set(value) {
            _isLogin = value
        }
    var currentPrice: Double
        get() = _currentPrice
        set(value) {
            _currentPrice = value // Set the value with 4 decimal precision
        }
    var dailyPercentChange: Double
        get() = _dailyPercentChange.format(2).toDouble()
        set(value) {
            _dailyPercentChange = value // Set the value with 4 decimal precision
        }
    var balance: Double
        get() = _balance.format(2).toDouble()
        set(value) {
            _balance = value // Set the value with 4 decimal precision
        }
    var ownedAmount: Double
        get() = _ownedShares.format(6).toDouble()
        set(value) {
            _ownedShares = value // Set the value with 4 decimal precision
        }
    var transactionAmount: Double
        get() = _transactionAmount.format(8).toDouble()
        set(value) {
            _transactionAmount = value // Set the value with 4 decimal precision
        }
    val totalTransactionCost: Double
        get() = (currentPrice * transactionAmount).format(4).toDouble()


    val isBuyEnabled : Boolean
        get() = balance > 0  && totalTransactionCost > 0 && balance >= totalTransactionCost
                && totalTransactionCost >= 0
                && !selectedItemId.equals(other = "tether", ignoreCase = true)

    val isSellEnabled : Boolean
        get() = transactionAmount > 0 && ownedAmount >= transactionAmount
                && totalTransactionCost >= 0
                && !selectedItemId.equals(other = "tether", ignoreCase = true)


}
