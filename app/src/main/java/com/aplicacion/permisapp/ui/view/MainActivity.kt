package com.aplicacion.permisapp.ui.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainBinding
import com.aplicacion.permisapp.domain.repository.AuthRepository
import com.aplicacion.permisapp.domain.repository.ClientRepository
import com.aplicacion.permisapp.domain.repository.ClientRHRepository


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authRepository = AuthRepository()
    private val clientRepository = ClientRepository()
    private lateinit var dialog: AlertDialog.Builder
    private val clientRHRepository = ClientRHRepository()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinbtn.setOnClickListener {
            codigo()
        }
        binding.ingresarbtn.setOnClickListener {
           Login()
        }
        binding.olvidocontrabtn.setOnClickListener {
            irRestablecer()
        }
        binding.ayuda.setOnClickListener {
            ayuda()
        }
        verificar()
    }

    //Link para ir al manual de usuario
    private fun ayuda(){
        binding.ayuda.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://drive.google.com/file/d/1LLEzuC4avRorOmv3U7inK1cP74srYFLr/view?usp=share_link")
            startActivity(intent)
        }
    }
    // abre el codigo de acceso al momento de tocar el botón registrar
    private fun codigo(){
        Code_access().show(supportFragmentManager,Code_access::class.java.simpleName)
    }

    private fun irRestablecer() {
        val i = Intent(this, MainActivity_restablecer::class.java)
        startActivity(i)
    }

    private fun iraregistro() {
        val i = Intent(this, MainActivityRegisterUser::class.java)
        startActivity(i)
    }

    //funcion para validar datos e iniciar sesión
    private fun Login(){
        val email = binding.correoelectronicotxt.text.toString()
        val pass = binding.passwordtxt.text.toString()

        if (validacion(email,pass)) {
            //funcion para iniciar sesion
            authRepository.logIn(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                   showMessage()
                } else {
                    showMessageError()
                }
            }
        }
    }



//verifica que no haya errores
    private fun validacion(email:String, pass: String):Boolean {
        if(email.isEmpty()){
            Toast.makeText(this, "Ingresa tu correo electronico", Toast.LENGTH_SHORT).show()
            return false
        }
        if(pass.isEmpty()){
            Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun verificar() {
        binding.correoelectronicotxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(binding.correoelectronicotxt.text.toString()).matches()) {
                    binding.ingresarbtn.isEnabled = true
                } else {
                    binding.ingresarbtn.isEnabled = false
                    binding.correoelectronicotxt.error = "Correo Invalido"
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }


    private fun showMessage(){
        val view = View.inflate(this, R.layout.dialog_progress, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        object: CountDownTimer(2000,2000){
            override fun onTick(millisUntilFinished: Long) {
            }
            //Ejecuta la pantalla de Home despues del tiempo asignado
            override fun onFinish() {
                dialog.dismiss()
                entrarApp()
            }
        }.start()
    }
    private fun showMessageError(){
        val view = View.inflate(this, R.layout.dialog_view, null)
        view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_cancel)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡Error al Iniciar sesión!"
        view.findViewById<TextView>(R.id.bodyDialog).text = "Algo salió mal \n las credenciales no son correctas \n intenta nuevamente"
        view.findViewById<Button>(R.id.botonAlert).setText("Reintentar")
        view.findViewById<Button>(R.id.botonAlert).setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))

        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            dialog.dismiss()
        }
    }

    //funcion que te lleva a la pagina principal de la app Home
    private fun entrarApp(){
        val i = Intent(this, MainActivity_home::class.java)
        //esta linea borra el historial de pantallas
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)

    }

    //ciclo de vida para mantener la sesion abierta
    override fun onStart() {
        super.onStart()
        if (authRepository.starSession()){
            entrarApp()
        }
    }
}


