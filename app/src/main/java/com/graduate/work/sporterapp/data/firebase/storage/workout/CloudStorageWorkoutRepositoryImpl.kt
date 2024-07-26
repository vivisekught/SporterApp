package com.graduate.work.sporterapp.data.firebase.storage.workout

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.firestore.toObject
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.SearchWorkoutParams
import com.graduate.work.sporterapp.core.SortDirection
import com.graduate.work.sporterapp.data.firebase.storage.workout.mapper.WorkoutMapper
import com.graduate.work.sporterapp.data.firebase.storage.workout.pojo.WorkoutFirestorePojo
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.CloudStorageWorkoutRepository
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CloudStorageWorkoutRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CloudStorageWorkoutRepository {

    private val mapper = WorkoutMapper()
    override fun getWorkouts(
        searchWorkoutParams: SearchWorkoutParams,
        userId: String,
    ): Flow<Response<List<Workout>>> = callbackFlow {
        // create query for workouts
        var query = firestore
            // choose workout collection
            .collection(WORKOUT_COLLECTION)
            // get only user's workouts
            .whereEqualTo("userId", userId)
            // sort by user filters
            .orderBy(searchWorkoutParams.sortWorkoutType.dbValue, if (searchWorkoutParams.sortDirection == SortDirection.ASC) {
                Direction.ASCENDING
            } else Direction.DESCENDING)
        // search by text
        if (searchWorkoutParams.searchText.isNotEmpty()) {
            query = query
                .whereGreaterThanOrEqualTo("name", searchWorkoutParams.searchText)
                .whereLessThanOrEqualTo("name", searchWorkoutParams.searchText + '\uf8ff')
        }
        // add snapshot listener
        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                // map firestore pojos to domain pojos
                val workouts = snapshot.toObjects(WorkoutFirestorePojo::class.java).map {
                    mapper.mapFirestorePojoToEntity(it)
                }
                Response.Success(workouts)
            } else {
                Response.Failure(e?.message ?: e.toString())
            }
            trySend(response).isSuccess
        }
        // close listener
        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getWorkout(
        routeId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (Workout) -> Unit,
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