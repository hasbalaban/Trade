package com.finance.trade_learn.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.trade_learn.viewModel.ViewModelMyWallet
import com.finance.trade_learn.Adapters.adapter_for_my_wallet
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentWalletPageBinding
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class WalletPage : BaseFragmentViewModel<FragmentWalletPageBinding, ViewModelMyWallet>(FragmentWalletPageBinding:: inflate), TextWatcher {


    private var viewVisible = true
    private lateinit var dataBindingWallet: FragmentWalletPageBinding
    private lateinit var adapter: adapter_for_my_wallet
    override val viewModel: ViewModelMyWallet by viewModels()
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
        viewModel.getMyCoinsDetails()
        dataBindingWallet.searchMyCoins.addTextChangedListener(this)
        dataBindingWallet.myCoins.layoutManager = LinearLayoutManager(requireContext())
        dataBindingWallet.myCoins.adapter = adapter
    }

    private fun observerFun() {
        getMyWalletDetails()
    }

    @SuppressLint("SetTextI18n")
    private fun getMyWalletDetails() {
        viewModel.myCoinsNewModel.observe(viewLifecycleOwner) {
            it?.let {
                myCoinsList.clear()
                myCoinsList.addAll(it)
                adapter.updateRecyclerView(it)
            }
        }

        viewModel.totalValue.observe(viewLifecycleOwner) { totalValue ->
            dataBindingWallet.totalValue.text =
                ("≈ " + (totalValue.toString() + "000000000000")).subSequence(0, 10).toString()
        }
    }

    override fun onResume() {
        getMyWalletDetails()
        viewModel.myCoinsNewModel.value = viewModel.myCoinsNewModel.value
        super.onResume()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val queryCoin = dataBindingWallet.searchMyCoins.text.toString().uppercase(Locale.getDefault())
        if (queryCoin.isNotEmpty()) {
            val newList = myCoinsList.filter { item ->
                item.CoinName.contains(queryCoin, ignoreCase = true)
            }
            adapter.updateRecyclerView(newList as ArrayList<NewModelForItemHistory>)
            return
        }
        adapter.updateRecyclerView(myCoinsList)
    }

    override fun onPause() {
        job?.cancel()
        super.onPause()
    }

}