package my.com.a221491_amiraizatbinharith_nelson_project2.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.com.a221491_amiraizatbinharith_nelson_project2.db.EcoDatabase
import my.com.a221491_amiraizatbinharith_nelson_project2.db.EcoRepository
import my.com.a221491_amiraizatbinharith_nelson_project2.db.UserProfileEntity

// ── UI state ──────────────────────────────────────────────────────────────────
sealed class AuthState {
    object Idle       : AuthState()
    object Loading    : AuthState()
    object Success    : AuthState()
    object ForgotSent : AuthState()
    data class Error(val message: String) : AuthState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val db   by lazy { EcoDatabase.getInstance(application) }
    private val repo by lazy {
        EcoRepository(db.topicProgressDao(), db.userProfileDao(), quizGameDao = db.quizGameDao())
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _sessionEpoch = MutableStateFlow(0)
    val sessionEpoch: StateFlow<Int> = _sessionEpoch

    init {
        _currentUser.value = FirebaseAuth.getInstance().currentUser
        if (_currentUser.value != null) {
            viewModelScope.launch {
                // Try to sync from cloud (may fail silently on emulator — that's OK)
                runCatching { repo.syncFromCloud() }
                _sessionEpoch.value++
            }
        }
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // FIX: Read the PREVIOUS uid BEFORE signing in.
                // Only wipe Room if a DIFFERENT account is logging in.
                // Same user logging back in → keep their Room data so profile
                // still shows even when Firestore is unavailable (e.g. emulator).
                val previousUid = auth.currentUser?.uid

                auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
                val newUid = auth.currentUser?.uid
                _currentUser.value = auth.currentUser

                if (previousUid != null && previousUid != newUid) {
                    // Truly different user — wipe old account data first
                    runCatching { repo.clearLocalUserData() }
                }

                // Try Firestore sync (fails silently on emulator — Room data preserved)
                runCatching { repo.syncFromCloud() }

                // If email is still empty after sync, seed it from Firebase Auth
                val profileAfterSync = repo.getProfile()
                if (profileAfterSync != null && profileAfterSync.email.isEmpty()) {
                    val firebaseEmail = auth.currentUser?.email ?: ""
                    if (firebaseEmail.isNotEmpty()) {
                        repo.saveProfile(profileAfterSync.copy(email = firebaseEmail))
                    }
                }

                _sessionEpoch.value++
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(friendlyMessage(e.message))
            }
        }
    }

    // ── Sign Up ───────────────────────────────────────────────────────────────
    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields.")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match.")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // New account — always wipe so old user data never leaks in
                runCatching { repo.clearLocalUserData() }

                auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
                _currentUser.value = auth.currentUser

                runCatching { repo.seedIfEmpty() }

                // Seed email immediately
                runCatching {
                    val newEmail = auth.currentUser?.email ?: ""
                    val joinDate = java.text.SimpleDateFormat(
                        "dd MMM yyyy", java.util.Locale.getDefault()
                    ).format(java.util.Date())

                    repo.saveProfile(
                        UserProfileEntity(
                            email    = newEmail,
                            level    = "Beginner",
                            joinDate = joinDate   // ← set at signup time!
                        )
                    )
                }

                _sessionEpoch.value++
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(friendlyMessage(e.message))
            }
        }
    }

    // ── Forgot Password ───────────────────────────────────────────────────────
    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email address.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email.trim()).await()
                _authState.value = AuthState.ForgotSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error(friendlyMessage(e.message))
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    fun logout() {
        // FIX: Do NOT wipe Room on logout.
        // Room data is kept so when the SAME user logs back in, their profile
        // shows instantly — even if Firestore is unavailable (emulator/offline).
        // Room is only wiped when a DIFFERENT user logs in (see login() above).
        auth.signOut()
        _currentUser.value = null
        _authState.value   = AuthState.Idle
        viewModelScope.launch {
            _sessionEpoch.value++
        }
    }

    // ── Reset UI state ────────────────────────────────────────────────────────
    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // ── User-friendly Firebase error messages ─────────────────────────────────
    private fun friendlyMessage(raw: String?): String = when {
        raw == null                               -> "Something went wrong. Try again."
        "no user record"          in raw          -> "No account found with this email."
        "password is invalid"     in raw          -> "Incorrect password."
        "email address is already in use" in raw  -> "This email is already registered."
        "badly formatted"         in raw          -> "Please enter a valid email address."
        "network error"           in raw          -> "No internet connection."
        "blocked all requests"    in raw          -> "Too many attempts. Try again later."
        else                                      -> raw
    }
}