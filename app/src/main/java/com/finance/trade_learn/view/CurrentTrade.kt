package com.finance.trade_learn.view

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.finance.trade_learn.R
import com.finance.trade_learn.enums.TradeType
import com.finance.trade_learn.models.SelectedPercent
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.utils.*
import com.finance.trade_learn.viewModel.ViewModelCurrentTrade
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.*
import java.lang.Runnable
import java.math.BigDecimal
import java.util.*
private fun getDetailsOfCoinFromDatabase(
    coinName: String = "tether",
    viewModel: ViewModelCurrentTrade
) {
    viewModel.getDetailsOfCoinFromDatabase(coinName)
}



// this fun for check logical states. is emty or avaible is more than i want buy etc.
private fun compare(amount : Double?, total : Double): Boolean {

    return try {
        var operationState = false
        if (amount != null ) {

            operationState = when (tradeState) {
                TradeType.Buy -> {
                    ((avaibleAmount.toDouble() >= total) && (avaibleAmount.toDouble() > 0.0) && (total > 0.0) && (amount.toDouble() > 0.0))
                }
                TradeType.Sell -> {
                    ((avaibleAmount.toDouble() >= amount.toDouble()) && (avaibleAmount.toDouble() > 0.0) && (total > 0.0) && (amount.toDouble() > 0.0))
                }
            }
        }
        return operationState

    }catch (e : Exception){
        false
    }
}

private fun operationTrade(itemAmount : Double, price : Double?, viewModel: ViewModelCurrentTrade, coinName: String) {
    when (tradeState) {
        TradeType.Buy -> {
            if (itemAmount.toString().isNullOrEmpty().not() && price != null) {
                val total = itemAmount * price
                viewModel.buyCoin(coinName.lowercase(Locale.getDefault()), itemAmount, total, currentPrice)
            }
        }

        TradeType.Sell -> {
            if (itemAmount.toString() != "" && price != null) {
                val total = itemAmount * price
                viewModel.sellCoin(coinName.lowercase(Locale.getDefault()), itemAmount, total, currentPrice)
            }
        }
    }
    viewModel.isSuccess
}

private fun buyClicked(viewModel: ViewModelCurrentTrade, coinName: String) {
    tradeState = TradeType.Buy
    viewModel.changeTradeType(TradeType.Buy)
    getDetailsOfCoinFromDatabase(coinName, viewModel)
}

private fun sellClicked(viewModel: ViewModelCurrentTrade, coinName: String) {
    tradeState = TradeType.Sell
    getDetailsOfCoinFromDatabase(coinName , viewModel)
    viewModel.changeTradeType(TradeType.Sell)
}




private fun changeAmounts(currentAmount: Double, quantity: Double, progress: CoinProgress) : Double{
    val newAmount = if (progress == CoinProgress.SUM) currentAmount + quantity else currentAmount - quantity
    val amount = if ( newAmount.toString().length>10 && newAmount.toString().subSequence(0,10).last().toString() != ".") newAmount.toString().substring(0,10) else  newAmount.toString()
    return amount.toDouble()
}

enum class CoinProgress (){ SUM,MINUS }

@Composable
private fun reviewUs(){
    //val reviewUs : ReviewUsI = this
 //   val reviewUsResult =  reviewUs.reviewUsRequestCompleteListener(activity = requireActivity(), context = requireContext())
  //  reviewUs.reviewUsStart(activity = requireActivity(), manager = reviewUsResult.first, reviewInfo = reviewUsResult.second)
}

@Composable
private fun showInterstitialAd() {
    val activity =  LocalContext.current as? Activity
    adInterstitial?.apply {
        if (activity != null) {
            show(activity)
        }
        adInterstitial = null
        SharedPreferencesManager(LocalContext.current).addSharedPreferencesLong("interstitialAdLoadedTime",System.currentTimeMillis()+(60*60*1000))
    }
}

private fun setInterstitialAd(context: Context) {
    if (Constants.SHOULD_SHOW_ADS.not()) return
    val currentMillis = System.currentTimeMillis()
    val updateTime = SharedPreferencesManager(context).getSharedPreferencesLong("interstitialAdLoadedTime", currentMillis)
    if (currentMillis < updateTime) return

    val adRequest = AdRequest.Builder().build()
    MobileAds.setRequestConfiguration(RequestConfiguration.Builder().build())
    InterstitialAd.load(context, "ca-app-pub-2861105825918511/1127322176", adRequest, object : InterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {}
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            adInterstitial = interstitialAd
        }
    })
}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun requestPostPermission(){
  //  if (NotificationPermissionManager.canAskNotificationPermission(requireActivity())){
      //  val requestedPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        //ActivityCompat.requestPermissions(requireActivity(), requestedPermissions, Constants.POST_NOTIFICATION)
     //   return
    //}
    //AlertDialogCustomBuilder.showNotificationPermissionPopup(requireContext(), layoutInflater, CustomAlertFields(R.drawable.notification_icon, getString(R.string.notification_title), getString(R.string.notification_subTitle), getString(R.string.notification_allow_button),null), requireActivity()).show()
}

