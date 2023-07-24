package com.aplicacion.permisapp.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplicacion.permisapp.Models.Client
import com.aplicacion.permisapp.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.adapters.HistoriesAdapter
import com.aplicacion.permisapp.adapters.HomeAdapter
import com.aplicacion.permisapp.adapters.TramitsAdapters
import com.aplicacion.permisapp.databinding.FragmentHomeBinding
import com.aplicacion.permisapp.databinding.FragmentTramitesBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.aplicacion.permisapp.providers.*
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.phone.SmsRetriever.getClient
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    val clientProvider = ClientProvider()
    val authProvider = AuthProvider()
    private val modalMenu = BottomSheetFragment()
    private var incidenciasProvider = IncidenciasProvider()
    private var histories = ArrayList<Incidencias>()
    private lateinit var adapter: HomeAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.iconPerson.setOnClickListener {
            val intent = Intent(activity, MainActivityPerfil::class.java)
            startActivity(intent)
        }
        binding.emaildefaultxt.setOnClickListener {
            val intent = Intent(activity, MainActivityPerfil::class.java)
            startActivity(intent)
        }
        binding.lactanciahbtn.setOnClickListener {
            val intent =
                Intent(activity, MainActivityLactancia::class.java)
            startActivity(intent)
        }

        binding.otrobtn.setOnClickListener {
            val intent =
                Intent(activity, MainActivity_Otro::class.java)
            startActivity(intent)
        }
        binding.llegartardebtn.setOnClickListener {
            val intent =
                Intent(activity, MainActivityLlegarTarde::class.java)
            startActivity(intent)
        }
        binding.consultabtn.setOnClickListener {
            val intent =
                Intent(activity, MainActivityAsistenciaMedica::class.java)
            startActivity(intent)
        }
        binding.daycashbtn.setOnClickListener {
            val intent =
                Intent(activity, MainActivityDiaEconomico::class.java)
            startActivity(intent)
        }
        binding.irseantesbtn.setOnClickListener {
            val intent =
                Intent(activity, MainActivityIrseAntes::class.java)
            startActivity(intent)
        }
        binding.walletoffbtn.setOnClickListener {
            val intent =
                Intent(activity, MainActivitySinSueldo::class.java)
            startActivity(intent)
        }


        val bottomSheetFragment = BottomSheetFragment()
        binding.menubtn.setOnClickListener {
            fragmentManager?.let { it1 -> bottomSheetFragment.show(it1, "BottomSheetDialog") }

        }

        val linearLayoutManager = LinearLayoutManager(this@HomeFragment.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.RecyclerViewHome.layoutManager = linearLayoutManager

        return binding.root

    }


    override fun onStart() {
        super.onStart()
        getClient()
        getHistories()
    }
    private fun getHistories() {

        histories.clear()
        incidenciasProvider.getLastTramits().get().addOnSuccessListener {

            for (document in it){
                val history = document.toObject(Incidencias::class.java)
                history.id = history.id
                histories.add(history)
            }
            adapter = HomeAdapter(this@HomeFragment.requireActivity(), histories)
            binding.RecyclerViewHome.adapter    = adapter

        }
    }

    private fun getClient() {
        clientProvider.getSP(authProvider.getid()).addOnSuccessListener { document ->
            if (document.exists()) {
                val client = document.toObject(Client::class.java)
                binding.emaildefaultxt.text = "${client?.nombre} ${client?.apellido}"

                if (client?.image != null) {
                    if (client.image != "") {
                        Glide.with(this).load(client.image).centerCrop().into(binding.userimagetxt)
                    }
                }
            }
        }
    }


}
















