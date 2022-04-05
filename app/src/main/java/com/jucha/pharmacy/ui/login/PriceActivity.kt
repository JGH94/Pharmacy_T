package com.jucha.pharmacy.ui.login

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Process
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jucha.pharmacy.R

class PriceActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_exit)
        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var NoBtn = findViewById<TextView>(R.id.PriceNoBtn);
        var YesBtn = findViewById<TextView>(R.id.PriceYesBtn);

        NoBtn.setOnClickListener {
            this.finish()
        }
        YesBtn.setOnClickListener {
            // TODO : click event

        }
    }
}