package com.aplicacion.permisapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.aplicacion.permisapp.Models.Client
import com.aplicacion.permisapp.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainOtroBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider
import com.aplicacion.permisapp.providers.IncidenciasProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity_Otro : AppCompatActivity() {

    private lateinit var binding: ActivityMainOtroBinding
    private val authProvider = AuthProvider()
    val clientProvider = ClientProvider()
    private val incidenciasProvider = IncidenciasProvider()
    private var imageFile: File? = null
    var imagebtn : Button?=null

    var button : Button?= null

    private val PERMISO_STORAGE: Int = 99
    //Sirven para subir un PDF a storage
    private val fileResult = 1
    val PDF : Int = 0
    lateinit var uri : Uri
    private val database = Firebase.database
    private val myRef = database.getReference("PDF")

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainOtroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        imagebtn = binding.button



//lista desplegable de jefes
        val jefes =resources.getStringArray(R.array.jefes)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item,
            jefes)

            with(binding.autoCompleteTextView){
            setAdapter(adapter)
        }
        binding.fechaotro.setOnClickListener { showDatePickerDialog() }
        binding.horaInicialotro.setOnClickListener { showTimePickerDialog() }
        binding.horaFinalotro.setOnClickListener { showTimePickerDialog2() }
        binding.buttonEnviar.setOnClickListener { sendSolicitud() }
        binding.button.setOnClickListener { solicitarpermiso() }
        binding.button3otro.setOnClickListener { file() }
    }
    //Función para abrir el gestor de archivos y buscar un pdf
    private fun file(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        intent.type = "*/*"
        startActivityForResult(intent,fileResult)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileResult){
            if (resultCode == RESULT_OK && data != null){
                val clipData = data.clipData

                if ( clipData != null){
                    for (i in 0 until clipData.itemCount){
                        val uri = clipData.getItemAt(i).uri
                        uri?.let { fileUpload(it) }
                    }
                }else{
                    val uri = data.data
                    uri?.let { fileUpload(it) }
                }
            }
        }
    }

    //funcion para obtener el URI y subir el pdf a storage
    private fun fileUpload(mUri : Uri){
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("PDF")
        val path = mUri.lastPathSegment.toString()
        val fileName: StorageReference = folder.child(path.substring(path.lastIndexOf('/')+1))

        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { uri ->

                //Hacer visible la barra de brogreso
                binding.progressBarPDF.visibility = View.VISIBLE

                val hashMap = HashMap<String, String>()
                hashMap["link"] = java.lang.String.valueOf(uri)

                myRef.child(myRef.push().key.toString()).setValue(hashMap)
                Log.i("message","Receta médica cargada con exito")

                Toast.makeText(this, "Documento cargado con exito", Toast.LENGTH_SHORT).show()


            }
        }

            //Mensaje mostrado en la barra de progreso al cargar el pdf a storage
            .addOnProgressListener {
                val progress = (100 * it.bytesTransferred / it.totalByteCount).toInt()
                it.run {
                    binding.progressBarPDF.progress = progress
                    binding.tvProgress.text = String.format("Documento cargado al %s%%", progress)
                }

            }

            .addOnFailureListener {
                Log.i("message","Error al cargar el documento")

            }
    }

    //Habilitar boton para tomar fotos si tiene permisos
    private fun solicitarpermiso() {
        when {
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA, )
                    == PackageManager.PERMISSION_GRANTED -> {
                selectImage()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                mostrarmensaje("El permiso fue rechazado previamente")
            }
            else -> {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),
                    PERMISO_STORAGE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISO_STORAGE->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    selectImage()
                }
            }
            else->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun mostrarmensaje(mensaje: String) {
        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
    }


    //esta funcion guardar la imagen dentro de el textView
    private val startImageForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            imageFile = File(fileUri?.path)
            binding.imageView2.setImageURI(fileUri)
            Toast.makeText(this,
                "Firma obtenida correctamente",
                Toast.LENGTH_LONG).show()


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this,
                ImagePicker.getError(data),
                Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Tarea cancelada", Toast.LENGTH_LONG)
                .show()
        }
    }

    //esta funcion abre la galeria o la opccion para tomar una foto
    private fun selectImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }

    }


    //Esta funcion crea el registro de la solictud
    private fun sendSolicitud() {
        //estas variables son las que pide la pantalla
        val tipoIncidencia = binding.otropermiso.text.toString()
        val horaInicial = binding.horaInicialotro.text.toString()
        val horaFinal = binding.horaFinalotro.text.toString()
        val fechaSolictada = binding.fechaotro.text.toString()
        val Jefeinmediato = binding.autoCompleteTextView.text.toString()

        val status = "Solicitud enviada"
        //estas variables son para agregar la fecha actual para mostrar cuando se registro/hizo la solictud
        val date = Date()
        val fechaC = SimpleDateFormat("dd'/'MM'/'yyyy")
        val sFecha: String = fechaC.format(date)
        val format = SimpleDateFormat("hh:mm")
        val sTime : String = format.format(Date())

        //contador del Folio



        //esta funcion manda a llamar los demas datos de la incidencia para guardar en firebase
        //nombre, apellido, noEmpleado, area.
        clientProvider.getSP(authProvider.getid()).addOnSuccessListener { document ->
            val client = document.toObject(Client::class.java)
            val nombre = "${client?.nombre}"
            val apellido = "${client?.apellido}"
            val noEmpleado = "${client?.noEmpleado}"
            val area = "${client?.area}"

            //este if se encargara de registrar al usuario en firebaseAuthentication una vez que
            //los campos hayan sido validados
            if (validacion(horaInicial, horaFinal, fechaSolictada, Jefeinmediato)) {
                //este if registra la informacion del usuario

                val incidencias = Incidencias(
                    idSP= authProvider.getid(),
                    //esta variable obtiene el correo directamente desde Authentication
                    email = authProvider.auth.currentUser?.email.toString(),
                    nombre = nombre,
                    hora = sTime,
                    tipoIncidencia = tipoIncidencia,
                    horaFinal = horaFinal,
                    horaInicial = horaInicial,
                    fechaSolicitada = fechaSolictada,
                    Jefeinmediato = Jefeinmediato,
                    fechaSolicitud = sFecha,
                    status = status,
                    apellido = apellido,
                    area = area,
                    noEmpleado = noEmpleado,
                )
                if (imageFile != null) {
                    incidenciasProvider.uploadImageFirma(authProvider.getid(), imageFile!!)
                        .addOnSuccessListener { taskSnapshot ->
                            incidenciasProvider.getImageUrlFirma().addOnSuccessListener { url ->
                                val imageUrl = url.toString()
                                incidencias.firmaSP = imageUrl


                                incidenciasProvider.create(incidencias).addOnCompleteListener {
                                    if (it.isSuccessful) {

                                        showMessage()
                                        //aqui voy a agregar un progressDialog xd
                                    } else {
                                        Toast.makeText(this@MainActivity_Otro,
                                            "Hubo un error al procesar la solicitud ${it.exception.toString()}",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    }
                }
            }
        }
    }



    //este formulario valida que no se vayan a meter datos vacios xd
    private fun validacion(
        horaInicial: String, horaFinal: String, fechaSolicitada: String,
        Jefeinmediato: String
    ): Boolean {
        if (horaInicial.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la hora inicial", Toast.LENGTH_SHORT).show()
            return false
        }
        if (horaFinal.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la hora final", Toast.LENGTH_SHORT).show()
            return false
        }
        if (fechaSolicitada.isEmpty()) {
            Toast.makeText(this,
                "Debes ingresar la fecha para solictar tu incidencia",
                Toast.LENGTH_LONG).show()
            return false
        }
        if (Jefeinmediato.isEmpty()) {
            Toast.makeText(this,
                "Debes ingresar el nombre de tu Jefe Inmediato",
                Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun showMessage(){
        val view = View.inflate(this, R.layout.dialog_view, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            home()
            dialog.dismiss()
        }

    }

    private fun home(){
        val intent = Intent(this, MainActivity_home::class.java)
        startActivity(intent)
    }

    private fun showTimePickerDialog() {

        val timePicker = TimePickerFragment { onTimeSelected(it) }
        timePicker.show(supportFragmentManager, "time")
    }

    private fun onTimeSelected(time: String) {
        binding.horaInicialotro.setText("$time")

    }

    private fun showTimePickerDialog2() {

        val timePicker = TimePickerFragment { time -> onTimeSelected2(time) }
        timePicker.show(supportFragmentManager, "time")
    }

    private fun onTimeSelected2(time: String) {
        binding.horaFinalotro.setText(" $time ")

    }

    private fun showDatePickerDialog() {

        val datePicker = DatePiker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")


    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.fechaotro.setText("  $day/$month/$year")
    }


}