@Composable
fun TradeScreen(
    openHistoryScreen: () -> Unit,
    coinName : String,
    modifier: Modifier = Modifier,
    viewModel: ViewModelCurrentTrade,
){
    var runnable by remember {
        mutableStateOf(Runnable {  })
    }
    val handler by remember {
        mutableStateOf(Handler(Looper.getMainLooper()))
    }

    LifeCycleListener {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                runnable = Runnable {
                    viewModel.getSelectedCoinDetails(coinName)
                    getDetailsOfCoinFromDatabase(viewModel = viewModel)
                    handler.postDelayed(runnable, 100000L)
                }
                handler.post(runnable)            }
            Lifecycle.Event.ON_PAUSE -> {
                handler.removeCallbacks(runnable)
            }
            else -> {}
        }
    }

    val selectedItemInfoResponse = viewModel.selectedCoinToTradeDetails.observeAsState()
    val selectedItemInfo = selectedItemInfoResponse.value?.firstOrNull()

    val itemAmountData = viewModel.coinAmountLiveData.observeAsState()
    avaibleAmount = itemAmountData.value?.toDouble() ?: 0.0

    val tradeType = viewModel.tradeType.observeAsState()


    val selectedPercent = viewModel.selectedPercent.observeAsState()

    val result = viewModel.isSuccess.observeAsState()


    Column(modifier = modifier.fillMaxSize()

    ) {
        MainTopView(selectedItemInfo, modifier, coinName = coinName)
        Column(modifier = modifier.weight(1f)
        ) {
            MainView(tradeType.value ?: TradeType.Buy, itemAmountData.value, selectedItemInfo, modifier, viewModel, openHistoryScreen, selectedPercent.value, result.value, coinName)
        }
    }

}

@Composable
private fun MainTopView(selectedItemInfo: CoinDetail?, modifier: Modifier = Modifier, coinName: String){

    val painter = rememberAsyncImagePainter(model = selectedItemInfo?.image, filterQuality = FilterQuality.High)

    Row(modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp)
        .height(90.dp)
        .sizeIn(maxHeight = 100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                modifier = modifier
                    .width(50.dp)
                    .height(48.dp)
                    .padding(5.dp),
                painter = painter, contentDescription = null)

            Text(modifier = modifier
                .padding(start = 5.dp),
                text = "$coinName / USD",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = androidx.compose.ui.graphics.Color(0xff202BED)
            )
        }


        var coinPercentChange: String = selectedItemInfo?.price_change_percentage_24h?.toString() ?: "0.0"
        coinPercentChange = ((coinPercentChange.toDouble()).toString() + "0000").subSequence(0, 5).toString()
        val percentChangeInfo = when{
            selectedItemInfo?.price_change_percentage_24h.toString().subSequence(0, 1) == "-" -> Pair("$coinPercentChange%", colorResource(id = R.color.negative))
            coinPercentChange.isEmpty() -> Pair(stringResource(id = R.string.loading), colorResource(
                id = R.color.pozitive,
            ))
            else -> Pair("+ $coinPercentChange%", colorResource(id = R.color.pozitive))
        }

        Text(modifier = modifier.padding(end = 4.dp),
            text = percentChangeInfo.first ,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = percentChangeInfo.second
        )
    }

}


