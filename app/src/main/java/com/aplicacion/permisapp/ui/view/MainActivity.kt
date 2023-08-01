package com.aplicacion.permisapp.ui.view

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
import androidx.lifecycle.ViewModelProvider
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainBinding
import com.aplicacion.permisapp.ui.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        binding.signinbtn.setOnClickListener {
            viewModel.showCodeAccessDialog(supportFragmentManager)
        }
        binding.ingresarbtn.setOnClickListener {
            val email = binding.correoelectronicotxt.text.toString()
            val pass = binding.passwordtxt.text.toString()

            viewModel.login(email, pass,
                onSuccess = {
                    showMessage()
                },
                onError = {
                    showMessageError()
                }
            )
        }
        binding.olvidocontrabtn.setOnClickListener {
            viewModel.navigateToRestablecer(this)
        }
        binding.ayuda.setOnClickListener {
            viewModel.help(this@MainActivity)
        }

        viewModel.addEmailTextChangedListener(binding.correoelectronicotxtL)
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
            viewModel.checkSession { entrarApp() }
        }

}


