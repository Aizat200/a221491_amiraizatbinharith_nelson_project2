package my.com.a221491_amiraizatbinharith_nelson_project2.db

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * FirestoreSync
 * ─────────────────────────────────────────────────────────────────────────────
 * Two separate Firestore paths:
 *
 *   users/{uid}/profile/data             ← user profile (ProfileViewModel)
 *   users/{uid}/progress/{topicIndex}    ← topic chapter progress (TopicScreen)
 *   users/{uid}/quiz_games/{autoId}      ← quiz game results (QuizScreen)
 *
 * NOTE: photoBase64 is intentionally excluded from Firestore sync.
 *       Photos are stored on-device only (Room/SQLite). This is by design.
 */
object FirestoreSync {

    private const val TAG = "FirestoreSync"

    private val db   by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private fun uid(): String? = auth.currentUser?.uid

    // ── Profile ───────────────────────────────────────────────────────────────

    suspend fun pushProfile(entity: UserProfileEntity) {
        val currentUid = uid()
        if (currentUid == null) {
            Log.w(TAG, "pushProfile: no logged-in user — skipping")
            return
        }
        try {
            val data = mapOf(
                "name"       to entity.name,
                "email"      to entity.email,
                "university" to entity.university,
                "course"     to entity.course,
                "bio"        to entity.bio,
                "level"      to entity.level,
                "joinDate"   to entity.joinDate
                // photoBase64 intentionally excluded — local only (Option A)
            )
            db.collection("users").document(currentUid)
                .collection("profile").document("data")
                .set(data, SetOptions.merge()).await()
            Log.d(TAG, "pushProfile: saved for uid=$currentUid name=${entity.name}")
        } catch (e: Exception) {
            Log.e(TAG, "pushProfile: FAILED — ${e.message}", e)
            throw e   // rethrow so EcoRepository.runCatching{} records the failure
        }
    }

    suspend fun pullProfile(): UserProfileEntity? {
        val currentUid = uid()
        if (currentUid == null) {
            Log.w(TAG, "pullProfile: no logged-in user — returning null")
            return null
        }
        return try {
            val snap = db.collection("users").document(currentUid)
                .collection("profile").document("data").get().await()
            if (!snap.exists()) {
                Log.d(TAG, "pullProfile: no profile doc in Firestore for uid=$currentUid")
                return null
            }
            val entity = UserProfileEntity(
                id         = 1,
                name       = snap.getString("name")       ?: "",
                email      = snap.getString("email")      ?: "",
                university = snap.getString("university") ?: "",
                course     = snap.getString("course")     ?: "",
                bio        = snap.getString("bio")        ?: "",
                level      = snap.getString("level")      ?: "Beginner",
                joinDate   = snap.getString("joinDate")   ?: ""
                // photoBase64 not in Firestore — Room keeps the local value
            )
            Log.d(TAG, "pullProfile: loaded for uid=$currentUid name=${entity.name}")
            entity
        } catch (e: Exception) {
            Log.e(TAG, "pullProfile: FAILED — ${e.message}", e)
            throw e
        }
    }

    // ── Topic Chapter Progress (TopicScreen only) ─────────────────────────────

    suspend fun pushProgress(entity: TopicProgressEntity) {
        val currentUid = uid()
        if (currentUid == null) {
            Log.w(TAG, "pushProgress: no logged-in user — skipping")
            return
        }
        try {
            val data = mapOf(
                "topicIndex"        to entity.topicIndex,
                "chaptersCompleted" to entity.chaptersCompleted,
                "totalChapters"     to entity.totalChapters,
                "quizScoresRaw"     to entity.quizScoresRaw
            )
            db.collection("users").document(currentUid)
                .collection("progress").document(entity.topicIndex.toString())
                .set(data, SetOptions.merge()).await()
            Log.d(TAG, "pushProgress: topic=${entity.topicIndex} saved")
        } catch (e: Exception) {
            Log.e(TAG, "pushProgress: FAILED — ${e.message}", e)
            throw e
        }
    }

    suspend fun pullAllProgress(): List<TopicProgressEntity> {
        val currentUid = uid()
        if (currentUid == null) {
            Log.w(TAG, "pullAllProgress: no logged-in user — returning empty")
            return emptyList()
        }
        return try {
            val snap = db.collection("users").document(currentUid)
                .collection("progress").get().await()
            val results = snap.documents.mapNotNull { doc ->
                val idx = doc.getLong("topicIndex")?.toInt() ?: return@mapNotNull null
                TopicProgressEntity(
                    topicIndex        = idx,
                    chaptersCompleted = doc.getLong("chaptersCompleted")?.toInt() ?: 0,
                    totalChapters     = doc.getLong("totalChapters")?.toInt()     ?: 4,
                    quizScoresRaw     = doc.getString("quizScoresRaw") ?: "null,null,null,null"
                )
            }
            Log.d(TAG, "pullAllProgress: loaded ${results.size} topics")
            results
        } catch (e: Exception) {
            Log.e(TAG, "pullAllProgress: FAILED — ${e.message}", e)
            throw e
        }
    }

    // ── Quiz Game Results (QuizScreen only) ───────────────────────────────────

    suspend fun pushQuizGame(entity: QuizGameEntity) {
        val currentUid = uid()
        if (currentUid == null) {
            Log.w(TAG, "pushQuizGame: no logged-in user — skipping")
            return
        }
        try {
            val data = mapOf(
                "topicIndex" to entity.topicIndex,
                "difficulty" to entity.difficulty,
                "score"      to entity.score,
                "correct"    to entity.correct,
                "total"      to entity.total,
                "timestamp"  to entity.timestamp
            )
            db.collection("users").document(currentUid)
                .collection("quiz_games")
                .add(data).await()
            Log.d(TAG, "pushQuizGame: score=${entity.score} saved")
        } catch (e: Exception) {
            Log.e(TAG, "pushQuizGame: FAILED — ${e.message}", e)
            throw e
        }
    }

    suspend fun pullAllQuizGames(): List<QuizGameEntity> {
        val currentUid = uid()
        if (currentUid == null) {
            Log.w(TAG, "pullAllQuizGames: no logged-in user — returning empty")
            return emptyList()
        }
        return try {
            val snap = db.collection("users").document(currentUid)
                .collection("quiz_games").get().await()
            val results = snap.documents.mapNotNull { doc ->
                QuizGameEntity(
                    topicIndex = doc.getLong("topicIndex")?.toInt() ?: return@mapNotNull null,
                    difficulty = doc.getString("difficulty")         ?: return@mapNotNull null,
                    score      = doc.getLong("score")?.toInt()      ?: 0,
                    correct    = doc.getLong("correct")?.toInt()    ?: 0,
                    total      = doc.getLong("total")?.toInt()      ?: 0,
                    timestamp  = doc.getLong("timestamp")           ?: 0L
                )
            }
            Log.d(TAG, "pullAllQuizGames: loaded ${results.size} games")
            results
        } catch (e: Exception) {
            Log.e(TAG, "pullAllQuizGames: FAILED — ${e.message}", e)
            throw e
        }
    }
}