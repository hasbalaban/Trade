package com.finance.trade_learn.view.wallet

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.view.LocalBaseViewModel
import com.finance.trade_learn.view.LocalWalletPageViewModel
import com.finance.trade_learn.view.home.PortfolioCard1
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletScreen(
    goBack : () -> Unit,
    navigateToHistoryPage: () -> Unit,
    openTradePage: (String) -> Unit
) {
    val viewModel = LocalWalletPageViewModel.current
    val baseViewModel = LocalBaseViewModel.current
    val allCryptoItems = BaseViewModel.allCryptoItems.collectAsState()
    LaunchedEffect(Unit) {
        if (BaseViewModel.isLogin.value) {
            baseViewModel.getUserInfo()
        }
        else viewModel.getMyCoinsDetails()
    }

    val userInfo = BaseViewModel.userInfo.collectAsState()
    LaunchedEffect(key1 = allCryptoItems) {
        if (BaseViewModel.isLogin.value) {
            viewModel.getDataFromApi(userInfo.value.data?.balances?.map {it.itemName})
        }
        else viewModel.getDataFromApi(viewModel.myCoinsNewModel.value?.map { it.CoinName })
    }

    if (BaseViewModel.isLogin.value){
        viewModel.getDataFromApi(userInfo.value.data?.balances?.map {it.itemName})
    }

    Column(modifier = Modifier.fillMaxWidth()){

        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
            .padding(top = 24.dp), contentAlignment = Alignment.CenterStart){
            IconButton(
                onClick = {
                    goBack.invoke()
                }, modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            androidx.compose.material.Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.buy_sel_operations_text),
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }


        WalletContent(
            navigateToHistoryPage = navigateToHistoryPage,
            openTradePage = openTradePage,
            modifier = Modifier
        )


    }

}

@Composable
fun WalletContent(navigateToHistoryPage: () -> Unit, openTradePage : (String) -> Unit, modifier: Modifier) {
    val viewModel = LocalWalletPageViewModel.current
    val cryptoItems = viewModel.myCoinsNewModel.observeAsState(emptyList())
    val totalBalance = viewModel.totalBalance.collectAsState(0f)

    val animatedBalance by animateFloatAsState(
        targetValue = totalBalance.value,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ), label = ""
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .padding(horizontal = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ){
            Button(
                onClick = navigateToHistoryPage,
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colors.onPrimary,
                    contentColor = MaterialTheme.colors.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(id = R.string.transactions))
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Text(
                text = stringResource(id = R.string.total_balance),
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "\$${animatedBalance.toDouble().format(2)}",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                ),
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(start = 6.dp, bottom = 4.dp, end = 24.dp)
            )

        }

        val searchQuery = remember { mutableStateOf(TextFieldValue()) }

        TextField(
            value = searchQuery.value,
            onValueChange = { newText ->
                searchQuery.value = newText
            },
            leadingIcon = {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.primaryVariant,
                cursorColor = Color.Gray,

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,

                focusedLabelColor = Color.Transparent,
                textColor = MaterialTheme.colors.onPrimary
            ),
            placeholder = {
                Text(
                    fontSize = 16.sp,
                    text = stringResource(id = R.string.Search),
                    color = MaterialTheme.colors.onPrimary.copy(0.7f)
                )
            }

        )







        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(68.dp))
                    Text("Coin", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis)
                    Text(stringResource(id = R.string.amount).replace(":", ""), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(stringResource(id = R.string.value), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Spacer(modifier = Modifier.width(24.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(cryptoItems.value.filter {
                searchQuery.value.text.isBlank() || it.CoinName.contains(searchQuery.value.text, ignoreCase = true)
            }) { item ->

                PortfolioCard1(
                    portfolioItem = item,
                    modifier = Modifier
                        .clickable {
                            openTradePage.invoke(item.CoinName)
                        }
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

fun Double.format(digits: Int): String {
    if (!this.toString().contains("e", true)) return format(digits, this)

    return String.format(Locale.US, "%.${8}f", this)
}



fun format(digits: Int, number : Double): String {
    return String.format(Locale.US, "%.${digits}f", number)
}
@Composable
@Preview
private fun WalletScreenPreview() {
    WalletScreen(goBack = {}, navigateToHistoryPage = {}, openTradePage = {})
}