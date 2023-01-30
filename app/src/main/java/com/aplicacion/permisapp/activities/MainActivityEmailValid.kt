package com.aplicacion.permisapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aplicacion.permisapp.databinding.ActivityMainEmailValidBinding

class MainActivityEmailValid : AppCompatActivity() {
    private lateinit var binding : ActivityMainEmailValidBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainEmailValidBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }



}