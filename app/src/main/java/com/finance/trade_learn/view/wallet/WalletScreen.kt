package com.finance.trade_learn.view.wallet


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.view.LocalWalletPageViewModel
import java.util.Locale


@Composable
fun WalletScreen(
    navigateToHistoryPage : () -> Unit
) {
    val viewModel = LocalWalletPageViewModel.current
    LaunchedEffect(Unit) {
        viewModel.getMyCoinsDetails()
    }

    Scaffold(
        topBar = { WalletTopBar() },
        content = {paddingValue ->
            WalletContent(modifier = Modifier.padding(paddingValue), navigateToHistoryPage = navigateToHistoryPage)
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
fun WalletContent(modifier: Modifier, navigateToHistoryPage: () -> Unit) {
    val viewModel = LocalWalletPageViewModel.current


    val cryptoItems = viewModel.myCoinsNewModel.observeAsState(emptyList())

    val animatedBalance by animateFloatAsState(
        targetValue = cryptoItems.value?.sumOf { it.Total.toDouble() }?.toFloat() ?: 0.0f ,
        animationSpec = tween(
            durationMillis = 2000,
            easing = FastOutSlowInEasing
        ), label = ""
    )

    Column(modifier = Modifier.padding(16.dp)) {
        // Başlık ve çizgi
        Text(
            text = "Crypto Wallet",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        // Toplam bakiye gösterimi
        Text(
            text = "Toplam Bakiye",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "\$${animatedBalance.toDouble()?.format(2)}",
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
                    Text("Amount", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Value", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth(0.3f), textAlign = TextAlign.End)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Kripto itemlerin listesi
            items(cryptoItems.value.filter {
                searchQuery.value.text.isBlank() || it.CoinName.contains(searchQuery.value.text, ignoreCase = true)
            }) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.CoinName, style = MaterialTheme.typography.bodyMedium)
                    Text(item.CoinAmount.toDouble().format(6), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("\$${(item.Total).toDouble().format(2)}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth(0.3f), textAlign = TextAlign.End)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // İşlem geçmişi butonu
        Button(
            onClick = {
                navigateToHistoryPage.invoke()
            },
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
            WalletScreen{

            }
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