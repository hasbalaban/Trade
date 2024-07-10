package com.finance.trade_learn.view.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
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
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterial3Api::class)
private val LocalHomePageScrollBehavior = compositionLocalOf<TopAppBarScrollBehavior> { error("No TopAppBarScrollBehavior found") }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainToolbar(openSearch: () -> Unit) {
    val scrollBehavior = LocalHomePageScrollBehavior.current

    TopAppBar(
        modifier = Modifier.padding(top = 6.dp),
        title = {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                val context = LocalContext.current
                val (composeEmail, appName, search) = createRefs()

                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Send Email",
                    modifier = Modifier
                        .constrainAs(composeEmail) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .size(36.dp)
                        .clickable { clickSendEmailButton(context) },
                    tint = MaterialTheme.colors.onPrimary
                )

                Text(
                    text = stringResource(id = R.string.app_name),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    modifier = Modifier
                        .constrainAs(appName) {
                            start.linkTo(composeEmail.end)
                            end.linkTo(search.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                        }
                        .padding(horizontal = 8.dp)
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .constrainAs(search) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
                        .size(36.dp)
                        .clickable { openSearch() },
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(),
        scrollBehavior = scrollBehavior
    )




}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    page: Int = 1,
    shouldShowPopularCoins: Boolean = false,
    openSearch: () -> Unit,
    openTradePage: (String) -> Unit
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


    val popularItemListState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val popularItems = baseViewModel.listOfCryptoForPopular.observeAsState().value

    LaunchedEffect(Unit) {
        while (true){
            delay(3000)
            val newPosition =
                if (popularItemListState.canScrollForward) popularItemListState.layoutInfo.visibleItemsInfo.first().index + 1
                else 0
            popularItemListState.animateScrollToItem(newPosition)
        }
    }

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


    CompositionLocalProvider(LocalHomePageScrollBehavior provides scrollBehavior) {
        Scaffold(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MainToolbar(openSearch)
            },
            containerColor = MaterialTheme.colors.primary
        ) {

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
            ConstraintLayout(modifier = Modifier.padding(it).fillMaxSize()) {
                val (toolbar, divider1, mainItemsScreen) = createRefs()
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(toolbar) {
                        top.linkTo(parent.top)
                    }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = colorResource(id = R.color.light_grey))
                    ) {}

                    if (shouldShowPopularCoins) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = stringResource(id = R.string.popular_coins),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 6.dp, start = 12.dp),
                                color = MaterialTheme.colors.onPrimary
                            )

                            if (popularItems != null) {
                                LazyRow(
                                    state = popularItemListState ,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    items(popularItems) { item ->
                                        PopularCoinCard(
                                            item,
                                            Modifier.weight(1f)
                                        ) { selectedItemName ->
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
                    val updateList =
                        if (shouldShowPopularCoins) listOfItems.value else listOfItems.value?.sortedBy {
                            it.total_volume
                        }

                    HomePageItems(coinsHome = updateList) { selectedItemName ->
                        openTradePage.invoke(selectedItemName)
                    }
                }
            }
        }
    }


}
