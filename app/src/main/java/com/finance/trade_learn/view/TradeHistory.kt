package com.finance.trade_learn.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.trade_learn.Adapters.AdapterForHistoryTrade
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.database.dataBaseEntities.SaveCoin
import com.finance.trade_learn.databinding.FragmentHistoryOfTradeBinding
import com.finance.trade_learn.viewModel.ViewModeHomePage
import com.finance.trade_learn.viewModel.viewModelHistoryTrade


class TradeHistory : BaseFragmentViewModel<FragmentHistoryOfTradeBinding, ViewModeHomePage>(FragmentHistoryOfTradeBinding::inflate){

    private lateinit var viewModelHistory: viewModelHistoryTrade
    private lateinit var dataBindinghistoryOfTrade: FragmentHistoryOfTradeBinding
    override val viewModel: ViewModeHomePage by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dataBindinghistoryOfTrade = FragmentHistoryOfTradeBinding.inflate(inflater, container, false)
        return dataBindinghistoryOfTrade.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
        super.onViewCreated(view, savedInstanceState)
    }

    // function holder
    private fun setup() {
        callOneTime()

    }

    // just call when fragment start
    private fun callOneTime() {
        settingViewModel()
    }

    // this fun for viewModel Settings
    private fun settingViewModel() {
        viewModelHistory = viewModelHistoryTrade()
        viewModelHistory.getDataFromDatabase(requireContext())
        getDataFromDatabase()
    }


    // this fun for get data from database as observe data
    private fun getDataFromDatabase() {
        viewModelHistory.listOfTrade.observe(viewLifecycleOwner) {
            it?.let {
                settingsOfRecyclerView(it)
            }
        }

    }

    // this fun for recycler view set data
    private fun settingsOfRecyclerView(list_of_trade: ArrayList<SaveCoin>) {
        dataBindinghistoryOfTrade.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AdapterForHistoryTrade(list_of_trade)
        dataBindinghistoryOfTrade.recyclerViewHistory.adapter = adapter

    }

}