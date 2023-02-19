package com.aplicacion.permisapp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplicacion.permisapp.Models.Client
import com.aplicacion.permisapp.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainDiaEconomicoBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider
import com.aplicacion.permisapp.providers.IncidenciasProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivityDiaEconomico : AppCompatActivity() {

    private lateinit var binding: ActivityMainDiaEconomicoBinding
    private val authProvider = AuthProvider()
    val clientProvider = ClientProvider()
    private val incidenciasProvider = IncidenciasProvider()
    private var imageFile: File? = null
    var imagebtn : Button?=null
    private val PERMISO_STORAGE: Int = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainDiaEconomicoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        imagebtn = binding.button

        //lista desplegable de jefes
        val jefes =resources.getStringArray(R.array.jefes)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item,
            jefes)

        with(binding.autoCompleteTextView){ setAdapter(adapter) }

        binding.button.setOnClickListener {solicitarpermiso()   }

        binding.seleccionarfechaeconomic.setOnClickListener { showDatePickerDialog() }

        binding.button2DE.setOnClickListener { sendSolicitud()
            excel()
        }

    }


    //Excel
    private fun excel() {

        clientProvider.getSP(authProvider.getid()).addOnSuccessListener { document ->
            val client = document.toObject(Client::class.java)
            val nombre = "${client?.nombre}"
            val apellido = "${client?.apellido}"
            val noEmpleado = "${client?.noEmpleado}"
            val area = "${client?.area}"

            val incidencias = Incidencias(

                nombre = nombre,
                apellido = apellido,
                noEmpleado = noEmpleado,
                area = area)



            if (binding.editTextTextPersonName.text.toString().isEmpty() or
                binding.seleccionarfechaeconomic.text.toString().isEmpty() or
                binding.autoCompleteTextView.text.toString().isEmpty() or
                client?.nombre.toString().isEmpty() or
                client?.apellido.toString().isEmpty()
            ) {

                Toast.makeText(this@MainActivityDiaEconomico, "...", Toast.LENGTH_SHORT).show()

            } else {
                val url =
                    "https://script.google.com/macros/s/AKfycbwPT3J7U9rA1ucfER--enNAjRsvNXZXzPB0J1Ilu9LPliqOtLWCiAKNzy8N2BF6hidyTQ/exec"
                val stringRequest = object : StringRequest(Request.Method.POST, url,
                    Response.Listener {
                        Toast.makeText(this@MainActivityDiaEconomico,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()

                    }, Response.ErrorListener {
                        Toast.makeText(this@MainActivityDiaEconomico,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()

                    }) {
                    override fun getParams(): MutableMap<String, String>? {
                        val params = HashMap<String, String>()
                        params["nombre"]=client?.nombre.toString()
                        params["apellido"]=client?.apellido.toString()
                        params["noEmpleado"]=client?.noEmpleado.toString()
                        params["area"]=client?.area.toString()
                        params["incidencia"] = binding.editTextTextPersonName.text.toString()
                        params["fecha"] = binding.seleccionarfechaeconomic.text.toString()
                        params["jefeInmediato"] = binding.autoCompleteTextView.text.toString()

                        return params


                    }
                }

                val queue: RequestQueue = Volley.newRequestQueue(this@MainActivityDiaEconomico)
                queue.add(stringRequest)

            }
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
        val tipoIncidencia = binding.editTextTextPersonName.text.toString()
        //val horaInicial = binding.horaInicial.text.toString()
        //val horaFinal = binding.HoraFinal.text.toString()
        val fechaSolictada = binding.seleccionarfechaeconomic.text.toString()
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
            if (validacion(fechaSolictada, Jefeinmediato)) {
                //este if registra la informacion del usuario

                val incidencias = Incidencias(
                    idSP = authProvider.getid(),
                    //esta variable obtiene el correo directamente desde Authentication
                    email = authProvider.auth.currentUser?.email.toString(),
                    nombre = nombre,
                    tipoIncidencia = tipoIncidencia,
                    hora = sTime,
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
                                Log.d("STORAGE", "$imageUrl")

                                incidenciasProvider.create(incidencias).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        showMessage()
                                    } else {
                                        Toast.makeText(this@MainActivityDiaEconomico,
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
        fechaSolicitada: String,
        Jefeinmediato: String
    ): Boolean {

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

    private fun showDatePickerDialog() {

        val datePicker = DatePiker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")


    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.seleccionarfechaeconomic.setText("  $day/$month/$year")
    }
}