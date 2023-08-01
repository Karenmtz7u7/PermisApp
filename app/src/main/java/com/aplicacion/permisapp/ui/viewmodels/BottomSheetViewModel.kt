package com.aplicacion.permisapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.aplicacion.permisapp.domain.repository.AuthRepository

class BottomSheetViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    fun logOut() {
        authRepository.logOut()
    }
}