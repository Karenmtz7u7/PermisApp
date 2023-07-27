package com.aplicacion.permisapp.ui.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityUpdatepasswordBinding
import com.aplicacion.permisapp.domain.repository.AuthRepository

class MainActivityUpdatePassword() : DialogFragment(), DialogInterface.OnShowListener {
    private val authRepository = AuthRepository()
    private lateinit var binding: ActivityUpdatepasswordBinding
    var updateBtn : Button?=null
    var cancel : ImageView?=null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity->
            binding = ActivityUpdatepasswordBinding.inflate(LayoutInflater.from(context))

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

    @SuppressLint("SuspiciousIndentation")
    override fun onShow(dialogInterface: DialogInterface?) {
        val dialog = dialog as? AlertDialog
        dialog?.let {
            updateBtn = it.findViewById(R.id.updatePassbtn)
            cancel = it.findViewById(R.id.cancelbtn)
                updateBtn?.setOnClickListener {
                    passupdate()
                }

            cancel?.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun passupdate(){
        val pass = binding.passwordtxt.text.toString()
        val confirmPass = binding.passwordverifytxt.text.toString()
        if (pass.isEmpty() || confirmPass.isEmpty()){
            binding.textalert.text = "¡No puedes dejar cajas de texto vacias!"
        }else{
            if (pass == confirmPass){
                if (authRepository.starSession()){
                    authRepository.updatePassword(pass)?.addOnCompleteListener { task->
                        if (task.isSuccessful){
                            showMessage()
                            dismiss()
                        }else{
                            showMessageError()
                            dismiss()
                        }
                    }
                }
            }
            else{
                binding.textalert.text = "¡Las contraseñas no coinciden!"
                binding.passwordverifytxt.setText("")
            }
        }
    }

    private fun showMessage(){
        val view = View.inflate(requireContext(), R.layout.dialog_view, null)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡La contraseña se ha cambiado!"
        view.findViewById<TextView>(R.id.bodyDialog).text = "Se cambio exitosamente la contraseña"
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            binding.passwordtxt.setText("")
            binding.passwordverifytxt.setText("")
            dialog.dismiss()
        }
    }
    private fun showMessageError(){
        val view = View.inflate(requireContext(), R.layout.dialog_view, null)
        view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_cancel)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡Error al cambiar contraseña!"
        view.findViewById<TextView>(R.id.bodyDialog).text = "Algo salió mal, intenta nuevamente"
        view.findViewById<Button>(R.id.botonAlert).text = "Reintentar"
        view.findViewById<Button>(R.id.botonAlert).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.rojo))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        view.findViewById<Button>(R.id.botonAlert).setOnClickListener {
            binding.passwordtxt.setText("")
            binding.passwordverifytxt.setText("")
            dialog.dismiss()
        }
    }






}