package com.jucha.pharmacy.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import com.jucha.pharmacy.R
import com.jucha.pharmacy.base.BaseActivity
import com.jucha.pharmacy.databinding.ActivityLoginBinding
import com.jucha.pharmacy.databinding.DialogAgreeNotiBinding
import com.jucha.pharmacy.databinding.DialogExitBinding
import com.jucha.pharmacy.databinding.DialogNotAgreeiBinding
import com.jucha.pharmacy.ui.main.MainActivity
import com.jucha.pharmacy.utils.DialogManager
import com.jucha.pharmacy.utils.loading.Status
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    var agreeNotiDialog: AppCompatDialog? = null
    var notAgreeDiaglog: AppCompatDialog? = null
    var exitDiaglog: AppCompatDialog? = null
    //결제 다이어그램
    var priceDiaglog: AppCompatDialog? = null

    override fun init() {
        binding.apply {
            vm = viewModel
            activity = this@LoginActivity
        }

        viewModel.apply {
            dialogEvent eventObserve { showNotAgreeDialog() }
            toastEvent eventObserve { showToast(it) }

            login.observe(this@LoginActivity) {
                when (it.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        if (it.data?.result.toBoolean()) {
                            setLogin()
                            loginCheck()
                        } else {
                            showToast("로그인에 실패했습니다.\n다시시도해주세요..")
                        }
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
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loginCheck()
    }

    fun loginCheck() {
        if (viewModel.isLogin()) {
            //TODO 자동로그인 완료 시
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            //TODO 자동로그인 미완료 시
        }
    }

    fun onPayment() {
        val intent = Intent(this@LoginActivity, PriceActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    fun onShowAgree(view: View) {
        view.isSelected = !view.isSelected
        val AllCheck =  findViewById<ImageView>(R.id.Allcheck)
        val C_1 = findViewById<ImageView>(R.id.check1)
        val C_2 = findViewById<ImageView>(R.id.check2)
        val C_3 = findViewById<ImageView>(R.id.check3)

        if (view.isSelected) {
            when (view.tag) {
                "1" -> {
                    viewModel.liveAgree1.postValue(true)
                    C_1.setBackgroundResource(R.drawable.ic_check_on);
                }
                "2" -> {
                    viewModel.liveAgree2.postValue(true)
                    C_2.setBackgroundResource(R.drawable.ic_check_on);
                }
                "3" -> {
                    viewModel.liveAgree3.postValue(true)
                    C_3.setBackgroundResource(R.drawable.ic_check_on);
                }
                else -> {
                    showAgreeDialog()
                    viewModel.liveAgree1.postValue(true)
                    viewModel.liveAgree2.postValue(true)
                    viewModel.liveAgree3.postValue(true)

                    AllCheck.setBackgroundResource(R.drawable.ic_check_on);
                    C_1.setBackgroundResource(R.drawable.ic_check_on);
                    C_2.setBackgroundResource(R.drawable.ic_check_on);
                    C_3.setBackgroundResource(R.drawable.ic_check_on);
                }

            }
        } else {
            when (view.tag) {
                "1" -> {
                    viewModel.liveAgree1.postValue(false)
                    C_1.setBackgroundResource(R.drawable.ic_check_off);
                }
                "2" -> {
                    viewModel.liveAgree2.postValue(false)
                    C_2.setBackgroundResource(R.drawable.ic_check_off);
                }
                "3" -> {
                    viewModel.liveAgree3.postValue(false)
                    C_3.setBackgroundResource(R.drawable.ic_check_off);
                }
                else -> {
                    viewModel.liveAgree1.postValue(false)
                    viewModel.liveAgree2.postValue(false)
                    viewModel.liveAgree3.postValue(false)
                    AllCheck.setBackgroundResource(R.drawable.ic_check_off);
                    C_1.setBackgroundResource(R.drawable.ic_check_off);
                    C_2.setBackgroundResource(R.drawable.ic_check_off);
                    C_3.setBackgroundResource(R.drawable.ic_check_off);
                }
            }
        }
    }

    fun showAgreeDialog() {
        if (agreeNotiDialog == null) {
            agreeNotiDialog = AppCompatDialog(this@LoginActivity)
        }

        val binding = DataBindingUtil.inflate<DialogAgreeNotiBinding>(
            LayoutInflater.from(this@LoginActivity),
            R.layout.dialog_agree_noti,
            null,
            false
        )
        agreeNotiDialog?.apply {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(binding.root)
            show()
        }

        binding.apply {
            activity = this@LoginActivity
            vm = viewModel
            time = getNowTime()
        }
    }

    fun showNotAgreeDialog() {
        if (notAgreeDiaglog == null) {
            notAgreeDiaglog = AppCompatDialog(this@LoginActivity)
        }

        val binding = DataBindingUtil.inflate<DialogNotAgreeiBinding>(
            LayoutInflater.from(this@LoginActivity),
            R.layout.dialog_not_agreei,
            null,
            false
        )
        notAgreeDiaglog?.apply {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(binding.root)
            show()
        }

        binding.apply {
            activity = this@LoginActivity
            vm = viewModel
        }
    }

    fun showExitDialog() {
        if (exitDiaglog == null) {
            exitDiaglog = AppCompatDialog(this@LoginActivity)
        }

        val binding = DataBindingUtil.inflate<DialogExitBinding>(
            LayoutInflater.from(this@LoginActivity),
            R.layout.dialog_exit,
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
            activity = this@LoginActivity
            vm = viewModel
        }
    }

    fun getNowTime(): String {
        return SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분").format(Date(System.currentTimeMillis()))
    }

    fun onAgreeClose() {
        if (agreeNotiDialog != null) {
            agreeNotiDialog!!.dismiss()
        }
    }

    fun onNotAgreeClose() {
        if (notAgreeDiaglog != null) {
            notAgreeDiaglog!!.dismiss()
        }
    }

    fun onExitClose() {
        if (exitDiaglog != null) {
            exitDiaglog!!.dismiss()
        }
    }
    fun onPrice() {
        if (priceDiaglog != null) {
            priceDiaglog!!.dismiss()
        }
    }


    fun onExit() {
        moveTaskToBack(true) // 태스크를 백그라운드로 이동
        finishAndRemoveTask() // 액티비티 종료 + 태스크 리스트에서 지우기
        Process.killProcess(Process.myPid())
    }


    override fun onBackPressed() {
        showExitDialog()
    }
}