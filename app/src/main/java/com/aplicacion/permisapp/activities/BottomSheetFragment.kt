package com.aplicacion.permisapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.aplicacion.permisapp.*
import com.aplicacion.permisapp.databinding.BottomsheetFragmentBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.HashMap


class BottomSheetFragment() : BottomSheetDialogFragment(){
    var linearLayoutProfile : LinearLayout?=null
    var linearLayoutNotification : LinearLayout?=null
    var linearLayoutHistory: LinearLayout?=null
    var linearLayoutStatus: LinearLayout?=null
    var linearLayoutLogOut: LinearLayout?=null
    private val authProvider = AuthProvider()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_fragment, container, false)
        linearLayoutProfile = view.findViewById(R.id.miperfilbtn)
        linearLayoutNotification = view.findViewById(R.id.misnotificacionesbtn)
        linearLayoutHistory = view.findViewById(R.id.mihistorialbtn)
        linearLayoutStatus = view.findViewById(R.id.miestadobtn)
        linearLayoutLogOut = view.findViewById(R.id.salirbtn)

        linearLayoutProfile?.setOnClickListener {
            profile()
        this.dismiss()
        }
        linearLayoutNotification?.setOnClickListener {
            notofication()
            this.dismiss()
        }
        linearLayoutHistory?.setOnClickListener {
            history()
            this.dismiss()
        }
        linearLayoutStatus?.setOnClickListener {
            status()
            this.dismiss()
        }
        linearLayoutLogOut?.setOnClickListener { alertdialog() }
        return view
    }




    private fun  alertdialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@BottomSheetFragment.requireContext())
        builder.setTitle(getString(R.string.cerrarsesion))
        builder.setMessage(getString(R.string.cerrarpregunta))
        builder.setPositiveButton("Cerrar sesión", { _, _ ->
            cerrarsesion()
        })
        builder.setNegativeButton("Cancelar", {_, _ ->
            this.dismiss()
        })

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun cerrarsesion() {
        authProvider.logOut()
        val i= Intent(activity, MainActivity::class.java )
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)

    }

    private fun profile(){
        val i = Intent(activity, MainActivityPerfil::class.java)
        startActivity(i)
    }
    private fun notofication(){
        val i = Intent(activity, MainActivityNotofications::class.java)
        startActivity(i)
    }
    private fun history(){
        val i = Intent(activity, MainActivityHistory::class.java)
        startActivity(i)
    }
    private fun status(){
        val i = Intent(activity, MainActivityStatusTramites::class.java)
        startActivity(i)
    }


}














