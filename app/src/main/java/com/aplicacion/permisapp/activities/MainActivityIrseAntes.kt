package com.aplicacion.permisapp.activities

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.aplicacion.permisapp.Models.Client
import com.aplicacion.permisapp.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainIrseAntesBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider
import com.aplicacion.permisapp.providers.IncidenciasProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivityIrseAntes : AppCompatActivity() {
    private lateinit var binding: ActivityMainIrseAntesBinding
    private val authProvider = AuthProvider()
    val clientProvider = ClientProvider()
    private val incidenciasProvider = IncidenciasProvider()
    private var imageFile: File? = null
    var imagebtn : Button?=null
    private val PERMISO_STORAGE: Int = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainIrseAntesBinding.inflate(layoutInflater)
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

        binding.button.setOnClickListener {solicitarpermiso()   }


        binding.seleccionarfechaSalir.setOnClickListener {
            showDatePickerDialog()
        }


        binding.horaInicialSalir.setOnClickListener {
            showTimePickerDialog()
        }

        binding.horaFinalSalir.setOnClickListener {
            showTimePickerDialog2()
        }

        binding.buttonsalirantes.setOnClickListener { sendSolicitud() }

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
        val tipoIncidencia = binding.editTextTextPersonName2.text.toString()
        val horaInicial = binding.horaInicialSalir.text.toString()
        val horaFinal = binding.horaFinalSalir.text.toString()
        val fechaSolictada = binding.seleccionarfechaSalir.text.toString()
        val Jefeinmediato = binding.autoCompleteTextView.text.toString()
        val status = "Solicitud enviada"
        //estas variables son para agregar la fecha actual para mostrar cuando se registro/hizo la solictud
        val date = Date()
        val fechaC = SimpleDateFormat("dd'/'MMMM'/'yyyy")
        val sFecha: String = fechaC.format(date)
        //estas variables obtienen la hora actual
        val format = SimpleDateFormat("hh:mm")
        val sTime : String = format.format(Date())

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
                    idSP = authProvider.getid(),
                    //esta variable obtiene el correo directamente desde Authentication
                    email = authProvider.auth.currentUser?.email.toString(),
                    nombre = nombre,
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
                    hora = sTime,
                )
                if (imageFile != null) {
                    incidenciasProvider.uploadImageFirma(authProvider.getid(), imageFile!!)
                        .addOnSuccessListener { taskSnapshot ->
                            incidenciasProvider.getImageUrlFirma().addOnSuccessListener { url ->
                                val imageUrl = url.toString()
                                incidencias.firmaSP = imageUrl
                                Log.d("STORAGE", "$imageUrl")

                                incidenciasProvider.create(incidencias).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(this@MainActivityIrseAntes,
                                            "Solicitud exitosa",
                                            Toast.LENGTH_SHORT).show()
                                        //aqui voy a agregar un progressDialog xd
                                    } else {
                                        Toast.makeText(this@MainActivityIrseAntes,
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

    private fun showTimePickerDialog() {

        val timePicker = TimePickerFragment { time -> onTimeSelected(time) }
        timePicker.show(supportFragmentManager, "time")
    }

    private fun onTimeSelected(time: String) {
        binding.horaInicialSalir.setText(" $time ")

    }

    private fun showTimePickerDialog2() {

        val timePicker = TimePickerFragment { time -> onTimeSelected2(time) }
        timePicker.show(supportFragmentManager, "time")
    }


    private fun onTimeSelected2(time: String) {
        binding.horaFinalSalir.setText(" $time ")

    }


    private fun showDatePickerDialog() {

        val datePicker = DatePiker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")


    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.seleccionarfechaSalir.setText("  $day/$month/$year")
    }


}