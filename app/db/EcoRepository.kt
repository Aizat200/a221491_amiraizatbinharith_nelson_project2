package my.com.a221491_amiraizatbinharith_nelson_project2.db

import kotlinx.coroutines.flow.Flow

/**
 * EcoRepository
 * ─────────────────────────────────────────────────────────────────────────────
 * Single source of truth for all data operations.
 *
 * Two fully separate data paths:
 *
 *   TOPIC PROGRESS  → TopicProgressDao + Firestore users/{uid}/progress/
 *                     Written by: TopicScreen (chapter quizzes)
 *                     Read by:    HomeScreen, ProgressScreen
 *
 *   QUIZ GAME       → QuizGameDao + Firestore users/{uid}/quiz_games/
 *                     Written by: QuizScreen (game results)
 *                     Read by:    QuizScreen lobby stats only
 */
class EcoRepository(
    private val progressDao : TopicProgressDao,
    private val profileDao  : UserProfileDao,
    private val quizGameDao : QuizGameDao
) {

    // ── Observe (Room → Flow) ─────────────────────────────────────────────────

    /** Topic chapter progress — for HomeScreen & ProgressScreen. */
    val progressFlow: Flow<List<TopicProgressEntity>> = progressDao.observeAll()

    /** Profile flow. */
    val profileFlow : Flow<UserProfileEntity?>        = profileDao.observe()

    /**
     * Best quiz game score per topic — for QuizScreen lobby only.
     * NOT used on HomeScreen or ProgressScreen.
     */
    val quizBestPerTopicFlow: Flow<List<TopicBestScore>> = quizGameDao.observeBestPerTopic()

    /** Total correct answers from all quiz games — for QuizScreen lobby header. */
    val quizTotalCorrectFlow: Flow<Int> = quizGameDao.observeTotalCorrect()

    /** Total questions answered from all quiz games — for QuizScreen lobby accuracy. */
    val quizTotalAnsweredFlow: Flow<Int> = quizGameDao.observeTotalAnswered()

    // ── Topic Chapter Progress ────────────────────────────────────────────────

    /**
     * Save chapter progress locally and push to Firestore.
     * Called ONLY from TopicScreen when a chapter quiz is answered.
     */
    suspend fun saveProgress(entity: TopicProgressEntity) {
        progressDao.upsert(entity)
        runCatching { FirestoreSync.pushProgress(entity) }
    }

    suspend fun seedIfEmpty(totalTopics: Int = 4) {
        val hasData = (0 until totalTopics).any { progressDao.getByIndex(it) != null }
        if (!hasData) {
            val defaults = (0 until totalTopics).map { idx ->
                TopicProgressEntity(topicIndex = idx)
            }
            progressDao.upsertAll(defaults)
        }
    }

    suspend fun resetAllProgress(totalTopics: Int = 4) {
        progressDao.deleteAll()
        seedIfEmpty(totalTopics)
        (0 until totalTopics).forEach { idx ->
            runCatching { FirestoreSync.pushProgress(TopicProgressEntity(topicIndex = idx)) }
        }
    }

    // ── Quiz Game Results ─────────────────────────────────────────────────────

    /**
     * Save a completed quiz game result locally and push to Firestore.
     * Called ONLY from QuizScreen when a game session ends (Results screen).
     * Does NOT affect topic chapter progress at all.
     */
    suspend fun saveQuizGame(entity: QuizGameEntity) {
        quizGameDao.insert(entity)
        runCatching { FirestoreSync.pushQuizGame(entity) }
    }

    // ── User Profile ──────────────────────────────────────────────────────────

    /** One-shot getter — used by ProfileViewModel to read current photoBase64 before saving. */
    suspend fun getProfile(): UserProfileEntity? = profileDao.get()

    suspend fun saveProfile(entity: UserProfileEntity) {
        profileDao.upsert(entity)
        runCatching { FirestoreSync.pushProfile(entity) }
    }

    // ── Account switching ─────────────────────────────────────────────────────

    /**
     * Wipes ALL local Room data (profile, topic progress, quiz games) and
     * resets it to fresh defaults.
     *
     * WHY THIS IS NEEDED:
     * Room's user_profile table is a single row (id = 1) shared by whoever is
     * currently logged in on this device. Without calling this on logout and
     * before login/signup, a different account will keep showing the
     * PREVIOUS account's name, university, course, bio, level, and profile
     * photo — because syncFromCloud() only OVERWRITES fields that exist in
     * Firestore. A brand-new account has no Firestore profile document yet,
     * so pullProfile() returns null and the stale row is never cleared.
     *
     * Call this:
     *   - On logout (AuthViewModel.logout())
     *   - Before signUp() / login() for a different account
     */
    suspend fun clearLocalUserData(totalTopics: Int = 4) {
        // Reset profile to defaults (empty strings / "Beginner")
        profileDao.upsert(UserProfileEntity())

        // Reset topic chapter progress
        progressDao.deleteAll()
        (0 until totalTopics).forEach { idx ->
            progressDao.upsert(TopicProgressEntity(topicIndex = idx))
        }

        // Reset quiz game history
        quizGameDao.deleteAll()
    }

    // ── Cloud Sync ────────────────────────────────────────────────────────────

    /**
     * Pull latest data from Firestore and overwrite local Room data.
     * Called once on app start inside viewModelScope.
     */
    suspend fun syncFromCloud() {
        // Topic chapter progress
        runCatching {
            val cloudProgress = FirestoreSync.pullAllProgress()
            if (cloudProgress.isNotEmpty()) progressDao.upsertAll(cloudProgress)
        }

        // Quiz game results
        runCatching {
            val cloudGames = FirestoreSync.pullAllQuizGames()
            if (cloudGames.isNotEmpty()) {
                // Clear local and re-insert from cloud to avoid duplicates
                quizGameDao.deleteAll()
                cloudGames.forEach { quizGameDao.insert(it) }
            }
        }

        // Profile
        runCatching {
            val cloudProfile = FirestoreSync.pullProfile()
            if (cloudProfile != null) profileDao.upsert(cloudProfile)
        }
    }
}