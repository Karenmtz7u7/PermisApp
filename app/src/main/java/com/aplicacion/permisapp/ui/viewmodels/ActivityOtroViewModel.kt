package com.aplicacion.permisapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.aplicacion.permisapp.domain.repository.AuthRepository
import com.aplicacion.permisapp.domain.repository.ClientRepository
import com.aplicacion.permisapp.domain.repository.HistoriesRepository
import com.aplicacion.permisapp.domain.repository.IncidenciasRepository

class ActivityOtroViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val clientRepository = ClientRepository()
    private val incidenciasRepository = IncidenciasRepository()
    private val historiesRepository = HistoriesRepository()

}