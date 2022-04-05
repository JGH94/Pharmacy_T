package com.jucha.pharmacy.api

import com.google.gson.JsonObject
import com.jucha.pharmacy.model.*
import com.jucha.pharmacy.model.paycheck.ResponsePayCheck
import com.jucha.pharmacy.model.paylist.ResponsePaymentHistory
import com.jucha.pharmacy.model.payrequest.ResponsePayRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.util.HashMap

interface ApiHelper {
    suspend fun postImageUpload(files: MultipartBody.Part, map: HashMap<String, RequestBody>): Response<ResponseImageUpload>
    suspend fun postQRUpload(map: HashMap<String, RequestBody>): Response<ResponseQRUpload>
    suspend fun postPayRequest(map: HashMap<String, Any>): Response<ResponsePayRequest>
    suspend fun postPaySuccess(map: HashMap<String, Any>): Response<ResponseResult>
    suspend fun postPayHistory(map: HashMap<String, Any>): Response<ResponsePaymentHistory>
    suspend fun postWebHook(body: RequestBody): Response<String>
    suspend fun postPayCheck(map: HashMap<String, Any>): Response<ResponsePayCheck>
    suspend fun postDirectPayment(map: HashMap<String, Any>): Response<ResponseImageUpload>
    suspend fun postLoginAgree(map: HashMap<String, Any>): Response<ResponseResult>
    suspend fun postAccountInfo(map: HashMap<String, Any>): Response<ResponseAccountInfo>
}