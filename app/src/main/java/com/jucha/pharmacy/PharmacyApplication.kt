package com.jucha.pharmacy

import android.app.Application
import com.hjq.permissions.XXPermissions
import com.jucha.pharmacy.utils.BaseUtils.init
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PharmacyApplication : Application(){
    companion object{
        lateinit var pharmacyApplication: PharmacyApplication

        fun getInstance(): PharmacyApplication{
            return pharmacyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        pharmacyApplication = this
        init(this)
        XXPermissions.setScopedStorage(true);
    }
}