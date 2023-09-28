package com.finance.trade_learn.view

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentHistoryOfTradeBinding
import com.finance.trade_learn.viewModel.ViewModelHistoryTrade


class TradeHistory : BaseFragmentViewModel<FragmentHistoryOfTradeBinding, ViewModelHistoryTrade>(FragmentHistoryOfTradeBinding::inflate){

    override val viewModel: ViewModelHistoryTrade by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
        super.onViewCreated(view, savedInstanceState)
    }
    
    private fun setup() {
        getDataFromDatabase()
        setComposeView()
    }
    
    private fun setComposeView() {
        binding.composeView.setContent {
            viewModel.listOfTrade.observeAsState().value?.let {

                Column(modifier = Modifier.fillMaxSize()) {
                    val titles = arrayOf(
                        stringResource(id = R.string.name),
                        stringResource(id = R.string.Amount),
                        stringResource(id = R.string.price),
                        stringResource(id = R.string.total),
                        stringResource(id = R.string.date),
                        stringResource(id = R.string.state)
                    )

                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        titles.forEach {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(5.dp),
                                text = it,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .background(colorResource(id = R.color.hint_grey))
                            .fillMaxWidth()
                            .height(1.dp)
                    ) {}

                    HistoryItemComposeView(it)
                }
            }
        }

    }

    private fun getDataFromDatabase() {
        viewModel.getDataFromDatabase(requireContext())
    }

}