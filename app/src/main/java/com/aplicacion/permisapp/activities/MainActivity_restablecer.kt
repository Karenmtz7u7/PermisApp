package com.aplicacion.permisapp.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.aplicacion.permisapp.databinding.ActivityMainRestablecerBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity_restablecer : AppCompatActivity() {
    private lateinit var binding: ActivityMainRestablecerBinding
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRestablecerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        binding.restablecerbtn.setOnClickListener {
            var username = binding.correoetxt
            forgotPassword(username)
            startActivity(Intent(this, MainActivity::class.java));
            binding.correoetxt.setText("")
        }
        verificaremail()
    }

    private fun verificaremail() {
        binding.correoetxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(binding.correoetxt.text.toString()).matches()){
                    binding.restablecerbtn.isEnabled = true
                }else{
                    binding.restablecerbtn.isEnabled = false
                    binding.correoetxt.error = "Correo Inválido"
                }
            }
            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun forgotPassword(username: EditText) {
        auth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Se envió un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show()
                }
            }

    }
}