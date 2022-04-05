package com.jucha.pharmacy.api

import com.google.gson.JsonObject
import com.jucha.pharmacy.model.ResponseAccountInfo
import com.jucha.pharmacy.model.ResponseImageUpload
import com.jucha.pharmacy.model.ResponseQRUpload
import com.jucha.pharmacy.model.ResponseResult
import com.jucha.pharmacy.model.paycheck.ResponsePayCheck
import com.jucha.pharmacy.model.paylist.ResponsePaymentHistory
import com.jucha.pharmacy.model.payrequest.ResponsePayRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.util.HashMap
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
        private val apiService: ApiService) : ApiHelper {

    override suspend fun postImageUpload(files: MultipartBody.Part, map: HashMap<String, RequestBody>): Response<ResponseImageUpload> =
            apiService.postImageUpload(files, map)
    override suspend fun postQRUpload(map: HashMap<String, RequestBody>): Response<ResponseQRUpload> =
        apiService.postQRUpload(map)
    override suspend fun postPayRequest(map: HashMap<String, Any>): Response<ResponsePayRequest> =
            apiService.postPayRequest(map)

    override suspend fun postPaySuccess(map: HashMap<String, Any>): Response<ResponseResult> =
        apiService.postPaySuccess(map)

    override suspend fun postPayHistory(map: HashMap<String, Any>): Response<ResponsePaymentHistory> =
        apiService.postPayHistory(map)

    override suspend fun postWebHook(body: RequestBody): Response<String> =
        apiService.postWebHook(body)

    override suspend fun postPayCheck(map: HashMap<String, Any>): Response<ResponsePayCheck> =
            apiService.postPayCheck(map)

    override suspend fun postDirectPayment(map: HashMap<String, Any>): Response<ResponseImageUpload> =
            apiService.postDirectPayment(map)

    override suspend fun postLoginAgree(map: HashMap<String, Any>): Response<ResponseResult> =
            apiService.postLoginAgree(map)

    override suspend fun postAccountInfo(map: HashMap<String, Any>): Response<ResponseAccountInfo> =
            apiService.postAccountInfo(map)

}