package com.jucha.pharmacy.ui.upload

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jucha.pharmacy.base.BaseViewModel
import com.jucha.pharmacy.model.ResponseImageUpload
import com.jucha.pharmacy.model.ResponseQRUpload
import com.jucha.pharmacy.repository.Lastference
import com.jucha.pharmacy.repository.LoginPreference
import com.jucha.pharmacy.repository.PharmacyRepository
import com.jucha.pharmacy.utils.NetworkHelper
import com.jucha.pharmacy.utils.loading.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception
import java.net.SocketTimeoutException
import java.time.format.DateTimeFormatter
import java.util.HashMap
import javax.inject.Inject
import java.time.LocalDateTime
@HiltViewModel
class UploadPrescriptionViewModel @Inject constructor(
    val repository: PharmacyRepository,
    val loginPreference: LoginPreference,
    val Lastference: Lastference
) : BaseViewModel() {

    private val _liveUploadPrescription = MutableLiveData<Resource<ResponseImageUpload>>()
    val liveUploadPrescription: LiveData<Resource<ResponseImageUpload>> = _liveUploadPrescription

    var outFile: File? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchUploadPrescription(files: MultipartBody.Part, map: HashMap<String, RequestBody>){
        viewModelScope.launch {
            if(NetworkHelper.isNetworkConnected()){
                try{
                    repository.postImageUpload(files, map).let { response ->
                        if(response.isSuccessful){
                            _liveUploadPrescription.postValue(Resource.success(response.body()))
                            setIndate()
                         } else {
                            _liveUploadPrescription.postValue(Resource.error("ERROR CODE: ${response.code()}\n${response.message()}", null))
                        }
                    }
                } catch (e: SocketTimeoutException){
                    _liveUploadPrescription.postValue(Resource.timeoutError())
                } catch (e: Exception){
                    _liveUploadPrescription.postValue(Resource.error("",null))
                }
            } else {
                _liveUploadPrescription.postValue(Resource.networtError())
            }
        }
    }
    fun getUserName(): String{
        return loginPreference.getName()
    }

    fun getUserPhone(): String{
        return loginPreference.getPhone()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setIndate(){
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        val formatted = current.format(formatter)
        return  Lastference.setIndate(formatted.toString())

    }
}