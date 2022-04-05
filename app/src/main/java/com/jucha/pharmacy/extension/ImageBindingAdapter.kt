package com.jucha.pharmacy.extension

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("setUrl")
fun ImageView.setImageBinding(url: String){
    Glide.with(context).load(url).into(this)
}