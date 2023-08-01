package com.aplicacion.permisapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aplicacion.permisapp.domain.Models.Client
import com.aplicacion.permisapp.domain.Models.Incidencias
import com.aplicacion.permisapp.domain.repository.AuthRepository
import com.aplicacion.permisapp.domain.repository.ClientRepository
import com.aplicacion.permisapp.domain.repository.IncidenciasRepository

class HomeFragmentViewModel :  ViewModel() {

    private val clientRepository = ClientRepository()
    private val authRepository = AuthRepository()
    private val incidenciasRepository = IncidenciasRepository()
    private val histories = MutableLiveData<List<Incidencias>>()
    private val clientInfo = MutableLiveData<String>()
    private val clientImage = MutableLiveData<String?>()

    init {
        getClient()
        getHistories()
    }

    fun getClientInfo(): LiveData<String> {
        return clientInfo
    }

    fun getClientImage(): MutableLiveData<String?> {
        return clientImage
    }


    fun getClient() {
        val userId = authRepository.getid()
        clientRepository.getSP(userId).addOnSuccessListener { document ->
            if (document.exists()) {
                val client = document.toObject(Client::class.java)
                val clientName = "${client?.nombre} ${client?.apellido}"
                clientInfo.postValue(clientName)

                if (!client?.image.isNullOrEmpty()) {
                    clientImage.postValue(client?.image)
                }
            }
        }
    }

    fun getHistories() {
        incidenciasRepository.getLastTramits().get().addOnSuccessListener { querySnapshot ->
            val historyList = ArrayList<Incidencias>()
            for (document in querySnapshot) {
                val history = document.toObject(Incidencias::class.java)
                history.id = document.id
                historyList.add(history)
            }
            histories.postValue(historyList)
        }
    }
}