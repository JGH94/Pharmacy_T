package com.jucha.pharmacy.utils

import android.widget.Toast
import com.jucha.pharmacy.utils.BaseUtils.context

object ToastUtil {

    fun showTaost(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}