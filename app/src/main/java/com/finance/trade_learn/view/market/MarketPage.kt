package com.finance.trade_learn.view.market

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.utils.FirebaseLogEvents
import com.finance.trade_learn.view.LocalBaseViewModel
import com.finance.trade_learn.view.MarketPageItems
import com.finance.trade_learn.view.home.PortfolioCard
import com.finance.trade_learn.view.trade.FilterAndSortButtons
import com.finance.trade_learn.viewModel.MarketViewModel
import kotlinx.coroutines.delay
import java.math.BigDecimal


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
private fun MainToolbar(scrollBehavior: TopAppBarScrollBehavior, openTradePage: (String) -> Unit,
                        viewModel: MarketViewModel = hiltViewModel()
) {
    LargeTopAppBar(
        title = {TopAppBarScreen(openTradePage)},
        actions = {
            FilterAndSortButtons(
                onClickFilter = {
                    val bundle = Bundle()
                    bundle.putString("type", it.name)
                    FirebaseLogEvents.logClickFilterEvent(bundle)

                    viewModel.updateSearchBarViewState(viewModel.searchBarViewState.value.copy(filterType = it))
                }
            )
        },
        expandedHeight = 320.dp, // Dinamik olarak hesaplanan yükseklik atanıyor
        colors = topAppBarColors(
            containerColor = MaterialTheme.colors.primary,
            scrolledContainerColor = MaterialTheme.colors.primary
        ),

        windowInsets = TopAppBarDefaults.windowInsets,
        scrollBehavior = scrollBehavior
    )


}


@Composable
private fun PopularSection(
    openTradePage: (String) -> Unit
){
    val baseViewModel = LocalBaseViewModel.current
    val popularItems = baseViewModel.listOfCryptoForPopular.observeAsState().value

    val popularItemListState = rememberLazyListState()
    AutoScrollList(popularItemListState = popularItemListState)


    Column(modifier = Modifier.height(210.dp)){

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            text = stringResource(id = R.string.popular_coins),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onPrimary
        )

        if (popularItems != null) {

            LazyRow(
                state = popularItemListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            ) {
                items(popularItems) {

                    val item = NewModelForItemHistory(
                        CoinName = it.CoinName.split(" ").first(),
                        CoinAmount = it.CoinPrice.toDoubleOrNull() ?: 0.0,
                        Total = BigDecimal.ZERO,
                        Image = it.CoinImage,
                        currentPrice = it.CoinPrice + " $" ,
                    )


                    PortfolioCard(
                        portfolioItem = item,
                        modifier = Modifier
                            .clickable {
                                openTradePage.invoke(it.id)
                            }
                            .sizeIn(minWidth = 220.dp)
                            .padding(start = 4.dp, bottom = 8.dp,)

                    )
                }
            }
        }

    }
}

@Composable
private fun TopAppBarScreen(openTradePage: (String) -> Unit){
    Column(modifier = Modifier.height(260.dp)) {
        SearchBar()

        PopularSection(openTradePage = openTradePage)
    }
}


@Composable
fun SearchBar(viewModel : MarketViewModel = hiltViewModel<MarketViewModel>()) {
    val searchBarViewState by viewModel.searchBarViewState.collectAsState()

    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(isKeyboardOpen){
        if (!isKeyboardOpen) focusManager.clearFocus()
    }

    LaunchedEffect(searchBarViewState.isFocused){
        if (!searchBarViewState.isFocused){
            keyboardController?.hide()
        }
    }

    val items = BaseViewModel.currentItems.collectAsState()

    LaunchedEffect(items.value) {
        viewModel.updateSearchBarViewState(viewModel.searchBarViewState.value)
    }



    TextField(
        value = searchBarViewState.searchText,
        onValueChange = { newText ->
            viewModel.updateSearchBarViewState(viewModel.searchBarViewState.value.copy( searchText = newText))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary
            )
        },
        modifier = Modifier
            .height(56.dp)
            .onFocusChanged { focusState ->
                viewModel.updateSearchBarViewState(viewModel.searchBarViewState.value.copy(isFocused = focusState.isFocused))

                if (focusState.isFocused) {
                    FirebaseLogEvents.logEvent("market page click search box")
                }
            }
            .fillMaxWidth()
            .padding(end = 16.dp)
        ,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    openTradePage: (String) -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: MarketViewModel = hiltViewModel<MarketViewModel>()
) {
    val listOfItems by viewModel.itemList.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()



    LaunchedEffect(scrollBehavior.state.collapsedFraction) {
        if (viewModel.searchBarViewState.value.isFocused) {
            viewModel.updateSearchBarViewState(viewModel.searchBarViewState.value.copy(isFocused = false))
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {


        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MainToolbar(scrollBehavior = scrollBehavior, openTradePage = openTradePage)
            },
            containerColor = MaterialTheme.colors.primary
        ) { it ->

            Column(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize()
            ) {

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(id = R.color.light_grey))
                        .height(1.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    MarketPageItems(
                        coinsHome = listOfItems,
                        onViewClick = { selectedItemName ->
                            openTradePage.invoke(selectedItemName)
                        },
                        navigateToLogin = navigateToLogin
                    )
                }
            }

        }
    }
}

@Composable
fun AutoScrollList(popularItemListState: LazyListState) {
    LaunchedEffect(Unit) {
        while (true){
            delay(2000)
            val newPosition =
                if (popularItemListState.canScrollForward) popularItemListState.layoutInfo.visibleItemsInfo.first().index + 1
                else 0
            popularItemListState.animateScrollToItem(newPosition)
        }
    }
}


@Composable
fun keyboardAsState(): State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val view = LocalView.current
    val viewTreeObserver = view.viewTreeObserver
    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            keyboardState.value = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose { viewTreeObserver.removeOnGlobalLayoutListener(listener) }
    }
    return keyboardState
}

