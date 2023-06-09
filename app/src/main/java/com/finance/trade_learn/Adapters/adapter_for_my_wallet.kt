package com.finance.trade_learn.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.ItemForMyWalletCoinsBinding
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.utils.SharedPreferencesManager
import com.finance.trade_learn.utils.setImageSvg

class adapter_for_my_wallet(var myCoinList: ArrayList<NewModelForItemHistory>) :
    RecyclerView.Adapter<adapter_for_my_wallet.viewHolder>() {

    class viewHolder(val view: ItemForMyWalletCoinsBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = ItemForMyWalletCoinsBinding.inflate(inflater, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.view.apply {
            val currentItem = myCoinList[position]
            coinImage.setImageSvg(currentItem.Image)
            coinName.text = currentItem.CoinName
            coinAmount.text = currentItem.CoinAmount
            coinTotal.text = currentItem.Total
        }

        val animation = AnimationUtils.loadAnimation(
            holder.view.coinAmount.context, R.anim.animation_for_item_of_recyclers
        )
        holder.view.RelayoutWallet.animation = animation
        holder.view.RelayoutWallet.setOnClickListener {
            val coinName = solveCoinName(myCoinList[position].CoinName)
            SharedPreferencesManager(holder.view.root.context).addSharedPreferencesString("coinName", coinName)
            Navigation.findNavController(it).navigate(R.id.tradePage)
        }

    }

    override fun getItemCount(): Int {
        return myCoinList.size
    }

    fun updateRecyclerView(list: ArrayList<NewModelForItemHistory>) {
        myCoinList.clear()
        myCoinList.addAll(list)
        notifyDataSetChanged()

    }
}