package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import my.com.a221491_amiraizatbinharith_nelson_project2.data.EcoViewModel
import my.com.a221491_amiraizatbinharith_nelson_project2.data.QuizGameStats

// ── Game colour tokens ─────────────────────────────────────────────────────────
private val GameCorrect    = Color(0xFF22C55E)
private val GameWrong      = Color(0xFFEF4444)
private val GameCorrectBg  = Color(0xFFDCFCE7)
private val GameWrongBg    = Color(0xFFFEE2E2)
private val GameCorrectText= Color(0xFF166534)
private val GameWrongText  = Color(0xFF991B1B)
private val GameGold       = Color(0xFFF59E0B)
private val GamePurple     = Color(0xFF8B5CF6)

// ── Difficulty levels ──────────────────────────────────────────────────────────
enum class QuizDifficulty(
    val label      : String,
    val emoji      : String,
    val description: String,
    val lives      : Int,
    val pointsPerQ : Int,
    val bonusStreak: Int,
    val color      : Color
) {
    EASY   ("Easy",   "🌱", "Unlimited retries · 5 pts each",   99, 5,  0,  GameCorrect),
    MEDIUM ("Medium", "⚡", "3 lives · 10 pts · streak bonus",  3,  10, 5,  GameGold),
    HARD   ("Hard",   "🔥", "1 life · 20 pts · high risk",      1,  20, 10, GameWrong)
}

// ── Game states ────────────────────────────────────────────────────────────────
private sealed interface GameState {
    object Lobby     : GameState
    data class TopicSelect(val difficulty: QuizDifficulty) : GameState
    data class Playing(
        val topicIndex : Int,
        val difficulty : QuizDifficulty,
        val questions  : List<QuizData>,
        val current    : Int   = 0,
        val score      : Int   = 0,
        val lives      : Int   = 0,
        val streak     : Int   = 0,
        val answered   : Boolean? = null
    ) : GameState
    data class Results(
        val topicIndex : Int,
        val difficulty : QuizDifficulty,
        val score      : Int,
        val total      : Int,
        val correct    : Int
    ) : GameState
}

private fun questionsForTopic(topicIndex: Int): List<QuizData> =
    ALL_TOPICS.getOrNull(topicIndex)?.chapters?.map { it.quiz } ?: emptyList()

// ═════════════════════════════════════════════════════════════════════════════
// QuizScreen
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun QuizScreen(
    vm         : EcoViewModel = viewModel(),
    onOpenTopic: (Int) -> Unit = {}
) {
    var gameState by remember { mutableStateOf<GameState>(GameState.Lobby) }

    AnimatedContent(
        targetState    = gameState,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
        label          = "gameStateAnim"
    ) { state ->
        when (state) {
            is GameState.Lobby -> LobbyScreen(
                vm           = vm,
                onSelectDiff = { diff -> gameState = GameState.TopicSelect(diff) }
            )
            is GameState.TopicSelect -> TopicPickerScreen(
                difficulty    = state.difficulty,
                vm            = vm,
                onBack        = { gameState = GameState.Lobby },
                onSelectTopic = { idx ->
                    gameState = GameState.Playing(
                        topicIndex = idx,
                        difficulty = state.difficulty,
                        questions  = questionsForTopic(idx),
                        lives      = state.difficulty.lives
                    )
                }
            )
            is GameState.Playing -> PlayingScreen(
                state    = state,
                onAnswer = { correct ->
                    val s = state
                    if (correct) {
                        val streak   = s.streak + 1
                        val bonus    = if (streak > 0 && streak % 3 == 0) s.difficulty.bonusStreak else 0
                        val newScore = s.score + s.difficulty.pointsPerQ + bonus
                        val nextIdx  = s.current + 1
                        if (nextIdx >= s.questions.size) {
                            gameState = buildResults(s, newScore, completed = true)
                        } else {
                            gameState = s.copy(current = nextIdx, score = newScore,
                                streak = streak, answered = null)
                        }
                    } else {
                        val newLives = s.lives - 1
                        if (newLives <= 0 && s.difficulty != QuizDifficulty.EASY) {
                            gameState = buildResults(s, s.score, completed = false)
                        } else {
                            gameState = s.copy(lives = maxOf(newLives, 0), streak = 0, answered = false)
                        }
                    }
                },
                onRetry = { gameState = state.copy(answered = null) },
                onQuit  = { gameState = GameState.Lobby }
            )
            is GameState.Results -> ResultsScreen(
                state       = state,
                vm          = vm,
                onPlayAgain = { gameState = GameState.TopicSelect(state.difficulty) },
                onHome      = { gameState = GameState.Lobby },
                onReadTopic = { onOpenTopic(state.topicIndex) }
            )
        }
    }
}

