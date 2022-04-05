package com.jucha.pharmacy.ui.payment.success

import android.content.Intent
import com.jucha.pharmacy.R
import com.jucha.pharmacy.base.BaseActivity
import com.jucha.pharmacy.databinding.ActivityPaymentSuccessBinding
import com.jucha.pharmacy.model.ResponseBootpayDone
import com.jucha.pharmacy.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentSuccessActivity: BaseActivity<ActivityPaymentSuccessBinding, PaymentSuccessViewModel>(R.layout.activity_payment_success){

    val bootpayDone by lazy { intent.getSerializableExtra("data") as ResponseBootpayDone }

    override fun init() {
        binding.apply {
            vm = viewModel
            activity = this@PaymentSuccessActivity
            item = bootpayDone
        }
    }

    fun showMainActivity(){
        val intent = Intent(this@PaymentSuccessActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        startActivity(intent)
    }
}