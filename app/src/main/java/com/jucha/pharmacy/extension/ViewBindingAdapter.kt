package com.jucha.pharmacy.extension

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun View.isVisible(boolean: Boolean){
    this.isVisible(boolean)
}