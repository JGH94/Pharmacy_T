package com.jucha.pharmacy.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.observe
import com.jucha.pharmacy.utils.Event
import io.reactivex.disposables.CompositeDisposable
import java.lang.Exception
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes val layoutId: Int
) : AppCompatActivity() {

    var mToast: Toast? = null
    var isShowToast = false

    lateinit var binding: B

    @SuppressWarnings("unchecked")
    private val viewModelClass = ((javaClass.genericSuperclass as ParameterizedType?)
        ?.actualTypeArguments
        ?.get(1) as Class<VM>).kotlin

    protected open val viewModel by ViewModelLazy(
        viewModelClass,
        { viewModelStore },
        { defaultViewModelProviderFactory }
    )

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        try{
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (e: Exception){

        }
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        init()
    }

    abstract fun init()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    protected fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    protected infix fun <T> LiveData<Event<T>>.eventObserve(action: (T) -> Unit) {
        observe(this@BaseActivity) {
            it.get(action)
        }
    }
}