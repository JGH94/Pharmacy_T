package com.jucha.pharmacy.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jucha.pharmacy.base.BaseViewModel
import com.jucha.pharmacy.model.ResponseAccountInfo
import com.jucha.pharmacy.model.ResponseImageUpload
import com.jucha.pharmacy.model.paycheck.ResponsePayCheck
import com.jucha.pharmacy.repository.LoginPreference
import com.jucha.pharmacy.repository.PharmacyRepository
import com.jucha.pharmacy.utils.NetworkHelper
import com.jucha.pharmacy.utils.loading.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.lang.Exception
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
        loginPreference: LoginPreference,
        val repository: PharmacyRepository
) : BaseViewModel() {


    val liveName = MutableLiveData<String>(loginPreference.getName())
    val livePhone = MutableLiveData<String>(loginPreference.getPhone())
    val liveWebHook = MutableLiveData<Resource<String>>()
    val liveMessage = MutableLiveData<String>()
    val liveDirecyPayment = MutableLiveData<Resource<ResponseImageUpload>>()
    val liveAccountInfo = MutableLiveData<Resource<ResponseAccountInfo>>()

    val livePayCheck = MutableLiveData<Resource<ResponsePayCheck>>()

    private val _livePayType = MutableLiveData<String>("신용카드")
    val livePayType: LiveData<String> = _livePayType

    fun onSelectCard(){
        _livePayType.postValue("신용카드")
    }

    fun onSelectVBank(){
        _livePayType.postValue("무통장입금")
    }

    fun onSelectBank(){
        _livePayType.postValue("실시간 계좌이체")
    }

    fun onSelectDirect(){
        _livePayType.postValue("직접 송금")
    }

    fun fetchPayRequest(body: String, intentBody: String){
        liveMessage.postValue(intentBody)
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body)
        viewModelScope.launch {
            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postWebHook(requestBody).let { response ->
                        if(response.isSuccessful){
                            liveWebHook.postValue(Resource.success(response.body()))
                        } else {
                            liveWebHook.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    liveWebHook.postValue(Resource.timeoutError())
                } catch (e: Exception){
                    liveWebHook.postValue(Resource.error("",null))
                }
            } else {
                liveWebHook.postValue(Resource.networtError())
            }
        }
    }


    fun fetchDirectPayment(map: HashMap<String, Any>){
        viewModelScope.launch {
            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postDirectPayment(map).let { response ->
                        if(response.isSuccessful){
                            liveDirecyPayment.postValue(Resource.success(response.body()))
                        } else {
                            liveDirecyPayment.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    liveDirecyPayment.postValue(Resource.timeoutError())
                } catch (e: Exception){
                    liveDirecyPayment.postValue(Resource.error("",null))
                }
            } else {
                liveDirecyPayment.postValue(Resource.networtError())
            }
        }
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

    fun fetchPayCheck(payId: String, message: String){
        val map = HashMap<String, Any>()
        map["payid"] = payId
        liveMessage.postValue(message)
        viewModelScope.launch {
            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postPayCheck(map).let { response ->
                        if(response.isSuccessful){
                            livePayCheck.postValue(Resource.success(response.body()))
                        } else {
                            livePayCheck.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    livePayCheck.postValue(Resource.timeoutError())
                } catch (e: Exception){
                    livePayCheck.postValue(Resource.error("",null))
                }
            } else {
                livePayCheck.postValue(Resource.networtError())
            }
        }
    }
}