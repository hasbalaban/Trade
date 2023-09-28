package com.finance.trade_learn.view

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.SearchFragmentBinding
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import com.finance.trade_learn.utils.SharedPreferencesManager
import com.finance.trade_learn.viewModel.SearchCoinViewModel
import java.util.*

class SearchFragment :  BaseFragmentViewModel<SearchFragmentBinding, SearchCoinViewModel>(SearchFragmentBinding::inflate) {

    override val viewModel: SearchCoinViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().setTheme(R.style.thema_search)
        setup()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setup() {
        viewModel.getCoinList()
        setComposeView()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun setComposeView (){
        binding.composeView.setContent {
            var searchedItem by remember { mutableStateOf("") }
            var resultItems by remember { mutableStateOf(emptyList<CoinInfoList>()) }

            val textChanged : (String) -> Unit =  {
                searchedItem = it
                resultItems = getItemsList(searchedItem)
            }

            Column(modifier = Modifier.fillMaxSize()) {

                TextField(
                    modifier = Modifier
                        .padding(top = 1.dp, start = 2.dp, end = 2.dp)
                        .fillMaxWidth(),
                    value = searchedItem, onValueChange = {value -> textChanged(value) },
                    placeholder = {
                        Text(text = stringResource(id = R.string.hintSearch))
                    },
                    maxLines = 1,
                    singleLine = true
                    )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(
                        items = resultItems,
                        key = {
                            it.coinId
                        }
                    ) {
                        SearchItemComposeView(CoinInfo = it){itemId ->
                            SharedPreferencesManager(requireContext()).addSharedPreferencesString("coinName", itemId)
                            val directions = SearchFragmentDirections.actionSearchActivityToTradePage()
                            Navigation.findNavController(binding.root).navigate(directions)
                        }
                    }
                }

            }
        }
    }

    private fun getItemsList (searchedItems : String): List<CoinInfoList> {
        if (searchedItems.isEmpty()) return emptyList()

        val queryList = viewModel.coinListDetail.value?.filter {
            it.name.contains(searchedItems, ignoreCase = true)
        }
        return queryList ?: emptyList()
    }
}