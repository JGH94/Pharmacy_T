package com.jucha.pharmacy.repository

import com.google.gson.JsonObject
import com.jucha.pharmacy.api.ApiHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.HashMap
import javax.inject.Inject

class PharmacyRepository @Inject constructor(private val apiHelper: ApiHelper) {
    suspend fun postImageUpload(files: MultipartBody.Part, map: HashMap<String, RequestBody>) =
        apiHelper.postImageUpload(files, map)
    suspend fun postQRUpload(map: HashMap<String, RequestBody>) =
        apiHelper.postQRUpload(map)
    suspend fun postPayRequest(map: HashMap<String, Any>) =
        apiHelper.postPayRequest(map)
    suspend fun postPaySuccess(map: HashMap<String, Any>) =
        apiHelper.postPayRequest(map)
    suspend fun postPayHistory(map: HashMap<String, Any>) =
        apiHelper.postPayHistory(map)
    suspend fun postWebHook(body: RequestBody) =
        apiHelper.postWebHook(body)
    suspend fun postPayCheck(map: HashMap<String, Any>) =
        apiHelper.postPayCheck(map)
    suspend fun postDirectPayment(map: HashMap<String, Any>) =
        apiHelper.postDirectPayment(map)
    suspend fun postLoginAgree(map: HashMap<String, Any>) =
        apiHelper.postLoginAgree(map)
    suspend fun postAccountInfo(map: HashMap<String, Any>) =
        apiHelper.postAccountInfo(map)
}