private fun buildResults(s: GameState.Playing, finalScore: Int, completed: Boolean) =
    GameState.Results(
        topicIndex = s.topicIndex,
        difficulty = s.difficulty,
        score      = finalScore,
        total      = s.questions.size,
        correct    = if (completed) s.questions.size else s.current
    )

// ═════════════════════════════════════════════════════════════════════════════
// 1. LOBBY SCREEN
// Uses vm.quizGameStats — separate from topic chapter progress
// ═════════════════════════════════════════════════════════════════════════════
@Composable
private fun LobbyScreen(
    vm           : EcoViewModel,
    onSelectDiff : (QuizDifficulty) -> Unit
) {
    // ✅ Uses quizGameStats (quiz game history) NOT vm.topics (chapter progress)
    val stats by vm.quizGameStats.collectAsStateWithLifecycle()
    val totalQuestions = ALL_TOPICS.sumOf { it.chapters.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(GreenPrimary, Color(0xFF157A5A))))
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 32.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎮", fontSize = 28.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("EcoQuiz", color = GreenSurface, fontSize = 22.sp,
                            fontWeight = FontWeight.Bold)
                        Text("Test your knowledge!", color = GreenLight, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(20.dp))

                // Stats from quiz game history (not topic chapter progress)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LobbyStatItem("🏆", "${stats.totalCorrect}", "Correct")
                    VerticalDivider(modifier = Modifier.height(36.dp),
                        color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
                    LobbyStatItem("📝", "$totalQuestions", "Questions")
                    VerticalDivider(modifier = Modifier.height(36.dp),
                        color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
                    LobbyStatItem("🎯", "${stats.accuracyPct}%", "Accuracy")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("CHOOSE DIFFICULTY", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            color = TextSecondary, letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuizDifficulty.entries.forEach { diff ->
                DifficultyCard(diff = diff, onClick = { onSelectDiff(diff) })
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("AVAILABLE TOPICS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            color = TextSecondary, letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp))

        // Topic overview — uses quiz game best scores per topic
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ALL_TOPICS.forEach { meta ->
                val total   = meta.chapters.size
                // ✅ Best score from quiz game history (not chapter progress)
                val best    = stats.bestPerTopic.firstOrNull { it.topicIndex == meta.index }
                val correct = best?.bestCorrect ?: 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardBg)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(meta.iconBg),
                        contentAlignment = Alignment.Center
                    ) { Text(meta.emoji, fontSize = 18.sp) }
                    Column(Modifier.weight(1f)) {
                        Text(meta.title, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = TextPrimary)
                        Text("$total questions", fontSize = 11.sp, color = TextSecondary)
                    }
                    if (best != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (correct == total) GameCorrectBg else GreenSurface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Best $correct/$total ✓",
                                fontSize = 11.sp,
                                color = if (correct == total) GameCorrectText else GreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(GreenSurface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Not played", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun LobbyStatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = GreenLight, fontSize = 11.sp)
    }
}

@Composable
private fun DifficultyCard(diff: QuizDifficulty, onClick: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue  = 1f,
        targetValue   = if (diff == QuizDifficulty.HARD) 1.02f else 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "diffPulse"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pulse)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape)
                .background(diff.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) { Text(diff.emoji, fontSize = 22.sp) }
        Column(Modifier.weight(1f)) {
            Text(diff.label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(diff.description, fontSize = 12.sp, color = TextSecondary)
        }
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape)
                .background(diff.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = diff.color,
                modifier = Modifier.size(18.dp))
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// 2. TOPIC PICKER SCREEN
// Shows quiz game best scores per topic (not chapter progress)
// ═════════════════════════════════════════════════════════════════════════════
@Composable
private fun TopicPickerScreen(
    difficulty   : QuizDifficulty,
    vm           : EcoViewModel,
    onBack       : () -> Unit,
    onSelectTopic: (Int) -> Unit
) {
    // ✅ Uses quizGameStats for best-scores, NOT vm.topics
    val stats by vm.quizGameStats.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 16.dp)
                .padding(top = 48.dp, bottom = 24.dp)
        ) {
            Column {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = GreenSurface)
                }
                Spacer(Modifier.height(8.dp))
                Text("${difficulty.emoji}  ${difficulty.label} Mode",
                    color = GreenSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Select a topic to quiz yourself on",
                    color = GreenLight, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (difficulty != QuizDifficulty.EASY)
                        Text("❤️ ${difficulty.lives} lives", color = GreenSurface, fontSize = 12.sp)
                    else
                        Text("♾️ Unlimited retries", color = GreenSurface, fontSize = 12.sp)
                    Text("⭐ ${difficulty.pointsPerQ} pts/Q", color = GreenSurface, fontSize = 12.sp)
                    if (difficulty.bonusStreak > 0)
                        Text("🔥 +${difficulty.bonusStreak} streak", color = GreenSurface, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("PICK A TOPIC", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            color = TextSecondary, letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ALL_TOPICS.forEach { meta ->
                val total   = meta.chapters.size
                val best    = stats.bestPerTopic.firstOrNull { it.topicIndex == meta.index }
                val correct = best?.bestCorrect ?: 0
                val bestPct = if (total > 0) (correct * 100 / total) else 0

                TopicPickCard(
                    meta    = meta,
                    correct = correct,
                    total   = total,
                    bestPct = bestPct,
                    diff    = difficulty,
                    onClick = { onSelectTopic(meta.index) }
                )
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun TopicPickCard(
    meta   : TopicMeta,
    correct: Int,
    total  : Int,
    bestPct: Int,
    diff   : QuizDifficulty,
    onClick: () -> Unit
) {
    val maxScore = total * diff.pointsPerQ
    Card(
        modifier  = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)).clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(meta.iconBg),
                    contentAlignment = Alignment.Center
                ) { Text(meta.emoji, fontSize = 26.sp) }
                Column(Modifier.weight(1f)) {
                    Text(meta.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(meta.subtitle, fontSize = 12.sp, color = TextSecondary)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoPill("$total Qs", GreenSurface, GreenPrimary)
                        InfoPill("Max $maxScore pts", AmberSurface, GameGold)
                        if (correct > 0)
                            InfoPill("Best $bestPct%", GameCorrectBg, GameCorrectText)
                    }
                }
                Icon(Icons.Default.PlayArrow, contentDescription = "Start",
                    tint = GreenPrimary, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(12.dp))
            val prog = if (total > 0) correct.toFloat() / total else 0f
            LinearProgressIndicator(
                progress   = { prog.coerceIn(0f, 1f) },
                modifier   = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)),
                color      = GreenPrimary,
                trackColor = BorderColor,
                strokeCap  = StrokeCap.Round
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (correct > 0) "$correct / $total best score" else "No games played yet",
                fontSize = 11.sp, color = TextSecondary
            )
        }
    }
}

