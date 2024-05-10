package com.graduate.work.sporterapp.data.firebase.storage

import com.google.firebase.firestore.DocumentChange.Type.REMOVED
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.graduate.work.sporterapp.data.firebase.storage.mapper.RouteMapper
import com.graduate.work.sporterapp.data.firebase.storage.pojo.FirestoreRoutePojo
import com.graduate.work.sporterapp.domain.firebase.storage.routes.CloudStorageRouteRepository
import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route
import javax.inject.Inject

class CloudStorageRouteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CloudStorageRouteRepository {

    private val routeMapper = RouteMapper()

    private var listenerRegistration: ListenerRegistration? = null
    override fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Route) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        val query = firestore.collection(ROUTES_COLLECTION).whereEqualTo("userId", userId)

        listenerRegistration = query.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            value?.documentChanges?.forEach {
                val wasDocumentDeleted = it.type == REMOVED
                val firestoreRoutePojo =
                    it.document.toObject<FirestoreRoutePojo>().copy(routeId = it.document.id)
                val route = routeMapper.mapFirestorePojoToEntity(firestoreRoutePojo)
                onDocumentEvent(wasDocumentDeleted, route)
            }
        }
    }

    override fun removeListener() {
        listenerRegistration?.remove()
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