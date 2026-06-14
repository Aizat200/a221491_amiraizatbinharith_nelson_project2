package my.com.a221491_amiraizatbinharith_nelson_project2.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.com.a221491_amiraizatbinharith_nelson_project2.db.EcoDatabase
import my.com.a221491_amiraizatbinharith_nelson_project2.db.EcoRepository
import my.com.a221491_amiraizatbinharith_nelson_project2.db.QuizGameEntity
import my.com.a221491_amiraizatbinharith_nelson_project2.db.TopicBestScore
import my.com.a221491_amiraizatbinharith_nelson_project2.db.TopicProgressEntity
import my.com.a221491_amiraizatbinharith_nelson_project2.db.toQuizScores
import my.com.a221491_amiraizatbinharith_nelson_project2.db.toRaw

// ─────────────────────────────────────────────────────────────────────────────
// TopicState — UI model for topic CHAPTER progress only
// Used by: HomeScreen, ProgressScreen, TopicScreen
// NOT used for QuizScreen game stats
// ─────────────────────────────────────────────────────────────────────────────
data class TopicState(
    val topicIndex       : Int,
    val chaptersCompleted: Int            = 0,
    val totalChapters    : Int            = 4,
    val quizScores       : List<Boolean?> = List(4) { null }   // per-chapter pass/fail
) {
    /** Chapter completion ratio — used on Home & Progress screens. */
    val progress: Float
        get() = if (totalChapters == 0) 0f
        else chaptersCompleted.toFloat() / totalChapters.toFloat()

    val completed: Boolean get() = chaptersCompleted >= totalChapters
}

// ─────────────────────────────────────────────────────────────────────────────
// QuizGameStats — UI model for QuizScreen lobby stats
// Derived from QuizGameEntity rows, NOT from TopicState
// ─────────────────────────────────────────────────────────────────────────────
data class QuizGameStats(
    /** Total correct answers across all quiz game sessions. */
    val totalCorrect : Int                 = 0,
    /** Total questions answered across all quiz game sessions. */
    val totalAnswered: Int                 = 0,
    /** Best score per topic — list indexed by topicIndex. */
    val bestPerTopic : List<TopicBestScore> = emptyList()
) {
    /** Overall accuracy percentage (0–100). */
    val accuracyPct: Int
        get() = if (totalAnswered > 0) totalCorrect * 100 / totalAnswered else 0
}

// ── Mapping helpers ───────────────────────────────────────────────────────────

private fun TopicProgressEntity.toUiState() = TopicState(
    topicIndex        = topicIndex,
    chaptersCompleted = chaptersCompleted,
    totalChapters     = totalChapters,
    quizScores        = quizScoresRaw.toQuizScores()
)

private fun TopicState.toEntity() = TopicProgressEntity(
    topicIndex        = topicIndex,
    chaptersCompleted = chaptersCompleted,
    totalChapters     = totalChapters,
    quizScoresRaw     = quizScores.toRaw()
)

// ─────────────────────────────────────────────────────────────────────────────
// EcoViewModel
// ─────────────────────────────────────────────────────────────────────────────
class EcoViewModel(application: Application) : AndroidViewModel(application) {

    private val db   = EcoDatabase.getInstance(application)
    private val repo = EcoRepository(
        progressDao = db.topicProgressDao(),
        profileDao  = db.userProfileDao(),
        quizGameDao = db.quizGameDao()          // ← wire up new DAO
    )

    // ── Topic Chapter Progress ────────────────────────────────────────────────
    // Used by: HomeScreen (progress bars), ProgressScreen, TopicScreen
    // NOT used by: QuizScreen game lobby

    val topics: StateFlow<List<TopicState>> = repo.progressFlow
        .map { entities ->
            (0 until 4).map { idx ->
                entities.firstOrNull { it.topicIndex == idx }?.toUiState()
                    ?: TopicState(topicIndex = idx)
            }
        }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = List(4) { TopicState(topicIndex = it) }
        )

    // ── Quiz Game Stats ───────────────────────────────────────────────────────
    // Used by: QuizScreen lobby ONLY
    // HomeScreen and ProgressScreen do NOT use this

    val quizGameStats: StateFlow<QuizGameStats> = combine(
        repo.quizTotalCorrectFlow,
        repo.quizTotalAnsweredFlow,
        repo.quizBestPerTopicFlow
    ) { correct, answered, bestPerTopic ->
        QuizGameStats(
            totalCorrect  = correct,
            totalAnswered = answered,
            bestPerTopic  = bestPerTopic
        )
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuizGameStats()
    )

    init {
        viewModelScope.launch {
            repo.seedIfEmpty()
            repo.syncFromCloud()
        }
    }

    // ── Actions: Topic Chapter Progress ──────────────────────────────────────

    /**
     * Called when user answers a chapter quiz inside TopicScreen.
     * Updates chapter completion only — does NOT affect QuizScreen stats.
     */
    fun markChapterComplete(
        topicIndex       : Int,
        chapterIndex     : Int,
        answeredCorrectly: Boolean
    ) {
        viewModelScope.launch {
            val current = topics.value.getOrNull(topicIndex) ?: return@launch
            val scores  = current.quizScores.toMutableList()
                .also { it[chapterIndex] = answeredCorrectly }
            val completed = scores.count { it == true }
            val updated = current.copy(chaptersCompleted = completed, quizScores = scores)
            repo.saveProgress(updated.toEntity())
        }
    }

    /** Reset a wrong chapter quiz answer so the user can retry. */
    fun resetChapterQuiz(topicIndex: Int, chapterIndex: Int) {
        viewModelScope.launch {
            val current = topics.value.getOrNull(topicIndex) ?: return@launch
            val scores  = current.quizScores.toMutableList()
                .also { it[chapterIndex] = null }
            val completed = scores.count { it == true }
            val updated = current.copy(chaptersCompleted = completed, quizScores = scores)
            repo.saveProgress(updated.toEntity())
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch { repo.resetAllProgress() }
    }

    // ── Actions: Quiz Game Results ────────────────────────────────────────────

    /**
     * Called when a QuizScreen game session ends (Results screen shown).
     * Saves the game result to its own table — does NOT touch topic progress.
     */
    fun saveQuizGameResult(
        topicIndex: Int,
        difficulty: String,
        score     : Int,
        correct   : Int,
        total     : Int
    ) {
        viewModelScope.launch {
            repo.saveQuizGame(
                QuizGameEntity(
                    topicIndex = topicIndex,
                    difficulty = difficulty,
                    score      = score,
                    correct    = correct,
                    total      = total
                )
            )
        }
    }
}