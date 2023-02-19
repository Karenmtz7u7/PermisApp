package com.aplicacion.permisapp.providers

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.aplicacion.permisapp.Models.Client
import com.aplicacion.permisapp.Models.Incidencias
import com.aplicacion.permisapp.adapters.HistoriesAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.File

class IncidenciasProvider {


    val db = Firebase.firestore.collection("Incidencias")
    var storage = FirebaseStorage.getInstance().getReference().child("firmas")
    val authProvider = AuthProvider()


    //Esta funcion guarda los datos del tramite

    fun create(incidencias: Incidencias): Task<Void> {
        val info: MutableMap<String, Any> = HashMap()
        info["firmaSP"] = incidencias.firmaSP!!

        return db.document().set(incidencias)
    }

    fun updateImageFirma(incidencias: Incidencias):  Task<Void> {

        return db.document().set(incidencias)
    }

    //esta funcion va a guardar la foto de firmas en fireStorage
    fun uploadImageFirma(id: String, file: File): StorageTask<UploadTask.TaskSnapshot> {
        val fromFile = Uri.fromFile(file)
        val ref = storage.child("$id.jpg")
        storage = ref
        val uploadTask = ref.putFile(fromFile)


        return uploadTask.addOnFailureListener {
            Log.d("STORAGE", "ERROR: ${it.message}")
        }
    }

    fun getImageUrlFirma(): Task<Uri> {
        return storage.downloadUrl
    }

    fun getHistories(): Query {
        return db.whereEqualTo("idSP", authProvider.getid())
    }

    fun getLastTramits(): Query {
        return db.whereEqualTo("idSP", authProvider.getid()).orderBy("fecha").orderBy("hora").limit(6)
    }




}


