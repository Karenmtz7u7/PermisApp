package com.aplicacion.permisapp.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aplicacion.permisapp.domain.Models.Incidencias
import com.aplicacion.permisapp.R
import com.google.api.ResourceDescriptor

class HomeAdapter(val context: Activity):
    RecyclerView.Adapter<HomeAdapter.HomeAdapterViewHolder>() {
    val histories = ArrayList<Incidencias>()

    // Sirve para instanciar la lista que se va a mostrar en el historial
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_cardview,parent,false)
        return HomeAdapterViewHolder(view)
    }


    // Se establece la información en los textView
    override fun onBindViewHolder(holder: HomeAdapterViewHolder, position: Int) {



        val incidencias = histories[position] //Se obtiene un solo historial
        holder.tipoincidenciaHistorial.text = incidencias.tipoIncidencia
        holder.fechaHistorial.text = incidencias.fechaSolicitud
        holder.statusHistorial.text = incidencias.status

    }

   // Sirve para mostrar el tamaño de la lista que se va a mostrar
    override fun getItemCount(): Int {
        return histories.size
    }



    //Clase que permite instanciar las vistas que tienen ID en el layout
    class HomeAdapterViewHolder(view: View): RecyclerView.ViewHolder(view){

        val tipoincidenciaHistorial: TextView
        val fechaHistorial: TextView
        val statusHistorial: TextView

        //constructor para inicializar las variables
        init {
            tipoincidenciaHistorial = view.findViewById(R.id.tipoincidenciaHistorial)
            fechaHistorial = view.findViewById(R.id.fechaHistorial)
            statusHistorial = view.findViewById(R.id.statusHistorial)
        }

    }



}