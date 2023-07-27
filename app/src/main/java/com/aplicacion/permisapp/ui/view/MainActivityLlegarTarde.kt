package com.aplicacion.permisapp.ui.view

import android.app.TimePickerDialog
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
import com.aplicacion.permisapp.domain.Models.Client
import com.aplicacion.permisapp.domain.Models.Histories
import com.aplicacion.permisapp.domain.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainLlegarTardeBinding
import com.aplicacion.permisapp.domain.repository.AuthRepository
import com.aplicacion.permisapp.domain.repository.ClientRepository
import com.aplicacion.permisapp.domain.repository.HistoriesRepository
import com.aplicacion.permisapp.domain.repository.IncidenciasRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivityLlegarTarde : AppCompatActivity() {
    private lateinit var binding: ActivityMainLlegarTardeBinding
    private val authRepository = AuthRepository()
    val clientRepository = ClientRepository()
    private val incidenciasRepository = IncidenciasRepository()
    private val historiesRepository = HistoriesRepository()
    var histories = ArrayList<Histories>()
    private var imageFile: File? = null
    private val PERMISO_STORAGE: Int = 99
    var contador = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainLlegarTardeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        messageTramitsCheck()

        //lista desplegable de jefes
        val jefes = resources.getStringArray(R.array.jefes)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item,
            jefes)

        with(binding.autoCompleteTextView) {
            setAdapter(adapter)
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
            huellaDigital()
            authenticate {
                checkSolicitud()
            }
        }
    }

    //Funcion para insertar datos en excel
    private fun excel() {
        //esta funcion manda a llamar los datos del usuario desde firebase.
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
                        params["nombre"] = client?.nombre.toString()
                        params["apellido"] = client?.apellido.toString()
                        params["noEmpleado"] = client?.noEmpleado.toString()
                        params["area"] = client?.area.toString()
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
        val sTime: String = format.format(Date())//estas variables obtienen la hora actual


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
            if (validacion(horaInicial, horaFinal, fechaSolictada, Jefeinmediato)) {
                //este if registra la informacion del usuario

                val incidencias = Incidencias(
                    idSP = authRepository.getid(),
                    //esta variable obtiene el correo directamente desde Authentication
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
                    noEmpleado = noEmpleado,
                    hora = sTime,
                )
                incidenciasRepository.create(incidencias).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showMessage()
                        excel()
                    } else {
                        Toast.makeText(this@MainActivityLlegarTarde,
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
        startActivity(intent)
    }

    private fun hora() {

        val cal = Calendar.getInstance()
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { timePicker: TimePicker?, hour: Int, minute: Int ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.horaInicial.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
        TimePickerDialog(this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false).show()
    }

    private fun hora2() {

        val cal = Calendar.getInstance()
        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { timePicker: TimePicker?, hour: Int, minute: Int ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.HoraFinal.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
        TimePickerDialog(this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false).show()
    }

    private fun showDatePickerDialog() {

        val datePicker = DatePiker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")


    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.seleccionarfecha.setText("  $day/$month/$year")
    }

    private fun messageTramitsCheck() {
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
            view.findViewById<TextView>(R.id.bodyDialog).text =
                "Es importante para nosotros recordarte \n que solo puede solicitar 3 Incidencias al mes."
            view.findViewById<TextView>(R.id.extraDialog).text =
                "Hasta el momento te han autorizado: \n${recordsThisMonth} \nincidencias este mes."
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

    private fun checkSolicitud() {
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

            if (recordsThisMonth >= 3) {
                val view = View.inflate(this, R.layout.dialog_view, null)
                view.findViewById<ImageView>(R.id.imageDialog)
                    .setImageResource(R.drawable.ic_cancel)
                view.findViewById<TextView>(R.id.titleDialog).text = "¡Estimado servidor publico!"
                view.findViewById<TextView>(R.id.bodyDialog).isVisible = false
                view.findViewById<TextView>(R.id.extraDialog).text =
                    "Lo sentimos mucho \n haz solicitado todas las \n incidencias permitidas este semestre."
                view.findViewById<Button>(R.id.botonAlert)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
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
    private var canAuthenticate = false
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private fun huellaDigital() {
        if (BiometricManager.from(this).canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL) ==
            BiometricManager.BIOMETRIC_SUCCESS
        ) {
            canAuthenticate = true
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Incidencia: Llegar Tarde")
                .setSubtitle("¿Autorizas enviar la solicitud?")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()
        }
    }

    private fun authenticate(auth: (Auth: Boolean) -> Unit) {
        if (canAuthenticate) {
            BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        auth(true)
                    }
                }).authenticate(promptInfo)
        } else {
            auth(true)
        }
    }

}






