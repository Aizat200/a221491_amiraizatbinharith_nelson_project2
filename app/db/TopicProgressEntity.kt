package my.com.a221491_amiraizatbinharith_nelson_project2.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores per-topic quiz progress locally using Room.
 * One row per topic (topicIndex is the primary key).
 *
 * quizScores is stored as a comma-separated string e.g. "true,false,null,null"
 * because Room cannot store List<Boolean?> directly.
 */
@Entity(tableName = "topic_progress")
data class TopicProgressEntity(
    @PrimaryKey
    val topicIndex       : Int,
    val chaptersCompleted: Int    = 0,
    val totalChapters    : Int    = 4,
    val quizScoresRaw    : String = "null,null,null,null"   // serialised List<Boolean?>
)

// ── Serialisation helpers ─────────────────────────────────────────────────────

fun List<Boolean?>.toRaw(): String =
    joinToString(",") { it?.toString() ?: "null" }

fun String.toQuizScores(): List<Boolean?> =
    split(",").map { token ->
        when (token.trim()) {
            "true"  -> true
            "false" -> false
            else    -> null
        }
    }
