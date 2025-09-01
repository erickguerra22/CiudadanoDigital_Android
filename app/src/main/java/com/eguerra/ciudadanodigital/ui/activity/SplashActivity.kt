package com.eguerra.ciudadanodigital.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eguerra.ciudadanodigital.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        initEvents()
        setObservers()
    }

    private fun initEvents() {
//        userViewModel.getUserData(false)
        // Simulación de obtención de datos
        getUserDataSimulated()
    }

    private fun getUserDataSimulated() {
        lifecycleScope.launch {
            delay(3000)
            val intent = Intent(this@SplashActivity, UnloggedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setObservers() {
        // TODO()
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                userViewModel.userDataStateFlow.collectLatest { state ->
//                    when (state) {
//                        is UserSessionStatus.Logged -> {
//                            val intent = Intent(this@SplashActivity, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        }
//
//                        is UserSessionStatus.NotLogged -> {
//                            val intent = Intent(this@SplashActivity, UnloggedActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        }
//
//                        else -> {}
//                    }
//                }
//            }
//        }
    }
}