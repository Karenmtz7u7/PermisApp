package com.aplicacion.permisapp.domain.repository


import android.util.Log
import com.aplicacion.permisapp.domain.Models.Histories
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoriesRepository {
    val authRepository = AuthRepository()
    val db = Firebase.firestore.collection("Histories")

    fun create(histories: Histories): Task<DocumentReference> {

        return db.add(histories).addOnFailureListener {
            Log.d("FIRESTORE", "ERROR ${it.message}")
        }
    }
    fun getTramits(): Task<QuerySnapshot> {
        return db.whereEqualTo("idSP", authRepository.getid()).get()
    }

    fun getDoc(id: String): Task<DocumentSnapshot> {
        return db.document(id.toString()).get()
    }

    fun getIncidencias(): Task<QuerySnapshot> {
        return db.whereEqualTo("idSP", authRepository.getid())
           .whereNotEqualTo("tipoIncidencia", "Día económico")
            .whereEqualTo("status", "Aceptado por RRHH")
            .get()
    }

    fun getDaysEconomic(): Task<QuerySnapshot> {

        return db.whereEqualTo("idSP", authRepository.getid())
            .whereEqualTo("tipoIncidencia", "Día económico")
            .whereEqualTo("status", "Aceptado por RRHH").get()
    }
}