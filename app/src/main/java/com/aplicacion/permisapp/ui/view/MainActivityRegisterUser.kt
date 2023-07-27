package com.aplicacion.permisapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.aplicacion.permisapp.data.Models.Client
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainRegisterUserBinding
import com.aplicacion.permisapp.data.providers.AuthProvider
import com.aplicacion.permisapp.data.providers.ClientProvider

class MainActivityRegisterUser : AppCompatActivity() {
    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()
    private lateinit var binding: ActivityMainRegisterUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainRegisterUserBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //lista desplegable de jefes
        val carreras =resources.getStringArray(R.array.carreras)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item,
            carreras)

        with(binding.Carreratxt){
            setAdapter(adapter)
        }


        binding.signinbtn.setOnClickListener {
            registro()
        }

        binding.loginbtn.setOnClickListener {
            irlogin()
        }
    }
     //metodos para ir a las demas pantallas
    private fun irlogin() {
        val i = Intent(this, MainActivity::class.java)
         i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun showMessage(){
        val view = View.inflate(this, R.layout.dialog_view, null)
        view.findViewById<TextView>(R.id.titleDialog).text = "Registro Exitoso"
        view.findViewById<TextView>(R.id.bodyDialog).text = "Bienvenido a PermisApp Precione \n 'Aceptar' para iniciar sesión \n"
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            binding.emailusertxt.setText("")
            binding.passwordtxt.setText("")
            binding.passwordverifytxt.setText("")
            irlogin()
            dialog.dismiss()
        }
    }
    private fun showMessageError(){
        val view = View.inflate(this, R.layout.dialog_view, null)
        view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_cancel)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡Error al Registrar!"
        view.findViewById<TextView>(R.id.bodyDialog).text = "Algo salió mal \n tal vez el correo ya fué registrado \n intenta nuevamente"
        view.findViewById<Button>(R.id.botonAlert).setText("Reintentar")
        view.findViewById<Button>(R.id.botonAlert).setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))

        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            binding.emailusertxt.setText("")
            binding.passwordtxt.setText("")
            binding.passwordverifytxt.setText("")
            dialog.dismiss()
        }
    }

    //metodo para validar los campos y registrar a los usuarios
    private fun registro() {
        val nombre = binding.usuarionametxt.text.toString()
        val apellido = binding.usuarioapellidotxt.text.toString()
        val tel = binding.telphonetxt.text.toString()
        val noEmpleado = binding.noemtxt.text.toString()
        val email = binding.emailusertxt.text.toString()
        val pass = binding.passwordtxt.text.toString()
        val area = binding.Carreratxt.text.toString()
        val rfc = binding.RFCtxt.text.toString()
        val confirmPass = binding.passwordverifytxt.text.toString()

        //este if se encargara de registrar al usuario en firebaseAuthentication una vez que
        //los campos hayan sido validados
        if (validacion(nombre, apellido, tel, noEmpleado, email, area ,pass ,confirmPass )){
            authProvider.registrer(email, pass).addOnCompleteListener {
                //este if registra la informacion del usuario
                if (it.isSuccessful){

                    val client = Client(
                        id = authProvider.getid(),
                        nombre = nombre,
                        apellido = apellido,
                        tel = tel,
                        area = area,
                        rfc = rfc,
                        noEmpleado = noEmpleado,
                        email = email

                    )
                    clientProvider.create(client).addOnCompleteListener {
                        if (it.isSuccessful){
                            binding.usuarionametxt.setText("")
                            binding.usuarioapellidotxt.setText("")
                            binding.telphonetxt.setText("")
                            binding.noemtxt.setText("")
                            binding.emailusertxt.setText("")
                            binding.passwordtxt.setText("")
                            binding.Carreratxt.setText("")
                            binding.RFCtxt.setText("")
                            binding.passwordverifytxt.setText("")
                            showMessage()

                        }else{
                            showMessageError()
                        }
                    }
                }else{
                    showMessageError()
                }
            }
        }
    }


    //esta funcion verifica que los datos ingresados en el txt sean correctos para poder registar a los usuarios
    private fun validacion (nombre:String, apellido:String, tel:String, noEmpleado : String,
                            email:String, pass : String, confirmPass : String, area: String):Boolean{
        if(nombre.isEmpty()){
            Toast.makeText(this, "Debes ingresar tu(s) Nombre(s)", Toast.LENGTH_SHORT).show()
            return false
        }
        if(apellido.isEmpty()){
            Toast.makeText(this, "Debes ingresar tus Apellidos", Toast.LENGTH_SHORT).show()
            return false
        }

        if(noEmpleado.isEmpty()){
            Toast.makeText(this, "Debes ingresar tu No. de Empleado", Toast.LENGTH_SHORT).show()
            return false
        }
        if(area.isEmpty()){
            Toast.makeText(this, "Debes ingresar el área a la que perteneces", Toast.LENGTH_LONG).show()
            return false
        }
        if(email.isEmpty()){
            Toast.makeText(this, "Debes ingresar un email", Toast.LENGTH_SHORT).show()
            return false
        }
        if(pass.isEmpty()){
            Toast.makeText(this, "Debes ingresar tu contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        if(confirmPass.isEmpty()){
            Toast.makeText(this, "Debes validar tu contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        if(pass.length < 6){
            Toast.makeText(this, "Las contraseñas deben tener al menos 6 caracteres", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }





}



