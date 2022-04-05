package com.jucha.pharmacy.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginPreference @Inject constructor(@ApplicationContext context : Context){
    val PREF_NAME = "name"
    val PREF_PHONE = "phone"

    val prefs = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getName(): String {
        return prefs.getString(PREF_NAME,"")!!
    }
    fun setName(query: String) {
        prefs.edit().putString(PREF_NAME, query).apply()
    }

    fun getPhone(): String {
        return prefs.getString(PREF_PHONE,"")!!
    }
    fun setPhone(query: String) {
        prefs.edit().putString(PREF_PHONE, query).apply()
    }
}