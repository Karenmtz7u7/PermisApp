package com.aplicacion.permisapp.activities


import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.aplicacion.permisapp.databinding.AccessCodeBinding

class Code_access : DialogFragment(), DialogInterface.OnShowListener {
    private lateinit var binding: AccessCodeBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity->
            binding = AccessCodeBinding.inflate(LayoutInflater.from(context))
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


            binding.botonAcept.setOnClickListener {
                aceptar()

            }


        }
    }


        private fun iraregistro() {

            val intent =
                Intent(activity, MainActivityRegisterUser::class.java)
            startActivity(intent)
        }

        private fun aceptar() {

            if (binding.codeaccess.text.toString() == "teschi_2023") {
                iraregistro()
                return
            }
        }
    }







