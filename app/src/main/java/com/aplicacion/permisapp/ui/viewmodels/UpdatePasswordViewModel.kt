package com.aplicacion.permisapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aplicacion.permisapp.domain.repository.AuthRepository

class UpdatePasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {

    sealed class Message {
        data class Success(val message: String) : Message()
        data class Error(val message: String) : Message()
    }

    private val _messageLiveData = MutableLiveData<Message>()
    val messageLiveData: LiveData<Message> = _messageLiveData

    fun updatePassword(pass: String, confirmPass: String) {
        if (pass.isEmpty() || confirmPass.isEmpty()) {
            _messageLiveData.value = Message.Error("¡No puedes dejar cajas de texto vacías!")
        } else {
            if (pass == confirmPass) {
                if (authRepository.starSession()) {
                    authRepository.updatePassword(pass)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _messageLiveData.value = Message.Success("¡La contraseña se ha cambiado!")
                        } else {
                            _messageLiveData.value = Message.Error("Algo salió mal, intenta nuevamente")
                        }
                    }
                }
            } else {
                _messageLiveData.value = Message.Error("¡Las contraseñas no coinciden!")
            }
        }
    }


}