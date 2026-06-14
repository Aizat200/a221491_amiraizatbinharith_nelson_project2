package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import my.com.a221491_amiraizatbinharith_nelson_project2.data.EcoViewModel
import my.com.a221491_amiraizatbinharith_nelson_project2.data.TopicState

@Composable
fun ProgressScreen(
    modifier    : Modifier     = Modifier,
    vm          : EcoViewModel = viewModel(),
    onOpenTopic : (Int) -> Unit = {}
) {
    val topics by vm.topics.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ProgressHeader()
        OverallProgressCard(topics = topics)
        Spacer(modifier = Modifier.height(20.dp))
        TopicBreakdownSection(
            topics      = topics,
            onOpenTopic = onOpenTopic
        )
        Spacer(modifier = Modifier.height(20.dp))
        AchievementsSection(topics = topics)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun ProgressHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 28.dp)
    ) {
        Column {
            Text(text = "EcoEducation", color = GreenSurface,
                fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Your Learning\nProgress", color = GreenSurface,
                fontSize = 22.sp, fontWeight = FontWeight.Medium, lineHeight = 30.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Track how far you've come on SDG 7",
                color = GreenLight, fontSize = 13.sp)
        }
    }
}

// ── Overall Progress Card ─────────────────────────────────────────────────────
@Composable
private fun OverallProgressCard(topics: List<TopicState>) {
    val completedCount  = topics.count { it.completed }
    val inProgressCount = topics.count { !it.completed && it.progress > 0f }
    val notStartedCount = topics.count { it.progress == 0f }
    val overallProgress = if (topics.isEmpty()) 0f
    else topics.map { it.progress }.average().toFloat()

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (started) overallProgress else 0f,
        animationSpec = tween(900),
        label         = "overallProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(StatsPanelBg)
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .padding(20.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Overall Progress", fontSize = 16.sp,
                        fontWeight = FontWeight.Medium, color = TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = "$completedCount of ${topics.size} topics completed",
                        fontSize = 13.sp, color = TextSecondary
                    )
                }
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(GreenSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${(overallProgress * 100).toInt()}%",
                        fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GreenPrimary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress  = { animatedProgress.coerceIn(0f, 1f) },
                modifier  = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(99.dp)),
                color      = GreenPrimary,
                trackColor = BorderColor,
                strokeCap  = StrokeCap.Round
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniStatChip("✅", "Completed",   "$completedCount",  Modifier.weight(1f))
                MiniStatChip("🔄", "In Progress", "$inProgressCount", Modifier.weight(1f))
                MiniStatChip("🔒", "Not Started", "$notStartedCount", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MiniStatChip(
    emoji   : String,
    label   : String,
    value   : String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceBg)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(text = emoji,  fontSize = 18.sp)
        Text(text = value,  fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = label,  fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.Center)
    }
}

// ── Topic Breakdown ───────────────────────────────────────────────────────────
@Composable
private fun TopicBreakdownSection(
    topics     : List<TopicState>,
    onOpenTopic: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text         = "TOPIC BREAKDOWN",
            fontSize     = 11.sp,
            fontWeight   = FontWeight.Medium,
            color        = TextSecondary,
            letterSpacing = 1.sp,
            modifier     = Modifier.padding(bottom = 12.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            topics.forEachIndexed { idx, state ->
                val meta = ALL_TOPICS.getOrNull(idx)
                TopicProgressDetailCard(
                    emoji       = meta?.emoji    ?: "🌱",
                    title       = meta?.title    ?: "Topic $idx",
                    subtitle    = meta?.subtitle ?: "",
                    iconBg      = meta?.iconBg   ?: GreenSurface,
                    state       = state,
                    onOpenTopic = { onOpenTopic(idx) }
                )
            }
        }
    }
}

