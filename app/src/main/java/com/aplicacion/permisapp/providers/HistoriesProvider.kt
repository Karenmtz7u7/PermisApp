package com.aplicacion.permisapp.providers


import android.util.Log
import com.aplicacion.permisapp.Models.Histories
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoriesProvider {
    val authProvider = AuthProvider()
    val db = Firebase.firestore.collection("Histories")


    fun create(histories: Histories): Task<DocumentReference> {

        return db.add(histories).addOnFailureListener {
            Log.d("FIRESTORE", "ERROR ${it.message}")
        }
    }
    fun getTramits(): Task<QuerySnapshot> {
        return db.whereEqualTo("idSP", authProvider.getid()).get()
    }
    fun getDoc(id: String): Task<DocumentSnapshot> {
        return db.document(id.toString()).get()
    }

    //funcion para actualizar la informacion en FireStore



}