package com.aplicacion.permisapp.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aplicacion.permisapp.domain.Models.Histories
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.ui.view.MainActivityFormatPDF


class TramitsAdapters(val context: Activity, val histories: ArrayList<Histories>):
    RecyclerView.Adapter<TramitsAdapters.TramitsAdaptersViewHolder>() {

    // Sirve para instanciar la lista que se va a mostrar en el historial
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TramitsAdaptersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_cardview,parent,false)
        return TramitsAdaptersViewHolder(view)
    }

    // Se establece la información en los textView
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TramitsAdaptersViewHolder, position: Int) {

        val histories = histories[position] //Se obtiene un solo historial
        holder.tipoincidenciaHistorial.text = histories.tipoIncidencia
        holder.fechaHistorial.text = histories.fecha.toString()
        holder.horaHistorial.text = histories.hora

        holder.cardView.setOnClickListener { v ->
            val intent = Intent(v.context, MainActivityFormatPDF::class.java).apply {
                putExtra("id", histories.id)
                putExtra("folio", histories.folio)
                putExtra("area", histories.area)
                putExtra("tipoIncidencia", histories.tipoIncidencia)
                putExtra("horaInicial", histories.horaInicial)
                putExtra("horaFinal", histories.horaFinal)
                putExtra("fechaSolicitada", histories.fechaSolicitada)
                putExtra("fechaInicial", histories.fechaInicial)
                putExtra("fechaFinal", histories.fechaFinal)
                putExtra("razon", histories.razon)
                putExtra("noEmpleado", histories.noEmpleado)
                putExtra("nombre", histories.nombre)
                putExtra("apellido", histories.apellido)
                putExtra("idRH", histories.idRH)
                putExtra("firmaRH", histories.firmaRH)
                putExtra("firmaSP", histories.firmaSP)
            }
            v.context.startActivity(intent)
        }

    }

    // Sirve para mostrar el tamaño de la lista que se va a mostrar
    override fun getItemCount(): Int {
        return histories.size
    }



    //Clase que permite instanciar las vistas que tienen ID en el layout
    class TramitsAdaptersViewHolder(view: View): RecyclerView.ViewHolder(view){

        val tipoincidenciaHistorial: TextView
        val fechaHistorial: TextView
        val horaHistorial : TextView
        val cardView : CardView

        //constructor para inicializar las variables
        init {
            tipoincidenciaHistorial = view.findViewById(R.id.tipoincidenciaHistorial)
            fechaHistorial = view.findViewById(R.id.fechaHistorial)
            horaHistorial = view.findViewById(R.id.horaHistorial)
            cardView = view.findViewById(R.id.cardView)
        }

    }

}