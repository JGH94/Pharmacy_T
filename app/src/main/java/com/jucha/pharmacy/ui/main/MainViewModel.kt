package com.jucha.pharmacy.ui.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jucha.pharmacy.base.BaseViewModel
import com.jucha.pharmacy.model.LastHistory
import com.jucha.pharmacy.model.ResponseAccountInfo
import com.jucha.pharmacy.model.ResponseImageUpload
import com.jucha.pharmacy.model.ResponseQRUpload
import com.jucha.pharmacy.model.paylist.ResponsePaymentHistory
import com.jucha.pharmacy.model.payrequest.ResponsePayRequest
import com.jucha.pharmacy.repository.Lastference
import com.jucha.pharmacy.repository.LoginPreference
import com.jucha.pharmacy.repository.PharmacyRepository
import com.jucha.pharmacy.utils.Event
import com.jucha.pharmacy.utils.NetworkHelper
import com.jucha.pharmacy.utils.RegexUtil
import com.jucha.pharmacy.utils.loading.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val loginPreference: LoginPreference,
    val Lastference: Lastference,
    val repository: PharmacyRepository
) : BaseViewModel() {

    private val _liveUploadPrescriptionQR = MutableLiveData<Resource<ResponseQRUpload>>()
    val liveUploadPrescriptionQR: LiveData<Resource<ResponseQRUpload>> = _liveUploadPrescriptionQR

    private val _last = MutableLiveData<Resource<LastHistory>>()
    val last: LiveData<Resource<LastHistory>> = _last


    private val _loginEvent = MutableLiveData<Event<Boolean>>()
    val loginEvent: LiveData<Event<Boolean>> = _loginEvent

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>> = _toastEvent

    private val _loginVisible = MutableLiveData<Boolean>()
    val loginVisible: LiveData<Boolean> = _loginVisible


    private val _payRequest = MutableLiveData<Resource<ResponsePayRequest>>()
    val payRequest: LiveData<Resource<ResponsePayRequest>> = _payRequest

    val liveAccountInfo = MutableLiveData<Resource<ResponseAccountInfo>>()

    val liveName = MutableLiveData<String>("최명락")
    val livePhone = MutableLiveData<String>("01047858074")
    val _iNDATE = MutableLiveData<String>("-")
    val liveSelectPosition = MutableLiveData(1)

    fun fetchPayRequest(){
        if (loginPreference.getName().isEmpty()) {
            onLoginCheck()
            return
        }
        val map = HashMap<String, Any>()
        map["user_phone"] = loginPreference.getPhone()

        viewModelScope.launch {
            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postPayRequest(map).let { response ->
                        if(response.isSuccessful){
                            _payRequest.postValue(Resource.success(response.body()))
                        } else {
                            _payRequest.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    _payRequest.postValue(Resource.timeoutError())
                } catch (e: Exception){
                    _payRequest.postValue(Resource.error("",null))
                }
            } else {
                _payRequest.postValue(Resource.networtError())
            }
        }
    }

    fun onLoginCheck() {
        if (loginPreference.getName().isEmpty()) {
            _loginVisible.value = false
            _loginEvent.value = Event(false)
        } else {
            liveName.value = loginPreference.getName()
            livePhone.value = loginPreference.getPhone()
            _iNDATE.value = Lastference.getIndate()
            _loginVisible.value = true
            _loginEvent.value = Event(true)
        }
    }

    fun confirmationPayment() {
        //TODO 결제요청 리스트 체크
        //TODO 시결제 요청이 있을 시
        //TODO 결제 요청이 여러개일 시

        //TODO 결제 요청이 하나일 시

        //TODO 결제 요청이 없을 시
    }


    fun onPrescription() {

    }

    fun onPayment() {

    }


    fun onLogin() {
        if (liveName.value!!.isEmpty() || livePhone.value!!.isEmpty()) {
            _toastEvent.value = Event("정보를 입력해주세요")
            return
        }
        if (!livePhone.value!!.matches(RegexUtil.phone)) {
            _toastEvent.value = Event("휴대폰번호를 정확히 입력해주세요.")
            return
        }
        loginPreference.setName(liveName.value!!)
        loginPreference.setPhone(livePhone.value!!)
        _loginVisible.value = true
        _loginEvent.value = Event(true)
    }

    fun onLogout() {
        loginPreference.setName("")
        loginPreference.setPhone("")
        Lastference.setIndate("-")
        onLoginCheck()
    }

    fun fetchAcccountInfo(){
        viewModelScope.launch {
            val map = HashMap<String, Any>()
            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postAccountInfo(map).let { response ->
                        if(response.isSuccessful){
                            liveAccountInfo.postValue(Resource.success(response.body()))
                        } else {
                            liveAccountInfo.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    liveAccountInfo.postValue(Resource.timeoutError())
                } catch (e: Exception){
                    liveAccountInfo.postValue(Resource.error("",null))
                }
            } else {
                liveAccountInfo.postValue(Resource.networtError())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchUploadQR(map: java.util.HashMap<String, RequestBody>){
        viewModelScope.launch {
            map["user_name"] = RequestBody.create("text/plain".toMediaTypeOrNull(), loginPreference.getName())
            map["user_phone"] = RequestBody.create("text/plain".toMediaTypeOrNull(), loginPreference.getPhone())

            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postQRUpload(map).let { response ->
                        if(response.isSuccessful){
                            _liveUploadPrescriptionQR.postValue(Resource.success(response.body()))
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
                            val formatted = current.format(formatter)
                            Lastference.setIndate(formatted.toString())
                        } else {
                            _liveUploadPrescriptionQR.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    _liveUploadPrescriptionQR.postValue(Resource.timeoutError())
                } catch (e: java.lang.Exception){
                    _liveUploadPrescriptionQR.postValue(Resource.error("",null))
                }
            } else {
                _liveUploadPrescriptionQR.postValue(Resource.networtError())
            }
        }
    }
}