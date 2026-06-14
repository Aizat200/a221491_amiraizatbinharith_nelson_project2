package my.com.a221491_amiraizatbinharith_nelson_project2.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores the user's profile data locally using Room.
 * Single-row table — always id = 1.
 *
 * photoBase64: JPEG photo encoded as Base64 string.
 *              Empty string = no photo set.
 *              Stored on-device only — no Firebase Storage needed.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id          : Int    = 1,
    val name        : String = "",
    val email       : String = "",
    val university  : String = "",
    val course      : String = "",
    val bio         : String = "",
    val level       : String = "Beginner",
    val joinDate    : String = "",
    val photoBase64 : String = ""   // ← NEW: local photo storage, no Firebase needed
)