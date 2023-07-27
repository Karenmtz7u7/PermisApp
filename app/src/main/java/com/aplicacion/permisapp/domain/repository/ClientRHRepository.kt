package com.aplicacion.permisapp.domain.repository

import android.net.Uri
import android.util.Log
import com.aplicacion.permisapp.domain.Models.ClientRH
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.File

class ClientRHRepository {
    val db = Firebase.firestore.collection("UserRH")

    var storage = FirebaseStorage.getInstance().getReference().child("UserRH")

    //esta funcion inserta los datos en FireStorage solo si el usuario ya esta registrado y existe
    //en la base de datos
    fun create(clientRH: ClientRH): Task<Void> {
        return db.document(clientRH.idRH!!).set(clientRH)
    }

    //esta funcion va a guardar la foto de perfil en fireStorage
    fun uploadImage(id: String, file: File): StorageTask<UploadTask.TaskSnapshot> {
        val fromFile = Uri.fromFile(file)
        val ref = storage.child("$id.jpg")
        storage = ref
        val uploadTask = ref.putFile(fromFile)

        return uploadTask.addOnFailureListener {
            Log.d("STORAGE", "ERROR: ${it.message}")
        }
    }

    fun getIdRH(id: String): Task<DocumentSnapshot> {
        return db.document(id).get()
    }


    //funcion para mandar a llamar el cargo del usuario
    fun getRH(): Query {
        return db.whereEqualTo("cargo", "Jefe de área")
    }

    //funcion para actualizar la informacion en FireStore
    fun update(clientRH: ClientRH): Task<Void> {
        val info: MutableMap<String, Any> = HashMap()
        info["nombre"] = clientRH.nombre!!
        info["apellido"] = clientRH.apellido!!
        info["tel"] = clientRH.tel!!

        return db.document(clientRH.idRH!!).update(info)
    }

    fun updateImage(clientRH: ClientRH): Task<Void> {
        val info: MutableMap<String, Any> = HashMap()
        info["image"] = clientRH.image!!
        return db.document(clientRH.idRH!!).update(info)
    }

    fun getImageUrl(): Task<Uri> {
        return storage.downloadUrl
    }

    fun createToken(idRH: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
                updateToken(idRH, token)
            }
        }
    }

    fun updateToken(idRH: String, token: String): Task<Void> {
        val info: MutableMap<String, Any> = HashMap()
        info["token"] = token
        return db.document(idRH).update(info)
    }

}