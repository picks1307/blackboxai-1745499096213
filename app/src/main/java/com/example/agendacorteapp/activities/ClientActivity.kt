package com.example.agendacorteapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendacorteapp.adapters.AvailableTimesAdapter
import com.example.agendacorteapp.databinding.ActivityClientBinding
import com.example.agendacorteapp.models.Appointment
import com.example.agendacorteapp.models.AvailableTime
import com.example.agendacorteapp.utils.FirestoreUtil
import kotlinx.coroutines.launch

class ClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientBinding
    private lateinit var adapter: AvailableTimesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadAvailableTimes()
    }

    private fun setupRecyclerView() {
        adapter = AvailableTimesAdapter { availableTime ->
            showBookingDialog(availableTime)
        }

        binding.rvAvailableTimes.apply {
            layoutManager = LinearLayoutManager(this@ClientActivity)
            adapter = this@ClientActivity.adapter
        }
    }

    private fun loadAvailableTimes() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                val times = FirestoreUtil.getAvailableTimes()
                adapter.submitList(times)
                showEmptyState(times.isEmpty())
            } catch (e: Exception) {
                Toast.makeText(
                    this@ClientActivity,
                    "Erro ao carregar horários: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showBookingDialog(availableTime: AvailableTime) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Agendamento")
            .setMessage("Deseja agendar o horário ${availableTime.hora} com o barbeiro ${availableTime.barbeiro}?")
            .setPositiveButton("Confirmar") { _, _ ->
                bookAppointment(availableTime)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun bookAppointment(availableTime: AvailableTime) {
        lifecycleScope.launch {
            try {
                showLoading(true)
                
                val appointment = Appointment(
                    data = availableTime.data,
                    hora = availableTime.hora,
                    cliente = "Cliente", // In a real app, this would be the logged-in user's name
                    barbeiro = availableTime.barbeiro
                )

                val success = FirestoreUtil.addAppointment(appointment)
                if (success) {
                    FirestoreUtil.removeAvailableTime(availableTime.id)
                    Toast.makeText(
                        this@ClientActivity,
                        "Agendamento realizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadAvailableTimes() // Reload the list
                } else {
                    throw Exception("Falha ao criar agendamento")
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ClientActivity,
                    "Erro ao agendar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvAvailableTimes.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(show: Boolean) {
        binding.tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvAvailableTimes.visibility = if (show) View.GONE else View.VISIBLE
    }
}
