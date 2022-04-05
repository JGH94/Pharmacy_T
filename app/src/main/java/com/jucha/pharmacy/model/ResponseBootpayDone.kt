package com.jucha.pharmacy.model

import java.io.Serializable

data class ResponseBootpayDone(
    val action: String = "",
    val amount: Int = 0,
    val card_code: String = "",
    val card_name: String = "",
    val card_no: String = "",
    val card_quota: String = "",
    val item_name: String = "",
    val method: String = "",
    val method_name: String = "",
    val order_id: String = "",
    val params: String = "",
    val payment_group: String = "",
    val payment_group_name: String = "",
    val payment_name: String = "",
    val pg: String = "",
    val pg_name: String = "",
    val price: Int = 0,
    val purchased_at: String = "",
    val receipt_id: String = "",
    val requested_at: String = "",
    val status: Int = 0,
    val tax_free: Int = 0,
    val url: String = "",
    val bankcode: String = "",
    val bankname: String = "",
    val username: String = "",
    val accounthodler: String = "",
    val account: String = "",
    val expiredate: String = "",
    val ready_url: String = ""
): Serializable{

}