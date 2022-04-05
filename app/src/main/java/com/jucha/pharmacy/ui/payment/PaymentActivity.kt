package com.jucha.pharmacy.ui.payment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.gson.GsonBuilder
import com.jucha.pharmacy.R
import com.jucha.pharmacy.base.BaseActivity
import com.jucha.pharmacy.databinding.ActivityPaymentBinding
import com.jucha.pharmacy.databinding.DialogAccountBinding
import com.jucha.pharmacy.model.ResponseBootpayDone
import com.jucha.pharmacy.model.payrequest.ResponsePayRequestItem
import com.jucha.pharmacy.ui.payment.success.PaymentSuccessActivity
import com.jucha.pharmacy.utils.gsonnull.BooleanTypeAdapter
import com.jucha.pharmacy.utils.gsonnull.LongTypeAdapter
import com.jucha.pharmacy.utils.gsonnull.NonNullListDeserializer
import com.jucha.pharmacy.utils.gsonnull.StringTypeAdapter
import com.jucha.pharmacy.utils.loading.Status
import dagger.hilt.android.AndroidEntryPoint
import kr.co.bootpay.Bootpay
import kr.co.bootpay.enums.Method
import kr.co.bootpay.enums.PG
import kr.co.bootpay.enums.UX
import kr.co.bootpay.model.BootExtra
import kr.co.bootpay.model.BootUser
import org.json.JSONObject

