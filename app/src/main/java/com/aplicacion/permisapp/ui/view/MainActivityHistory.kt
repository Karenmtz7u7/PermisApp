package com.aplicacion.permisapp.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplicacion.permisapp.domain.Models.Histories
import com.aplicacion.permisapp.ui.adapters.TramitsAdapters
import com.aplicacion.permisapp.databinding.ActivityMainHistoryBinding
import com.aplicacion.permisapp.domain.repository.HistoriesRepository

class MainActivityHistory : AppCompatActivity() {
    private lateinit var binding: ActivityMainHistoryBinding
    private var tramits = ArrayList<Histories>()
    private var historyProvider = HistoriesRepository()
    private lateinit var adapter: TramitsAdapters

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainHistoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(this)
        binding.RecyclerViewTramits.layoutManager = linearLayoutManager

        getTramits()
        setContentView(binding.root)
    }


    private fun getTramits() {
        tramits.clear()
        historyProvider.getTramits().addOnSuccessListener {

            for (document in it){
                val histories = document.toObject(Histories::class.java)
                histories.id = document.id
                tramits.add(histories)
            }

            adapter = TramitsAdapters(this, tramits)
            binding.RecyclerViewTramits.adapter = adapter

        }
    }
}