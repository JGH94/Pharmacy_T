package com.jucha.pharmacy.extension

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter

@BindingAdapter("isTempData")
fun ViewGroup.setIsTempData(text: String){
    if(text == null || text == "null" || text.isEmpty()){
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}