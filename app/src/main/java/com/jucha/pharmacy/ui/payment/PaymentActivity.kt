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
            "????????????" -> {
                goBootpayRequest(Method.CARD)
            }
            "???????????????" -> {
                goBootpayRequest(Method.VBANK)
            }
            "????????? ????????????" -> {
                goBootpayRequest(Method.BANK)
            }
            "?????? ??????" -> {
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

        val stuck = 1 //?????? ??????

        Bootpay.init(this@PaymentActivity)
                .setApplicationId(application_id) // ?????? ????????????(???????????????)??? application id ???
                .setContext(this@PaymentActivity)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
                .setPG(PG.EASYPAY)
                .setMethod(type)
//                .setUserPhone("010-1234-5678") // ????????? ????????????
                .setName("?????????") // ????????? ?????????
                .setOrderId(payItem.Pay_ID) // ?????? ????????????expire_month
                .setPrice(payItem.Pay_Price!!.toInt()) // ????????? ??????
                .onConfirm { message ->
                    if(payItem.Pay_ID == "0"){
                        //?????? ID?????? ??????
                        Bootpay.confirm(message)
                    } else {
                        viewModel.fetchPayCheck(payItem.Pay_ID!!, message)
                    }
//                    if (0 < stuck) Bootpay.confirm(message); // ????????? ?????? ??????.
//                    else Bootpay.removePaymentWindow(); // ????????? ?????? ????????? ???????????? ?????? ?????? ??????
                    Log.d("ASDAWRQARWR", "confirm" + message);
                }
                .onDone { message ->
                    Log.d("ASDAWRQARWR", "done" + message)

                    viewModel.apply {
                        val type = livePayType.value
                        when (type) {
                            "???????????????" -> {
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
                        showToast("???????????? ???????????? ????????????.\n????????????????????????.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("????????? ?????????????????????.\n????????????????????????.")
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
                        showToast("???????????? ???????????? ????????????.\n????????????????????????.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("????????? ?????????????????????.\n????????????????????????.")
                    }
                }
            })

            liveDirecyPayment.observe(this@PaymentActivity, {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        if(it.data!!.result){
                            if(livePayType.value == "?????? ??????"){
                                showToast("?????? ????????? ?????? ????????????.")
                            } else {
                                showToast("???????????? ???????????? ????????????.")
                            }
                            finish()
                        } else {
                            showToast("????????? ????????? ???????????????.\n????????????????????????.")
                        }

                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showToast(msg) }
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("???????????? ???????????? ????????????.\n????????????????????????.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("????????? ?????????????????????.\n????????????????????????.")
                    }
                }
            })

            liveAccountInfo.observe(this@PaymentActivity, {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        val account = it.data?.get(0)
                        val text = "????????? : ${account?.masterName}\n???????????? : ${account?.masterNumber}\n????????? : ${account?.bankName}\n"
                        showAgreeDialog(text)
                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showToast(msg) }
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("???????????? ???????????? ????????????.\n????????????????????????.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("????????? ?????????????????????.\n????????????????????????.")
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

        tv_text?.text = "?????? ????????? ?????? ???????????????."
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