package com.jucha.pharmacy.ui.upload

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialog
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.jucha.pharmacy.R
import com.jucha.pharmacy.base.BaseActivity
import com.jucha.pharmacy.databinding.ActivityUploadPrescriptionBinding
import com.jucha.pharmacy.databinding.DialogSelectPhotoBinding
import com.jucha.pharmacy.repository.Lastference
import com.jucha.pharmacy.repository.PharmacyRepository
import com.jucha.pharmacy.utils.camera.CameraUtils
import com.jucha.pharmacy.utils.DialogManager
import com.jucha.pharmacy.utils.loading.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec


@AndroidEntryPoint
class ActivityUploadPrescription : BaseActivity<ActivityUploadPrescriptionBinding, UploadPrescriptionViewModel>(R.layout.activity_upload_prescription) {

    val TAG = "ActivityUploadPrescription"
    val uri by lazy { intent.getStringExtra("uri") }
    val bitmap by lazy { intent.getStringExtra("bitmap") }

    val key = "lakueqwejekflsef"

    var outFile: File? = null

    override fun init() {
        binding.apply {
            url = uri
            vm = viewModel
            activity = this@ActivityUploadPrescription
        }
        encodeImage()
        setObserver()


//        Glide.with(this).load(outFile).into(binding.ivEncode)

//        val inputStream2: InputStream =   FileInputStream(outFile)
//        val fileSize2 = inputStream2.available()

//        val tempData2 = ByteArray(fileSize2)
//        inputStream2.read(tempData2);
//        val data2: ByteArray = decodeFile(key, tempData2)!!
//        val bitmap = BitmapFactory.decodeByteArray(data2, 0, data2.size)

//        Glide.with(this).load(bitmap).into(binding.ivDecode)
    }

    fun encodeImage(){
        val inputStream: InputStream = contentResolver.openInputStream(uri!!.toUri())!!
        val fileSize: Int = inputStream.available()

        val tempData = ByteArray(fileSize)

        inputStream.read(tempData)
        inputStream.close()

        val data: ByteArray = encodeFile(key, tempData)!!

        outFile = createImageFile()
        val fileOutputStream = FileOutputStream(outFile)
        fileOutputStream.write(data)
        fileOutputStream.close()
    }

//    fun convertImageToByte(uri: Uri?): ByteArray? {
//        var data: ByteArray? = null
//        try {
//            val cr: ContentResolver = baseContext.contentResolver
//            val inputStream: InputStream = cr.openInputStream(uri!!)!!
//            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
//            val baos = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//            data = baos.toByteArray()
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//        return data
//    }

    @SuppressLint("GetInstance")
    @Throws(Exception::class)
    fun encodeFile(key: String, fileData: ByteArray?): ByteArray? {
        val cipher = Cipher.getInstance("AES")
        val keySpec =SecretKeySpec(key.toByteArray(), "AES/CBC/PKCS5Padding")

        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        return cipher.doFinal(fileData)
    }

//    fun decodeFile(key: String, fileData: ByteArray?): ByteArray? {
//
//        var decrypted = fileData
//        decrypted = try {
////            val keyBytes =
////                byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
//            val skeySpec = SecretKeySpec(key.toByteArray(), "AES")
//            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding") ///ECB/NoPadding");  ///ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec)
//            cipher.doFinal(fileData) // BadPaddingException 예외처리. 이유? 키가 다르거나 AES 키 크기가 맞지 않을때.
//        } catch (e: IllegalBlockSizeException) {
//
//
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//            fileData
//        } catch (e: BadPaddingException) {
//
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//            fileData
//        } catch (e: NoSuchAlgorithmException) {
//
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//            fileData
//        } catch (e: NoSuchPaddingException) {
//
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//            fileData
//        } catch (e: InvalidKeyException) {
//
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//            fileData
//        }
//        return decrypted
//    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {

