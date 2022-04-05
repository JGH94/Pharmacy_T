package com.jucha.pharmacy.model.paylist

import com.google.gson.annotations.SerializedName

data class ResponsePaymentHistoryItem(
        @SerializedName("Pay_ID")
        val payId: String = "",
        @SerializedName("Pay_Indate")
        val payIndate: String = "",
        @SerializedName("Pay_OK")
        val payOK: String = "",
        @SerializedName("Pay_Price")
        val payPrice: String = "",
        @SerializedName("Pay_Type")
        val payType: String = "",
        @SerializedName("User_Name")
        val userName: String = "",
        @SerializedName("pg_id")
        val pgId: String = "",
        @SerializedName("userphone")
        val userPhone: String = "",
        @SerializedName("bankname")
        val bankName: String = "",
        @SerializedName("accountholder")
        val accountHolder: String = "",
        @SerializedName("account")
        val account: String = "",
        @SerializedName("reason")
        val reason: String = ""
)