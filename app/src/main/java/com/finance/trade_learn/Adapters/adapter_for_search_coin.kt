package com.finance.trade_learn.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.ItemOfSearchBinding
import com.finance.trade_learn.models.SearchedModel
import com.finance.trade_learn.utils.sharedPreferencesManager
import com.finance.trade_learn.view.SearchFragmentDirections

class adapter_for_search_coin(
    val list: ArrayList<String>,
    val context: Context
) :
    RecyclerView.Adapter<adapter_for_search_coin.viewHolder>() {

    class viewHolder(val view: ItemOfSearchBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemOfSearchBinding>(
            inflater,
            R.layout.item_of_search,
            parent,
            false
        )

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.view.coin = SearchedModel(list[position], "")
        holder.view.CoinText.setOnClickListener {
            val coinName = list[position]

            sharedPreferencesManager(context)
                .addSharedPreferencesString("coinName", coinName)


            val directions = SearchFragmentDirections.actionSearchActivityToTradePage()
            Navigation.findNavController(holder.view.root).navigate(directions)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateAdapterSearchCoin(newList: List<String>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()

    }

    fun navigateManager(coinName: String) {
        //  val action:
    }
}