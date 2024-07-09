package com.finance.trade_learn.view.wallet

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.view.LocalWalletPageViewModel
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    navigateToHistoryPage : () -> Unit,
) {
    val viewModel = LocalWalletPageViewModel.current
    LaunchedEffect(Unit) {
        viewModel.getMyCoinsDetails()
    }

    Scaffold(
        topBar = { WalletTopBar() },
        content = {
            WalletContent(modifier = modifier, navigateToHistoryPage = navigateToHistoryPage)
        }
    )
}

@Composable
fun WalletTopBar() {
    androidx.compose.material.TopAppBar(
        title = { Text("Crypto Wallet", color = MaterialTheme.colorScheme.onPrimary) },
        backgroundColor = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        // Toplam bakiye gösterimi
        Text(
            text = "Toplam Bakiye",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "\$${animatedBalance.toDouble().format(2)}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Arama kısmı
        val searchQuery = remember { mutableStateOf(TextFieldValue()) }
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Arama yapın...", color = MaterialTheme.colorScheme.onBackground) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.primary
            )
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
                    Text("Coin", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground)
                    Text("Amount", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text("Value", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.fillMaxWidth(0.3f), textAlign = TextAlign.End)
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
                    Text(item.CoinName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                    Text(item.CoinAmount.toDouble().format(6), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text("\$${(item.Total).toDouble().format(2)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.fillMaxWidth(0.3f), textAlign = TextAlign.End)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // İşlem geçmişi butonu
        Button(
            onClick = {
                navigateToHistoryPage.invoke()
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("İşlem Geçmişi")
        }
    }
}

@Composable
@Preview
private fun WalletScreenPreview() {
    Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            WalletScreen(modifier = Modifier) {}
        }
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

private fun getSearchedList(searchedItem: String, itemList: ArrayList<NewModelForItemHistory>?): List<NewModelForItemHistory> {
    val queryCoin = searchedItem.uppercase(Locale.getDefault())

    if (queryCoin.isEmpty()) return itemList?.map { it } ?: emptyList()

    return itemList?.filter { item ->
        item.CoinName.contains(queryCoin, ignoreCase = true)
    } ?: emptyList()
}
