package com.finance.trade_learn.base


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.models.UserBalance
import com.finance.trade_learn.models.UserInfo
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.watchList.WatchListItem
import com.finance.trade_learn.models.watchList.WatchListRequestItem
import com.finance.trade_learn.service.ctryptoApi.cryptoService
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.utils.DataStoreKeys
import com.finance.trade_learn.utils.readStringPreference
import com.finance.trade_learn.utils.transformationCoinItemDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
) : ViewModel() {


    private var userEmail = ""
    private var userPassword = ""


    var listOfCryptoForPopular = MutableLiveData<List<NewModelForItemHistory>>()

    private val _shouldShowBottomNavigationBar = MutableLiveData<Boolean>()
    val shouldShowBottomNavigationBar : LiveData<Boolean> get() = _shouldShowBottomNavigationBar

    fun setBottomNavigationBarStatus(shouldShow : Boolean){
        _shouldShowBottomNavigationBar.value = shouldShow
    }

    fun getAllCrypto() {
        viewModelScope.launch {
            val response = cryptoService().getCoinList()

            when(response.isSuccessful){
                true -> {
                    response.body()?.data?.let {newList ->
                        val copiedList = allCryptoItems.value

                        copiedList.removeAll { oldItem ->
                            newList.any {newItem ->  newItem.id == oldItem.id }
                        }
                        copiedList.addAll(newList)
                        allCryptoItems.value = copiedList

                        val mappedList = convertCryptoList(allCryptoItems.value)
                        if (mappedList.isNotEmpty()){
                            listOfCryptoForPopular.value = convertPopularCoinList(mappedList)

                            currentItems.value = mappedList
                        }
                    }
                }
                false -> {
                    listOfCryptoForPopular.value = convertPopularCoinList(currentItems.value)
                }
            }

        }
    }

    private fun convertCryptoList(t: List<CoinDetail>): ArrayList<CoinsHome> {
        return transformationCoinItemDTO(list = t)
    }




    private fun convertPopularCoinList(list: List<CoinsHome>?): List<NewModelForItemHistory> {
        val popList = arrayListOf<CoinsHome>()
        val populerlist = mutableListOf("bit", "bnb", "eth", "sol", "gate", "avax")
        list?.let{
            for (i in list){
                if (popList.size == 3) break
                if (populerlist.contains(i.CoinName.subSequence(0, 3).toString().lowercase())) {
                    popList.add(i)
                    populerlist.remove(i.CoinName.subSequence(0, 3))
                }
            }

            val maxItemSize = if (list.isEmpty()) 0 else minOf(list.size, 20)
            for (i in list.sortedBy { it.total_volume }.subList(0, maxItemSize)) {
                if (!popList.contains(i)) popList.add(i)
            }
        }
        return popList.map {
            NewModelForItemHistory(
                CoinName = it.CoinName.split(" ").first(),
                CoinAmount = it.CoinPrice.toDoubleOrNull() ?: 0.0,
                Total = BigDecimal.ZERO,
                Image = it.CoinImage,
                currentPrice = it.CoinPrice + " $" ,
            )
        }
    }


    private var job : Job = Job()
    fun checkUserInfo(context: Context) {
        job.cancel()
        job = viewModelScope.launch {
            val userEmailFlow = context.readStringPreference(DataStoreKeys.StringKeys.email)
            val userPasswordFlow = context.readStringPreference(DataStoreKeys.StringKeys.password)

            userEmailFlow.zip(userPasswordFlow) { email, password ->
                email to password
            }.collect { result ->
                if (result.first.isNotBlank() && result.second.isNotBlank()) {
                    userEmail = result.first
                    userPassword = result.second
                    getUserInfo()
                    return@collect
                }


                _userInfo.value = WrapResponse()
                _isLogin.value = false
            }

        }
    }

    suspend fun getUserInfo() {
        viewModelScope.launch {
            setLockMainActivityStatus(true)
            val userService = UserApi()
            val response = userService.getUserInfo(email = userEmail)

            setLockMainActivityStatus(false)

            if (response.isSuccessful){
                response.body()?.let {
                    updateUserInfo(it)
                    updateUserLoginStatus(isLogin = true)
                }
                println(response.body()?.success)
                response.body()?.data
                return@launch
            }

            updateUserLoginStatus(isLogin = false)

            println(response.message())
            println(response.body()?.message)
            println(response.body()?.error)
            println(response.body()?.success)
        }
    }

    suspend fun saveOrRemoveWatchListItem(itemId : String) {
        val userInfo = userInfo.value.data ?: return
        val isRemoved = userInfo.userWatchList.any {
            it.itemId.contains(itemId)
        }

        val watchListRequestItem = WatchListRequestItem(
            userId = userInfo.userId,
            itemId = itemId,
            isRemoved = isRemoved,
        )
        viewModelScope.launch {
            setLockMainActivityStatus(true)
            val userService = UserApi()
            val response = userService.addOrRemoveWatchListItem(watchListRequestItem = watchListRequestItem)

            setLockMainActivityStatus(false)
            if (response.isSuccessful){
                response.body()?.data?.let {
                    updateUserWatchList(it)
                }
                println(response.body()?.success)
                response.body()?.data
                return@launch
            }

            updateUserLoginStatus(isLogin = false)

            println(response.message())
            println(response.body()?.message)
        }
    }


    companion object {
        var currentItems = MutableStateFlow<MutableList<CoinsHome>>(mutableListOf())
        var allCryptoItems = MutableStateFlow<MutableList<CoinDetail>>(mutableListOf())


        private val _userInfo = MutableStateFlow<WrapResponse<UserInfo>>(WrapResponse())
        val userInfo : StateFlow<WrapResponse<UserInfo>> get() = _userInfo


        private val _isLogin = MutableStateFlow<Boolean>(false)
        val isLogin : StateFlow<Boolean> get() = _isLogin


        private val _lockMainActivityToAction = MutableLiveData<Boolean>()
        val lockMainActivityToAction : LiveData<Boolean> get() = _lockMainActivityToAction


        fun updateUserInfo(response : WrapResponse<UserInfo>){
            _userInfo.value = response
        }

        fun updateUserBalance(updatedBalance : List<UserBalance>){
            _userInfo.value = userInfo.value.copy(data = userInfo.value.data?.copy(balances = updatedBalance))
        }

        fun updateUserWatchList(userWatchList: List<WatchListItem>){
            _userInfo.value = userInfo.value.copy(data = userInfo.value.data?.copy(userWatchList = userWatchList))
        }

        fun updateUserLoginStatus(isLogin : Boolean){
            _isLogin.value = isLogin
        }


        fun setLockMainActivityStatus(shouldLockScreen : Boolean){
            _lockMainActivityToAction.postValue(shouldLockScreen)
        }
    }

}