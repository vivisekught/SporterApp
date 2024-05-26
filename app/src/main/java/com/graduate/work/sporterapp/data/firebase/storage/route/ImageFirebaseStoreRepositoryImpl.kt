package com.graduate.work.sporterapp.data.firebase.storage.route

import com.google.firebase.storage.FirebaseStorage
import com.graduate.work.sporterapp.domain.firebase.storage.routes.ImageFirebaseStoreRepository
import javax.inject.Inject

class ImageFirebaseStoreRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
) : ImageFirebaseStoreRepository {
    override fun saveImage(
        path: String,
        byteArray: ByteArray,
        onSuccess: (downloadUrl: String) -> Unit,
        onFailure: (Exception?) -> Unit,
    ) {
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val storageRef = storage.reference.child(path).child(fileName)
        val uploadTask = storageRef.putBytes(byteArray)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    onSuccess(downloadUrl)
                }
            } else {
                onFailure(task.exception)
            }
        }
    }
}