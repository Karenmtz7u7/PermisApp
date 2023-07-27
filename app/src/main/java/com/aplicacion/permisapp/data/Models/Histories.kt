package com.aplicacion.permisapp.data.Models

import com.beust.klaxon.Klaxon
import com.google.firebase.firestore.Exclude
import java.util.Date

private val klaxon = Klaxon()
data class Histories(
    @get:Exclude var id: String? = null,
    val idSP: String ? = null,
    val idRH: String ? = null,
    val folio: String?= null,
    val area: String ? = null,
    val tipoIncidencia:String?=null,
    val horaInicial:String?="(No aplica)",
    val horaFinal:String?="(No aplica)",
    val fechaSolicitada: String ?="(No aplica)", //fecha que se solicita en la incidencia
    val fechaInicial: String?="(No aplica)",
    val fechaFinal:String?="(No aplica)",
    val razon: String?="(No aplica)",
    val noEmpleado: String ? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val jefeInmediato : String?=null,
    var firmaRH:String?=null,
    var firmaSP:String?=null,
    val hora: String? = null,
    val fecha: String? = null, //fecha de aprobacion
    val status:String?=null,
    val firmaSA: String? = null,
    val firmaJI:String?=null,
    val evidencia: String?="(No aplica)",
    val fechaSolicitud: String ?="(No aplica)", ///fecha del día en que se hizo la solicitud

    val email: String ? = null,
    ) { public fun toJson() = klaxon.toJsonString(this)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Histories

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    companion object {
        public fun fromJson(json: String) = klaxon.parse<Histories>(json)
    }



}