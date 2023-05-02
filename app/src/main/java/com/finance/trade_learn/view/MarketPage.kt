package com.finance.trade_learn.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.trade_learn.Adapters.AdapterForMarket
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentMarketPageBinding
import com.finance.trade_learn.viewModel.ViewModelMarket
import kotlinx.coroutines.*
import java.lang.Runnable


var firstSet = true

class MarketPage() : BaseFragmentViewModel<FragmentMarketPageBinding, ViewModelMarket>(FragmentMarketPageBinding::inflate) {

    private var viewVisible = true
    private var job: Job? = null

    private var runnable = Runnable { }
    private var handler = Handler(Looper.getMainLooper())
    private var timeLoop = 8000L
    private lateinit var adapter: AdapterForMarket
    override val viewModel : ViewModelMarket by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setup () {
        clickToSearch()
        setAdapter()
        isIntializeViewModel()
        //setAd()
    }

    private fun setAdapter() {
        adapter = AdapterForMarket(requireContext(), arrayListOf())
        binding.RecyclerViewMarket.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        binding.RecyclerViewMarket.layoutManager = layoutManager
    }

    private fun isIntializeViewModel() {

        val status = viewModel.isInitialized
        if (status.value!!) {


            viewModel.listOfCrypto.observe(viewLifecycleOwner) {
                it?.let {
                    adapter.updateData(it)
                    viewModel.isInitialized.value = true
                    binding.progressBar.visibility = View.INVISIBLE
                }

            }
        }

    }

    private fun getData() {
            viewModel.state.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.listOfCrypto.observe(viewLifecycleOwner) { list ->
                        list?.let {
                            timeLoop = 10000
                            adapter.updateData(list)
                            binding.RecyclerViewMarket
                            binding.progressBar.visibility = View.INVISIBLE
                        }

                    }
                }
            }
    }

    private fun update() {
        job = CoroutineScope(Dispatchers.Main + Job()).launch {
            runnable = Runnable {
                viewModel.runGetAllCryptoFromApi()
                getData()
                handler.postDelayed(runnable, timeLoop)
            }
            handler.post(runnable)
        }
    }


    override fun onPause() {
        viewVisible = false
        job?.cancel()
        Log.i("onPause", "onPause")
        handler.removeCallbacks(runnable)
        super.onPause()
    }

    override fun onResume() {
        update()
        viewVisible = true
        super.onResume()
    }

    private fun clickToSearch() {
        binding.searchedCoin.setOnClickListener {
            val directions = MarketPageDirections.actionMarketPageToSearchActivity()
            Navigation.findNavController(binding.root).navigate(directions)
        }
    }


}