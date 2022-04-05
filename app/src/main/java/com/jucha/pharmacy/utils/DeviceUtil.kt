package com.jucha.pharmacy.utils

import com.jucha.pharmacy.utils.BaseUtils.context

object DeviceUtil {
    val deviceWidth: Int
        get() = context.resources.displayMetrics.widthPixels

    val deviceHeight: Int
        get() = context.resources.displayMetrics.heightPixels
}