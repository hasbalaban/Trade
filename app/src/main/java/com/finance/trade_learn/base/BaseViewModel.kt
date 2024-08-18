package com.finance.trade_learn.base


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.models.DataForHomePage
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.service.ctryptoApi.cryptoService
import com.finance.trade_learn.utils.ConvertOperation
import com.finance.trade_learn.utils.DataStoreKeys
import com.finance.trade_learn.utils.RemoteConfigs
import com.finance.trade_learn.utils.readStringPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
) : ViewModel() {

    //private val _isLogin = MutableStateFlow<Boolean>(false)
    //val isLogin : StateFlow<Boolean> get() = _isLogin

    private var baseDisposable: CompositeDisposable = CompositeDisposable()


    var isLoading = MutableLiveData<Boolean>(false)

    var currentItemsLiveData = MutableLiveData<List<CoinsHome>>()
    var listOfCryptoForPopular = MutableLiveData<List<CoinsHome>>()

    private val _shouldShowBottomNavigationBar = MutableLiveData<Boolean>()
    val shouldShowBottomNavigationBar : LiveData<Boolean> get() = _shouldShowBottomNavigationBar

    fun setBottomNavigationBarStatus(shouldShow : Boolean){
        _shouldShowBottomNavigationBar.value = shouldShow
    }

    fun getAllCrypto(page : Int) {
        isLoading.value = true
        viewModelScope.launch {
            val response = cryptoService().getCoinList(page)
            when(response.isSuccessful){
                true -> {

                    isLoading.value = false

                    response.body()?.data?.let {
                        val newList = it.filter {newItem->
                            !allCryptoItems.any {oldItem -> oldItem.id == newItem.id }
                        }
                        allCryptoItems.addAll(newList)

                        val data = convertCryptoList(allCryptoItems)
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

                    isLoading.value = false

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



    suspend fun isLogin(context: Context){
        val userEmail =  context.readStringPreference(DataStoreKeys.StringKeys.email)

        val userpassword = context.readStringPreference(DataStoreKeys.StringKeys.password)

        userEmail.zip(userpassword){email, password ->
            //_isLogin.value = email.isNotBlank() && password.isNotBlank()
        }
    }



    override fun onCleared() {
        super.onCleared()
        baseDisposable.clear()
    }

    companion object {
        var currentItems : List<CoinsHome> = emptyList()
        var lastItems : List<CoinsHome> = emptyList()

        var allCryptoItems = ArrayList<CoinDetail>()
    }

}