package com.jucha.pharmacy.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jucha.pharmacy.base.BaseViewModel
import com.jucha.pharmacy.model.ResponseResult
import com.jucha.pharmacy.repository.Lastference
import com.jucha.pharmacy.repository.LoginPreference
import com.jucha.pharmacy.repository.PharmacyRepository
import com.jucha.pharmacy.utils.Event
import com.jucha.pharmacy.utils.NetworkHelper
import com.jucha.pharmacy.utils.RegexUtil
import com.jucha.pharmacy.utils.loading.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.HashMap
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val loginPreference: LoginPreference,
    val Lastference: Lastference,
    val repository: PharmacyRepository
) : BaseViewModel() {

    val Tag = LoginViewModel::class.java.name


    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>> = _toastEvent

    private val _dialogEvent = MutableLiveData<Event<Boolean>>()
    val dialogEvent: LiveData<Event<Boolean>> = _dialogEvent

    private val _login = MutableLiveData<Resource<ResponseResult>>()
    val login: LiveData<Resource<ResponseResult>> = _login
    
    val liveName = MutableLiveData("")
    val livePhone = MutableLiveData("")
    val indate_ = MutableLiveData("")

    val liveAgree1 = MutableLiveData(false)
    val liveAgree2 = MutableLiveData(false)
    val liveAgree3 = MutableLiveData(false)



//    private val _loginEvent = MutableLiveData<Event<Boolean>>()
//    val loginEvent: LiveData<Event<Boolean>> = _loginEvent

    init {
//        _loginEvent.value = Event(false)
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
        if(!liveAgree1.value!! || !liveAgree2.value!! || !liveAgree3.value!!){
            _dialogEvent.value = Event(false)
            return
        }

//        _loginEvent.value = Event(true)
        fetchPayRequest()
    }

    fun setLogin(){
        loginPreference.setName(liveName.value!!)
        loginPreference.setPhone(livePhone.value!!)
        Lastference.setIndate("-")
    }

    fun fetchPayRequest(){
        val map = HashMap<String, Any>()
        map["user_name"] = liveName.value!!
        map["user_phone"] = livePhone.value!!
        map["agree_1"] = true
        map["agree_2"] = true
        map["agree_3"] = true

        viewModelScope.launch {
            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postLoginAgree(map).let { response ->
                        if(response.isSuccessful){
                            _login.postValue(Resource.success(response.body()))
                        } else {
                            _login.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    _login.postValue(Resource.timeoutError())
                } catch (e: Exception){
                    _login.postValue(Resource.error("",null))
                }
            } else {
                _login.postValue(Resource.networtError())
            }
        }
    }

    fun isLogin(): Boolean {
        return !(loginPreference.getName().isEmpty() || loginPreference.getPhone().isEmpty())
    }
}