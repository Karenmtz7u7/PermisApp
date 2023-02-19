package com.aplicacion.permisapp.activities


import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.aplicacion.permisapp.Models.Client
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.FragmentDeleteaccountBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider


class DeleteAccountFragment : DialogFragment(), DialogInterface.OnShowListener {

    private lateinit var binding: FragmentDeleteaccountBinding
    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity->
        binding = FragmentDeleteaccountBinding.inflate(LayoutInflater.from(context))
            binding.let {
                val builder = AlertDialog.Builder(activity)
                    .setView(it.root)

                val dialog = builder.create()
                dialog.setOnShowListener(this)

                return dialog

            }
        }
        return super.onCreateDialog(savedInstanceState)
    }



    override fun onShow(dialogInterface: DialogInterface?) {
        val dialog = dialog as? AlertDialog
        dialog?.let {
            binding.deleteAccountbtn.setOnClickListener {
                deleteAccount()


            }
            binding.cancelbtn.setOnClickListener {
                dismiss()
            }

        }
    }

    private fun deleteAccount() {
        val email = binding.emailusertxt.text.toString()
        if (authProvider.auth.currentUser != null){
            val verifyEmail = authProvider.auth.currentUser!!.email

            if(verifyEmail == email){
                dismiss()
               val client = Client(
                   id = authProvider.getid()
               )
                clientProvider.remove(client).addOnSuccessListener {
                    authProvider.deleteAccount()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showMessage()

                        } else {
                            showMessageError()
                            dismiss()
                        }
                    }
                }
            }
            else{
                binding.textalert.text = "¡El correo electronico no pertenece a esta cuenta!"
            }
        }
    }
    private fun showMessage(){
        val view = View.inflate(requireContext(), R.layout.dialog_view, null)
        view.findViewById<TextView>(R.id.titleDialog).text = "Es una pena que te vayas"
        view.findViewById<TextView>(R.id.bodyDialog).text = "La cuenta se ha eliminado correctamente"
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            cerrarsesion()
            dialog.dismiss()
        }
    }


    private fun showMessageError(){
        val view = View.inflate(requireContext(), R.layout.dialog_view, null)
        view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_cancel)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡Error al eliminar cuenta!"
        view.findViewById<TextView>(R.id.bodyDialog).text = "Algo salió mal, intenta nuevamente"
        view.findViewById<Button>(R.id.botonAlert).text = "Reintentar"
        view.findViewById<Button>(R.id.botonAlert).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rojo))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun cerrarsesion() {
        authProvider.logOut()
        val i= Intent(activity, MainActivity::class.java )
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)

    }


}