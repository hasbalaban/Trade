package com.finance.trade_learn.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
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
        clickToSearch()
        clickSendEmailButton()
        setSubscribeObservers()

        binding.mainView.setContent {
            HomePageItems(){
                Navigation.findNavController(binding.root).navigate(it)
            }
        }

        binding.progressBar.setContent {
            CircularProgressIndicator(
                color = Color(resources.getColor(R.color.pozitive, null))
            )
        }

        binding.view1.setContent {
            Row(modifier = Modifier.background(color = colorResource(id = R.color.light_grey))) {

            }
        }

        binding.dividerAd.setContent {
            Row(modifier = Modifier.background(color = colorResource(id = R.color.light_grey))) {

            }
        }

        super.onViewCreated(view, savedInstanceState)
    }


    private fun setSubscribeObservers() {

        viewModel.isLoading.observe(viewLifecycleOwner){
            binding.progressBar.isVisible = it
        }

        viewModel.listOfCryptoForPopular.observe(viewLifecycleOwner) {
            binding.PopulerCoins.setContent {
                PopularItemsView(it){selectedItemName ->
                    SharedPreferencesManager(requireContext()).addSharedPreferencesString("coinName", selectedItemName)
                    Navigation.findNavController(binding.root).navigate(R.id.tradePage)
                }
            }
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
        binding.searchCoin.setOnClickListener {
             val action = HomeDirections.actionHomeToSearchActivity()
             Navigation.findNavController(it).navigate(action)
        }
    }

    private fun clickSendEmailButton(){
        binding.sendMail.setOnClickListener {
            composeEmail(arrayOf("learntradeapp@gmail.com"),"A intent or Request")
        }
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