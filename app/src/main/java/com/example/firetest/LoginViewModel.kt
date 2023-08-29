package com.example.firetest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.firetest.ui.theme.TAG
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel @Inject constructor() : ViewModel() {

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _error = mutableStateOf("")
    val error: State<String> = _error

    private val _userEmail = mutableStateOf("")
    val userEmail: State<String> = _userEmail

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    // Setters
    fun setUserEmail(email: String) {
        _userEmail.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setError(error: String) {
        _error.value = error
    }

    init {
        _isLoggedIn.value = getCurrentUser() != null
    }

    fun createUserWithEmailAndPassword() = viewModelScope.launch {
        _error.value = ""
        Firebase.auth.createUserWithEmailAndPassword(userEmail.value, password.value)
            .addOnCompleteListener { task -> signInCompletedTask(task) }
    }

    fun signInWithEmailAndPassword() = viewModelScope.launch {
        try {
            _error.value = ""
            Firebase.auth.signInWithEmailAndPassword(userEmail.value, password.value)
                .addOnCompleteListener { task -> signInCompletedTask(task) }
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "Unknown error"
            Log.d(TAG, "Sign in fail: $e")
        }
    }

    private fun signInCompletedTask(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            // TODO success action and error message
            Log.d(TAG, "SignInWithEmail:success")
        } else {
            _error.value = task.exception?.localizedMessage ?: "Unknown error"
            // If sign in fails, display a message to the user.
            Log.w(TAG, "SignInWithEmail:failure", task.exception)
        }
        viewModelScope.launch {
            _isLoggedIn.value = getCurrentUser() != null
        }
    }

    private fun getCurrentUser() : FirebaseUser? {
        val user = Firebase.auth.currentUser
        Log.d(TAG, "user display name: ${user?.displayName}, email: ${user?.email}")
        return user
    }

    fun isValidEmailAndPassword() : Boolean {
        if (userEmail.value.isBlank() || password.value.isBlank()) {
            return false
        }
        return true
    }
}