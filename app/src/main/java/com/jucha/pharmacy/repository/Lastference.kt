package com.jucha.pharmacy.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Lastference @Inject constructor(@ApplicationContext context : Context){
    val PREF_INDATE = "indate"

    val prefs = context.getSharedPreferences("last_date", Context.MODE_PRIVATE)

    fun getIndate(): String {
        return prefs.getString(PREF_INDATE,"")!!
    }
    fun setIndate(query: String) {
        prefs.edit().putString(PREF_INDATE, query).apply()
    }
}
