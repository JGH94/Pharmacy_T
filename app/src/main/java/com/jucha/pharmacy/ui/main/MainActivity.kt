package com.jucha.pharmacy.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.zxing.integration.android.IntentIntegrator
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jucha.pharmacy.R
import com.jucha.pharmacy.base.BaseActivity
import com.jucha.pharmacy.databinding.*
import com.jucha.pharmacy.ui.login.LoginActivity
import com.jucha.pharmacy.ui.map.MapActivity
import com.jucha.pharmacy.ui.payment.PaymentActivity
import com.jucha.pharmacy.ui.payment.list.PaymentHistoryActivity
import com.jucha.pharmacy.ui.payment.request.PaymentRequestActivity
import com.jucha.pharmacy.ui.upload.ActivityUploadPrescription
import com.jucha.pharmacy.utils.DialogManager
import com.jucha.pharmacy.utils.camera.CameraUtils
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
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {

    val TAG = "MainActivity"
    var isNotNotiPermission = MutableLiveData<Boolean>(false)

    private var tempFile: File? = null

    var selectCameraDialog: Dialog? = null
    var loginDialog: AppCompatDialog? = null

    lateinit var cameraUtils: CameraUtils
    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                val intent = Intent(this@MainActivity, ActivityUploadPrescription::class.java)
                intent.putExtra("uri", photoUri.toString())
                startActivity(intent)
            }
        }

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val intent = Intent(this@MainActivity, ActivityUploadPrescription::class.java)
                intent.putExtra("uri", result.data?.data.toString())
                startActivity(intent)
            }

        }

    override fun init() {
        binding.apply {
            vm = viewModel
            activity = this@MainActivity
        }
        cameraUtils = CameraUtils(this)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        viewModel.apply {
            loginEvent eventObserve {
                if (!it) {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            toastEvent eventObserve { showToast(it) }
            fetchAcccountInfo()
        }
        setObserver()
    }

    fun setObserver() {
        viewModel.apply {
            payRequest.observe(this@MainActivity, androidx.lifecycle.Observer {
                when (it.status) {
                    Status.LOADING -> {
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
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            val data = it.data!!
                            if (data.size > 1) {
                                val intent =
                                    Intent(this@MainActivity, PaymentRequestActivity::class.java)
                                intent.putExtra("data", data)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this@MainActivity, PaymentActivity::class.java)
                                intent.putExtra("data", data[0])
                                startActivity(intent)
                            }
                        } else {
                            showToast("결제가능건이 없습니다.")
                        }

                    }
                }
            })
            liveSelectPosition.observe(this@MainActivity, androidx.lifecycle.Observer {
                binding.apply {
                    when (it) {
                        1 -> {

                            cardCall.visibility = View.GONE
                            cardPayList.visibility = View.GONE
                            cardPhoto.visibility = View.VISIBLE
                            QRSelect.visibility = View.GONE
                        }
                        2 -> {
                        }
                        3 -> {
                        }
                        4 -> {
                            cardCall.visibility = View.VISIBLE
                            cardPayList.visibility = View.VISIBLE
                            cardPhoto.visibility = View.GONE
                            QRSelect.visibility = View.GONE
                        }
                    }
                }

            })
            liveAccountInfo.observe(this@MainActivity, {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        val account = it.data?.get(0)
                        binding.apply {

                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showToast(msg) }
                    }
                    Status.NETWORK_ERROR -> {
                        showToast("데이터가 원활하지 않습니다.\n다시시도해주세요.")
                    }
                    Status.TIMEOUT_ERROR -> {
                        showToast("시간이 초과되었습니다.\n다시시도해주세요.")
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        isNotNotiPermission.value = areNotificationsEnabled()
        viewModel.onLoginCheck()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.fetchPayRequest()
    }

    fun areNotificationsEnabled(channelId: String = "fcm_fallback_notification_channel"): Boolean {
        // check if global notification switch is ON or OFF
        if (NotificationManagerCompat.from(this).areNotificationsEnabled())
        // if its ON then we need to check for individual channels in OREO
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = manager.getNotificationChannel(channelId)
                return channel?.importance != NotificationManager.IMPORTANCE_NONE
            } else {
                // if this less then OREO it means that notifications are enabled
                true
            }
        // if this is less then OREO it means that notifications are disabled
        return false
    }


    fun onShowSelectCamera() {
        if (selectCameraDialog == null) {
            selectCameraDialog = Dialog(this@MainActivity)
        }

        val binding = DataBindingUtil.inflate<DialogSelectPhotoBinding>(
            LayoutInflater.from(this@MainActivity),
            R.layout.dialog_select_photo,
            null,
            false
        )
        selectCameraDialog?.apply {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(binding.root)
            show()
        }

        binding.apply {
            activity = this@MainActivity
        }
    }

    fun onPaymentList() {
        PaymentHistoryActivity.startPaymentHistoryActivity(this@MainActivity)
    }

    fun onShowCamera() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            XXPermissions.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.CAMERA)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
//                            cameraUtils.captureCamera()
                        try {
                            tempFile = cameraUtils.createImageFile()
                        } catch (e: IOException) {
                            Toast.makeText(
                                this@MainActivity,
                                "이미지 처리 오류! 다시 시도해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
//                            finish()
                            e.printStackTrace()
                        }
                        if (tempFile != null) {
                            photoUri = FileProvider.getUriForFile(
                                applicationContext,
                                packageName,
                                tempFile!!
                            )
                            photoLauncher.launch(photoUri)
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)

                    }
                })
        } else {
            XXPermissions.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.CAMERA)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        try {
                            tempFile = cameraUtils.createImageFile()
                        } catch (e: IOException) {
                            Toast.makeText(
                                this@MainActivity,
                                "이미지 처리 오류! 다시 시도해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
//                            finish()
                            e.printStackTrace()
                        }
                        if (tempFile != null) {
                            photoUri = Uri.fromFile(tempFile)
                            photoLauncher.launch(photoUri)
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)

                    }
                })
        }
    }

    fun ShowQR(){
        IntentIntegrator(this).initiateScan()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // result.contents에는 스캔한 결과가 포함된다. 만약 null이라면 사용자가 스캔을 완료하지 않고 QR 리더 액티비티를 종료한 것이다.
            if (result.contents != null) {
                //${result.formatName}
                onUploadQR(Change_QR_DATA(result.contents))
                Toast.makeText(this, "전송되었습니다.", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
            // integrator.setBarcodeImageEnabled(true)를 하였다면 아래와 같이 스캔된 결과 이미지 파일을 저장하고 그 경로를 가져올 수 있다.
            if (result.barcodeImagePath != null) {
                Log.i(TAG, "onActivityResult: ${result.barcodeImagePath}")
                val bitmap = BitmapFactory.decodeFile(result.barcodeImagePath)
             }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 1000) {
                val intent = Intent(this@MainActivity, ActivityUploadPrescription::class.java)
                intent.putExtra("uri", cameraUtils.imageUri)
                startActivity(intent)
            }
        }

    }

    fun Change_QR_DATA(QRCon: String):String
    {
        val changeValue = QRCon.replace("'", "''")
        return changeValue
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onUploadQR(QRCon:String){

        //DialogManager.getInstance().showLoading(this)
        CoroutineScope(Dispatchers.Main).launch {
            val map = HashMap<String, RequestBody>()
            CoroutineScope(Dispatchers.Default).async {
                map["device"] = RequestBody.create("text/plain".toMediaTypeOrNull(), "103")
                map["qr_data"] = RequestBody.create("text/plain".toMediaTypeOrNull(), QRCon)

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
                viewModel.fetchUploadQR(map)
            })
        }
    }

    fun onShowCameraAlbum() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            XXPermissions.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.CAMERA)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = MediaStore.Images.Media.CONTENT_TYPE
                        intent.type = "image/*"
                        albumLauncher.launch(intent)
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)

                    }
                })
        } else {
            XXPermissions.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.CAMERA)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = MediaStore.Images.Media.CONTENT_TYPE
                        intent.type = "image/*"
                        albumLauncher.launch(intent)
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)
                    }
                })
        }
    }

    @SuppressLint("MissingPermission")
    fun onShowLogin() {

        if (loginDialog == null) {
            loginDialog = AppCompatDialog(this@MainActivity)
        }

        val binding = DataBindingUtil.inflate<DialogLoginBinding>(
            LayoutInflater.from(this@MainActivity),
            R.layout.dialog_login,
            null,
            false
        )
        loginDialog?.apply {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(binding.root)
            show()
        }

        binding.apply {
            activity = this@MainActivity
            vm = viewModel
        }

    }

    fun onLoginClose() {
        if (loginDialog != null) {
            loginDialog!!.dismiss()
        }
        viewModel.onLoginCheck()
    }

    lateinit var photoUri: Uri


    fun onNotiSetting() {
        try {
            var intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            var intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            startActivity(intent)
        }
    }

    fun onShowPayment() {
        viewModel.apply {
            fetchPayRequest()
        }
//        val intent = Intent(this@MainActivity, PaymentActivity::class.java)
//        startActivity(intent)
    }

    fun onShowCall() {
        val tel = "tel:0512486637"
//        startActivity(Intent("android.intent.action.CALL", Uri.parse(tel)))

        XXPermissions.with(this)
            .permission(Permission.CALL_PHONE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(tel))
                    startActivity(intent)
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)

                }
            })
    }

    fun showHome() {
        viewModel.liveSelectPosition.postValue(1)
        Select_menu(1);
    }

    fun showMap() {
        val intent = Intent(this@MainActivity, MapActivity::class.java)
        startActivity(intent)
    }

    fun showChat() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://pf.kakao.com/_XSPxlb"))
        startActivity(intent)
    }
    fun copyAccount() {
        //클립보드 사용 코드
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText ("pharmacy_account", binding.tvAccountBank.text.toString()); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
        clipboardManager.setPrimaryClip(clipData)

        //복사가 되었다면 토스트메시지 노출
        showToast("계좌번호가 복사되었습니다.")
    }
    fun showMypage() {
        viewModel.liveSelectPosition.postValue(4)
        Select_menu(4);
    }
    fun Select_menu(num_:Int ){
        if(num_ == 1)
        {
            val H = findViewById<ImageView>(R.id.ic_home_)
            H.setBackgroundResource(R.drawable.background_round_34);
            val M = findViewById<LinearLayout>(R.id.showMap_)
            M.setBackgroundResource(0);
            val C = findViewById<LinearLayout>(R.id.showChat_)
            C.setBackgroundResource(0);
            val Y = findViewById<ImageView>(R.id.showMypage_image)
            Y.setBackgroundResource(0);

        }
        else if(num_ == 4)
        {
            val H = findViewById<ImageView>(R.id.ic_home_)
            H.setBackgroundResource(0);
            val M = findViewById<LinearLayout>(R.id.showMap_)
            M.setBackgroundResource(0);
            val C = findViewById<LinearLayout>(R.id.showChat_)
            C.setBackgroundResource(0);
            val Y = findViewById<ImageView>(R.id.showMypage_image)
            Y.setBackgroundResource(R.drawable.background_round_34);
        }
    }

    var exitDiaglog: AppCompatDialog? = null
    fun showExitDialog(){
        if (exitDiaglog == null) {
            exitDiaglog = AppCompatDialog(this@MainActivity)
        }

        val binding = DataBindingUtil.inflate<DialogLogoutBinding>(
            LayoutInflater.from(this@MainActivity),
            R.layout.dialog_logout,
            null,
            false
        )
        exitDiaglog?.apply {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(binding.root)
            show()
        }

        binding.apply {
            activity = this@MainActivity
            vm = viewModel
        }
    }
    fun noBtnClick(){
        if (exitDiaglog != null) {
            exitDiaglog!!.dismiss()
        }
    }
    fun Last_Date(){

    }
}