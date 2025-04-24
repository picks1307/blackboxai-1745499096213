package com.example.agendacorteapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.agendacorteapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnClient.setOnClickListener {
            startActivity(Intent(this, ClientActivity::class.java))
        }

        binding.btnBarber.setOnClickListener {
            startActivity(Intent(this, BarberActivity::class.java))
        }
    }
}
