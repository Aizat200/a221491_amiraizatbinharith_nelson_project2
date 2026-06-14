package my.com.a221491_amiraizatbinharith_nelson_project2.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

// ── Data class ────────────────────────────────────────────────────────────────
// ✅ FIX: All fields referenced in LeaderboardScreen are declared here:
//         displayName, topicIndex, difficulty, correct, total, score
data class LeaderboardEntry(
    val displayName: String = "",
    val topicIndex : Int    = 0,
    val difficulty : String = "",
    val correct    : Int    = 0,
    val total      : Int    = 0,
    val score      : Int    = 0,
    val timestamp  : Long   = 0L
)

// ── Repository ────────────────────────────────────────────────────────────────
object LeaderboardRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private fun col() = db.collection("leaderboard")

    /**
     * Save a quiz result to the global leaderboard collection.
     * Uses a fixed displayName for single-user project; swap for Auth UID + name later.
     */
    suspend fun saveScore(
        topicIndex : Int,
        difficulty : String,
        score      : Int,
        correct    : Int,
        total      : Int,
        displayName: String = "Amir Aizat"   // replace with Auth display name when ready
    ) {
        val entry = hashMapOf(
            "displayName" to displayName,
            "topicIndex"  to topicIndex,
            "difficulty"  to difficulty,
            "score"       to score,
            "correct"     to correct,
            "total"       to total,
            "timestamp"   to System.currentTimeMillis()
        )
        // Each submission gets its own document (auto-ID)
        runCatching { col().add(entry).await() }
    }

    /**
     * Fetch top 50 scores ordered by score descending.
     * Returns an empty list silently if offline or on error.
     */
    suspend fun getTopScores(limit: Long = 50): List<LeaderboardEntry> =
        runCatching {
            col()
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    LeaderboardEntry(
                        displayName = doc.getString("displayName") ?: return@mapNotNull null,
                        topicIndex  = doc.getLong("topicIndex")?.toInt()  ?: 0,
                        difficulty  = doc.getString("difficulty")          ?: "",
                        correct     = doc.getLong("correct")?.toInt()     ?: 0,
                        total       = doc.getLong("total")?.toInt()       ?: 0,
                        score       = doc.getLong("score")?.toInt()       ?: 0,
                        timestamp   = doc.getLong("timestamp")            ?: 0L
                    )
                }
        }.getOrElse { emptyList() }
}