@Composable
private fun MainView(tradeType : TradeType,
                     itemAmountData : BigDecimal?,
                     selectedItemInfo: CoinDetail?,
                     modifier: Modifier = Modifier,
                     viewModel: ViewModelCurrentTrade,
                     openHistoryScreen: () -> Unit,
                     selectedPercent: SelectedPercent?,
                     result: Boolean?,
                     coinName: String
){

    val context = LocalContext.current

    var inputAmount : Double? by remember { mutableStateOf(0.0) }



    if (result == true){
        getDetailsOfCoinFromDatabase(
            coinName = if (viewModel.tradeType.value == TradeType.Buy) "tether" else coinName,
            viewModel = viewModel
        )
        Toast.makeText(context, R.string.succes, Toast.LENGTH_SHORT).show()
        //reviewUs()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
            //requestPostPermission()
        }
        adInterstitial?.let { showInterstitialAd() } ?: run { setInterstitialAd(context) }
        viewModel.isSuccess.value = null
    }else if (result == true){
        Toast.makeText(context, R.string.fail, Toast.LENGTH_SHORT).show()
        adInterstitial?.let { showInterstitialAd() } ?: run { setInterstitialAd(context) }
        viewModel.isSuccess.value = null
    }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)) {

        Row(modifier = modifier
            .fillMaxWidth()
            .paddingFromBaseline(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(modifier = modifier
                .clickable {
                    getDetailsOfCoinFromDatabase(viewModel = viewModel)
                    tradeState = TradeType.Buy
                    buyClicked(viewModel, coinName)
                }
                .clip(RoundedCornerShape(8f))
                .background(color = colorResource(id = if (tradeType == TradeType.Buy) R.color.onClickBuyBack else R.color.BuyBack))
                .padding(vertical = 10.dp)
                .weight(1f),
                color = colorResource(id = if (tradeType == TradeType.Buy) R.color.white else R.color.BuyText),
                fontSize = 24.sp,
                text = stringResource(id = R.string.textBuy),
                textAlign = TextAlign.Center
            )

            Text(modifier = modifier
                .padding(start = 20.dp)
                .clickable {
                    getDetailsOfCoinFromDatabase(coinName, viewModel)
                    tradeState = TradeType.Sell
                    sellClicked(viewModel, coinName)
                }
                .clip(RoundedCornerShape(8f))
                .background(color = colorResource(id = if (tradeType == TradeType.Sell) R.color.onClickSellBack else R.color.SellBack))
                .padding(vertical = 10.dp)
                .weight(1f),
                color = colorResource(id = if (tradeType == TradeType.Sell) R.color.white else R.color.SellText),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.textSell)
            )

        }


        val coinPrice = if ((selectedItemInfo?.current_price?.toString()?.length ?: 0) > 10
            && selectedItemInfo?.current_price?.toString()?.subSequence(0, 10)?.last()?.toString() != "."
        ) selectedItemInfo?.current_price.toString().substring(0,10)
        else selectedItemInfo?.current_price.toString()


        Text(text = coinPrice, modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 12.dp)
            .background(color = colorResource(id = R.color.SellBack))
            .clip(RoundedCornerShape(8f))
            .padding(10.dp),
            color = androidx.compose.ui.graphics.Color.Black,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )


        Row(modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(8f))

        ) {

            Image(modifier = modifier
                .fillMaxHeight()
                .clickable {
                    if (inputAmount.toString() != "" && inputAmount != 0.0) {
                        val currentAmount = inputAmount ?: 0.0
                        if ((selectedItemInfo?.current_price ?: 0.0) < 50.0) {
                            inputAmount = changeAmounts(currentAmount, 1.000, CoinProgress.MINUS)
                            return@clickable
                        }
                        inputAmount = changeAmounts(currentAmount, 0.001, CoinProgress.MINUS)
                        return@clickable
                    }
                    Toast
                        .makeText(context, R.string.trueValue, Toast.LENGTH_SHORT)
                        .show()
                }
                .padding(start = 12.dp)
                .padding(16.dp),
                painter = painterResource(id = R.drawable.minus), contentDescription = null)

            TextField(
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSecondary),
                modifier = modifier.weight(1f), value = inputAmount.toString(), onValueChange ={
                inputAmount = try {
                    it.toDouble()
                } catch (_ : Exception) {
                    null
                }
            } )

            Image(
                modifier = modifier
                    .clickable {
                        if (inputAmount
                                .toString()
                                .isNotEmpty()
                        ) {
                            val currentAmount = inputAmount
                                .toString()
                                .toDouble()
                            if ((selectedItemInfo?.current_price ?: 0.0) < 50.0) {
                                inputAmount = changeAmounts(currentAmount, 1.000, CoinProgress.SUM)
                                return@clickable
                            }
                            inputAmount = changeAmounts(currentAmount, 0.001, CoinProgress.SUM)
                            return@clickable
                        }
                        val currentAmount = 0.000
                        inputAmount = changeAmounts(currentAmount, 1.000, CoinProgress.SUM)

                    }
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp)
                    .padding(16.dp),
                painter = painterResource(id = R.drawable.raise), contentDescription = null)

        }

        val price : Double? = selectedItemInfo?.current_price
        val amount : Double? = inputAmount

        val totalPrice =  if (price != null && amount!= null && amount> 0 &&  price.toDouble() > 0.0) {
            val total = (amount * price).toString()
            if (total.length>10 && total.subSequence(0,10).last().toString() != ".") total.substring(0,10) else  total
        }
        else "0"


        Text(text = totalPrice, modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(8f))
            .background(color = colorResource(id = R.color.SellBack))
            .padding(10.dp),
            textAlign = TextAlign.Center,

            fontSize = 16.sp
        )

        Row(modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(24.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            val maxPurchase = (avaibleAmount / (price ?: 0.0))
            PercentItemView(percentPosition = SelectedPercent.Percent25,
                selectedPercent = selectedPercent, tradeType,
                modifier = modifier
                    .clickable {
                        viewModel.changeSelectedPercent(SelectedPercent.Percent25)
                        inputAmount = maxPurchase * (1.0 / 4.0)
                    }
                    .weight(1f))
            PercentItemView(percentPosition = SelectedPercent.Percent50,
                selectedPercent = selectedPercent, tradeType,
                modifier = modifier
                    .clickable {
                        viewModel.changeSelectedPercent(SelectedPercent.Percent50)
                        inputAmount = maxPurchase * (2.0 / 4.0)
                    }
                    .weight(1f)
                    .padding(start = 16.dp))
            PercentItemView(
                percentPosition = SelectedPercent.Percent75,
                selectedPercent = selectedPercent, tradeType,
                modifier = modifier
                    .clickable {
                        viewModel.changeSelectedPercent(SelectedPercent.Percent75)
                        inputAmount = maxPurchase * (3.0 / 4.0)
                    }
                    .weight(1f)
                    .padding(start = 16.dp))
            PercentItemView(
                percentPosition = SelectedPercent.Percent100,
                selectedPercent = selectedPercent, tradeType,
                modifier = modifier
                    .clickable {
                        viewModel.changeSelectedPercent(SelectedPercent.Percent100)
                        inputAmount = maxPurchase * (1.0)
                    }
                    .weight(1f)
                    .padding(start = 16.dp))
        }

        Row(modifier = modifier
            .padding(top = 12.dp)
            .height(20.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = (itemAmountData ?: 0).toString(),
                modifier = modifier
                    .fillMaxHeight()
                    .padding(end = 10.dp),
                color = colorResource(id = R.color.red),
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )

            val selectedAvailableItem = when {
                tradeState == TradeType.Buy && itemAmountData != null -> "USD"
                tradeState == TradeType.Sell && itemAmountData != null -> coinName
                else -> ""
            }


            Text(text = selectedAvailableItem,
                modifier = modifier.fillMaxHeight(),
                color = colorResource(id = R.color.pozitive),
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )

        }

        Button(modifier = modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .height(60.dp),
            shape = RoundedCornerShape(12),
            onClick = {

                inputAmount?.let {amount ->
                    totalPrice.toDouble()?.let {totalPrice->
                        //check views and other is empty or not etc.
                        val logicalCompare = compare(
                            amount = amount,
                            total = totalPrice,
                        )
                        if (logicalCompare) {
                            operationTrade(inputAmount ?: 0.0, selectedItemInfo?.current_price, viewModel, coinName)
                            return@Button
                        }
                        Toast.makeText(context, R.string.proggresState, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    return@Button
                }
                Toast.makeText(context, R.string.enterAmountDialog, Toast.LENGTH_SHORT).show()

            }
            ,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = if (tradeType == TradeType.Buy) R.color.pozitive else R.color.negative)))
        {
            Text(text = if (tradeType == TradeType.Buy) stringResource(id = R.string.textBuy) else stringResource(id = R.string.textSell),
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End) {
            Image(modifier = modifier
                .size(50.dp)
                .clickable {
                    openHistoryScreen.invoke()
                },
                painter = painterResource(id = R.drawable.history_of_trade), contentDescription = null)
        }

    }
}

@Composable
private fun PercentItemView(
    percentPosition : SelectedPercent, selectedPercent: SelectedPercent? = null, tradeType: TradeType, modifier: Modifier){
    val backgroundImage = when{
        selectedPercent == null -> R.drawable.percent
        tradeType == TradeType.Buy && percentPosition.value <= selectedPercent.value -> R.drawable.buy
        tradeType == TradeType.Sell && percentPosition.value <= selectedPercent.value -> R.drawable.sell
        else -> R.drawable.percent
    }
    Image(
        modifier = modifier,
        painter = painterResource(id = backgroundImage), contentDescription = null,
        contentScale = ContentScale.FillBounds)

}



private var currentPrice = 0.0
private var tradeState = TradeType.Buy
private var avaibleAmount : Double = 0.0


private var timeLoop = 10000L
private var job : Job? = null
private var adInterstitial: InterstitialAd? = null