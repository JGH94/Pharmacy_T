package com.jucha.pharmacy.ui.map

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import com.jucha.pharmacy.R
import com.jucha.pharmacy.databinding.DialogMapExitBinding
import java.lang.Exception

class MapActivity : AppCompatActivity() {

    var exitDiaglog: AppCompatDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try{
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } catch (e: Exception){

        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val map = findViewById<ImageView>(R.id.map)

        map.setOnClickListener {
            showExitDialog()
        }
    }


    fun showExitDialog(){
        if (exitDiaglog == null) {
            exitDiaglog = AppCompatDialog(this@MapActivity)
        }

        val binding = DataBindingUtil.inflate<DialogMapExitBinding>(
                LayoutInflater.from(this@MapActivity),
                R.layout.dialog_map_exit,
                null,
                false
        )
        exitDiaglog?.apply {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(binding.root)
            show()
        }

        binding.apply {
            activity = this@MapActivity
        }
    }

    fun onExitClose() {
        if (exitDiaglog != null) {
            exitDiaglog!!.dismiss()
        }
    }

    fun onFinish(){
        finish()
    }
}