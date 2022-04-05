package com.jucha.pharmacy.ui.payment.request

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jucha.pharmacy.R
import com.jucha.pharmacy.databinding.ItemPaymentRequestBinding

class PaymentRequestAdapter (private val viewModel: PaymentRequestViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var dataCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        DataBindingUtil.inflate<ItemPaymentRequestBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_payment_request,
            parent,
            false
        ).let {
            return ReviewViewHolder(it)
        }
    }

    override fun getItemCount() = dataCount

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ReviewViewHolder).onBind(viewModel, position)
    }

    inner class ReviewViewHolder(private val binding: ItemPaymentRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Any, pos: Int) {
            binding.apply {
                this.vm = item as PaymentRequestViewModel
                this.position = pos
            }
        }
    }

}