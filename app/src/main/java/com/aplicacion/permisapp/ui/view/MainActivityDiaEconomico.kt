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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.aplicacion.permisapp.data.Models.Client
import com.aplicacion.permisapp.data.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainDiaEconomicoBinding
import com.aplicacion.permisapp.data.providers.AuthProvider
import com.aplicacion.permisapp.data.providers.ClientProvider
import com.aplicacion.permisapp.data.providers.HistoriesProvider
import com.aplicacion.permisapp.data.providers.IncidenciasProvider
import java.text.SimpleDateFormat
import java.util.*

class MainActivityDiaEconomico : AppCompatActivity() {

    private lateinit var binding: ActivityMainDiaEconomicoBinding
    private val authProvider = AuthProvider()
    val clientProvider = ClientProvider()
    private val incidenciasProvider = IncidenciasProvider()
    private val historiesProvider = HistoriesProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainDiaEconomicoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //lista desplegable de jefes
        val jefes = resources.getStringArray(R.array.jefes)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item,
            jefes)
        with(binding.autoCompleteTextView) { setAdapter(adapter) }
        binding.seleccionarfechaeconomic.setOnClickListener { showDatePickerDialog() }
        binding.button2DE.setOnClickListener {
            huellaDigital()
            authenticate {
               checkSolicitud()
            }
        }
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
            if (binding.editTextTextPersonName.text.toString().isEmpty() or
                binding.seleccionarfechaeconomic.text.toString().isEmpty() or
                binding.autoCompleteTextView.text.toString().isEmpty() or
                client?.nombre.toString().isEmpty() or
                client?.apellido.toString().isEmpty()
            ) {
                Toast.makeText(this@MainActivityDiaEconomico, "...", Toast.LENGTH_SHORT).show()
            } else {
                val url =
                    "https://script.google.com/macros/s/AKfycbyMfnB_Dm3noTK8z8aAS0avnbhp6QZXjMCCL3eawYbxiMbUZ3HC0HCUpPmmK7haEAvV/exec"
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
                        params["nombre"] = client?.nombre.toString()
                        params["apellido"] = client?.apellido.toString()
                        params["noEmpleado"] = client?.noEmpleado.toString()
                        params["area"] = client?.area.toString()
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
        val sTime: String = format.format(Date())

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
                incidenciasProvider.create(incidencias).addOnCompleteListener {
                    if (it.isSuccessful) {
                        excel()
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
    private fun showMessage() {
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

    private fun home() {
        val intent = Intent(this, MainActivity_home::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePiker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.seleccionarfechaeconomic.setText("  $day/$month/$year")
    }

    //Checara cuantos registros pertenecen al mes actual y cuales han sido aceptados
    private fun messageTramitsCheck() {
        val fechaInicial = Calendar.getInstance()
        fechaInicial.set(Calendar.MONTH, 0) // Enero (0)
        fechaInicial.set(Calendar.DAY_OF_MONTH, 1) // Día 1
        val fechaFinal = Calendar.getInstance()
        fechaFinal.set(Calendar.MONTH, 5) // Junio (5)
        fechaFinal.set(Calendar.DAY_OF_MONTH, 30) // Día 30
        var noTramits = 0 //periodo 20xx-2
        var tramitstwo = 0 //periodo 20xx-1
        historiesProvider.getDaysEconomic().addOnSuccessListener { documents ->
            for (document in documents) {
                val dateStr = document.get("fecha")
                val dateFormat = SimpleDateFormat("dd'/'MM'/'yyyy")
                val date = dateStr?.let { dateFormat.parse(it.toString()) }
                val calendar = Calendar.getInstance()
                calendar.time = date

                if (calendar in fechaInicial..fechaFinal) {
                    noTramits++
                }

            }
            val view = View.inflate(this, R.layout.dialog_view, null)
            view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_inciden)
            view.findViewById<TextView>(R.id.titleDialog).text = "¡Estimado servidor publico!"
            view.findViewById<TextView>(R.id.bodyDialog).text =
                "Es importante para nosotros recordarte \n que este periodo 2023-1 solo puede solicitar 4 \n Dias economicos al semestre."
            view.findViewById<TextView>(R.id.extraDialog).text =
                "Hasta el momento te han autorizado: \n${noTramits} \n Dias economicos."
            view.findViewById<Button>(R.id.botonAlert).text = "Entendido"
            val builder = AlertDialog.Builder(this)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
                dialog.dismiss()
            }

            if (noTramits == 1) {
                binding.ellipse1.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
            }
            if (noTramits == 2) {
                binding.ellipse1.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
                binding.rectangle1.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rectangle_6))
                binding.ellipse2.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
            }
            if (noTramits == 3) {
                binding.ellipse1.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
                binding.rectangle1.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rectangle_6))
                binding.ellipse2.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
                binding.rectangle2.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rectangle_6))
                binding.ellipse3.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
            }
            if (noTramits == 4) {
                binding.ellipse1.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
                binding.rectangle1.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rectangle_6))
                binding.ellipse2.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
                binding.rectangle2.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rectangle_6))
                binding.ellipse3.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
                binding.rectangle3.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rectangle_6))
                binding.ellipse4.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.ellipse_6))
            }
        }
    }
    private fun checkSolicitud() {
        val fechaInicial = Calendar.getInstance()
        fechaInicial.set(Calendar.MONTH, 0) // Enero (0)
        fechaInicial.set(Calendar.DAY_OF_MONTH, 1) // Día 1
        val fechaFinal = Calendar.getInstance()
        fechaFinal.set(Calendar.MONTH, 5) // Junio (5)
        fechaFinal.set(Calendar.DAY_OF_MONTH, 30) // Día 30
        var noTramits = 0
        historiesProvider.getDaysEconomic().addOnSuccessListener { documents ->
            for (document in documents) {
                val dateStr = document.get("fecha")
                val dateFormat = SimpleDateFormat("dd'/'MM'/'yyyy")
                val date = dateStr?.let { dateFormat.parse(it.toString()) }
                val calendar = Calendar.getInstance()
                calendar.time = date

                if (calendar in fechaInicial..fechaFinal) {
                    noTramits++
                }
            }
            if (noTramits >= 4) {
                val view = View.inflate(this, R.layout.dialog_view, null)
                view.findViewById<ImageView>(R.id.imageDialog)
                    .setImageResource(R.drawable.ic_cancel)
                view.findViewById<TextView>(R.id.titleDialog).text = "¡Estimado servidor publico!"
                view.findViewById<TextView>(R.id.extraDialog).text = "Lo sentimos muchos \n haz solicitado todos los \n Dias economicos permitidos al semestre."
                view.findViewById<Button>(R.id.botonAlert).text = "Entendido"
                val builder = AlertDialog.Builder(this)
                builder.setView(view)
                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
                    dialog.dismiss()
                }
            } else {
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
                .setTitle("Incidencia: Día Económico")
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