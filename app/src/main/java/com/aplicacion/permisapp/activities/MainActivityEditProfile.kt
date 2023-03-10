package com.aplicacion.permisapp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.aplicacion.permisapp.Models.Client
import com.aplicacion.permisapp.databinding.ActivityMainEditProfileBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask.TaskSnapshot
import java.io.File

class MainActivityEditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityMainEditProfileBinding
    val clientProvider = ClientProvider()
    val authProvider = AuthProvider()
    private var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

        getInformation()

        binding= ActivityMainEditProfileBinding.inflate(layoutInflater)
            setContentView(binding.root)


        binding.editprofilebtn.setOnClickListener {
                UpdateInfo()
        }
        binding.Camerabtn.setOnClickListener {selectImage()}


    }


    private fun getInformation() {
        clientProvider.getSP(authProvider.getid()).addOnSuccessListener { document ->
            if (document.exists()) {
                val client = document.toObject(Client::class.java)
                binding.usuarionametxt.setText("${client?.nombre}")
                binding.usuarioapellidotxt.setText("${client?.apellido}")
                binding.email.setText("${client?.email}")
                binding.Carreratxt.setText("${client?.area}")
                binding.noemtxt.setText( "${client?.noEmpleado}")
                binding.telphonetxt.setText("${client?.tel}")

                if (client?.image != null) {
                    if (client.image != "") {
                        Glide.with(this).load(client.image).centerCrop().into(binding.userimagetxt)
                    }
                }
            }
        }
    }

    private fun UpdateInfo(){
        val  nombre = binding.usuarionametxt.text.toString()
        val  apellido = binding.usuarioapellidotxt.text.toString()
        val  tel = binding.telphonetxt.text.toString()

        val client = Client(
            id = authProvider.getid(),
            nombre = nombre,
            apellido = apellido,
            tel = tel
        )
        clientProvider.update(client).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "No se pudo actualizar la informacion", Toast.LENGTH_LONG).show()
            }
        }
    }

    //esta funcion guardar la imagen dentro de el textView
    private val startImageForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK){
            val fileUri = data?.data
            imageFile = File(fileUri?.path)
            binding.userimagetxt.setImageURI(fileUri)
            imageUpload()

        }else if(resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this@MainActivityEditProfile, ImagePicker.getError(data), Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this@MainActivityEditProfile, "Tarea cancelada", Toast.LENGTH_LONG).show()
        }



    }

    //esta funcion abre la galeria o la opccion para tomar una foto
    private fun selectImage() {
        ImagePicker.with(this@MainActivityEditProfile)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }

    }
    private fun imageUpload(){
        val client = Client(
            id = authProvider.getid()
        )
        if (imageFile != null) {
            clientProvider.uploadImage(authProvider.getid(), imageFile!!)
                .addOnSuccessListener { taskSnapshot ->
                    clientProvider.getImageUrl().addOnSuccessListener { url ->
                        val imageUrl = url.toString()
                        client.image = imageUrl
                        clientProvider.updateImage(client).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this,
                                    "Imagen actualizada correctamente",
                                    Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this,
                                    "No se pudo actualizar la foto",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                        Log.d("STORAGE", "$imageUrl")
                    }
                }
        }
    }








}









