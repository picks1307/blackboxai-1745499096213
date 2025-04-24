package com.example.agendacorteapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agendacorteapp.databinding.ItemAvailableTimeBinding
import com.example.agendacorteapp.models.AvailableTime
import java.text.SimpleDateFormat
import java.util.Locale

class AvailableTimesAdapter(
    private val onBookClick: (AvailableTime) -> Unit
) : ListAdapter<AvailableTime, AvailableTimesAdapter.ViewHolder>(AvailableTimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAvailableTimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemAvailableTimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

        fun bind(time: AvailableTime) {
            binding.apply {
                tvDate.text = dateFormat.format(time.data.toDate())
                tvTime.text = time.hora
                tvBarber.text = "Barbeiro: ${time.barbeiro}"

                btnBook.setOnClickListener {
                    onBookClick(time)
                }
            }
        }
    }

    private class AvailableTimeDiffCallback : DiffUtil.ItemCallback<AvailableTime>() {
        override fun areItemsTheSame(oldItem: AvailableTime, newItem: AvailableTime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AvailableTime, newItem: AvailableTime): Boolean {
            return oldItem == newItem
        }
    }
}
