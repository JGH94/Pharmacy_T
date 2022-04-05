package com.jucha.pharmacy.model.payrequest

import java.io.Serializable

data class ResponsePayRequestItem(
    val Pay_ID: String?,
    val Pay_Indate: String?,
    val Pay_OK: String?,
    val Pay_Price: String?,
    val Pay_Type: String?,
    val User_Name: String?,
    val userphone: String?
): Serializable