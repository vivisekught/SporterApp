package com.graduate.work.sporterapp.domain.firebase.auth.usecases

import com.google.firebase.auth.FirebaseUser
import com.graduate.work.sporterapp.domain.firebase.auth.FirebaseAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCurrentAuthStateUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
) {
    operator fun invoke(scope: CoroutineScope): StateFlow<FirebaseUser?> =
        firebaseAuthRepository.getAuthState(scope)
}