package com.eguerra.ciudadanodigital.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eguerra.ciudadanodigital.databinding.ActivityMainBinding
import com.eguerra.ciudadanodigital.helpers.InternetStatusListener
import com.eguerra.ciudadanodigital.helpers.InternetStatusManager
import com.eguerra.ciudadanodigital.helpers.SessionManager
import com.eguerra.ciudadanodigital.ui.dialogs.LoadingDialog
import com.eguerra.ciudadanodigital.ui.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), InternetStatusListener {

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivityMainBinding

    private val mainUserViewModel: UserViewModel by viewModels()
    private var isFirstLoad = true
    private val loadingDialog = LoadingDialog()
    private val loadingViewModel: LoadingViewModel by viewModels()
    var isLoggingOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        isFirstLoad = savedInstanceState == null
        InternetStatusManager.addListener(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessionManager.logoutEvents.collect { reason ->
                    handleLogoutAction(reason)
                }
            }
        }

        setContentView(binding.root)
        setObservers()
    }

    override fun onStart() {
        super.onStart()

        if (isFirstLoad) {
            isFirstLoad = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        InternetStatusManager.removeListener(this)
    }

    fun handleLogoutAction(message: String = "Se ha cerrado sesión correctamente") {
        if (isLoggingOut) return
        isLoggingOut = true
        mainUserViewModel.logout()

        showToast(message, this, true)

        val intent = Intent(this, UnloggedActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loadingViewModel.isLoading.collectLatest { isLoading ->
                    manageLoadingComponent(isLoading)
                }
            }
        }
    }

    private fun manageLoadingComponent(isLoading: Boolean) {
        if (isLoading) {
            if (!loadingDialog.isAdded) {
                loadingDialog.show(supportFragmentManager, "Loading")
            }
        } else {
            if (loadingDialog.isAdded) {
                loadingDialog.dismiss()
            }
        }
    }

    override fun onInternetStatusChanged(isConnected: Boolean) {
        showToast("Estás conectado: $isConnected", this)
    }
}