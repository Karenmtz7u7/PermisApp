package com.aplicacion.permisapp.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.aplicacion.permisapp.domain.repository.AuthRepository
import com.aplicacion.permisapp.ui.view.Code_access
import com.aplicacion.permisapp.ui.view.MainActivityRegisterUser
import com.aplicacion.permisapp.ui.view.MainActivity_restablecer
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class MainActivityViewModel  : ViewModel() {
    private val authRepository = AuthRepository()

    // Link para ir al manual de usuario
    fun help(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("https://drive.google.com/file/d/1LLEzuC4avRorOmv3U7inK1cP74srYFLr/view?usp=share_link")
        context.startActivity(intent)
    }

    // Abre el código de acceso al momento de tocar el botón registrar
    fun showCodeAccessDialog(fragmentManager: FragmentManager) {
        Code_access().show(fragmentManager, Code_access::class.java.simpleName)
    }

    fun navigateToRestablecer(context: Context) {
        val intent = Intent(context, MainActivity_restablecer::class.java)
        context.startActivity(intent)
    }

    fun navigateToRegistro(context: Context) {
        val intent = Intent(context, MainActivityRegisterUser::class.java)
        context.startActivity(intent)
    }

    // Función para validar datos e iniciar sesión
    fun login(email: String, password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        if (validation(email, password)) {
            authRepository.logIn(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onError()
                }
            }
        }
    }

    // Verifica que no haya errores
    private fun validation(email: String, pass: String): Boolean {
        if (email.isEmpty()) {

        }
        if (pass.isEmpty()) {
            // Agregar el código para mostrar el Toast con el mensaje adecuado
            return false
        }
        return true
    }

    fun checkSession(onSuccess: () -> Unit) {
        if (authRepository.starSession()) {
            onSuccess()
        }
    }

    fun addEmailTextChangedListener(textInputLayout: TextInputLayout) {
        textInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    textInputLayout.error = null
                } else {
                    textInputLayout.error = "Correo Inválido"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}