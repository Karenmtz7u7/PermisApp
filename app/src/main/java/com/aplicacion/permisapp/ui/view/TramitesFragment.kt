package com.aplicacion.permisapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplicacion.permisapp.data.Models.Incidencias
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.ui.adapters.HistoriesAdapter
import com.aplicacion.permisapp.databinding.FragmentTramitesBinding
import com.aplicacion.permisapp.data.providers.IncidenciasProvider


class TramitesFragment : Fragment(R.layout.fragment_tramites) {
    private lateinit var binding: FragmentTramitesBinding
    private var incidenciasProvider = IncidenciasProvider()
    private var histories  = ArrayList <Incidencias>()
    private lateinit var adapter: HistoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTramitesBinding.inflate(layoutInflater)
        val linearLayoutManager = LinearLayoutManager(this@TramitesFragment.requireContext())
        binding.RecyclerViewHistorial.layoutManager = linearLayoutManager

        getHistories()


        return binding.root
    }

    private fun getHistories() {

        histories.clear()
        incidenciasProvider.getHistories().get().addOnSuccessListener {

            for (document in it){
                val history = document.toObject(Incidencias::class.java)
                history.id = history.id
                histories.add(history)
            }
            adapter = HistoriesAdapter(this@TramitesFragment.requireActivity(), histories)
            binding.RecyclerViewHistorial.adapter    = adapter

        }
    }
}







