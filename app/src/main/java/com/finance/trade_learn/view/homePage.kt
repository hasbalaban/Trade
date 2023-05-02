package com.finance.trade_learn.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.trade_learn.Adapters.adapterForHotCoins
import com.finance.trade_learn.Adapters.AdapterForPopulerCoins
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentHomeBinding
import com.finance.trade_learn.viewModel.ViewModeHomePage
import kotlinx.coroutines.*
import java.lang.Runnable

class Home : BaseFragmentViewModel<FragmentHomeBinding, ViewModeHomePage>(FragmentHomeBinding::inflate) {

    override val viewModel : ViewModeHomePage by viewModels()

    private lateinit var adapterForHotList: adapterForHotCoins
    private lateinit var adapterForPopulerList: AdapterForPopulerCoins
    private var runnable = Runnable { }
    private var handler = Handler(Looper.getMainLooper())
    private var timeLoop = 2000L
    private var job : Job? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapterForHotList = adapterForHotCoins(requireContext(), arrayListOf())
        binding.RecyclerViewCoinsOfToday.layoutManager = LinearLayoutManager(requireContext())
        binding.RecyclerViewCoinsOfToday.adapter = adapterForHotList

        adapterForPopulerList = AdapterForPopulerCoins(requireContext(), arrayListOf())
        binding.RecyclerViewPopulerCoins.adapter = adapterForPopulerList


        clickToSearch()
        clickSendEmailButton()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setSubscribeObservers() {
        viewModel.listOfCrypto.observe(viewLifecycleOwner) { list ->
            adapterForHotList.updateData(list)
        }
        viewModel.listOfCryptoForPopular.observe(viewLifecycleOwner) {
            adapterForPopulerList.updateData(it)
        }
    }

    private fun getData() {
        viewModel.listOfCrypto.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapterForHotList.updateData(it)
                timeLoop = 10000
                binding.progressBar.visibility = View.INVISIBLE
                setSubscribeObservers()
            }
        }

        viewModel.listOfCryptoForPopular.value?.let { list ->
            adapterForPopulerList.updateData(list)
        }
    }

    private fun update() {
        runnable = Runnable {
            runBlocking {
                viewModel.getAllCryptoFromApi()
                getData()
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
        if (viewModel.isLoading.value == false) getData()
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