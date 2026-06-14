package my.com.a221491_amiraizatbinharith_nelson_project2.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * QuizGameEntity
 * ─────────────────────────────────────────────────────────────────────────────
 * Stores results from the QuizScreen game (the arcade-style game with
 * difficulty levels and lives). This is SEPARATE from TopicProgressEntity,
 * which tracks chapter-by-chapter completion inside TopicScreen.
 *
 * Home screen and ProgressScreen only ever read TopicProgressEntity.
 * QuizScreen lobby stats read QuizGameEntity.
 */
@Entity(tableName = "quiz_game_results")
data class QuizGameEntity(
    @PrimaryKey(autoGenerate = true)
    val id         : Int    = 0,
    val topicIndex : Int,
    val difficulty : String,   // "Easy" | "Medium" | "Hard"
    val score      : Int,
    val correct    : Int,
    val total      : Int,
    val timestamp  : Long   = System.currentTimeMillis()
)
