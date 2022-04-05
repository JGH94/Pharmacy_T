package com.jucha.pharmacy.ui.payment.success

import com.jucha.pharmacy.base.BaseViewModel
import com.jucha.pharmacy.repository.LoginPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentSuccessViewModel  @Inject constructor(
    loginPreference: LoginPreference
) : BaseViewModel() {

}