package com.finance.trade_learn.view.wallet

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.finance.trade_learn.R
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.view.LocalWalletPageViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletScreen(
    navigateToHistoryPage: () -> Unit,
) {
    val viewModel = LocalWalletPageViewModel.current
    LaunchedEffect(Unit) {
        viewModel.getMyCoinsDetails()
    }

    Scaffold(
        topBar = { WalletTopBar() },
        content = {
            WalletContent(navigateToHistoryPage = navigateToHistoryPage)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletTopBar() {
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.cripto_wallet),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletContent(navigateToHistoryPage: () -> Unit) {
    val viewModel = LocalWalletPageViewModel.current
    val cryptoItems = viewModel.myCoinsNewModel.observeAsState(emptyList())

    val animatedBalance by animateFloatAsState(
        targetValue = cryptoItems.value.sumOf { it.Total.toDouble() }.toFloat(),
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ), label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.cripto_wallet),
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colors.onPrimary.copy(alpha = 0.5f)
        )

        Text(
            text = stringResource(id = R.string.total_balance),
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(vertical = 8.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "\$${animatedBalance.toDouble().format(2)}",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
            )

            Button(
                onClick = navigateToHistoryPage,
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(id = R.string.transactions))
            }
        }

        val searchQuery = remember { mutableStateOf(TextFieldValue()) }
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text(stringResource(id = R.string.Search), color = MaterialTheme.colors.onPrimary) },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.onPrimary,
                unfocusedBorderColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colors.onPrimary
            )
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
                    Spacer(modifier = Modifier.width(48.dp))
                    Text("Coin", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis)
                    Text(stringResource(id = R.string.amount), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(stringResource(id = R.string.value), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(cryptoItems.value.filter {
                searchQuery.value.text.isBlank() || it.CoinName.contains(searchQuery.value.text, ignoreCase = true)
            }) { item ->
                CryptoItem(item = item)
                HorizontalDivider(modifier = Modifier
                    .alpha(0.5f)
                    .padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun CryptoItem(item: NewModelForItemHistory) {

    val painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current)
        .data(item.Image)
        .apply {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.error)
        }.build()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painter,
            contentDescription = item.CoinName,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(item.CoinName, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis)
        Text(item.CoinAmount.toDouble().format(6), style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text("\$${item.Total.toDouble().format(2)}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}


fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Composable
@Preview
private fun WalletScreenPreview() {
    WalletScreen{

    }
}