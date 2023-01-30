package com.aplicacion.permisapp.activities

import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.aplicacion.permisapp.R
import com.aplicacion.permisapp.databinding.ActivityMainHomeBinding
import com.aplicacion.permisapp.providers.AuthProvider
import com.aplicacion.permisapp.providers.ClientProvider


class MainActivity_home : AppCompatActivity() {
    private lateinit var binding: ActivityMainHomeBinding
    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val homefragment = HomeFragment()
        val notifyfragment = NotifyFragment()
        val tramitesfragment = TramitesFragment()

        binding.bottomNavigationView.setOnNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.nav_inicio -> {
                    setCurrentFragment(homefragment)
                    true
                }
                R.id.nav_notificacion -> {
                    setCurrentFragment(notifyfragment)
                    true
                }
                R.id.nav_tramites -> {
                    setCurrentFragment(tramitesfragment)
                    true
                }

                else -> false
            }
        }

        binding.bottomNavigationView.getOrCreateBadge(R.id.nav_notificacion).apply {
            isVisible = true
            number
        }
        createToken()
//        setDialog()

    }

    private fun setCurrentFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.containerView, fragment)
            commit()
        }
    }
    //funcion para abrir PrgressBar
//    private fun setDialog(): Boolean {
//        val progressDialog = ProgressDialog(this)
//        progressDialog.setMessage("Un momento...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//        Handler().postDelayed({progressDialog.dismiss()}, 1500)
//        return true
//    }

    private fun createToken(){
        clientProvider.createToken(authProvider.getid())
    }




}


