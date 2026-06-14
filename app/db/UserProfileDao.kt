package my.com.a221491_amiraizatbinharith_nelson_project2.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    /** Observe the single profile row as a live Flow. */
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observe(): Flow<UserProfileEntity?>

    /** Insert or fully replace the profile row. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserProfileEntity)

    /** Get profile once (one-shot). */
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun get(): UserProfileEntity?
}
