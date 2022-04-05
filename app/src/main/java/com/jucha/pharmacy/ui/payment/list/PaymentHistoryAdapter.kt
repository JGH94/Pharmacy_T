package com.jucha.pharmacy.ui.payment.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jucha.pharmacy.R
import com.jucha.pharmacy.databinding.ItemPaymentHistoryBinding
import com.jucha.pharmacy.model.paylist.ResponsePaymentHistoryItem

class PaymentHistoryAdapter(private val viewModel: PaymentHistoryViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var dataCount = 0
    var mItems = ArrayList<ResponsePaymentHistoryItem>()

    fun addItem(item: ArrayList<ResponsePaymentHistoryItem>){
        mItems.addAll(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        DataBindingUtil.inflate<ItemPaymentHistoryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_payment_history,
            parent,
            false
        ).let {
            return PaymentHistoryViewHolder(it)
        }
    }

    override fun getItemCount() = mItems.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PaymentHistoryViewHolder).onBind(position)
    }

    inner class PaymentHistoryViewHolder(private val binding: ItemPaymentHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(pos: Int){
            binding.apply {
                this.vm = viewModel
                this.position = pos
            }
        }
    }

}