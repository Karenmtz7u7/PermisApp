package com.aplicacion.permisapp.Models
import com.beust.klaxon.*
import com.google.firebase.firestore.Exclude

private val klaxon = Klaxon()
data class Incidencias(
    @get:Exclude var id : String? = null,
    val Jefeinmediato: String ? = "(No aplica)",
    val idSP: String ? = null,
    val idRH: String? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val status:String?=null,
    val firmaJI:String?=null,
    val firmaRH:String?=null,
    var firmaSP:String?=null,
    val folio:String?=null,
    val horaFinal:String?="(No aplica)",
    val horaInicial:String?="(No aplica)",
    val fechaInicial: String?="(No aplica)",
    val fechaFinal:String?="(No aplica)",
    val tipoIncidencia:String?="(No aplica)",
    val area: String ? = null,
    val hora: String? = null,
    val evidencia: String?="(No aplica)",
    val razon: String?="(No aplica)",
    val fecha: String? = "(No aplica)", //fecha de aprobacion
    val fechaSolicitada: String ?="(No aplica)", //fecha que se solicita en la incidencia
    val fechaSolicitud: String ?="(No aplica)", ///fecha del día en que se hizo la solicitud
    val noEmpleado: String ? = null,
    val email: String ? = null,
    )

{
    public fun toJson() = klaxon.toJsonString(this)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Incidencias

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    companion object {
        public fun fromJson(json: String) = klaxon.parse<Incidencias>(json)
    }
}