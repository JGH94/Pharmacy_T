package com.jucha.pharmacy.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatDialog
import com.jucha.pharmacy.R

object DialogManager {
    val dialogManager = DialogManager
    var progressDialog: AppCompatDialog? = null

    fun getInstance(): DialogManager{
        return dialogManager
    }

    fun showLoading(activity: Activity){
        if(activity.isFinishing){
            return
        }

        if(progressDialog != null && progressDialog?.isShowing!!){

        } else {
            progressDialog = AppCompatDialog(activity)
            progressDialog?.apply{
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setContentView(R.layout.progress_loading)
                show()
            }
        }
    }

    fun hideLoading(){
        if(progressDialog != null && progressDialog?.isShowing!!){
            progressDialog?.dismiss()
        }
    }
}