        // 이미지 파일 이름
        val timeStamp: String = SimpleDateFormat("HHmmss").format(Date())
        val imageFileName = "pharmacy" + timeStamp + "_"

        // 이미지가 저장될 폴더 이름
        var storageDir: File = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
            File( CameraUtils.NEW_HOME_DIRECTORY_CACHE)
        } else {
            File(CameraUtils.HOME_DIRECTORY_CACHE + "/pharmacy/")
        }
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        // 빈 파일 생성
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


//    fun getPathFromUri(uri: Uri): String? {
//        val cursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
//        cursor.moveToNext()
//        val path: String = cursor.getString(cursor.getColumnIndex("_data"))
//        cursor.close()
//        return path
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    fun onUploadPrescription(){
        DialogManager.getInstance().showLoading(this)
        CoroutineScope(Dispatchers.Main).launch {
            val map = HashMap<String, RequestBody>()
            var mpFile: MultipartBody.Part? = null
            CoroutineScope(Dispatchers.Default).async {
                val day = SimpleDateFormat("yyyyMMdd_HHmmssSSS")
                val date = Date()
                val fileName = day.format(date) + ".jpg"

                val rqFile: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), outFile!!)
                mpFile = MultipartBody.Part.createFormData("userfile", fileName, rqFile)

                map["user_name"] = RequestBody.create("text/plain".toMediaTypeOrNull(), viewModel.getUserName())
                map["user_phone"] = RequestBody.create("text/plain".toMediaTypeOrNull(),viewModel.getUserPhone())
                map["device"] = RequestBody.create("text/plain".toMediaTypeOrNull(), "103")


            }.await()
            Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = if(task.result == null) "" else task.result

                token?.let {
                    Log.d(TAG, "token = $it")
                    map["fcm_token"] = RequestBody.create("text/plain".toMediaTypeOrNull(), it)
                }
                Log.d(TAG, "maptoken = ${map["fcm_token"]}")

                viewModel.fetchUploadPrescription(mpFile!!, map)
            })
        }
    }



//    @SuppressLint("LongLogTag")
//    fun getFCMToken(): String?{
//        var token: String? = null
//        Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            token = if(task.result == null) "" else task.result
//
//            token?.let {
//                Log.d(TAG, "token = $it")
//            }
//        })
//        return token
//    }

    fun setObserver(){
        viewModel.apply{
            liveUploadPrescription.observe(this@ActivityUploadPrescription, androidx.lifecycle.Observer {
                when(it.status){
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        if(it.data?.result!!){
                            showSuccessDialog()
                        } else {
                            showToast(it.data.fail)
                        }
                        DialogManager.getInstance().hideLoading()
                    }
                    Status.ERROR -> {
                        showToast("서버 연결이 원활하지 않습니다.\n다시시도해주세요.")
                        DialogManager.getInstance().hideLoading()
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("데이터가 원활하지 않습니다.\n다시시도해주세요.")
                        DialogManager.getInstance().hideLoading()
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("시간이 초과되었습니다.\n다시시도해주세요.")
                        DialogManager.getInstance().hideLoading()
                    }
                }
            })
        }
    }

    fun onFinish(){
        finish()
    }
    var progressDialog: AppCompatDialog? = null


    fun showSuccessDialog(){
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressSET()
        } else {
            progressDialog = AppCompatDialog(this@ActivityUploadPrescription)
            progressDialog!!.setCancelable(false)
            progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog!!.setContentView(R.layout.dialog_ok)
            progressDialog!!.show()
        }

        val tv_close = progressDialog!!.findViewById<TextView>(R.id.tv_close)
        val tv_text = progressDialog!!.findViewById<TextView>(R.id.tv_text)

        tv_text?.text = "사진 전송이 완료되었습니다."
        tv_close?.setOnClickListener {

            finish()
        }

    }

    fun progressSET() {
        if (progressDialog == null || !progressDialog!!.isShowing) {
            return
        }
    }

    fun progressOFF() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

}