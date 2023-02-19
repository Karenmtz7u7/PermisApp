package com.aplicacion.permisapp.providers

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.aplicacion.permisapp.activities.MainActivity_home
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthProvider {
    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    //funcion para registrar el correo y contraseña en FirebaseAuthentication
    fun registrer(email:String, pass:String): Task<AuthResult>{
        return auth.createUserWithEmailAndPassword(email,pass)
    }

    //Funcion para iniciar sesion
    fun logIn(email:String, pass: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, pass)
    }

    //Funcion para obtener el ID de los usuarios
    fun getid(): String{
        return auth.currentUser?.uid ?: ""
    }

    fun updatePassword(password : String): Task<Void>? {
        return auth.currentUser?.updatePassword(password)
    }






    //Funcion para mantener la sesion activa
    fun starSession(): Boolean{
        var exist = false
        if(auth.currentUser != null){
            exist = true
        }
        return exist
    }
     fun logOut(){
        auth.signOut()
     }

    fun deleteAccount(): Task<Void>? {
        return auth.currentUser?.delete()
    }


}