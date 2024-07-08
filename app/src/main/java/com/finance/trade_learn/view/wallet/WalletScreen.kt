package com.finance.trade_learn.view.wallet


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.view.LocalWalletPageViewModel
import java.util.ArrayList
import java.util.Locale


@Composable
fun WalletScreen() {
    val viewModel = LocalWalletPageViewModel.current
    LaunchedEffect(Unit) {
        viewModel.getMyCoinsDetails()
    }

    Scaffold(
        topBar = { WalletTopBar() },
        content = {paddingValue ->
            WalletContent(modifier = Modifier.padding(paddingValue))
        }

    )
}

@Composable
fun WalletTopBar() {
    TopAppBar(
        title = { Text("Crypto Wallet") },
        backgroundColor = Color.Blue,
        contentColor = Color.White
    )
}

@Composable
fun WalletContent(modifier: Modifier) {
    val viewModel = LocalWalletPageViewModel.current


    val cryptoItems = viewModel.myCoinsNewModel.observeAsState(emptyList())


    var searchedItem by remember { mutableStateOf("") }
    var resultItems by remember { mutableStateOf(emptyList<NewModelForItemHistory>()) }

    val textChanged : (String) -> Unit =textChangedScope@{
        searchedItem = it
        resultItems = getSearchedList(searchedItem, itemList = viewModel.myCoinsNewModel.value )
    }



    Column(modifier = Modifier.padding(16.dp)) {
        // Başlık ve çizgi
        Text(
            text = "Crypto Wallet",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        // Toplam bakiye gösterimi
        val totalBalance = cryptoItems.value?.sumOf { it.Total.toDouble() }
        Text(
            text = "Toplam Bakiye",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "\$${totalBalance?.format(2)}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Arama kısmı
        val searchQuery = remember { mutableStateOf(TextFieldValue()) }
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Arama yapın...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Kripto item listesi
        LazyColumn {
            // Başlık satırı
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Coin", style = MaterialTheme.typography.bodySmall)
                    Text("Amount", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Text("Value", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Kripto itemlerin listesi
            items(cryptoItems.value.filter {
                it.CoinName.contains(searchQuery.value.text, ignoreCase = true)
            }) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.CoinName, style = MaterialTheme.typography.bodyMedium)
                    Text(item.CoinAmount.toString(), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text("\$${(item.CoinAmount * item.Total).toDouble().format(2)}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // İşlem geçmişi butonu
        Button(
            onClick = { /* İşlem geçmişi ekranına git */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("İşlem Geçmişi")
        }
    }
}



@Composable
@Preview
private fun WalletScreenPreview(){
    Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            WalletScreen()
        }
    }
}


// Veri modeli
data class CryptoItem(val name: String, val amount: Double, val usdValue: Double)

// Veri kaynağı (örnek veriler)
val cryptoItems = listOf(
    CryptoItem("Bitcoin", 1.5, 45000.0),
    CryptoItem("Ethereum", 10.0, 2500.0),
    CryptoItem("Litecoin", 20.0, 150.0)
)

fun Double.format(digits: Int) = "%.${digits}f".format(this)


private fun getSearchedList (searchedItem: String, itemList: ArrayList<NewModelForItemHistory>?): List<NewModelForItemHistory> {
    val queryCoin = searchedItem.uppercase(Locale.getDefault())

    if (queryCoin.isEmpty()) return itemList?.map { it } ?: emptyList()

    return itemList?.filter { item ->
        item.CoinName.contains(queryCoin, ignoreCase = true)
    } ?: emptyList()
}