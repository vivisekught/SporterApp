package com.graduate.work.sporterapp.data.firebase.storage.route

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.SearchRouteParams
import com.graduate.work.sporterapp.core.SearchWorkoutParams
import com.graduate.work.sporterapp.core.SortDirection
import com.graduate.work.sporterapp.data.firebase.storage.route.mapper.RouteMapper
import com.graduate.work.sporterapp.data.firebase.storage.route.pojo.FirestoreRoutePojo
import com.graduate.work.sporterapp.domain.firebase.storage.routes.CloudStorageRouteRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CloudStorageRouteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CloudStorageRouteRepository {

    private val routeMapper = RouteMapper()

    override fun getRoutes(
        userId: String,
        searchRouteParams: SearchRouteParams
    ): Flow<Response<List<Route>>> = callbackFlow {
        var query = firestore
            .collection(ROUTES_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy(searchRouteParams.sortType.dbValue, if (searchRouteParams.sortDirection == SortDirection.ASC) Query.Direction.ASCENDING else Query.Direction.DESCENDING)
        if (searchRouteParams.searchText.isNotEmpty()) {
            query = query
                .whereGreaterThanOrEqualTo("name", searchRouteParams.searchText)
                .whereLessThanOrEqualTo("name", searchRouteParams.searchText + '\uf8ff')
        }
        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                val workouts = snapshot.toObjects(FirestoreRoutePojo::class.java).map {
                    routeMapper.mapFirestorePojoToEntity(it)
                }
                Response.Success(workouts)
            } else {
                Response.Failure(e?.message ?: e.toString())
            }
            trySend(response).isSuccess
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getRoute(
        routeId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (Route) -> Unit,
    ) {
        firestore.collection(ROUTES_COLLECTION).document(routeId).get()
            .addOnSuccessListener {
                onSuccess(
                    routeMapper.mapFirestorePojoToEntity(
                        it.toObject() ?: FirestoreRoutePojo()
                    )
                )
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    override fun saveRoute(route: Route, onResult: (Throwable?) -> Unit) {
        firestore.collection(ROUTES_COLLECTION).add(routeMapper.mapEntityToFirestorePojo(route))
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun updateRoute(route: Route, onResult: (Throwable?) -> Unit) {
        firestore.collection(ROUTES_COLLECTION).document(route.routeId).set(route)
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    override fun deleteRoute(routeId: String, onResult: (Throwable?) -> Unit) {
        firestore.collection(ROUTES_COLLECTION).document(routeId).delete()
            .addOnCompleteListener {
                onResult(it.exception)
            }
    }

    companion object {
        private const val ROUTES_COLLECTION = "routes"
    }
}