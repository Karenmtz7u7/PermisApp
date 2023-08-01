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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityUpdatepasswordBinding
import com.aplicacion.permisapp.domain.repository.AuthRepository
import com.aplicacion.permisapp.ui.viewmodels.MainActivityViewModel
import com.aplicacion.permisapp.ui.viewmodels.UpdatePasswordViewModel

class MainActivityUpdatePassword() : DialogFragment(), DialogInterface.OnShowListener {
    private val authRepository = AuthRepository()

    private lateinit var binding: ActivityUpdatepasswordBinding
    private lateinit var viewModel: UpdatePasswordViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity ->
            binding = ActivityUpdatepasswordBinding.inflate(LayoutInflater.from(context))

            binding.let {
                val builder = AlertDialog.Builder(activity)
                    .setView(it.root)

                val dialog = builder.create()
                dialog.setOnShowListener(this)

                viewModel = ViewModelProvider(this).get(UpdatePasswordViewModel::class.java)

                viewModel.messageLiveData.observe(this, Observer { message ->
                    handleMessage(message)
                })

                return dialog
            }
        }

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onShow(dialogInterface: DialogInterface?) {
        val dialog = dialog as? AlertDialog
        dialog?.let {
            val updateBtn = it.findViewById<Button>(R.id.updatePassbtn)
            val cancel = it.findViewById<ImageView>(R.id.cancelbtn)
            updateBtn?.setOnClickListener {
                val pass = binding.passwordtxt.text.toString()
                val confirmPass = binding.passwordverifytxt.text.toString()
                viewModel.updatePassword(pass, confirmPass)
            }

            cancel?.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun handleMessage(message: UpdatePasswordViewModel.Message) {
        when (message) {
            is UpdatePasswordViewModel.Message.Success -> showMessage(message.message)
            is UpdatePasswordViewModel.Message.Error -> showMessageError(message.message)
        }
    }


    private fun showMessage(message : String){
        val view = View.inflate(requireContext(), R.layout.dialog_view, null)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡La contraseña se ha cambiado!"
        view.findViewById<TextView>(R.id.bodyDialog).text = message
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
    private fun showMessageError(message : String){
        val view = View.inflate(requireContext(), R.layout.dialog_view, null)
        view.findViewById<ImageView>(R.id.imageDialog).setImageResource(R.drawable.ic_cancel)
        view.findViewById<TextView>(R.id.titleDialog).text = "¡Error al cambiar contraseña!"
        view.findViewById<TextView>(R.id.bodyDialog).text = message
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