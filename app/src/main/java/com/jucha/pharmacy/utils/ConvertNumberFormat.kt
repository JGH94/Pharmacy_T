package com.jucha.pharmacy.utils

import java.text.DecimalFormat

object ConvertNumberFormat {

    fun numberFormat(count: Int, unit: String): String? {
        val decimalFormat = DecimalFormat("#,##0")
        return "${decimalFormat.format(count.toLong())}$unit"
    }

    fun numberFormatPositive(count: Int, unit: String): String? {
        val decimalFormat = DecimalFormat("#,##0")
        return "${decimalFormat.format((Math.abs(count)).toLong())}$unit"
    }
}