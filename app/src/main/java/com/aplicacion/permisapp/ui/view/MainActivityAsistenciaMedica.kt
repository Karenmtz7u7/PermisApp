package com.aplicacion.permisapp.ui.view


import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplicacion.permisapp.domain.Models.Client
import com.aplicacion.permisapp.domain.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.domain.repository.AuthRepository
import com.aplicacion.permisapp.domain.repository.ClientRepository
import com.aplicacion.permisapp.domain.repository.ClientRHRepository
import com.aplicacion.permisapp.domain.repository.HistoriesRepository
import com.aplicacion.permisapp.domain.repository.IncidenciasRepository
import com.aplicacion.permisapp.databinding.ActivityMainAsistenciaMedicaBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivityAsistenciaMedica : AppCompatActivity() {

    private lateinit var binding: ActivityMainAsistenciaMedicaBinding
    private val authRepository = AuthRepository()
    val clientRepository = ClientRepository()
    val clientRHRepository = ClientRHRepository()
    private val historiesRepository = HistoriesRepository()
    private val incidenciasRepository = IncidenciasRepository()
    //Código para verificar  si el telefono cuenta con datos biometricos
    private  var canAuthenticate = false
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val PERMISO_STORAGE: Int = 99
    var imagebtn : Button?=null
    private var namePDF : File? = null
    //Sirven para subir un PDF a storage
    private val fileResult = 1
    val PDF : Int = 0
    lateinit var uri : Uri
    private val database = Firebase.database
    private val myRef = database.getReference("PDF")



    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainAsistenciaMedicaBinding.inflate(layoutInflater)
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

        binding.fechamedico.setOnClickListener { showDatePickerDialog() }
        binding.horaInicialmedico.setOnClickListener { hora() }
        binding.HoraFinalmedico.setOnClickListener { hora2() }
        binding.buttonEnviarSolicitud.setOnClickListener {
            huellaDigital()
            authenticate {
                checkSolicitud()
            }
        }
        binding.button3.setOnClickListener { permission() }
        messageTramitsCheck()
    }
    //Insertar datos en Excel
    private fun excel() {
        clientRepository.getSP(authRepository.getid()).addOnSuccessListener { document ->
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
            if (binding.editTextTextPersonName2.text.toString().isEmpty() or
                binding.horaInicialmedico.text.toString().isEmpty() or
                binding.HoraFinalmedico.text.toString().isEmpty() or
                binding.fechamedico.text.toString().isEmpty() or
                binding.autoCompleteTextView.text.toString().isEmpty() or
                client?.nombre.toString().isEmpty() or
                client?.apellido.toString().isEmpty()
            ) {
                Toast.makeText(this@MainActivityAsistenciaMedica, "...", Toast.LENGTH_SHORT).show()
            } else {
                val url =
                    "https://script.google.com/macros/s/AKfycbyMfnB_Dm3noTK8z8aAS0avnbhp6QZXjMCCL3eawYbxiMbUZ3HC0HCUpPmmK7haEAvV/exec"
                val stringRequest = object : StringRequest(Request.Method.POST, url,
                    Response.Listener {
                        Toast.makeText(this@MainActivityAsistenciaMedica,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()
                    }, Response.ErrorListener {
                        Toast.makeText(this@MainActivityAsistenciaMedica,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getParams(): MutableMap<String, String>? {
                        val params = HashMap<String, String>()
                        params["nombre"]=client?.nombre.toString()
                        params["apellido"]=client?.apellido.toString()
                        params["noEmpleado"]=client?.noEmpleado.toString()
                        params["area"]=client?.area.toString()
                        params["incidencia"] = binding.editTextTextPersonName2.text.toString()
                        params["hora_inicial"] = binding.horaInicialmedico.text.toString()
                        params["hora_final"] = binding.HoraFinalmedico.text.toString()
                        params["fecha"] = binding.fechamedico.text.toString()
                        params["jefeInmediato"] = binding.autoCompleteTextView.text.toString()
                        return params
                    }
                }
                val queue: RequestQueue = Volley.newRequestQueue(this@MainActivityAsistenciaMedica)
                queue.add(stringRequest)
            }
        }
    }
    //solicitar permisos
    fun permission(){
        if (checkpermiss()){
            file()
        }else{
            requestPermissions()
        }
    }

    private fun checkpermiss(): Boolean {
        val write = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val reading = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        return write == PackageManager.PERMISSION_GRANTED && reading == PackageManager.PERMISSION_GRANTED

    }
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),200)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==200){
            if (grantResults.size > 0){
                val writeStorage =  grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage =  grantResults[1] == PackageManager.PERMISSION_GRANTED

                if(writeStorage && readStorage){
                    file()
                }
                else{
                    Toast.makeText(this, "Los permisos fueron rechazados previamente", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    //Función para abrir el gestor de archivos y buscar un pdf
    private fun file(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        intent.type = "application/pdf"
        startActivityForResult(intent,fileResult)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileResult && resultCode == RESULT_OK && data != null && data.data != null){

                val fileUri = data.data
                val namePDF = fileUri?.lastPathSegment
                binding.pdfEvidenciatxt.text = namePDF.toString()


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

    //funcion para obtener el URI y subir el pdf a storage



    private fun fileUpload(mUri : Uri){
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("PDF")
        val path = mUri.lastPathSegment.toString()
        val fileName: StorageReference = folder.child(path.substring(path.lastIndexOf('/')+1))

        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { url ->
                val urlPDF = url.toString()
                binding.pdfEvidenciaUrl.text = urlPDF


                //Hacer visible la barra de brogreso
                binding.progressBarPDF.visibility = View.VISIBLE


                val hashMap = HashMap<String, String>()
                hashMap["link"] = java.lang.String.valueOf(url)


                myRef.child(myRef.push().key.toString()).setValue(hashMap)


                Log.i("message","Receta médica cargada con exito")

                Toast.makeText(this, "Receta médica cargada con exito", Toast.LENGTH_SHORT).show()


            }
        }

            //Mensaje mostrado en la barra de progreso al cargar el pdf a storage
            .addOnProgressListener {
                val progress = (100 * it.bytesTransferred / it.totalByteCount).toInt()
                it.run {
                    binding.progressBarPDF.progress = progress
                    binding.tvProgress.text = String.format("Receta cargada al %s%%", progress)
                }
            }
            .addOnFailureListener {
                Log.i("message","Error al cargar la receta")
            }
    }

    //Esta funcion crea el registro de la solictud
    private fun sendSolicitud() {
        //estas variables son las que pide la pantalla
        val tipoIncidencia = "Consulta Medica"
        val horaInicial = binding.horaInicialmedico.text.toString()
        val horaFinal = binding.HoraFinalmedico.text.toString()
        val fechaSolictada = binding.fechamedico.text.toString()
        val Jefeinmediato = binding.autoCompleteTextView.text.toString()
        val evidencia = binding.pdfEvidenciaUrl.text.toString()
        val status = "Solicitud enviada"
        //estas variables son para agregar la fecha actual para mostrar cuando se registro/hizo la solictud
        val date = Date()
        val fechaC = SimpleDateFormat("dd'/'MM'/'yyyy")
        val sFecha: String = fechaC.format(date)
        val format = SimpleDateFormat("hh:mm")
        val sTime : String = format.format(Date())
        //esta funcion manda a llamar los demas datos de la incidencia para guardar en firebase
        //nombre, apellido, noEmpleado, area.
        clientRepository.getSP(authRepository.getid()).addOnSuccessListener { document ->
            val client = document.toObject(Client::class.java)
            val nombre = "${client?.nombre}"
            val apellido = "${client?.apellido}"
            val noEmpleado = "${client?.noEmpleado}"
            val area = "${client?.area}"
            //este if se encargara de registrar al usuario en firebaseAuthentication una vez que
            //los campos hayan sido validados
            if (validacion(horaInicial, horaFinal, fechaSolictada, Jefeinmediato, )) {
                //este if registra la informacion del usuario
                val incidencias = Incidencias(
                    idSP = authRepository.getid(), //esta variable obtiene el correo directamente desde Authentication
                    email = authRepository.auth.currentUser?.email.toString(),
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
                    evidencia = evidencia,
                    noEmpleado = noEmpleado,
                    hora = sTime,
                )
                            incidenciasRepository.create(incidencias).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    showMessage()
                                    excel()
                                    //aqui voy a agregar un progressDialog xd
                                } else {
                                    Toast.makeText(this@MainActivityAsistenciaMedica,
                                        "Hubo un error al procesar la solicitud ${it.exception.toString()}",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                }else{
                    nohayfirma()
                }
        }
    }
    //este formulario valida que no se vayan a meter datos vacios xd
    private fun validacion(horaInicial: String, horaFinal: String, fechaSolicitada: String, Jefeinmediato: String): Boolean {
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

    private fun nohayfirma(){
        val view = View.inflate(this, R.layout.dialog_view, null)
        view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_cancel)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡Error al enviar la incidencia!"
        view.findViewById<TextView>(R.id.bodyDialog).isVisible = false
        view.findViewById<TextView>(R.id.extraDialog).text = "No puedes enviar la incidencia \n necesitas subir tu justificante \n expedido por ISSEMYN \n en formato PDF"
        view.findViewById<Button>(R.id.botonAlert).setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
        view.findViewById<Button>(R.id.botonAlert).text = "¡Entendido!"
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun home(){
        val intent = Intent(this, MainActivity_home::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun hora(){
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker: TimePicker?, hour: Int, minute: Int ->
            cal.set(Calendar.HOUR_OF_DAY,hour)
            cal.set(Calendar.MINUTE,minute)
            binding.horaInicialmedico.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(this,timeSetListener,  cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show()
    }

    private fun hora2(){
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker: TimePicker?, hour: Int, minute: Int ->
            cal.set(Calendar.HOUR_OF_DAY,hour)
            cal.set(Calendar.MINUTE,minute)
            binding.HoraFinalmedico.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(this,timeSetListener,  cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show()
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePiker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.fechamedico.setText("  $day/$month/$year")
    }

    private fun messageTramitsCheck(){
        historiesRepository.getIncidencias().addOnSuccessListener { documents ->
            val currentDate = Calendar.getInstance().time
            val currentMonth = currentDate.month
            var recordsThisMonth = 0
            for (document in documents) {
                val dateStr = document.get("fecha")
                val dateFormat = SimpleDateFormat("dd'/'MM'/'yyyy")
                val date = dateStr?.let { dateFormat.parse(it.toString()) }
                val calendar = Calendar.getInstance()
                calendar.time = date
                val month = calendar.get(Calendar.MONTH)
                if (month == currentMonth) {
                    recordsThisMonth++
                }
            }
            val view = View.inflate(this, R.layout.dialog_view, null)
            view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_inciden)
            view.findViewById<TextView>(R.id.titleDialog).text = "¡Estimado servidor publico!"
            view.findViewById<TextView>(R.id.bodyDialog).text = "Es importante para nosotros recordarte \n que solo puede solicitar 3 Incidencias al mes."
            view.findViewById<TextView>(R.id.extraDialog).text = "Hasta el momento te han autorizado: \n${recordsThisMonth} \nincidencias este mes."
            view.findViewById<Button>(R.id.botonAlert).text = "Entendido"
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

    private fun checkSolicitud(){
        historiesRepository.getIncidencias().addOnSuccessListener { documents ->
            val currentDate = Calendar.getInstance().time
            val currentMonth = currentDate.month
            var recordsThisMonth = 0
            for (document in documents) {
                val dateStr = document.get("fecha")
                val dateFormat = SimpleDateFormat("dd'/'MM'/'yyyy")
                val date = dateStr?.let { dateFormat.parse(it.toString()) }
                val calendar = Calendar.getInstance()
                calendar.time = date
                val month = calendar.get(Calendar.MONTH)
                if (month == currentMonth) {
                    recordsThisMonth++
                }
            }

            if(recordsThisMonth >= 3){
                val view = View.inflate(this, R.layout.dialog_view, null)
                view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_cancel)
                view.findViewById<TextView>(R.id.titleDialog).text = "¡Estimado servidor publico!"
                view.findViewById<TextView>(R.id.bodyDialog).isVisible = false
                view.findViewById<TextView>(R.id.extraDialog).text = "Lo sentimos mucho \n haz solicitado todas las \n incidencias permitidas este semestre."
                view.findViewById<Button>(R.id.botonAlert).setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                view.findViewById<Button>(R.id.botonAlert).text = "Entendido"
                val builder = AlertDialog.Builder(this)
                builder.setView(view)
                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
                    dialog.dismiss()
                }
            }else{
                sendSolicitud()
            }
        }
    }

    private fun huellaDigital(){
        if (BiometricManager.from(this).canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL) ==
            BiometricManager.BIOMETRIC_SUCCESS){
            canAuthenticate = true
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Incidencia: Consulta Médica")
                .setSubtitle("¿Autorizas enviar la solicitud?")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()
        }
    }

    private fun authenticate(auth: (Auth: Boolean) -> Unit){
        if (canAuthenticate){
            BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                object : BiometricPrompt.AuthenticationCallback(){
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        auth(true)
                    }
                }).authenticate(promptInfo)
        }else{
            auth(true)
        }
    }
}