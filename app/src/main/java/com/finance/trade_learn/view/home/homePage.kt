package com.finance.trade_learn.view.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat.startActivity
import com.finance.trade_learn.R
import com.finance.trade_learn.view.HomePageItems
import com.finance.trade_learn.view.LocalBaseViewModel
import com.finance.trade_learn.view.coin.PopularCoinCard
import com.finance.trade_learn.viewModel.ViewModeHomePage
import kotlinx.coroutines.*
import java.lang.Runnable


private fun clickSendEmailButton( context: Context) {
    composeEmail(arrayOf("learntradeapp@gmail.com"),"A intent or Request", context)
}
private fun composeEmail(addresses: Array<String>, subject: String, context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, addresses)
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    try { startActivity(context, intent, null)
    }catch (_:Exception){ }
}

@Composable
private fun MainToolbar(openSearch : () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary)
            .padding(horizontal = 6.dp)
    ) {
        val context = LocalContext.current
        val (composeEmail, appName, search) = createRefs()
        Image(painter = painterResource(id = R.drawable.send_mail),
            contentDescription = "Send Email",
            modifier = Modifier
                .constrainAs(composeEmail) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .height(36.dp)
                .width(36.dp)
                .clickable {
                    clickSendEmailButton(context)
                }
                .padding(start = 3.dp, top = 5.dp, bottom = 2.dp)
        )
        Text(text = stringResource(id = R.string.app_name),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.pozitive),
            fontSize = 17.sp,
            modifier = Modifier
                .constrainAs(appName) {
                    start.linkTo(composeEmail.end)
                    end.linkTo(search.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(composeEmail.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 3.dp, top = 5.dp)
        )

        Image(painter = painterResource(id = R.drawable.search),
            contentDescription = "Send Email",
            modifier = Modifier
                .constrainAs(search) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .height(40.dp)
                .width(40.dp)
                .clickable(role = Role.DropdownList) {
                    openSearch()
                }
                .padding(start = 3.dp, top = 5.dp, bottom = 2.dp)
        )
    }
}
@Composable
fun MainView(
    page: Int = 1,
    shouldShowPopularCoins : Boolean = false,
    openSearch: () -> Unit,
    openTradePage: (String) -> Unit,
    viewModel: ViewModeHomePage = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val baseViewModel = LocalBaseViewModel.current

    var runnable by remember {
        mutableStateOf(Runnable {  })
    }
    val handler by remember {
        mutableStateOf(Handler(Looper.getMainLooper()))
    }
    val timeLoop by remember {
        mutableStateOf(60000L)
    }

    val popularItems = baseViewModel.listOfCryptoForPopular.observeAsState().value

    DisposableEffect(Unit) {
        runnable = Runnable {
            runBlocking {
                if (false) baseViewModel.getAllCrypto(page)
                else baseViewModel.getAllCrypto(page)
            }
            handler.postDelayed(runnable, timeLoop)
        }
        handler.post(runnable)

        onDispose {
            handler.removeCallbacks(runnable)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val isLoading = baseViewModel.isLoading.observeAsState().value ?: false
        if (isLoading){
            Row(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.pozitive),
                    )
                }
            }
        }
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (toolbar, divider1, mainItemsScreen) = createRefs()
            Column(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(toolbar) {
                    top.linkTo(parent.top)
                }) {
                MainToolbar(openSearch)

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colorResource(id = R.color.light_grey))) {}

                if (shouldShowPopularCoins){
                    Column(modifier = Modifier
                        .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Popular Coins",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        if (popularItems != null){
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp, top = 4.dp)
                            ) {
                                items(popularItems) { item ->
                                    PopularCoinCard(item, Modifier.weight(1f)){selectedItemName ->
                                        openTradePage.invoke(selectedItemName)
                                    }
                                }
                            }
                        }

                    }
                }

            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.light_grey))
                .height(1.dp)
                .constrainAs(divider1) {
                    top.linkTo(toolbar.bottom)
                }
            ) {}

            Column(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(mainItemsScreen) {
                    top.linkTo(divider1.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }

            ) {

                val listOfItems = baseViewModel.currentItemsLiveData.observeAsState()
                val updateList = if(shouldShowPopularCoins) listOfItems.value else listOfItems.value?.sortedBy {
                    it.total_volume
                }

                HomePageItems(coinsHome = updateList){selectedItemName->
                    openTradePage.invoke(selectedItemName)
                }
            }
        }
    }

}
