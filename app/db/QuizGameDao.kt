package my.com.a221491_amiraizatbinharith_nelson_project2.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizGameDao {

    /** Insert a new quiz game result. */
    @Insert
    suspend fun insert(entity: QuizGameEntity)

    /**
     * Observe best score per topic (highest score for each topicIndex).
     * Used by QuizScreen lobby to show per-topic best result.
     */
    @Query("""
        SELECT topicIndex,
               MAX(score)   AS bestScore,
               MAX(correct) AS bestCorrect,
               COUNT(*)     AS playCount
        FROM quiz_game_results
        GROUP BY topicIndex
        ORDER BY topicIndex ASC
    """)
    fun observeBestPerTopic(): Flow<List<TopicBestScore>>

    /**
     * Total correct answers across ALL game sessions (for the lobby header stat).
     * Returns 0 if no rows exist.
     */
    @Query("SELECT COALESCE(SUM(correct), 0) FROM quiz_game_results")
    fun observeTotalCorrect(): Flow<Int>

    /**
     * Total questions answered across ALL game sessions (for accuracy stat).
     * Returns 0 if no rows exist.
     */
    @Query("SELECT COALESCE(SUM(total), 0) FROM quiz_game_results")
    fun observeTotalAnswered(): Flow<Int>

    /** Delete all game results (for reset). */
    @Query("DELETE FROM quiz_game_results")
    suspend fun deleteAll()
}

/**
 * Lightweight projection returned by [QuizGameDao.observeBestPerTopic].
 * Not a Room entity — just a query result holder.
 */
data class TopicBestScore(
    val topicIndex : Int,
    val bestScore  : Int,
    val bestCorrect: Int,
    val playCount  : Int
)
