package com.aplicacion.permisapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplicacion.permisapp.data.Models.Client
import com.aplicacion.permisapp.data.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.ui.adapters.HomeAdapter
import com.aplicacion.permisapp.databinding.FragmentHomeBinding
import com.aplicacion.permisapp.data.providers.AuthProvider
import com.aplicacion.permisapp.data.providers.ClientProvider
import com.aplicacion.permisapp.data.providers.IncidenciasProvider
import com.aplicacion.permisapp.providers.*
import com.bumptech.glide.Glide

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    val clientProvider = ClientProvider()
    val authProvider = AuthProvider()
    private val modalMenu = com.aplicacion.permisapp.ui.view.BottomSheetFragment()
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


        val bottomSheetFragment = com.aplicacion.permisapp.ui.view.BottomSheetFragment()
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
















