package com.aplicacion.permisapp.data.providers

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.aplicacion.permisapp.data.Models.Incidencias
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.File

class IncidenciasProvider {



    var storage = FirebaseStorage.getInstance().getReference().child("PDF")
    val db = Firebase.firestore.collection("Incidencias")
    val authProvider = AuthProvider()


    //Esta funcion guarda los datos del tramite

    fun create(incidencias: Incidencias): Task<Void> {
        return db.document().set(incidencias)
    }

    //esta funcion va a guardar la foto de firmas en fireStorage
    fun getHistories(): Query {
        return db.whereEqualTo("idSP", authProvider.getid())
    }

    fun getLastTramits(): Query {
        return db.whereEqualTo("idSP", authProvider.getid()).orderBy("fecha").orderBy("hora").limit(6)
    }
}


