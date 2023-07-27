package com.aplicacion.permisapp.data.providers

import android.app.ProgressDialog
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Toast
import com.aplicacion.permisapp.data.Models.Client
import com.aplicacion.permisapp.data.Models.Users

import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.File

class UsersProvider {

    val db = Firebase.firestore.collection("UserAdds")
    val authProvider = AuthProvider()

    //esta funcion inserta los datos en FireStore solo si el usuario ya esta registrado y existe
    //en la base de datos
    fun create(users : Users): Task<Void>{
        return db.document(users.id!!).set(users)
    }
    //esta funcion va a guardar la foto de perfil en fireStorage


    //funcion para mandar a llamar el id del usuario
    fun getuser(id: String): Task<DocumentSnapshot>{
        return db.document(id).get()
    }

    fun removeuser(id: String): Task<Void> {
        return db.document(id).delete()
    }





}