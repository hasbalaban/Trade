package com.finance.trade_learn.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.trade_learn.Adapters.AdapterForSearchCoin
import com.finance.trade_learn.data.Cache_Data
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.SearchFragmentBinding
import com.finance.trade_learn.models.SearchCoinItem
import com.finance.trade_learn.viewModel.SearchCoinViewModel
import kotlinx.coroutines.*
import java.util.*

class SearchFragment : Fragment(), TextWatcher {


    var job = arrayListOf<Job>()
    var fromCoinGecko = true
    private lateinit var adapter: AdapterForSearchCoin
    private lateinit var binding: SearchFragmentBinding
    private val viewModel: SearchCoinViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().setTheme(R.style.thema_search)
        setup()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setup() {
        adapter = AdapterForSearchCoin(arrayListOf(), requireContext())
        setAdapter()
        binding.searchCoin.addTextChangedListener(this)
        viewModel.getCoinList()
    }

    private fun setAdapter() {
        binding.searchedCoins.layoutManager = LinearLayoutManager(requireContext())
        binding.searchedCoins.adapter = adapter
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(s.toString()!= s.toString().uppercase(Locale.getDefault())){

            binding.searchCoin.setText(binding.searchCoin.text.toString()
                .uppercase(Locale.getDefault()))
        }

        job.clear()
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isNotEmpty()) {

            val newJob = CoroutineScope(Dispatchers.IO).launch {
                if (fromCoinGecko){
                    val queryList = viewModel.coinListDetail.value?.filter {
                    it.name.contains(s.toString(), ignoreCase = true)
                }

                    queryList?.let {
                        withContext(Dispatchers.Main){
                                adapter.updateAdapterSearchCoin(queryList)
                        }
                    }
                }
                else{
                    val listCoin = Cache_Data().coinsName()
                    val textOfQuery = binding.searchCoin.text.toString()
                        .uppercase(Locale.getDefault())

                    val queryList = listCoin.filter {
                        it.startsWith(textOfQuery)
                    }.map {
                        SearchCoinItem(it)
                    }
                    withContext(Dispatchers.Main) {
                        adapter.updateAdapterSearchCoin(queryList)
                    }
                }
            }
            job.add(newJob)
        }

        val edittextCursorPosition =  binding.searchCoin.length()
        binding.searchCoin.setSelection(edittextCursorPosition)
    }



}
