package com.finance.trade_learn.view

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.finance.trade_learn.R
import com.finance.trade_learn.viewModel.ViewModelMarket
import java.lang.Runnable


var firstSet = true



@Composable
private fun ComposeView(viewModel : ViewModelMarket = androidx.lifecycle.viewmodel.compose.viewModel()){
    update(viewModel)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.fillMaxSize()) {
            val isLoading = viewModel.isLoading.observeAsState().value ?: false
            if (isLoading){
                Row(modifier = Modifier.fillMaxSize()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(R.color.pozitive),
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
                    MainToolbar()

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = colorResource(id = R.color.light_grey))) {}
                }

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(mainItemsScreen) {
                        top.linkTo(toolbar.bottom)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    }

                ) {
                    val listOfItems = viewModel.listOfCrypto.observeAsState()
                    HomePageItems(coinsHome = listOfItems.value){
                       // Navigation.findNavController(binding.root).navigate(it)
                    }
                }
            }
        }

    }
}

private fun update(viewModel: ViewModelMarket) {
    var runnable = Runnable { }
    var handler = Handler(Looper.getMainLooper())
    val timeLoop = 20000L
    runnable = Runnable {
        viewModel.runGetAllCryptoFromApi()
        handler.postDelayed(runnable, timeLoop)
    }
    handler.post(runnable)
}

@Composable
private fun MainToolbar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.white))
            .padding(horizontal = 2.dp)
    ) {
        OutlinedButton(
            shape = RoundedCornerShape(40),
            colors = ButtonDefaults.outlinedButtonColors(
                colorResource(id = R.color.search_background)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp),
            contentPadding = PaddingValues(
                horizontal = 2.dp,
                vertical = 10.dp
            ),
            border = BorderStroke(1.dp, colorResource(id = R.color.search_background_border)),
            onClick = {
                //    val directions = MarketPageDirections.actionMarketPageToSearchActivity()
                //   Navigation.findNavController(binding.root).navigate(directions)
            }) {

            Image(painter = painterResource(id = R.drawable.search),
                contentDescription = "Send Email",
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 3.dp, top = 5.dp, bottom = 2.dp)
                    .clickable(role = Role.DropdownList) {
                        //  val directions = MarketPageDirections.actionMarketPageToSearchActivity()
                        //  Navigation
                        //      .findNavController(binding.root)
                        //      .navigate(directions)
                    },

                )

            Text(text = stringResource(id = R.string.hintSearch),
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.hint_grey),
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(start = 3.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.name),
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(start = 3.dp)
            )
            Text(text = stringResource(id = R.string.lastPrice),
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(start = 3.dp)
            )
            Text(text = stringResource(id = R.string.change24),
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(start = 3.dp)
            )
        }
    }
}

@Preview
@Composable
fun MarketPagePreview(){
    ComposeView()

}