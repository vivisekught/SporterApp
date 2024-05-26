package com.graduate.work.sporterapp.data.firebase.storage.workout

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.graduate.work.sporterapp.data.firebase.storage.workout.mapper.WorkoutMapper
import com.graduate.work.sporterapp.data.firebase.storage.workout.pojo.WorkoutFirestorePojo
import com.graduate.work.sporterapp.domain.firebase.storage.workout.CloudStorageWorkoutRepository
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.Workout
import javax.inject.Inject

class CloudStorageWorkoutRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CloudStorageWorkoutRepository {

    private var listenerRegistration: ListenerRegistration? = null
    private val mapper = WorkoutMapper()
    override fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Workout) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        val query = firestore.collection(WORKOUT_COLLECTION).whereEqualTo("userId", userId)

        listenerRegistration = query.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            value?.documentChanges?.forEach {
                val wasDocumentDeleted = it.type == DocumentChange.Type.REMOVED
                val workoutFirestorePojo =
                    it.document.toObject<WorkoutFirestorePojo>().copy(workoutId = it.document.id)
                val workout = mapper.mapFirestorePojoToEntity(workoutFirestorePojo)
                onDocumentEvent(wasDocumentDeleted, workout)
            }
        }
    }

    override fun removeListener() {
        listenerRegistration?.remove()
    }

    override fun getWorkout(
        routeId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (Workout?) -> Unit,
    ) {
        firestore.collection(WORKOUT_COLLECTION).document(routeId).get()
            .addOnSuccessListener {
                onSuccess(
                    mapper.mapFirestorePojoToEntity(
                        it.toObject() ?: WorkoutFirestorePojo()
                    )
                )
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    override fun saveWorkout(workout: Workout, onResult: (Throwable?) -> Unit) {
        firestore.collection(WORKOUT_COLLECTION).add(mapper.mapEntityToFirestorePojo(workout))
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun updateWorkout(workout: Workout, onResult: (Throwable?) -> Unit) {
        firestore.collection(WORKOUT_COLLECTION).document(workout.workoutId).set(workout)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun deleteWorkout(workoutId: String, onResult: (Throwable?) -> Unit) {
        firestore.collection(WORKOUT_COLLECTION).document(workoutId).delete()
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    companion object {
        private const val WORKOUT_COLLECTION = "workouts"
    }
}