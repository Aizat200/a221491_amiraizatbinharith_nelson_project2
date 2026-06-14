package my.com.a221491_amiraizatbinharith_nelson_project2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Version history:
 *   1 → initial (TopicProgressEntity, UserProfileEntity)
 *   2 → added QuizGameEntity (quiz game results, separate from topic progress)
 *   3 → added photoBase64 column to UserProfileEntity (local photo storage)
 */
@Database(
    entities  = [
        TopicProgressEntity::class,
        UserProfileEntity::class,
        QuizGameEntity::class
    ],
    version   = 3,           // ← bumped from 2
    exportSchema = false
)
abstract class EcoDatabase : RoomDatabase() {

    abstract fun topicProgressDao(): TopicProgressDao
    abstract fun userProfileDao()  : UserProfileDao
    abstract fun quizGameDao()     : QuizGameDao

    companion object {
        @Volatile
        private var INSTANCE: EcoDatabase? = null


        private val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_profile ADD COLUMN photoBase64 TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): EcoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EcoDatabase::class.java,
                    "eco_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}