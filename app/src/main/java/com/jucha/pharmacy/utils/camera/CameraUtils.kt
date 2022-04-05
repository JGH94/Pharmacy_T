package com.jucha.pharmacy.utils.camera

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.jucha.pharmacy.utils.BaseUtils.context
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CameraUtils(val context: Context) {

    companion object{
        var cw = ContextWrapper(context)

        val HOME_DIRECTORY = Environment.getExternalStorageDirectory().path
        val HOME_DIRECTORY_CACHE = cw.externalCacheDir?.path
        val NEW_HOME_DIRECTORY = cw.getExternalFilesDir(null)!!.path
        val NEW_HOME_DIRECTORY_CACHE = cw.externalCacheDir?.path
    }



    var imageUri: Uri? = null


    @Throws(IOException::class)
    fun createImageFile(): File? {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        val timeStamp: String = SimpleDateFormat("HHmmss").format(Date())
        val imageFileName = "pharmacy" + timeStamp + "_"
        // 이미지가 저장될 폴더 이름 ( blackJin )
        var storageDir: File = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            File(NEW_HOME_DIRECTORY_CACHE)
        } else {
            File("$HOME_DIRECTORY_CACHE")
        }
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        // 빈 파일 생성
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


    //카메라 작동
    fun captureCamera() {
        val state = Environment.getExternalStorageState()

        //외장메모리 검사
        if (Environment.MEDIA_MOUNTED == state) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(context.packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (e: IOException) {
                }
                if (photoFile != null) {

                    //getUriForFile 의 두번째 인자는 매니패스트 provider의 authorites와 일치해야한다
                    val providerURI: Uri =
                        FileProvider.getUriForFile(context, context.packageName, photoFile)
                    imageUri = providerURI

                    //인텐트 전달 할때는 FileProvider의 return값인 Content://로만 , providerURI 값에 카메라 데이터를 넣어보냄
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                    (context as Activity).startActivityForResult(intent, 1000)
                }
            }
        } else {
            return
        }
    }
}