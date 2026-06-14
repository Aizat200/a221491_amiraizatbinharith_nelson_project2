package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.com.a221491_amiraizatbinharith_nelson_project2.db.EcoDatabase
import my.com.a221491_amiraizatbinharith_nelson_project2.db.EcoRepository
import my.com.a221491_amiraizatbinharith_nelson_project2.db.UserProfileEntity

// ── Data class ────────────────────────────────────────────────────────────────
data class UserProfile(
    val name      : String = "",
    val email     : String = "",
    val university: String = "",
    val course    : String = "",
    val bio       : String = "",
    val level     : String = "Beginner",
    val joinDate  : String = ""
)

// ── Mapping helpers ───────────────────────────────────────────────────────────
private fun UserProfileEntity.toUi() = UserProfile(
    name       = name,
    email      = email,
    university = university,
    course     = course,
    bio        = bio,
    level      = level,
    joinDate   = joinDate
)

private fun UserProfile.toEntity(existingPhotoBase64: String = "") = UserProfileEntity(
    id          = 1,
    name        = name,
    email       = email,
    university  = university,
    course      = course,
    bio         = bio,
    level       = level,
    joinDate    = joinDate,
    photoBase64 = existingPhotoBase64   // FIX: preserve existing photo — never wipe it
)

// ── ViewModel ─────────────────────────────────────────────────────────────────
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db   = EcoDatabase.getInstance(application)
    private val repo = EcoRepository(db.topicProgressDao(), db.userProfileDao(), quizGameDao = db.quizGameDao())

    // Map Room entity flow → UI UserProfile flow
    val profile: StateFlow<UserProfile> = repo.profileFlow
        .map { entity -> entity?.toUi() ?: UserProfile() }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserProfile()
        )

    // ── FIX: Pull latest profile from Firestore on every VM creation ──────────
    // This ensures profile data loads after login (new device / new session)
    init {
        viewModelScope.launch {
            repo.syncFromCloud()
        }
    }

    /**
     * FIX: Reads the current Room row FIRST to preserve photoBase64,
     * then merges the updated UI fields on top before saving.
     *
     * Previously, toEntity() always set photoBase64 = "" which silently
     * wiped the profile photo every time the profile was saved.
     */
    fun updateProfile(updated: UserProfile) {
        viewModelScope.launch {
            val existingPhoto = repo.getProfile()?.photoBase64 ?: ""
            repo.saveProfile(updated.toEntity(existingPhotoBase64 = existingPhoto))
        }
    }

    /**
     * Call this after login to re-sync the logged-in user's profile from
     * Firestore into Room. Useful when the user logs in on a fresh install.
     */
    fun syncAfterLogin() {
        viewModelScope.launch {
            repo.syncFromCloud()
            // If profile is still empty after sync, seed with Firebase Auth email
            val current = profile.value
            if (current.name.isEmpty() && current.email.isEmpty()) {
                val firebaseEmail = com.google.firebase.auth.FirebaseAuth
                    .getInstance().currentUser?.email ?: ""
                if (firebaseEmail.isNotEmpty()) {
                    updateProfile(current.copy(email = firebaseEmail))
                }
            }
        }
    }
}