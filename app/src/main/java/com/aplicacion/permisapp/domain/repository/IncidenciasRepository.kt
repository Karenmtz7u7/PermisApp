package com.aplicacion.permisapp.domain.repository

import com.aplicacion.permisapp.domain.Models.Incidencias
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class IncidenciasRepository {

    var storage = FirebaseStorage.getInstance().getReference().child("PDF")
    val db = Firebase.firestore.collection("Incidencias")
    val authRepository = AuthRepository()

    //Esta funcion guarda los datos del tramite

    fun create(incidencias: Incidencias): Task<Void> {
        return db.document().set(incidencias)
    }

    //esta funcion va a guardar la foto de firmas en fireStorage
    fun getHistories(): Query {
        return db.whereEqualTo("idSP", authRepository.getid())
    }

    fun getLastTramits(): Query {
        return db.whereEqualTo("idSP", authRepository.getid()).orderBy("fecha").orderBy("hora").limit(6)
    }
}


