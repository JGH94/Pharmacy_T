package com.jucha.pharmacy.ui.payment.request

import android.content.Intent
import com.jucha.pharmacy.R
import com.jucha.pharmacy.base.BaseActivity
import com.jucha.pharmacy.databinding.ActivityPaymentRequestBinding
import com.jucha.pharmacy.model.payrequest.ResponsePayRequestItem
import com.jucha.pharmacy.ui.payment.PaymentActivity

class PaymentRequestActivity : BaseActivity<ActivityPaymentRequestBinding, PaymentRequestViewModel>(R.layout.activity_payment_request) {

    val data by lazy { intent.getSerializableExtra("data") as ArrayList<ResponsePayRequestItem> }

    override fun init() {
        binding.apply {
            vm = viewModel
            activity = this@PaymentRequestActivity
        }

        viewModel.apply{
            setPayRequest(data)
            paymentRequestEvent eventObserve {showItemPayment(it)}
        }
    }

    private fun showItemPayment(item: ResponsePayRequestItem){
        val intent = Intent(this@PaymentRequestActivity, PaymentActivity::class.java)
        intent.putExtra("data",item)
        startActivity(intent)
    }

    fun onFinish(){
        finish()
    }

}