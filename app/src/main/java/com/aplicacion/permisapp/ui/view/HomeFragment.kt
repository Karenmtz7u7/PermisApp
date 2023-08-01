package com.aplicacion.permisapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplicacion.permisapp.domain.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.ui.adapters.HomeAdapter
import com.aplicacion.permisapp.databinding.FragmentHomeBinding
import com.aplicacion.permisapp.ui.viewmodels.HomeFragmentViewModel
import com.bumptech.glide.Glide

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var adapter: HomeAdapter
    private val histories = MutableLiveData<List<Incidencias>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)

        setupClickListeners()
        setupRecyclerView()


        val bottomSheetFragment = com.aplicacion.permisapp.ui.view.BottomSheetFragment()
        binding.menubtn.setOnClickListener {
            fragmentManager?.let { it1 -> bottomSheetFragment.show(it1, "BottomSheetDialog") }

        }

        val linearLayoutManager = LinearLayoutManager(this@HomeFragment.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.RecyclerViewHome.layoutManager = linearLayoutManager

        return binding.root

    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this@HomeFragment.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.RecyclerViewHome.layoutManager = linearLayoutManager
        adapter = HomeAdapter(this@HomeFragment.requireActivity())
        binding.RecyclerViewHome.adapter = adapter
    }



    override fun onStart() {
        super.onStart()
        viewModel.getClientInfo().observe(viewLifecycleOwner, { clientInfo ->
            binding.emaildefaultxt.text = clientInfo
        })

        viewModel.getClientImage().observe(viewLifecycleOwner, { clientImage ->
            if (clientImage != null) {
                if (clientImage.isNotEmpty()) {
                    Glide.with(this).load(clientImage).centerCrop().into(binding.userimagetxt)
                }
            }
        })

        viewModel.getHistories()
    }


    private fun setupClickListeners() {

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
    }

}
















