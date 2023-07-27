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
import com.aplicacion.permisapp.data.Models.Client
import com.aplicacion.permisapp.data.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainOtroBinding
import com.aplicacion.permisapp.data.providers.AuthProvider
import com.aplicacion.permisapp.data.providers.ClientProvider
import com.aplicacion.permisapp.data.providers.HistoriesProvider
import com.aplicacion.permisapp.data.providers.IncidenciasProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class MainActivity_Otro : AppCompatActivity() {

    private lateinit var binding: ActivityMainOtroBinding
    private val authProvider = AuthProvider()
    val clientProvider = ClientProvider()
    private val incidenciasProvider = IncidenciasProvider()
    private val historiesProvider = HistoriesProvider()



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
        binding.horaInicialotro.setOnClickListener { hora() }
        binding.horaFinalotro.setOnClickListener { hora2() }
        binding.buttonEnviar.setOnClickListener {
            huellaDigital()
            authenticate {
                checkSolicitud()
            }

        }
        binding.button3otro.setOnClickListener { permission() }

        messageTramitsCheck()
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

            if (binding.otropermiso.text.toString().isEmpty() or
                binding.fechaotro.text.toString().isEmpty() or
                binding.autoCompleteTextView.text.toString().isEmpty() or
                client?.nombre.toString().isEmpty() or
                client?.apellido.toString().isEmpty()
            ) {

                Toast.makeText(this@MainActivity_Otro, "...", Toast.LENGTH_SHORT).show()

            } else {
                val url =
                    "https://script.google.com/macros/s/AKfycbyMfnB_Dm3noTK8z8aAS0avnbhp6QZXjMCCL3eawYbxiMbUZ3HC0HCUpPmmK7haEAvV/exec"
                val stringRequest = object : StringRequest(Request.Method.POST, url,
                    Response.Listener {
                        Toast.makeText(this@MainActivity_Otro,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()

                    }, Response.ErrorListener {
                        Toast.makeText(this@MainActivity_Otro,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()

                    }) {
                    override fun getParams(): MutableMap<String, String>? {
                        val params = HashMap<String, String>()
                        params["nombre"]=client?.nombre.toString()
                        params["apellido"]=client?.apellido.toString()
                        params["noEmpleado"]=client?.noEmpleado.toString()
                        params["area"]=client?.area.toString()
                        params["incidencia"] = binding.otropermiso.text.toString()
                        params["hora_inicial"] = binding.horaInicialotro.text.toString()
                        params["hora_final"] = binding.horaFinalotro.text.toString()
                        params["fecha"] = binding.fechaotro.text.toString()
                        params["jefeInmediato"] = binding.autoCompleteTextView.text.toString()
                        return params
                    }
                }
                val queue: RequestQueue = Volley.newRequestQueue(this@MainActivity_Otro)
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
        val tipoIncidencia = binding.otropermiso.text.toString()
        val horaInicial = binding.horaInicialotro.text.toString()
        val horaFinal = binding.horaFinalotro.text.toString()
        val fechaSolictada = binding.fechaotro.text.toString()
        val Jefeinmediato = binding.autoCompleteTextView.text.toString()
        val razon = binding.otroRazontxt.text.toString()
        val status = "Solicitud enviada"
        val evidencia = binding.pdfEvidenciaUrl.text.toString()
        //estas variables son para agregar la fecha actual para mostrar cuando se registro/hizo la solictud
        val date = Date()
        val fechaC = SimpleDateFormat("dd'/'MM'/'yyyy")
        val sFecha: String = fechaC.format(date)
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
                    evidencia = evidencia,
                    apellido = apellido,
                    razon = razon,
                    area = area,
                    noEmpleado = noEmpleado,
                )
                incidenciasProvider.create(incidencias).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showMessage()
                        excel()
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
            binding.horaInicialotro.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(this,timeSetListener,  cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show()
    }

    private fun hora2(){

        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker: TimePicker?, hour: Int, minute: Int ->
            cal.set(Calendar.HOUR_OF_DAY,hour)
            cal.set(Calendar.MINUTE,minute)
            binding.horaFinalotro.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        TimePickerDialog(this,timeSetListener,  cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show()
    }


    private fun showDatePickerDialog() {
        val datePicker = DatePiker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.fechaotro.setText("  $day/$month/$year")
    }
    private fun messageTramitsCheck(){
        historiesProvider.getIncidencias().addOnSuccessListener {  documents ->
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
            view.findViewById<TextView>(R.id.bodyDialog).text = "Es importante para nosotros recordarte \n que solo puede solicitar 4 Incidencias al mes."
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
        historiesProvider.getIncidencias().addOnSuccessListener {  documents ->
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
    //Código para verificar  si el telefono cuenta con datos biometricos
    private  var canAuthenticate = false
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private fun huellaDigital(){
        if (BiometricManager.from(this).canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL) ==
            BiometricManager.BIOMETRIC_SUCCESS){

            canAuthenticate = true

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Incidencia: Otro Tipo")
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
                })
                .authenticate(promptInfo)

        }else{
            auth(true)
        }
    }
}