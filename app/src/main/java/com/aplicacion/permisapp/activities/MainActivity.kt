package com.aplicacion.permisapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.aplicacion.permisapp.databinding.ActivityMainBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider
import com.aplicacion.permisapp.providers.ClientRHProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.checkerframework.common.returnsreceiver.qual.This


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()
    private lateinit var dialog: AlertDialog.Builder
    private val clientRHProvider = ClientRHProvider()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.signinbtn.setOnClickListener {
            iraregistro()
        }
        binding.ingresarbtn.setOnClickListener {
           Login()
        }
        binding.olvidocontrabtn.setOnClickListener {
            irRestablecer()
        }
        verificar()
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
            authProvider.logIn(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    setDialog()
                } else {
                    Toast.makeText(this@MainActivity, "Inicio de sesion incorrecto",
                        Toast.LENGTH_SHORT).show()
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

    private fun setDialog(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Iniciando sesion...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        object: CountDownTimer(2000,2000){
            override fun onTick(millisUntilFinished: Long) {
            }
            //Ejecuta la pantalla de Home despues del tiempo asignado
            override fun onFinish() {
                progressDialog.dismiss()
                entrarApp()
            }
        }.start()
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
        if (authProvider.starSession()){
            entrarApp()
        }
    }
}


