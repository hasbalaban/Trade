package com.finance.trade_learn.view

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.finance.trade_learn.Adapters.solveCoinName
import com.finance.trade_learn.viewModel.ViewModelMyWallet
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentWalletPageBinding
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class WalletPage : BaseFragmentViewModel<FragmentWalletPageBinding, ViewModelMyWallet>(FragmentWalletPageBinding:: inflate) {

    override val viewModel: ViewModelMyWallet by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
        setComposeView()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setup() {
        viewModel.getMyCoinsDetails()
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    private fun setComposeView(){
        binding.composeView.setContent {


            val myCoins = viewModel.myCoinsNewModel.observeAsState().value?.map { it }
            var searchedItem by remember { mutableStateOf("") }
            var resultItems by remember { mutableStateOf(emptyList<NewModelForItemHistory>()) }

            val textChanged : (String) -> Unit =  textChangedScope@{
                searchedItem = it
                resultItems = getItemsList(searchedItem)
            }
            val totalValuePrice = viewModel.totalValue.observeAsState().value?.let {
                ("≈ " + (it.toString() + "000000000000")).subSequence(0, 10).toString()
            } ?: ""
            
            Column(modifier = Modifier.fillMaxSize()) {
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        modifier = Modifier.size(64.dp).padding(start = 12.dp, top = 6.dp),
                        painter = painterResource(id = R.drawable.ust), contentDescription = null,
                        contentScale = ContentScale.Inside
                    )

                    Text(
                        modifier = Modifier.weight(1f).padding(10.dp),
                        text = stringResource(id = R.string.TotalValue),
                        textAlign = TextAlign.Center,
                        color = colorResource(id = R.color.onClickSellBack),
                        fontSize = 30.sp
                    )
                }


                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                        .padding(10.dp),
                    text = totalValuePrice,
                    color = colorResource(id = R.color.pozitive),
                    fontSize =24.sp
                )

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.light_grey))
                    .height(1.dp)
                ) {}



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

                Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    Text(modifier = Modifier.weight(1f), text = "")
                    Text(modifier = Modifier.weight(2f), text = stringResource(id = R.string.Symbol), textAlign = TextAlign.Start, color = Color.Red, fontSize = 16.sp)
                    Text(modifier = Modifier.weight(3f), text = stringResource(id = R.string.Amount), textAlign = TextAlign.Start, color = Color.Red, fontSize = 16.sp)
                    Text(modifier = Modifier.weight(2f), text = stringResource(id = R.string.Value), textAlign = TextAlign.Start, color = Color.Red, fontSize = 16.sp)

                }

                if (searchedItem.isEmpty()){
                    myCoins?.let {
                        WalletItemComposeView(it){itemName->
                            val coinName = solveCoinName(itemName)
                            SharedPreferencesManager(requireContext()).addSharedPreferencesString("coinName", coinName)
                            Navigation.findNavController(binding.root).navigate(R.id.tradePage)
                        }
                    }
                }
                else{
                    resultItems.let {
                        WalletItemComposeView(it){itemName->
                            val coinName = solveCoinName(itemName)
                            SharedPreferencesManager(requireContext()).addSharedPreferencesString("coinName", coinName)
                            Navigation.findNavController(binding.root).navigate(R.id.tradePage)
                        }
                    }
                }
                
            }
        }
    }

    private fun getItemsList (searchedItem: String): List<NewModelForItemHistory> {
        val queryCoin = searchedItem.uppercase(Locale.getDefault())
        return if (queryCoin.isNotEmpty()) {
            return viewModel.myCoinsNewModel.value?.filter { item ->
                item.CoinName.contains(queryCoin, ignoreCase = true)
            } ?: emptyList()
        }
        else viewModel.myCoinsNewModel.value?.map { it } ?: emptyList()
    }

}