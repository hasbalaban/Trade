package com.finance.trade_learn.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.trade_learn.viewModel.ViewModelMyWallet
import com.finance.trade_learn.Adapters.adapter_for_my_wallet
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.FragmentWalletPageBinding
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.utils.Ads
import com.finance.trade_learn.utils.sharedPreferencesManager
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class WalletPage : Fragment(), TextWatcher {


    private var viewVisible = true
    private lateinit var dataBindingWallet: FragmentWalletPageBinding
    private lateinit var adapter: adapter_for_my_wallet
    private lateinit var viewModelMyWallet: ViewModelMyWallet
    //  private var disposable = CompositeDisposable()

    private var myCoinsList = ArrayList<NewModelForItemHistory>()
    private var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = adapter_for_my_wallet(arrayListOf())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBindingWallet = DataBindingUtil.inflate(
            inflater, R.layout.fragment_wallet_page,
            container, false
        )
        return dataBindingWallet.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
        observerFun()
        //setAd()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setup() {
        viewModelMyWallet = ViewModelMyWallet(requireContext())
        viewModelMyWallet.getMyCoinsDetails()
        dataBindingWallet.searchMyCoins.addTextChangedListener(this)
        dataBindingWallet.myCoins.layoutManager = LinearLayoutManager(requireContext())
        dataBindingWallet.myCoins.adapter = adapter
    }

    private fun observerFun() {
        getMyWalletDetails()
    }

    private fun getMyWalletDetails() {
        if (viewVisible) {
            viewModelMyWallet.myCoinsNewModel.observe(viewLifecycleOwner) {
                it?.let {
                    myCoinsList.clear()
                    myCoinsList.addAll(it)
                    adapter.updateRecyclerView(it)
                    if (viewVisible) { viewModelMyWallet.totalValue.observe(viewLifecycleOwner) { totalValue ->
                            dataBindingWallet.totalValue.setText(("≈ " + (totalValue.toString() + "000000000000")).subSequence(0, 10).toString())
                        }
                    }
                }
            }

        }
    }

    override fun onResume() {
        getMyWalletDetails()
        viewModelMyWallet.myCoinsNewModel.value = viewModelMyWallet.myCoinsNewModel.value
        super.onResume()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val queryCoin = dataBindingWallet.searchMyCoins.text.toString().uppercase(Locale.getDefault())
        if (queryCoin != "") {
            val newList = myCoinsList.filter { item ->
                item.CoinName.contains(queryCoin)
            }
            adapter.updateRecyclerView(newList as ArrayList<NewModelForItemHistory>)
        } else {
            adapter.updateRecyclerView(myCoinsList)
        }
    }

    private fun setAd() {

        val currentMillis = System.currentTimeMillis()
        val updateTime = sharedPreferencesManager(requireContext()).getSharedPreferencesLong("walletPage",currentMillis)
        val delayTime = if (currentMillis >= updateTime) 0L else updateTime-currentMillis
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(delayTime)
            withContext(Dispatchers.Main) {
                dataBindingWallet.adView.apply {
                    loadAd(AdRequest.Builder().build())
                    adListener = Ads.listenerAdRequest(dataBindingWallet.adView,"walletPage",requireContext())
                }
            }
        }
    }

    override fun onPause() {
        job?.cancel()
        super.onPause()
    }




}