package com.aplicacion.permisapp.activities


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aplicacion.permisapp.Models.Histories
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainFormatPdfBinding
import com.aplicacion.permisapp.providers.HistoriesProvider
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream


class MainActivityFormatPDF : AppCompatActivity() {
    private lateinit var binding : ActivityMainFormatPdfBinding
    private var histories: Histories? = null
    var historiesProvider = HistoriesProvider()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFormatPdfBinding.inflate(layoutInflater)

        binding.downloadPDFbtn.setOnClickListener {
            createPDF()
        }
        setContentView(binding.root)

        getInfo()
    }

    private fun getInfo() {
        val id = intent.getStringExtra("id")
        historiesProvider.getDoc(id.toString()).addOnSuccessListener { document->
            if (document.exists()){
                val histories = document.toObject(Histories::class.java)
                if(histories != null){
                    binding.foliochecktxt.setText(histories.folio)
                    binding.areachecktxt.setText(histories.area)
                    binding.fechaSolicitudchecktxt.setText(histories.fecha)
                    binding.incideciachecktxt.setText(histories.tipoIncidencia)
                    binding.horainicialchecktxt.setText(histories.horaInicial)
                    binding.Horafinalchecktxt.setText(histories.horaFinal)
                    binding.fechasolictadachecktxt.setText(histories.fechaSolicitada)
                    binding.fechainicialchecktxt.setText(histories.fechaInicial)
                    binding.fechafinalchecktxt.setText(histories.fechaFinal)
                    binding.razonchecktxt.setText(histories.razon)
                    binding.nombrespchecktxt.setText("${histories.nombre} ${histories.apellido}")
                    binding.noEmpleadochecktxt.setText(histories.noEmpleado)
                    Glide.with(this).load(histories.firmaSP).centerCrop().into(binding.firmaSPtxt)
                    Glide.with(this).load(histories.firmaRH).centerCrop().into(binding.firmaRHtxt)
                }
            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    fun createPDF() {

        val id = intent.getStringExtra("id")
        historiesProvider.getDoc(id.toString()).addOnSuccessListener { document ->
            val histories = document.toObject(Histories::class.java)
            val folio = histories?.folio
            val area = histories?.area
            val fecha = histories?.fecha
            val tipoIncidencia = histories?.tipoIncidencia
            val horaInicial = histories?.horaInicial
            val horaFinal = histories?.horaFinal
            val fechaSolicitada = histories?.fechaSolicitada
            val fechaInicial = histories?.fechaInicial
            val fechaFinal = histories?.fechaFinal
            val razon = histories?.razon
            val noEmpleado = histories?.noEmpleado
            val nombre = "${histories?.nombre} ${histories?.apellido}"
//                Glide.with(this).load(histories?.firmaSP).centerCrop().into(binding.firmaSPtxt)
//                Glide.with(this).load(histories?.firmaRH).centerCrop().into(binding.firmaRHtxt)
            val myDocument = PdfDocument()
            val myInfo = PdfDocument.PageInfo.Builder(250, 350,1).create()
            val myPage = myDocument.startPage(myInfo)
            val paint = Paint()
            val forLinePaint = Paint()
            val canvas = myPage.canvas

            //estas lineas imprimen el logotipo del estado
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.escudo)
            val bitmapEscala = Bitmap.createScaledBitmap(bitmap, 30, 33,false)
            canvas.drawBitmap(bitmapEscala, 15f, 10f, paint)

            //estas lineas imprimen el logotipo del TesChi
            val bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.logoteschi)
            val bitmapEscala2 = Bitmap.createScaledBitmap(bitmap2, 60, 30,false)
            canvas.drawBitmap(bitmapEscala2, 182f, 10f, paint)

            //Estas lineas Imprimen mucho texto XD
            paint.textSize = 3.5f
            paint.color = Color.BLACK
            canvas.drawText("Gobierno del Estado de México", 65f, 22f, paint)
            canvas.drawText("Secretaría de Educación", 65f, 27f, paint)
            canvas.drawText("Subsecretaría de Educación Media Superior y Superior", 65f, 32f, paint)
            canvas.drawText("Tecnológico de Estudios Superiores de Chimalhuacán", 65f, 37f, paint)

            //se declaran nuevamente para cambiar el tamaño de letra
            paint.textSize = 5.8f
            paint.color = Color.BLACK
            //aqui se concatena el dato extraido de Firebase c:
            canvas.drawText("Folio: $folio", 185f, 58f, paint)

            //se dibuja la linea para poner bonito el folio uwu
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(199f, 60f,230f, 60f, forLinePaint)

            //titulo
            paint.textSize = 7.0f
            paint.color = Color.BLACK
            canvas.drawText("AVISO DE JUSTIFICACIÓN DE INCIDENCIA DE LA PUNTUALIDAD", 20f, 85f, paint)
            canvas.drawText("  Y ASISTENCIA", 95f, 94f, paint)


            //área
            paint.textSize = 6.0f
            paint.color = Color.BLACK
            //aqui se concatena el dato extraido de Firebase c:
            canvas.drawText("Área: $area", 20f, 125f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(35f, 127f,120f, 127f, forLinePaint)

            //FECHA
            canvas.drawText("Fecha:  $fecha", 155f, 125f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(175f, 127f,230f, 127f, forLinePaint)

            //se declaran nuevamente para cambiar el tamaño de letra
            paint.textSize = 6.3f
            paint.color = Color.BLACK
            canvas.drawText("Por este conducto se notifica que el trabajador esta autorizado a:", 55f, 150f, paint)

            //tipo de Incidencia
            paint.textSize = 6.5f
            paint.color = Color.BLACK
            canvas.drawText("$tipoIncidencia", 35f, 165f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(30f, 167f,220f, 167f, forLinePaint)

            //Hora Inicial
            paint.textSize = 6.0f
            paint.color = Color.BLACK
            //aqui se concatena el dato extraido de Firebase c:
            canvas.drawText("Desde las:  $horaInicial", 20f, 180f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(47f, 182f,100f, 182f, forLinePaint)

            //hora final
            canvas.drawText("Hasta las: $horaFinal", 130f, 180f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(155f, 182f,230f, 182f, forLinePaint)

            //fecha Solicitada
            canvas.drawText("Del dia:  $fechaSolicitada", 20f, 195f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(40f, 195f,230f, 195f, forLinePaint)

            //separación cute
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 1f
            canvas.drawLine(20f, 205f,230f, 205f, forLinePaint)

            //Fecha Inicial
            canvas.drawText("Desde el día:  $fechaInicial", 20f, 220f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(60f, 222f,100f, 222f, forLinePaint)

            //Fecha final
            canvas.drawText("Hasta el día:  $fechaFinal", 110f, 220f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.3f
            canvas.drawLine(150f, 222f,230f, 222f, forLinePaint)

            //Razon
            canvas.drawText("Motivo:  $razon", 20f, 235f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.5f
            canvas.drawLine(38f, 237f,230f, 237f, forLinePaint)

            //No Empleado
            paint.textSize = 5.8f
            paint.color = Color.BLACK
            canvas.drawText("No. de Empleado:", 185f, 255f, paint)
            canvas.drawText("$noEmpleado", 188f, 265f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.5f
            canvas.drawLine(180f, 267f,230f, 267f, forLinePaint)

            //nombreSP
            canvas.drawText("Nombre del Servidor Publico:", 28f, 275f, paint)
            canvas.drawText("$nombre", 25f, 286f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.5f
            canvas.drawLine(20f, 288f,110f, 288f, forLinePaint)

            //firma
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.5f
            canvas.drawLine(25f, 320f,115f, 320f, forLinePaint)
            canvas.drawText("Firma del servidor publico ", 35f, 330f, paint)


            canvas.drawText("Encargado de la subdireccion", 160f, 278f, paint)
            canvas.drawText("de servicios administrativos", 160f, 285f, paint)
            forLinePaint.style = Paint.Style.STROKE
            forLinePaint.strokeWidth = 0.5f
            canvas.drawLine(140f, 320f,230f, 320f, forLinePaint)
            canvas.drawText("Firma del encargado", 165f, 330f, paint)


            myDocument.finishPage(myPage)
            val file = File(this.getExternalFilesDir("/"), "PermisApp.pdf")
            try {
                myDocument.writeTo(FileOutputStream(file))
                Toast.makeText(this, "Se Descargo Correctamente el PDF",
                    Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                e.printStackTrace()
            }
            myDocument.close()

        }
    }
}

