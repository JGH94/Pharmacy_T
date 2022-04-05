package com.jucha.pharmacy.ui.payment.list

import android.content.Context
import android.content.Intent
import com.jucha.pharmacy.R
import com.jucha.pharmacy.base.BaseActivity
import com.jucha.pharmacy.databinding.ActivityPaymentHistoryBinding
import com.jucha.pharmacy.utils.DialogManager
import com.jucha.pharmacy.utils.loading.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentHistoryActivity : BaseActivity<ActivityPaymentHistoryBinding, PaymentHistoryViewModel>(R.layout.activity_payment_history) {

    companion object {
        fun startPaymentHistoryActivity(
                context: Context
        ) {
            val intent = Intent(context, PaymentHistoryActivity::class.java)
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun init() {
        binding.apply {
            vm = viewModel
            activity = this@PaymentHistoryActivity
        }

        viewModel.apply {
            fetchPayRequest()

            history.observe(this@PaymentHistoryActivity, {
                when(it.status){
                    Status.LOADING -> {
                    }
                    Status.ERROR -> {
                        showToast("서버 연결이 원활하지 않습니다.\n다시시도해주세요.")
                        DialogManager.getInstance().hideLoading()
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("데이터가 원활하지 않습니다.\n다시시도해주세요.")
                        DialogManager.getInstance().hideLoading()
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("시간이 초과되었습니다.\n다시시도해주세요.")
                        DialogManager.getInstance().hideLoading()
                    }
                    Status.SUCCESS -> {

                    }
                }
            })
        }
    }

    fun onFinish() {
        finish()
    }
}