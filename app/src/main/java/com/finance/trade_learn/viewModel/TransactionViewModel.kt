package com.finance.trade_learn.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions
import com.finance.trade_learn.database.dataBaseService
import com.finance.trade_learn.models.ResetPasswordRequest
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.view.history.TransactionViewState
import com.finance.trade_learn.view.loginscreen.codeverification.CodeVerificationViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class TransactionViewModel : ViewModel() {
    private val _transactionViewState = MutableStateFlow<TransactionViewState>(TransactionViewState())
    val transactionViewState: StateFlow<TransactionViewState> get() = _transactionViewState



    private val _transactionHistoryResponse = MutableStateFlow<WrapResponse<List<UserTransactions>>>(WrapResponse())
    val transactionHistoryResponse: StateFlow<WrapResponse<List<UserTransactions>>> get() = _transactionHistoryResponse

    fun getTransactionHistory(){
        val email = BaseViewModel.userInfo.value.data?.email ?: return
        _transactionViewState.value = transactionViewState.value.copy(isLoading = true)
        BaseViewModel.setLockMainActivityStatus(true)

        viewModelScope.launch {
            val userService = UserApi()

            val response = userService.getTransactionHistory(email = email)

            _transactionViewState.value = transactionViewState.value.copy(isLoading = false)
            BaseViewModel.setLockMainActivityStatus(false)

            if (response.isSuccessful){
                response.body()?.let {
                    _transactionHistoryResponse.value = it
                }
                println(response.body()?.success)
                response.body()?.data
                return@launch
            }

            println(response.message())
            println(response.body()?.message)
            println(response.body()?.error)
            println(response.body()?.success)


        }
    }



    fun getDataFromDatabase(context: Context) {
        val dao = dataBaseService.invoke(context).databaseDao()
        CoroutineScope(Dispatchers.Main).launch {
            val list = dao.getAllTrades()
            convertListForAdapter(list)
        }
    }

    private fun convertListForAdapter(list: List<UserTransactions>) {

        val userTransactions = ArrayList<UserTransactions>()
        for (i in list) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val dateStr = i.date
            val timeInMillis = sdf.parse(dateStr).time.toString()



            val name = i.transactionItemName
            val amount = i.amount.toBigDecimal()
            val price = i.price.toBigDecimal()
            val total = i.transactionTotalPrice.toBigDecimal()

            val state = i.transactionType
            val itemOfHistory = UserTransactions(
                id = i.id,
                transactionItemName = name,
                amount = amount.toString(),
                price = price.toString(),
                transactionTotalPrice = total.toString(),
                transactionType = state,
                date = timeInMillis
            )
            userTransactions.add(itemOfHistory)
        }
        _transactionHistoryResponse.value = WrapResponse(
            success = true,
            data = list
        )
    }

}