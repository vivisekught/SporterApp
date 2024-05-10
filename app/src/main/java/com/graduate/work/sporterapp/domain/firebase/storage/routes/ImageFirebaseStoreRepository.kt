package com.graduate.work.sporterapp.domain.firebase.storage.routes

interface ImageFirebaseStoreRepository {
    fun saveImage(
        path: String,
        byteArray: ByteArray,
        onSuccess: (downloadUrl: String) -> Unit,
        onFailure: (Exception?) -> Unit,
    )
}