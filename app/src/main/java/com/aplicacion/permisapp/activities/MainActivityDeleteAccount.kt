package com.aplicacion.permisapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.aplicacion.permisapp.Models.Client
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainDeleteAccountBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider

class MainActivityDeleteAccount : AppCompatActivity() {
    private lateinit var binding : ActivityMainDeleteAccountBinding

    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainDeleteAccountBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        binding.deleteAccountbtn.setOnClickListener {
            deleteAccount()
        }
        binding.backbtn.setOnClickListener {
            backProfile()
        }


        setContentView(binding.root)
    }

    private fun deleteAccount() {
        val email = binding.emailusertxt.text.toString()
        if (authProvider.auth.currentUser != null){
            val verifyEmail = authProvider.auth.currentUser!!.email
            if(verifyEmail == email){
                val client = Client(
                    id = authProvider.getid()
                )
                clientProvider.remove(client).addOnSuccessListener {
                    authProvider.deleteAccount()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            setDialog()
                        } else {
                            showMessageError()
                        }
                    }
                }
            }
            else{
                binding.textalert.text = "¡El correo electronico no pertenece a esta cuenta!"
            }
        }
    }

    private fun setDialog(){

        val view = View.inflate(this, R.layout.dialog_view, null)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡Se han borrado los datos!"
        view.findViewById<TextView>(R.id.bodyDialog).text = "Lamentamos mucho que te vayas"
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        object: CountDownTimer(2500,2500){
            override fun onTick(millisUntilFinished: Long) {
            }
            //Ejecuta la pantalla de inicio despues del tiempo asignado
            override fun onFinish() {
                dialog.dismiss()
                cerrarsesion()
            }
        }.start()
    }

    private fun cerrarsesion() {
        authProvider.logOut()
        val i= Intent(this, MainActivity::class.java )
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
    private fun backProfile() {
        val i= Intent(this, MainActivityPerfil::class.java )
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun showMessageError(){
        val view = View.inflate(this, R.layout.dialog_view, null)
        view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_cancel)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡Error al eliminar cuenta!"
        view.findViewById<TextView>(R.id.bodyDialog).text = "Algo salió mal, intenta nuevamente"
        view.findViewById<Button>(R.id.botonAlert).text = "Reintentar"
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
}