@Composable
private fun TopicProgressDetailCard(
    emoji      : String,
    title      : String,
    subtitle   : String,
    iconBg     : Color,
    state      : TopicState,
    onOpenTopic: () -> Unit
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (started) state.progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(700),
        label         = "topicProg_$title"
    )

    val statusColor = when {
        state.completed     -> GreenPrimary
        state.progress > 0f -> Color(0xFFF59E0B)
        else                -> BorderColor
    }
    val statusLabel = when {
        state.completed     -> "Completed"
        state.progress > 0f -> "In Progress"
        else                -> "Not Started"
    }
    val statusBg = when {
        state.completed     -> GreenSurface
        state.progress > 0f -> AmberSurface
        else                -> SurfaceBg
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(16.dp)
    ) {
        // Top row
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) { Text(text = emoji, fontSize = 22.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title,    fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(statusBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = statusLabel, fontSize = 11.sp,
                    fontWeight = FontWeight.Medium, color = statusColor)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Chapter dots
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            repeat(state.totalChapters) { chIdx ->
                val score    = state.quizScores.getOrNull(chIdx)
                val dotColor = when (score) {
                    true  -> GreenPrimary
                    false -> Color(0xFFF59E0B)
                    null  -> BorderColor
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(dotColor)
                )
            }
            Spacer(Modifier.width(6.dp))
            Text(
                text     = "${state.chaptersCompleted}/${state.totalChapters} ch",
                fontSize = 11.sp,
                color    = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(10.dp))

        // Progress bar + %
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LinearProgressIndicator(
                progress  = { animatedProgress },
                modifier  = Modifier.weight(1f).height(7.dp).clip(RoundedCornerShape(99.dp)),
                color      = statusColor,
                trackColor = BorderColor,
                strokeCap  = StrokeCap.Round
            )
            Text(
                text       = "${(state.progress * 100).toInt()}%",
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold,
                color      = statusColor,
                modifier   = Modifier.width(38.dp),
                textAlign  = TextAlign.End
            )
        }

        Spacer(Modifier.height(10.dp))

        // Status line
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector        = if (state.completed) Icons.Default.CheckCircle
                else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint               = if (state.completed) GreenPrimary else BorderColor,
                modifier           = Modifier.size(16.dp)
            )
            Text(
                text     = when {
                    state.completed     -> "All chapters complete!"
                    state.progress > 0f -> "Keep going — ${state.totalChapters - state.chaptersCompleted} chapter(s) left"
                    else                -> "Start this topic to begin"
                },
                fontSize = 12.sp,
                color    = TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Go to topic button ────────────────────────────────────────────────
        Button(
            onClick  = onOpenTopic,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(10.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = if (state.completed) GreenLight else GreenPrimary,
                contentColor   = Color.White
            )
        ) {
            Text(
                text = when {
                    state.completed     -> "Review Topic"
                    state.progress > 0f -> "Continue Learning"
                    else                -> "Start Topic"
                },
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                modifier = Modifier.size(14.dp))
        }
    }
}

// ── Achievements Section ──────────────────────────────────────────────────────
private data class Achievement(
    val emoji      : String,
    val title      : String,
    val description: String,
    val unlocked   : Boolean
)

@Composable
private fun AchievementsSection(topics: List<TopicState>) {
    val solar   = topics.getOrNull(0)
    val wind    = topics.getOrNull(1)
    val hydro   = topics.getOrNull(2)
    val biomass = topics.getOrNull(3)

    val achievements = listOf(
        Achievement("⚡", "First Spark",    "Complete your first chapter",  unlocked = topics.any { it.chaptersCompleted >= 1 }),
        Achievement("☀️", "Solar Scholar", "Complete all Solar chapters",   unlocked = solar?.completed == true),
        Achievement("💨", "Wind Chaser",    "Complete all Wind chapters",   unlocked = wind?.completed == true),
        Achievement("💧", "Hydro Half-Way", "Reach 50% on Hydropower",     unlocked = (hydro?.progress ?: 0f) >= 0.5f),
        Achievement("🌿", "Green Explorer", "Start Biomass Energy",         unlocked = (biomass?.progress ?: 0f) > 0f),
        Achievement("🏆", "Eco Champion",   "Complete all 4 topics",       unlocked = topics.all { it.completed }),
    )

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text          = "ACHIEVEMENTS",
            fontSize      = 11.sp,
            fontWeight    = FontWeight.Medium,
            color         = TextSecondary,
            letterSpacing = 1.sp,
            modifier      = Modifier.padding(bottom = 12.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(CardBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            achievements.forEachIndexed { i, ach ->
                AchievementRow(ach)
                if (i < achievements.lastIndex)
                    HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun AchievementRow(achievement: Achievement) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (achievement.unlocked) AmberSurface else SurfaceBg),
            contentAlignment = Alignment.Center
        ) {
            if (achievement.unlocked) {
                Text(text = achievement.emoji, fontSize = 18.sp)
            } else {
                Icon(Icons.Default.Lock, contentDescription = "Locked",
                    tint = BorderColor, modifier = Modifier.size(18.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = achievement.title, fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (achievement.unlocked) TextPrimary else TextSecondary
            )
            Text(text = achievement.description, fontSize = 12.sp, color = TextSecondary)
        }

        if (achievement.unlocked) {
            Icon(Icons.Default.EmojiEvents, contentDescription = "Unlocked",
                tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProgressScreenPreview() {
    MaterialTheme { ProgressScreen() }
}