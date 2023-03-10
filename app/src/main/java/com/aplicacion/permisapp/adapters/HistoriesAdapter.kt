package com.aplicacion.permisapp.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aplicacion.permisapp.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.providers.IncidenciasProvider
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.api.ResourceDescriptor

class HistoriesAdapter(val context: Activity,
                       val histories: ArrayList<Incidencias>,
                       val incidenciasProvider: IncidenciasProvider = IncidenciasProvider()
):
    RecyclerView.Adapter<HistoriesAdapter.HistoriesAdapterViewHolder>() {


    // Sirve para instanciar la lista que se va a mostrar en el historial
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriesAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.status_tramite_cardview,parent,false)
        return HistoriesAdapterViewHolder(view)
    }


    // Se establece la información en los textView
    override fun onBindViewHolder(holder: HistoriesAdapterViewHolder, position: Int) {

        val incidencias = histories[position] //Se obtiene un solo historial
        holder.tipoincidenciaHistorial.text = incidencias.tipoIncidencia
        holder.fechaHistorial.text = incidencias.fechaSolicitud
        holder.statusHistorial.text = incidencias.status

        holder.progressBar.progress = holder.statusHistorial.length() * (100/3)-15

        holder.sendcbx.isChecked = incidencias.status == "Solicitud enviada"
        holder.jicbx.isChecked = incidencias.status == "Vo.Bo. Jefe Inmediato"
        holder.sacbx.isChecked = incidencias.status ==  "Vo.Bo. Subdirector Academico"
        holder.rhcbx.isChecked = incidencias.status == "Aceptado por RH"
    }

   // Sirve para mostrar el tamaño de la lista que se va a mostrar
    override fun getItemCount(): Int {
        return histories.size
    }

    //Clase que permite instanciar las vistas que tienen ID en el layout
    class HistoriesAdapterViewHolder(view: View): RecyclerView.ViewHolder(view){

        val tipoincidenciaHistorial: TextView
        val fechaHistorial: TextView
        val statusHistorial: TextView
        val progressBar : LinearProgressIndicator
        val sendcbx : CheckBox
        val jicbx : CheckBox
        val sacbx : CheckBox
        val rhcbx: CheckBox

        //constructor para inicializar las variables
        init {
            tipoincidenciaHistorial = view.findViewById(R.id.tipoincidenciaHistorial)
            fechaHistorial = view.findViewById(R.id.fechaHistorial)
            statusHistorial = view.findViewById(R.id.statusHistorial)
            progressBar = view.findViewById(R.id.progresssBar)
            sendcbx = view.findViewById(R.id.sendcbx)
            jicbx = view.findViewById(R.id.VoBoJIcbx)
            sacbx = view.findViewById(R.id.VoBoSAcbx)
            rhcbx = view.findViewById(R.id.AcepttRHcbx)
        }

    }




}