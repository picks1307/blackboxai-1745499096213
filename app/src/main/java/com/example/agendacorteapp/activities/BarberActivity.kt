package com.example.agendacorteapp.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendacorteapp.adapters.AppointmentsAdapter
import com.example.agendacorteapp.databinding.ActivityBarberBinding
import com.example.agendacorteapp.models.AvailableTime
import com.example.agendacorteapp.utils.FirestoreUtil
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BarberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarberBinding
    private lateinit var adapter: AppointmentsAdapter
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        loadAppointments()
    }

    private fun setupRecyclerView() {
        adapter = AppointmentsAdapter()
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(this@BarberActivity)
            adapter = this@BarberActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTime.setOnClickListener {
            showAddTimeDialog()
        }
    }

    private fun loadAppointments() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                val appointments = FirestoreUtil.getAppointments()
                adapter.submitList(appointments)
                showEmptyState(appointments.isEmpty())
            } catch (e: Exception) {
                Toast.makeText(
                    this@BarberActivity,
                    "Erro ao carregar agendamentos: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showAddTimeDialog() {
        val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
        val editText = dialogView.findViewById<EditText>(android.R.id.text1)
        editText.hint = "Nome do Barbeiro"

        AlertDialog.Builder(this)
            .setTitle("Adicionar Horário")
            .setView(dialogView)
            .setPositiveButton("Próximo") { _, _ ->
                val barberName = editText.text.toString()
                if (barberName.isNotBlank()) {
                    showDatePicker(barberName)
                } else {
                    Toast.makeText(this, "Nome do barbeiro é obrigatório", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDatePicker(barberName: String) {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                showTimePicker(barberName)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(barberName: String) {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                addAvailableTime(barberName)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun addAvailableTime(barberName: String) {
        val timestamp = Timestamp(calendar.time)
        val time = timeFormat.format(calendar.time)

        val availableTime = AvailableTime(
            data = timestamp,
            hora = time,
            barbeiro = barberName
        )

        lifecycleScope.launch {
            try {
                showLoading(true)
                val success = FirestoreUtil.addAvailableTime(availableTime)
                if (success) {
                    Toast.makeText(
                        this@BarberActivity,
                        "Horário adicionado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    throw Exception("Falha ao adicionar horário")
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@BarberActivity,
                    "Erro ao adicionar horário: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvAppointments.visibility = if (show) View.GONE else View.VISIBLE
        binding.fabAddTime.isEnabled = !show
    }

    private fun showEmptyState(show: Boolean) {
        binding.tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvAppointments.visibility = if (show) View.GONE else View.VISIBLE
    }
}
