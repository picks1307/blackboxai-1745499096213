package com.example.agendacorteapp.models

import com.google.firebase.Timestamp

data class Appointment(
    val id: String = "",
    val data: Timestamp = Timestamp.now(),
    val hora: String = "",
    val cliente: String = "",
    val barbeiro: String = ""
) {
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "data" to data,
            "hora" to hora,
            "cliente" to cliente,
            "barbeiro" to barbeiro
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any>): Appointment {
            return Appointment(
                id = id,
                data = map["data"] as Timestamp,
                hora = map["hora"] as String,
                cliente = map["cliente"] as String,
                barbeiro = map["barbeiro"] as String
            )
        }
    }
}
