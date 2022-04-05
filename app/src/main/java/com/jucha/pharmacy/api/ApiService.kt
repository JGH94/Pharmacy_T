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
import retrofit2.http.*
import java.util.HashMap

interface ApiService {

    @Multipart
    @POST("/PHP/ImageSand.php")
    suspend fun postImageUpload(
            @Part files: MultipartBody.Part, @PartMap partMap: HashMap<String, RequestBody>
    ): Response<ResponseImageUpload>

    @Multipart
    @POST("/PHP/QRSand.php")
    suspend fun postQRUpload(
        @PartMap partMap: HashMap<String, RequestBody>
    ): Response<ResponseQRUpload>

    @FormUrlEncoded
    @POST("/PHP/Selelct_Pay_Info.php")
    suspend fun postPayRequest(
            @FieldMap partMap: HashMap<String, Any>
    ): Response<ResponsePayRequest>

    @FormUrlEncoded
    @POST("/PHP/update_Pay.php")
    suspend fun postPaySuccess(
            @FieldMap partMap: HashMap<String, Any>
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("/PHP/Selelct_Pay_Info_List.php")
    suspend fun postPayHistory(
            @FieldMap partMap: HashMap<String, Any>
    ): Response<ResponsePaymentHistory>


    @Headers(
        "Accept:application/json, text/plain, */*",
        "Content-Type:application/json;charset=UTF-8"
    )
    @POST("/PHP/Pay/Test.php")
    suspend fun postWebHook(
            @Body body: RequestBody
    ): Response<String>

    @FormUrlEncoded
    @POST("/PHP/Selelct_Pay_Info_Have.php")
    suspend fun postPayCheck(
            @FieldMap partMap: HashMap<String, Any>
    ): Response<ResponsePayCheck>

    @FormUrlEncoded
    @POST("/PHP/Selelct_Pay_Info_go_pay.php")
    suspend fun postDirectPayment(
            @FieldMap partMap: HashMap<String, Any>
    ): Response<ResponseImageUpload>

    @FormUrlEncoded
    @POST("/PHP/insert_Agree.php")
    suspend fun postLoginAgree(
            @FieldMap partMap: HashMap<String, Any>
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("/PHP/Select_Account_Info.php")
    suspend fun postAccountInfo(
            @FieldMap partMap: HashMap<String, Any>
    ): Response<ResponseAccountInfo>

}