@AndroidEntryPoint
class PaymentActivity :
        BaseActivity<ActivityPaymentBinding, PaymentViewModel>(R.layout.activity_payment) {

    val payItem by lazy { intent.getSerializableExtra("data") as ResponsePayRequestItem }
    val application_id by lazy {applicationContext.resources.getString(R.string.payment_id)}

    override fun init() {
        binding.apply {
            vm = viewModel
            activity = this@PaymentActivity
            item = payItem
        }
        setObserver()
    }

    fun onSitePayment(){
        val map = HashMap<String, Any>()
        map["payid"] = payItem.Pay_ID!!
        map["kinds"] = 1
        viewModel.fetchDirectPayment(map)
    }

    fun onPaymentSuccess(type: String) {
        when (type) {
            "신용카드" -> {
                goBootpayRequest(Method.CARD)
            }
            "무통장입금" -> {
                goBootpayRequest(Method.VBANK)
            }
            "실시간 계좌이체" -> {
                goBootpayRequest(Method.BANK)
            }
            "직접 송금" -> {
                viewModel.fetchAcccountInfo()
//                val map = HashMap<String, Any>()
//                map["payid"] = payItem.Pay_ID
//                map["kinds"] = 2
//                viewModel.fetchDirectPayment(map)
            }
        }
//        val intent = Intent(this@ActivityPayment, ActivityPaymentSuccess::class.java)
//        startActivity(intent)
    }

    fun goBootpayRequest(type: Method) {

        val bootUser = BootUser().setPhone(viewModel.liveName.value)
        val bootExtra = BootExtra().setQuotas(intArrayOf(0, 2, 3))

        val stuck = 1 //재고 있음

        Bootpay.init(this@PaymentActivity)
                .setApplicationId(application_id) // 해당 프로젝트(안드로이드)의 application id 값
                .setContext(this@PaymentActivity)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
                .setPG(PG.EASYPAY)
                .setMethod(type)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("사용료") // 결제할 상품명
                .setOrderId(payItem.Pay_ID) // 결제 고유번호expire_month
                .setPrice(payItem.Pay_Price!!.toInt()) // 결제할 금액
                .onConfirm { message ->
                    if(payItem.Pay_ID == "0"){
                        //결제 ID체크 패스
                        Bootpay.confirm(message)
                    } else {
                        viewModel.fetchPayCheck(payItem.Pay_ID!!, message)
                    }
//                    if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
//                    else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                    Log.d("ASDAWRQARWR", "confirm" + message);
                }
                .onDone { message ->
                    Log.d("ASDAWRQARWR", "done" + message)

                    viewModel.apply {
                        val type = livePayType.value
                        when (type) {
                            "무통장입금" -> {
                                viewModel.fetchPayRequest(stringToJson(message), message)
                            }
                            else -> {
                                startPaymentSuccessActivity(message)
                            }
                        }
                    }


                }
                .onReady { message ->
                    Log.d("ASDAWRQARWR", "ready" + message)
                }
                .onCancel { message ->
                    Log.d("ASDAWRQARWR", "cancel" + message)
                }
                .onError { message ->
                    Log.d("ASDAWRQARWR", "error" + message)
                }
                .onClose { message ->
                    Log.d("ASDAWRQARWR", "close")
                }
                .request()
    }

    fun stringToJson(str: String): String {
        val jsonObject = JSONObject(str)
        val paymentDataJson = JSONObject()
        paymentDataJson.put("bankname", jsonObject["bankname"])
        paymentDataJson.put("accountholder", jsonObject["accounthodler"])
        paymentDataJson.put("account", jsonObject["account"])
        paymentDataJson.put("expiredate", jsonObject["expiredate"])
        paymentDataJson.put("receipt_id", jsonObject["receipt_id"])
        paymentDataJson.put("n", jsonObject["item_name"])
        paymentDataJson.put("p", jsonObject["price"])
        paymentDataJson.put("pg", jsonObject["pg_name"])
        paymentDataJson.put("pm", jsonObject["payment_name"])
        paymentDataJson.put("pg_a", jsonObject["pg"])
        paymentDataJson.put("pm_a", jsonObject["method"])
        paymentDataJson.put("o_id", jsonObject["order_id"])
        paymentDataJson.put("s", jsonObject["status"])

        jsonObject.put("application_id", jsonObject["receipt_id"])
        jsonObject.put("payment_data", paymentDataJson)
        jsonObject.put("application_id", "")
        jsonObject.put("application_id", "")

        Log.d("ASDAWRQARWR", "done stringToJson : $jsonObject")
        return jsonObject.toString()

    }

    fun setObserver() {
        viewModel.apply {
            liveWebHook.observe(this@PaymentActivity, Observer {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        startPaymentSuccessActivity(liveMessage.value!!)
                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showToast(msg) }
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("데이터가 원활하지 않습니다.\n다시시도해주세요.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("시간이 초과되었습니다.\n다시시도해주세요.")
                    }
                }
            })

            livePayCheck.observe(this@PaymentActivity, Observer {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        if (it.data?.count_ == "0") {
                            showSuccessDialog()
                        } else {
                            Bootpay.confirm(viewModel.liveMessage.value)
                        }

                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showToast(msg) }
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("데이터가 원활하지 않습니다.\n다시시도해주세요.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("시간이 초과되었습니다.\n다시시도해주세요.")
                    }
                }
            })

            liveDirecyPayment.observe(this@PaymentActivity, {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        if(it.data!!.result){
                            if(livePayType.value == "직접 송금"){
                                showToast("해당 계좌로 송금 바랍니다.")
                            } else {
                                showToast("현장결제 해주시기 바랍니다.")
                            }
                            finish()
                        } else {
                            showToast("서버에 문제가 생겼습니다.\n다시시도해주세요.")
                        }

                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showToast(msg) }
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("데이터가 원활하지 않습니다.\n다시시도해주세요.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("시간이 초과되었습니다.\n다시시도해주세요.")
                    }
                }
            })

            liveAccountInfo.observe(this@PaymentActivity, {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        val account = it.data?.get(0)
                        val text = "받는분 : ${account?.masterName}\n계좌번호 : ${account?.masterNumber}\n은행명 : ${account?.bankName}\n"
                        showAgreeDialog(text)
                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showToast(msg) }
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("데이터가 원활하지 않습니다.\n다시시도해주세요.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("시간이 초과되었습니다.\n다시시도해주세요.")
                    }
                }
            })
        }
    }

    var accountInfoDialog: AppCompatDialog? = null

    fun showAgreeDialog(title: String){
        if (accountInfoDialog == null) {
            accountInfoDialog = AppCompatDialog(this@PaymentActivity)
        }

        val binding = DataBindingUtil.inflate<DialogAccountBinding>(
            LayoutInflater.from(this@PaymentActivity),
            R.layout.dialog_account,
            null,
            false
        )
        accountInfoDialog?.apply {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(binding.root)
            show()
        }

        binding.apply {
            activity = this@PaymentActivity
            text = title
        }
    }

    fun onClose(){
        val map = HashMap<String, Any>()
        map["payid"] = payItem.Pay_ID!!
        map["kinds"] = 2
        viewModel.fetchDirectPayment(map)
    }

    fun startPaymentSuccessActivity(message: String) {
        val gson = GsonBuilder()
                .registerTypeAdapter(ArrayList::class.java, NonNullListDeserializer<Any>())
                .registerTypeAdapter(String::class.java, StringTypeAdapter())
                .registerTypeAdapter(Long::class.java, LongTypeAdapter())
                .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
                .disableHtmlEscaping()
                .create()
        val bootpay = gson.fromJson(message, ResponseBootpayDone::class.java)
//                        viewModel.fetchPayRequest(liveMessage.value!!)

        val intent = Intent(this@PaymentActivity, PaymentSuccessActivity::class.java)
        intent.putExtra("data", bootpay)
        startActivity(intent)
    }

    fun bootPayConfirm(message: String) {
        Bootpay.confirm(message)
    }

    var progressDialog: AppCompatDialog? = null

    fun showSuccessDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressSET()
        } else {
            progressDialog = AppCompatDialog(this@PaymentActivity)
            progressDialog!!.setCancelable(false)
            progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog!!.setContentView(R.layout.dialog_ok)
            progressDialog!!.show()
        }

        val tv_close = progressDialog!!.findViewById<TextView>(R.id.tv_close)
        val tv_text = progressDialog!!.findViewById<TextView>(R.id.tv_text)

        tv_text?.text = "결제 요청이 취소 되었습니다."
        tv_close?.setOnClickListener {
            finish()
        }

    }

    fun progressSET() {
        if (progressDialog == null || !progressDialog!!.isShowing) {
            return
        }
    }

    fun progressOFF() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    fun onFinish() {
        finish()
    }

}