package com.aplicacion.permisapp.Models

import android.widget.TextView
import com.beust.klaxon.*

private val klaxon = Klaxon()

data class Users(
    val id: String ? = null,
    val idRH: String?=null,
    val rfc: String?= null,
    val nombre: String ? = null,
    val apellido: String ? = null,
    val tel: String ? = null,
    var image: String ? = null,
    val area: String ? = null,
    val noEmpleado: String ? = null,
    val email: String ? = null,


    ) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<Users>(json)
    }
}