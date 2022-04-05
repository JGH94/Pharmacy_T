package com.jucha.pharmacy.utils

object RegexUtil {
    val phone = Regex("^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")
}