package com.finance.trade_learn.base


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.models.DataForHomePage
import com.finance.trade_learn.models.UserBalance
import com.finance.trade_learn.models.UserInfo
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.watchList.WatchListItem
import com.finance.trade_learn.models.watchList.WatchListRequestItem
import com.finance.trade_learn.service.ctryptoApi.cryptoService
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.utils.ConvertOperation
import com.finance.trade_learn.utils.DataStoreKeys
import com.finance.trade_learn.utils.RemoteConfigs
import com.finance.trade_learn.utils.readStringPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
) : ViewModel() {


    private var userEmail = ""
    private var userPassword = ""


    private var baseDisposable: CompositeDisposable = CompositeDisposable()


    var currentItemsLiveData = MutableLiveData<List<CoinsHome>>()
    var listOfCryptoForPopular = MutableLiveData<List<CoinsHome>>()

    private val _shouldShowBottomNavigationBar = MutableLiveData<Boolean>()
    val shouldShowBottomNavigationBar : LiveData<Boolean> get() = _shouldShowBottomNavigationBar

    fun setBottomNavigationBarStatus(shouldShow : Boolean){
        _shouldShowBottomNavigationBar.value = shouldShow
    }

    fun getAllCrypto(page : Int) {
        setLockMainActivityStatus(true)

        viewModelScope.launch {
            val response = cryptoService().getCoinList(page)
            setLockMainActivityStatus(false)

            when(response.isSuccessful){
                true -> {
                    response.body()?.data?.let {
                        val newList = it.filter {newItem->
                            !allCryptoItems.value.any {oldItem -> oldItem.id == newItem.id }
                        }
                        allCryptoItems.value.addAll(newList)

                        val data = convertCryptoList(allCryptoItems.value)
                        if (data.ListOfCrypto.isNotEmpty()){
                            currentItemsLiveData.value = data.ListOfCrypto
                            listOfCryptoForPopular.value = convertPopularCoinList(data.ListOfCrypto)

                            currentItems = data.ListOfCrypto
                            lastItems = data.lastCrypoList
                        }
                    }
                }
                false -> {
                    RemoteConfigs.SHOULD_BE_LOCAL_REQUEST = !RemoteConfigs.SHOULD_BE_LOCAL_REQUEST
                    currentItemsLiveData.value = currentItems
                    listOfCryptoForPopular.value = convertPopularCoinList(currentItems)
                }
            }

        }
    }

    fun convertCryptoList(t: List<CoinDetail>): DataForHomePage {
        return ConvertOperation(t, lastItems).convertDataToUse()
    }


    private fun convertPopularCoinListShortByTotalVolume(list: ArrayList<CoinsHome>?): List<CoinsHome>? {
        return list?.sortedBy {
            it.total_volume
        }?.take(3)
    }

    private fun convertPopularCoinList(list: List<CoinsHome>?): ArrayList<CoinsHome> {
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

            val maxItemSize = if (list.isEmpty()) 0 else minOf(list.size, 12)
            for (i in list.sortedBy { it.total_volume }.subList(0, maxItemSize)) {
                if (!popList.contains(i)) popList.add(i)
            }
        }
        return popList
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
            val userService = UserApi()
            val response = userService.getUserInfo(email = userEmail)

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


    override fun onCleared() {
        super.onCleared()
        baseDisposable.clear()
    }

    companion object {
        var currentItems : List<CoinsHome> = emptyList()
        var lastItems : List<CoinsHome> = emptyList()

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