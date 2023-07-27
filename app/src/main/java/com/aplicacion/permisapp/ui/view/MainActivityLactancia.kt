package com.aplicacion.permisapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import com.aplicacion.permisapp.databinding.ActivityMainLactanciaBinding
import com.aplicacion.permisapp.data.providers.AuthProvider
import com.aplicacion.permisapp.data.providers.ClientProvider
import com.aplicacion.permisapp.data.providers.HistoriesProvider
import com.aplicacion.permisapp.data.providers.IncidenciasProvider
import java.text.SimpleDateFormat
import java.util.*

class MainActivityLactancia : AppCompatActivity() {
    private lateinit var binding: ActivityMainLactanciaBinding
    private val authProvider = AuthProvider()
    val clientProvider = ClientProvider()
    private val incidenciasProvider = IncidenciasProvider()
    private val historiesProvider = HistoriesProvider()


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainLactanciaBinding.inflate(layoutInflater )
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
        binding.horaInicialembarazo.setOnClickListener {
            showDatePickerDialog()
        }
        binding.horaFinalembarazo.setOnClickListener {
            showDatePickerDialog2()
        }
        binding.buttonembarazo.setOnClickListener {
            huellaDigital()
            authenticate {
                checkSolicitud()
            }
            messageTramitsCheck()
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
            if (binding.editTextTextPersonName2.text.toString().isEmpty() or
                binding.horaInicialembarazo.text.toString().isEmpty() or
                binding.horaFinalembarazo.text.toString().isEmpty() or
                binding.autoCompleteTextView.text.toString().isEmpty() or
                client?.nombre.toString().isEmpty() or
                client?.apellido.toString().isEmpty()
            ) {
                Toast.makeText(this@MainActivityLactancia, "...", Toast.LENGTH_SHORT).show()
            } else {
                val url =
                    "https://script.google.com/macros/s/AKfycbyMfnB_Dm3noTK8z8aAS0avnbhp6QZXjMCCL3eawYbxiMbUZ3HC0HCUpPmmK7haEAvV/exec"
                val stringRequest = object : StringRequest(Request.Method.POST, url,
                    Response.Listener {
                        Toast.makeText(this@MainActivityLactancia,
                            it.toString(),
                            Toast.LENGTH_SHORT).show()
                    }, Response.ErrorListener {
                        Toast.makeText(this@MainActivityLactancia,
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
                        params["fecha"] = binding.horaInicialembarazo.text.toString()
                        params["fecha_final_embarazo"] = binding.horaFinalembarazo.text.toString()
                        params["jefeInmediato"] = binding.autoCompleteTextView.text.toString()
                        return params
                    }
                }
                val queue: RequestQueue = Volley.newRequestQueue(this@MainActivityLactancia)
                queue.add(stringRequest)
            }
        }
    }

    //Esta funcion crea el registro de la solictud
    private fun sendSolicitud() {
        //estas variables son las que pide la pantalla
        val tipoIncidencia = binding.editTextTextPersonName2.text.toString()
        val fechaInicial = binding.horaInicialembarazo.text.toString()
        val fechaFinal = binding.horaFinalembarazo.text.toString()
        //val fechaSolictada = binding.seleccionarfecha.text.toString()
        val Jefeinmediato = binding.autoCompleteTextView.text.toString()
        val status = "Solicitud enviada"
        //estas variables son para agregar la fecha actual para mostrar cuando se registro/hizo la solictud
        val date = Date()
        val fechaC = SimpleDateFormat("dd'/'MM'/'yyyy")
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
            if (validacion(fechaInicial, fechaFinal,  Jefeinmediato)) {
                //este if registra la informacion del usuario

                val incidencias = Incidencias(
                    idSP = authProvider.getid(),
                    //esta variable obtiene el correo directamente desde Authentication
                    email = authProvider.auth.currentUser?.email.toString(),
                    nombre = nombre,
                    tipoIncidencia = tipoIncidencia,
                    fechaFinal = fechaFinal,
                    fechaInicial = fechaInicial,
                    hora = sTime,
                    Jefeinmediato = Jefeinmediato,
                    fechaSolicitud = sFecha,
                    status = status,
                    apellido = apellido,
                    area = area,
                    noEmpleado = noEmpleado,
                )
                incidenciasProvider.create(incidencias).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showMessage()
                        excel()
                    } else {
                        Toast.makeText(this@MainActivityLactancia,
                            "Hubo un error al procesar la solicitud ${it.exception.toString()}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //este formulario valida que no se vayan a meter datos vacios xd
    private fun validacion(
        fechaInicial: String, fechaFinal: String,
        Jefeinmediato: String
    ): Boolean {
        if (fechaInicial.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la fecha para solictar tu incidencia", Toast.LENGTH_SHORT).show()
            return false
        }
        if (fechaFinal.isEmpty()) {
            Toast.makeText(this, "Debes ingresar la fecha para solictar tu incidencia", Toast.LENGTH_SHORT).show()
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
        binding.horaInicialembarazo.setText("  $day/$month/$year")
    }

    private fun showDatePickerDialog2() {
        val datePicker = DatePiker { day, month, year -> onDateSelected2(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected2(day: Int, month: Int, year: Int){
        binding.horaFinalembarazo.setText("  $day/$month/$year")
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
                .setTitle("Incidencia: Lactancia")
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