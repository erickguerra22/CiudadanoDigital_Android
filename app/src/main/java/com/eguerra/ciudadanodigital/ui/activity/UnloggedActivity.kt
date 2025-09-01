package com.eguerra.ciudadanodigital.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eguerra.ciudadanodigital.databinding.ActivityUnloggedBinding
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class UnloggedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUnloggedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUnloggedBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
    }
}