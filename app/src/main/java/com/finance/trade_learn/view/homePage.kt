package com.finance.trade_learn.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentHomeBinding
import com.finance.trade_learn.utils.SharedPreferencesManager
import com.finance.trade_learn.viewModel.ViewModeHomePage
import kotlinx.coroutines.*
import java.lang.Runnable

class Home : BaseFragmentViewModel<FragmentHomeBinding, ViewModeHomePage>(FragmentHomeBinding::inflate) {

    override val viewModel : ViewModeHomePage by viewModels()

    private var runnable = Runnable { }
    private var handler = Handler(Looper.getMainLooper())
    private var timeLoop = 30000L
    private var job : Job? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.homeScreenCompose.setContent {
            MainView()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    @Composable
    private fun MainView (viewModel : ViewModeHomePage = androidx.lifecycle.viewmodel.compose.viewModel()){

        Box(modifier = Modifier.fillMaxSize()) {
            val isLoading = viewModel.isLoading.observeAsState().value ?: false
            if (isLoading){
                Row(modifier = Modifier.fillMaxSize()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(resources.getColor(R.color.pozitive, null)),
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


                    Column(modifier = Modifier.padding(top = 3.dp)) {
                        PopularItemsView(){selectedItemName ->
                            SharedPreferencesManager(requireContext()).addSharedPreferencesString("coinName", selectedItemName)
                            Navigation.findNavController(binding.root).navigate(R.id.tradePage)
                        }
                    }



                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.light_grey))
                    .height(1.dp)
                    .padding(top = 3.dp, bottom = 3.dp)
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

                    val listOfItems = viewModel.listOfCrypto.observeAsState()
                    HomePageItems(coinsHome = listOfItems.value){
                        Navigation.findNavController(binding.root).navigate(it)
                    }
                }
            }
        }

    }

    @Composable
    private fun MainToolbar() {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.light_grey))
                    .padding(horizontal = 6.dp)
            ) {
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
                            clickSendEmailButton()
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
                            clickToSearch()
                        }
                        .padding(start = 3.dp, top = 5.dp, bottom = 2.dp)
                )
            }
    }

    private fun update() {
        runnable = Runnable {
            runBlocking {
                viewModel.getAllCryptoFromApi()
            }
            handler.postDelayed(runnable, timeLoop)
        }
        handler.post(runnable)
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        job?.cancel()
        super.onPause()
    }

    override fun onResume() {
        update()
        super.onResume()
    }


    private fun clickToSearch() {
        val action = HomeDirections.actionHomeToSearchActivity()
        Navigation.findNavController(binding.root).navigate(action)
        }

    private fun clickSendEmailButton() {
            composeEmail(arrayOf("learntradeapp@gmail.com"),"A intent or Request")
    }
    private fun composeEmail(addresses: Array<String>, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        try { startActivity(intent)
        }catch (_:Exception){ }
    }
}