package com.jucha.pharmacy.extension

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.jucha.pharmacy.R
import com.jucha.pharmacy.utils.ConvertNumberFormat

@BindingAdapter("select")
fun TextView.setSelectType(type: String){
    this.isSelected = type == this.text
}

@BindingAdapter("textEmptyCheck")
fun TextView.setTextEmptyCheck(str: String){
    if(str.isEmpty()){
        this.visibility = View.GONE
    } else {
        this.text = str
        this.visibility = View.VISIBLE
    }
}

@BindingAdapter("textWon")
fun TextView.setWonConvert(str: String){
    this.text = ConvertNumberFormat.numberFormat(str.toInt(),"원")
}


@BindingAdapter("dateFormat")
fun TextView.setDateFormat(date: String){
    this.text = date.substring(0,10)
}

@BindingAdapter("textWonPayment")
fun TextView.setWonConvertPayment(str: String){
    this.text = ConvertNumberFormat.numberFormat(str.toInt(),"원 결제하기")
}

@SuppressLint("SetTextI18n")
@BindingAdapter("textCardNum")
fun TextView.setCardNum(cardNum: String){
    if(cardNum.isNotEmpty()){
        this.text = "${cardNum.substring(0,4)}-****-****-${cardNum.substring(cardNum.length-5,cardNum.length-1)}"
    }
}

@BindingAdapter("stateColor")
fun TextView.setStateColor(state: String){
    when(state){
        "결제완료" -> {
            this.setTextColor(resources.getColor(R.color.point1000))
        }
        "결제취소","결제취소실패","결제취소진행중","결제실패","결제승인실패" -> {
            this.setTextColor(resources.getColor(R.color.red_1000))
        }
        else -> { //"결제대기","결제승인전","결제승인중", "결제요청"
            this.setTextColor(resources.getColor(R.color.green_700))
        }
    }
}