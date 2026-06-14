package my.com.a221491_amiraizatbinharith_nelson_project2.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicProgressDao {

    /** Observe all topic rows as a live Flow. */
    @Query("SELECT * FROM topic_progress ORDER BY topicIndex ASC")
    fun observeAll(): Flow<List<TopicProgressEntity>>

    /** Insert or fully replace a topic row. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TopicProgressEntity)

    /** Insert or replace a list of topic rows (used for seed / restore). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<TopicProgressEntity>)

    /** Get a single topic by index (one-shot, not a Flow). */
    @Query("SELECT * FROM topic_progress WHERE topicIndex = :index LIMIT 1")
    suspend fun getByIndex(index: Int): TopicProgressEntity?

    /** Delete everything (used when resetting progress). */
    @Query("DELETE FROM topic_progress")
    suspend fun deleteAll()
}
