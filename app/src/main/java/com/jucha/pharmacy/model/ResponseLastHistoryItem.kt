package com.jucha.pharmacy.model

import com.google.gson.annotations.SerializedName

data class ResponseLastHistoryItem(
    @SerializedName("Indate")
    val indate: String = "",
)