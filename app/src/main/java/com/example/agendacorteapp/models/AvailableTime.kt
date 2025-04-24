package com.example.agendacorteapp.models

import com.google.firebase.Timestamp

data class AvailableTime(
    val id: String = "",
    val data: Timestamp = Timestamp.now(),
    val hora: String = "",
    val barbeiro: String = ""
) {
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "data" to data,
            "hora" to hora,
            "barbeiro" to barbeiro
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any>): AvailableTime {
            return AvailableTime(
                id = id,
                data = map["data"] as Timestamp,
                hora = map["hora"] as String,
                barbeiro = map["barbeiro"] as String
            )
        }
    }
}
