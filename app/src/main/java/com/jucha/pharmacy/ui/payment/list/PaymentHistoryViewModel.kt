package com.jucha.pharmacy.ui.payment.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jucha.pharmacy.base.BaseViewModel
import com.jucha.pharmacy.model.paylist.ResponsePaymentHistory
import com.jucha.pharmacy.repository.LoginPreference
import com.jucha.pharmacy.repository.PharmacyRepository
import javax.inject.Inject
import com.jucha.pharmacy.utils.NetworkHelper
import com.jucha.pharmacy.utils.loading.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.HashMap

@HiltViewModel
class PaymentHistoryViewModel  @Inject constructor(
        savedStateHandle: SavedStateHandle,
        private val repository: PharmacyRepository,
        val loginPreference: LoginPreference
) : BaseViewModel() {

    val paymentHistoryAdapter = PaymentHistoryAdapter(this)

    private val _history = MutableLiveData<Resource<ResponsePaymentHistory>>()
    val history: LiveData<Resource<ResponsePaymentHistory>> = _history

    val historyEmpty = MutableLiveData<Boolean>(false)
//    val historyEmpty: LiveData<Event<Boolean>> = _historyEmpty

    fun fetchPayRequest(){
        val map = HashMap<String, Any>()
        map["user_phone"] = loginPreference.getPhone()
        map["user_name"] = loginPreference.getName()

        viewModelScope.launch {
            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postPayHistory(map).let { response ->
                        if(response.isSuccessful){
                            val data = Resource.success(response.body()).data
                            _history.postValue(Resource.success(response.body()))
                            if(data != null){
                                historyEmpty.postValue(true)
                                paymentHistoryAdapter.addItem(data!!)
                            }
                        } else {
                            _history.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    _history.postValue(Resource.timeoutError())
                } catch (e: Exception){
                    _history.postValue(Resource.error("",null))
                }
            } else {
                _history.postValue(Resource.networtError())
            }
        }
    }


}