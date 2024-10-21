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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.Dp
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
import com.finance.trade_learn.view.coin.CoinItemScreen
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
private fun MainToolbar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { SearchBar() },
        expandedHeight = 60.dp,
        collapsedHeight = 0.dp,
        colors = topAppBarColors(
            containerColor = MaterialTheme.colors.primary,
            scrolledContainerColor = MaterialTheme.colors.primary
        ),
        scrollBehavior = scrollBehavior
    )


}


@Composable
private fun PopularSection(
    openTradePage: (String) -> Unit
){
    val popularItems by BaseViewModel.listOfCryptoForPopular.collectAsState(emptyList())

    val popularItemListState = rememberLazyListState()
    AutoScrollList(popularItemListState = popularItemListState)


    Column(modifier = Modifier){

        Text(
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.popular_coins),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onPrimary
        )

        if (popularItems != null) {

            LazyRow(
                state = popularItemListState,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                items(popularItems) {
                    PortfolioCard(
                        portfolioItem = it,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable {
                                openTradePage.invoke(it.CoinName)
                            }
                            .sizeIn(minWidth = 220.dp)
                    )
                }
            }
        }

    }
}


@Composable
fun SearchBar(viewModel : MarketViewModel = hiltViewModel<MarketViewModel>()) {
    val searchBarViewState by viewModel.searchBarViewState.collectAsState()

    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val items = BaseViewModel.currentItems.collectAsState()

    LaunchedEffect(isKeyboardOpen){
        if (!isKeyboardOpen) focusManager.clearFocus()
    }

    LaunchedEffect(searchBarViewState.isFocused){
        if (!searchBarViewState.isFocused){
            keyboardController?.hide()
        }
    }


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
        trailingIcon = {
            if (searchBarViewState.searchText.isNotEmpty()){
                Icon(
                    modifier = Modifier.clickable {
                        viewModel.updateSearchBarViewState(viewModel.searchBarViewState.value.copy(searchText = ""))
                    },
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary
                )
            }
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
                .nestedScroll(scrollBehavior.nestedScrollConnection)
            ,
            topBar = {
                MainToolbar(scrollBehavior = scrollBehavior)
            },
            containerColor = MaterialTheme.colors.primary
        ) { it ->

            Column(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize()
            ) {
                TopSection(scrollBehavior = scrollBehavior)

                FilterAndSortButtons(
                    onClickFilter = {
                        val bundle = Bundle()
                        bundle.putString("type", it.name)
                        FirebaseLogEvents.logClickFilterEvent(bundle)

                        viewModel.updateSearchBarViewState(viewModel.searchBarViewState.value.copy(filterType = it))
                    }
                )

                listOfItems.let {item ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(start = 16.dp)){
                        items(
                            items = item,
                            key = {
                                it.id
                            }
                        ){
                            CoinItemScreen(
                                coin = it,
                                navigateToLogin = navigateToLogin,
                                clickedItem = { selectedItemName ->
                                    openTradePage.invoke(selectedItemName)
                                }
                            )
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopSection(
    scrollBehavior: TopAppBarScrollBehavior,
){


    var size : Pair<Dp, Dp>? by remember {
        mutableStateOf(null)
    }
    val newHeight by remember {
        derivedStateOf {
            size?.let { (it.second - (it.second * scrollBehavior.state.collapsedFraction)) } ?: 0.dp

        }
    }

    CalculateSize(
        calculatedSize = {
            if (size != null) return@CalculateSize
            size = it
        }
    ) {
        Column(
            modifier = Modifier.then(
                if (size == null) Modifier
                else Modifier.height(newHeight)
            )
        ) {
            PopularSection { }
        }
    }
}


@Composable
fun CalculateSize(calculatedSize : (Pair<Dp, Dp>) -> Unit, content: @Composable () -> Unit){
    var size by remember { mutableStateOf(Pair(0.dp, 0.dp)) }
    val localDensity = LocalDensity.current


    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .onGloballyPositioned { coordinates ->
            size = with(localDensity) {
                val width = coordinates.size.width.toDp()
                val height = coordinates.size.height.toDp()
                Pair(first = width, second = height)
            }
            calculatedSize.invoke(size)
        }) {
        content()
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

