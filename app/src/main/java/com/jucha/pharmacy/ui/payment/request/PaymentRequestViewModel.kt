package com.jucha.pharmacy.ui.payment.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.jucha.pharmacy.base.BaseViewModel
import com.jucha.pharmacy.model.payrequest.ResponsePayRequestItem
import com.jucha.pharmacy.utils.Event
import javax.inject.Inject

class PaymentRequestViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle//,
//    private val profileRepository: ProfileRepository
) : BaseViewModel() {

    val paymentRequestAdapter = PaymentRequestAdapter(this)

    private val _paymentRequest = MutableLiveData<ArrayList<ResponsePayRequestItem>>()
    val paymentRequest: LiveData<ArrayList<ResponsePayRequestItem>> = _paymentRequest

    private val _paymentRequestEvent = MutableLiveData<Event<ResponsePayRequestItem>>()
    val paymentRequestEvent: LiveData<Event<ResponsePayRequestItem>> = _paymentRequestEvent

    fun setPayRequest(data: ArrayList<ResponsePayRequestItem>){
        _paymentRequest.value = data
        paymentRequestAdapter.dataCount = paymentRequest.value!!.size
    }

    fun showPayment(paymentRequest: ResponsePayRequestItem) {
        _paymentRequestEvent.value = Event(paymentRequest)
    }

}