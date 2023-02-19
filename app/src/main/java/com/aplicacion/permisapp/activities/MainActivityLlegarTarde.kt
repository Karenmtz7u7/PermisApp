package com.aplicacion.permisapp.activities

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TimePicker
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

import com.aplicacion.permisapp.databinding.ActivityMainLlegarTardeBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider
import com.aplicacion.permisapp.providers.IncidenciasProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date as Date

class MainActivityLlegarTarde : AppCompatActivity() {
    private lateinit var binding: ActivityMainLlegarTardeBinding
    private val authProvider = AuthProvider()
    val clientProvider = ClientProvider()
    private val incidenciasProvider = IncidenciasProvider()
    var imagebtn : Button?=null
    private var imageFile: File? = null
    private val PERMISO_STORAGE: Int = 99
    var contador = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainLlegarTardeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        imagebtn = binding.button

        //lista desplegable de jefes
        val jefes = resources.getStringArray(R.array.jefes)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item,
            jefes)

        with(binding.autoCompleteTextView) {
            setAdapter(adapter)
        }

        binding.button.setOnClickListener {
            solicitarpermiso()
        }

        binding.seleccionarfecha.setOnClickListener {
            showDatePickerDialog()
        }

        binding.horaInicial.setOnClickListener {
            hora()
        }
        binding.HoraFinal.setOnClickListener {
            hora2()
        }
        binding.sendbtn.setOnClickListener {
            sendSolicitud()
            excel()



        }

    }




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

            if (binding.tipoIncidenciatxt.text.toString().isEmpty() or
                binding.horaInicial.text.toString().isEmpty() or
                binding.HoraFinal.text.toString().isEmpty() or
                binding.seleccionarfecha.text.toString().isEmpty() or
                binding.autoCompleteTextView.text.toString().isEmpty() or
                client?.nombre.toString().isEmpty() or
                client?.apellido.toString().isEmpty()
            ) {

                Toast.makeText(this@MainActivityLlegarTarde, "...", Toast.LENGTH_SHORT).show()

            } else {
                val url =
                    "https://script.google.com/macros/s/AKfycbyMfnB_Dm3noTK8z8aAS0avnbhp6QZXjMCCL3eawYbxiMbUZ3HC0HCUpPmmK7haEAvV/exec"
                val stringRequest = object : StringRequest(Request.Method.POST, url,
                    Response.Listener {
                        Toast.makeText(this@MainActivityLlegarTarde,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()

                    }, Response.ErrorListener {
                        Toast.makeText(this@MainActivityLlegarTarde,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()

                    }) {
                    override fun getParams(): MutableMap<String, String>? {
                        val params = HashMap<String, String>()
                        params["nombre"]=client?.nombre.toString()
                        params["apellido"]=client?.apellido.toString()
                        params["noEmpleado"]=client?.noEmpleado.toString()
                        params["area"]=client?.area.toString()
                        params["incidencia"] = binding.tipoIncidenciatxt.text.toString()
                        params["hora_inicial"] = binding.horaInicial.text.toString()
                        params["hora_final"] = binding.HoraFinal.text.toString()
                        params["fecha"] = binding.seleccionarfecha.text.toString()
                        params["jefeInmediato"] = binding.autoCompleteTextView.text.toString()

                        return params


                    }
                }

                val queue: RequestQueue = Volley.newRequestQueue(this@MainActivityLlegarTarde)
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
    private val startImageForResult = registerForActivityResult(ActivityResultContracts.
    StartActivityForResult()) { result: ActivityResult ->
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
            Toast.makeText(this@MainActivityLlegarTarde,
                ImagePicker.getError(data),
                Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@MainActivityLlegarTarde, "Tarea cancelada", Toast.LENGTH_LONG)
                .show()
        }
    }

    //esta funcion abre la galeria o la opccion para tomar una foto
    private fun selectImage() {
        ImagePicker.with(this@MainActivityLlegarTarde)
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
        val tipoIncidencia = binding.tipoIncidenciatxt.text.toString()
        val horaInicial = binding.horaInicial.text.toString()
        val horaFinal = binding.HoraFinal.text.toString()
        val fechaSolictada = binding.seleccionarfecha.text.toString()
        val Jefeinmediato = binding.autoCompleteTextView.text.toString()
        val status = "Solicitud enviada"
        //estas variables son para agregar la fecha actual para mostrar cuando se registro/hizo la solictud
        val date = Date()
        val fechaC = SimpleDateFormat("dd'/'MM'/'yyyy")
        val sFecha: String = fechaC.format(date)
        //estas variables obtienen la hora actual
        val format = SimpleDateFormat("hh:mm")
        val sTime : String = format.format(Date())//estas variables obtienen la hora actual


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
                                        showMessage()
                                    } else {
                                        Toast.makeText(this@MainActivityLlegarTarde,
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


    private fun hora(){

        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker: TimePicker?, hour: Int, minute: Int ->
            cal.set(Calendar.HOUR_OF_DAY,hour)
            cal.set(Calendar.MINUTE,minute)
            binding.horaInicial.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(this,timeSetListener,  cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show()
    }

    private fun hora2(){

        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker: TimePicker?, hour: Int, minute: Int ->
            cal.set(Calendar.HOUR_OF_DAY,hour)
            cal.set(Calendar.MINUTE,minute)
            binding.HoraFinal.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(this,timeSetListener,  cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show()
    }



    private fun showDatePickerDialog() {

        val datePicker = DatePiker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")


    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.seleccionarfecha.setText("  $day/$month/$year")
    }
}