@Composable
private fun InfoPill(text: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, fontSize = 10.sp, color = textColor, fontWeight = FontWeight.SemiBold)
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// 3. PLAYING SCREEN (unchanged in logic)
// ═════════════════════════════════════════════════════════════════════════════
@Composable
private fun PlayingScreen(
    state   : GameState.Playing,
    onAnswer: (Boolean) -> Unit,
    onRetry : () -> Unit,
    onQuit  : () -> Unit
) {
    val meta     = ALL_TOPICS.getOrNull(state.topicIndex) ?: return
    val question = state.questions.getOrNull(state.current) ?: return
    val progress = (state.current.toFloat() / state.questions.size).coerceIn(0f, 1f)

    val scoreScale by animateFloatAsState(
        targetValue   = if (state.answered == true) 1.3f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label         = "scoreScale"
    )

    Column(modifier = Modifier.fillMaxSize().background(SurfaceBg)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 16.dp)
                .padding(top = 48.dp, bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onQuit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, "Quit", tint = GreenSurface,
                        modifier = Modifier.size(18.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(meta.emoji, fontSize = 16.sp)
                    Text(meta.title, color = GreenSurface, fontSize = 13.sp,
                        fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier.scale(scoreScale).clip(RoundedCornerShape(99.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text("⭐ ${state.score}", color = GreenSurface, fontSize = 14.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LinearProgressIndicator(
                    progress   = { progress },
                    modifier   = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(99.dp)),
                    color      = GreenSurface,
                    trackColor = Color.White.copy(alpha = 0.25f),
                    strokeCap  = StrokeCap.Round
                )
                Text("${state.current + 1}/${state.questions.size}",
                    color = GreenLight, fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.difficulty != QuizDifficulty.EASY) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(state.difficulty.lives) { i ->
                            Text(if (i < state.lives) "❤️" else "🖤", fontSize = 14.sp)
                        }
                    }
                } else {
                    Text("♾️ Easy mode", color = GreenLight, fontSize = 12.sp)
                }
                if (state.streak >= 2) {
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(99.dp))
                            .background(GameGold.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🔥", fontSize = 12.sp)
                        Text("${state.streak} streak!", color = GameGold, fontSize = 12.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)).background(CardBg).padding(18.dp)
            ) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(meta.iconBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Chapter ${state.current + 1}", fontSize = 11.sp,
                        color = TextPrimary, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(12.dp))
                Text(question.question, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                    color = TextPrimary, lineHeight = 24.sp)
            }

            question.options.forEachIndexed { idx, option ->
                AnswerOption(
                    label     = ('A' + idx).toString(),
                    text      = option,
                    answered  = state.answered,
                    isCorrect = idx == question.correctIndex,
                    onClick   = { if (state.answered == null) onAnswer(idx == question.correctIndex) }
                )
            }

            AnimatedVisibility(visible = state.answered != null) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val correct = state.answered == true
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(if (correct) GameCorrectBg else GameWrongBg).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(if (correct) "🎉" else "😬", fontSize = 22.sp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (correct) "Correct! +${state.difficulty.pointsPerQ} pts"
                                else "Wrong answer!",
                                fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                color = if (correct) GameCorrectText else GameWrongText
                            )
                            if (correct && state.streak > 0 && state.streak % 3 == 0 && state.difficulty.bonusStreak > 0) {
                                Text("🔥 Streak bonus +${state.difficulty.bonusStreak}!",
                                    fontSize = 12.sp, color = GameGold, fontWeight = FontWeight.Medium)
                            }
                            if (!correct && state.difficulty != QuizDifficulty.EASY) {
                                Text(
                                    if (state.lives > 1) "❤️ ${state.lives - 1} lives remaining — try again!"
                                    else "💔 Last life — game over if wrong again!",
                                    fontSize = 12.sp, color = GameWrongText
                                )
                            }
                        }
                    }
                    if (state.answered == false) {
                        Button(
                            onClick  = onRetry,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = GameWrong)
                        ) {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Try Again", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AnswerOption(
    label    : String,
    text     : String,
    answered : Boolean?,
    isCorrect: Boolean,
    onClick  : () -> Unit
) {
    val bgColor = when {
        answered == null -> CardBg
        isCorrect        -> GameCorrectBg
        else             -> GameWrongBg
    }
    val borderColor = when {
        answered == null -> BorderColor
        isCorrect        -> GameCorrect
        else             -> GameWrong
    }
    val textColor = when {
        answered == null -> TextPrimary
        isCorrect        -> GameCorrectText
        else             -> GameWrongText
    }
    val scale by animateFloatAsState(
        targetValue   = if (answered != null && isCorrect) 1.02f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label         = "optionScale"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(if (answered == null) 2.dp else 0.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .then(if (answered == null) Modifier.clickable { onClick() } else Modifier)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape)
                .background(
                    when {
                        answered != null && isCorrect -> GameCorrect
                        answered != null              -> GameWrong.copy(alpha = 0.2f)
                        else                          -> SurfaceBg
                    }
                )
                .border(1.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (answered != null && isCorrect)
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            else if (answered != null && !isCorrect)
                Icon(Icons.Default.Close, null, tint = GameWrong, modifier = Modifier.size(14.dp))
            else
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        }
        Text(text, fontSize = 14.sp, color = textColor, lineHeight = 20.sp,
            modifier = Modifier.weight(1f))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// 4. RESULTS SCREEN
// Saves via vm.saveQuizGameResult() — does NOT touch topic chapter progress
// ═════════════════════════════════════════════════════════════════════════════
@Composable
private fun ResultsScreen(
    state      : GameState.Results,
    vm         : EcoViewModel,
    onPlayAgain: () -> Unit,
    onHome     : () -> Unit,
    onReadTopic: () -> Unit
) {
    val meta    = ALL_TOPICS.getOrNull(state.topicIndex) ?: return
    val pct     = if (state.total > 0) state.correct * 100 / state.total else 0
    val perfect = state.correct == state.total
    val passed  = pct >= 60

    // ✅ Save via ViewModel — writes to QuizGameEntity in Room + Firestore users/{uid}/quiz_games/
    //    Does NOT touch TopicProgressEntity or chapter completion at all.
    LaunchedEffect(state) {
        vm.saveQuizGameResult(
            topicIndex = state.topicIndex,
            difficulty = state.difficulty.label,
            score      = state.score,
            correct    = state.correct,
            total      = state.total
        )
    }

    val animScore by animateIntAsState(
        targetValue   = state.score,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label         = "scoreCountUp"
    )
    val rank = when {
        perfect   -> Triple("🏆", "Perfect!", GameGold)
        pct >= 80 -> Triple("🥇", "Excellent!", GreenPrimary)
        pct >= 60 -> Triple("🥈", "Good job!", GamePurple)
        else      -> Triple("📚", "Keep learning!", GameWrongText)
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(GreenPrimary, Color(0xFF157A5A))))
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp, bottom = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(rank.first, fontSize = 52.sp)
                Spacer(Modifier.height(8.dp))
                Text(rank.second, color = GreenSurface, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("${meta.emoji} ${meta.title} · ${state.difficulty.label}",
                    color = GreenLight, fontSize = 13.sp)
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 28.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⭐ $animScore", color = GreenSurface, fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold)
                        Text("points earned", color = GreenLight, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResultStatCard("✅", "${state.correct}", "Correct",
                GameCorrectBg, GameCorrectText, Modifier.weight(1f))
            ResultStatCard("❌", "${state.total - state.correct}", "Wrong",
                GameWrongBg, GameWrongText, Modifier.weight(1f))
            ResultStatCard("📊", "$pct%", "Accuracy",
                if (passed) GameCorrectBg else GameWrongBg,
                if (passed) GameCorrectText else GameWrongText,
                Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (passed) GameCorrectBg else AmberSurface).padding(16.dp)
        ) {
            Text(
                if (perfect) "🎉 Amazing! You answered every question correctly!"
                else if (passed) "👏 Well done! You passed this quiz."
                else "📖 Review the topic material and try again to improve your score!",
                fontSize = 13.sp,
                color = if (passed) GameCorrectText else Color(0xFF92400E),
                lineHeight = 20.sp
            )
        }

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick  = onPlayAgain,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Play Again", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick  = onReadTopic,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary),
                border   = BorderStroke(1.5.dp, GreenPrimary)
            ) {
                Text("${meta.emoji} Read ${meta.title}", fontSize = 15.sp,
                    fontWeight = FontWeight.Medium)
            }
            TextButton(
                onClick  = onHome,
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text("Back to Quiz Home", fontSize = 14.sp, color = TextSecondary)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ResultStatCard(
    emoji    : String,
    value    : String,
    label    : String,
    bg       : Color,
    textColor: Color,
    modifier : Modifier = Modifier
) {
    Column(
        modifier = modifier.shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)).background(CardBg).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(bg),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 14.sp) }
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenPreview() {
    MaterialTheme { QuizScreen() }
}