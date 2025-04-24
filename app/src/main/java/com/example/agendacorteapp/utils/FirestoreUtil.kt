package com.example.agendacorteapp.utils

import com.example.agendacorteapp.models.Appointment
import com.example.agendacorteapp.models.AvailableTime
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object FirestoreUtil {
    private val db = FirebaseFirestore.getInstance()
    private val availableTimesRef = db.collection("horarios_disponiveis")
    private val appointmentsRef = db.collection("agendamentos")

    suspend fun getAvailableTimes(): List<AvailableTime> {
        return try {
            availableTimesRef
                .orderBy("data", Query.Direction.ASCENDING)
                .get()
                .await()
                .documents
                .map { doc ->
                    AvailableTime.fromMap(doc.id, doc.data ?: emptyMap())
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addAvailableTime(availableTime: AvailableTime): Boolean {
        return try {
            availableTimesRef.add(availableTime.toMap()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeAvailableTime(timeId: String): Boolean {
        return try {
            availableTimesRef.document(timeId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAppointments(): List<Appointment> {
        return try {
            appointmentsRef
                .orderBy("data", Query.Direction.ASCENDING)
                .get()
                .await()
                .documents
                .map { doc ->
                    Appointment.fromMap(doc.id, doc.data ?: emptyMap())
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addAppointment(appointment: Appointment): Boolean {
        return try {
            appointmentsRef.add(appointment.toMap()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getBarberAppointments(barberName: String): List<Appointment> {
        return try {
            appointmentsRef
                .whereEqualTo("barbeiro", barberName)
                .orderBy("data", Query.Direction.ASCENDING)
                .get()
                .await()
                .documents
                .map { doc ->
                    Appointment.fromMap(doc.id, doc.data ?: emptyMap())
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
