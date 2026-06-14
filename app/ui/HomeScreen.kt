package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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

// ─────────────────────────────────────────────────────────────────────────────
// Colour palette
// ─────────────────────────────────────────────────────────────────────────────
val GreenPrimary  = Color(0xFF1D9E75)
val GreenLight    = Color(0xFF5DCAA5)
val GreenSurface  = Color(0xFFE1F5EE)
val AmberSurface  = Color(0xFFFAEEDA)
val BlueSurface   = Color(0xFFE6F1FB)
val RedSurface    = Color(0xFFFCEBEB)
val TextPrimary   = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B7280)
val SurfaceBg     = Color(0xFFF5F5F5)
val CardBg        = Color(0xFFFFFFFF)
val BorderColor   = Color(0xFFE5E7EB)
val StatsPanelBg  = Color(0xFF17825F)

// Kept for backward compatibility
data class EnergyTopic(
    val emoji      : String,
    val title      : String,
    val subtitle   : String,
    val description: String,
    val iconBg     : Color,
    val completed  : Boolean,
    val progress   : Float = if (completed) 1f else 0f
)

// ─────────────────────────────────────────────────────────────────────────────
// Home tab content
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HomeContent(
    modifier         : Modifier     = Modifier,
    vm               : EcoViewModel = viewModel(),
    onOpenTopic      : (Int) -> Unit = {},
    onOpenQuiz       : () -> Unit = {},
    onOpenCalculator : () -> Unit = {}
) {
    val topics by vm.topics.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSection()
        StatsSection(topics)
        Spacer(Modifier.height(20.dp))
        TopicList(topics = topics, onOpenTopic = onOpenTopic)
        Spacer(Modifier.height(16.dp))
        QuizBanner(onOpenQuiz = onOpenQuiz)
        Spacer(Modifier.height(12.dp))
        CalculatorBanner(onOpenCalculator = onOpenCalculator)
        Spacer(Modifier.height(24.dp))
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("EcoEducation", color = GreenSurface, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(GreenLight))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Good morning 👋", color = GreenLight, fontSize = 13.sp,
                fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Renewable Energy\nLearning",
                color = GreenSurface, fontSize = 22.sp,
                fontWeight = FontWeight.Medium, lineHeight = 30.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Star, null, tint = GreenLight, modifier = Modifier.size(16.dp))
                Text("SDG 7 · Clean & Affordable Energy", color = GreenSurface, fontSize = 13.sp)
            }
        }
    }
}

// ── Stats Section ─────────────────────────────────────────────────────────────
@Composable
fun StatsSection(topics: List<TopicState> = emptyList()) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(StatsPanelBg)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        StatsRow(topics = topics)
    }
}

@Composable
fun StatsRow(topics: List<TopicState> = emptyList()) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopicsStatCard(topics = topics, modifier = Modifier.weight(1.0f).fillMaxHeight())
        TopicProgressCard(topics = topics, modifier = Modifier.weight(1.7f))
    }
}

@Composable
fun TopicsStatCard(topics: List<TopicState> = emptyList(), modifier: Modifier = Modifier) {
    val completedCount = topics.count { it.completed }

    Column(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AmberSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📚", fontSize = 16.sp)
        }

        Text(
            text = "${topics.size}",
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            lineHeight = 28.sp
        )
        Text(text = "Total topics", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .background(GreenSurface)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "$completedCount completed",
                fontSize = 12.sp,
                color = GreenPrimary
            )
        }
    }
}

@Composable
fun TopicProgressCard(topics: List<TopicState> = emptyList(), modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .heightIn(min = 180.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp)
    ) {
        Text(
            text = "📊 Progress",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )

        Spacer(Modifier.height(12.dp))

        val labels = listOf("Solar", "Wind", "Hydro", "Biomass")
        val emojis = listOf("☀️", "💨", "💧", "🌿")

        topics.forEachIndexed { idx, state ->
            TopicProgressRow(
                emoji    = emojis.getOrElse(idx) { "🌱" },
                label    = labels.getOrElse(idx) { "Topic" },
                progress = state.progress
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun TopicProgressRow(emoji: String, label: String, progress: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = emoji, fontSize = 14.sp)

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.width(70.dp),
            maxLines = 1
        )

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(99.dp)),
            color      = GreenPrimary,
            trackColor = BorderColor,
            strokeCap  = StrokeCap.Round
        )

        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
    }
}

// ── Topic list ────────────────────────────────────────────────────────────────
@Composable
fun TopicList(
    topics     : List<TopicState>,
    onOpenTopic: (Int) -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("ENERGY TOPICS", fontSize = 11.sp, fontWeight = FontWeight.Medium,
            color = TextSecondary, letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            topics.forEachIndexed { idx, state ->
                val meta = ALL_TOPICS.getOrNull(idx) ?: return@forEachIndexed
                TopicCard(
                    emoji       = meta.emoji,
                    title       = meta.title,
                    subtitle    = meta.subtitle,
                    description = meta.chapters.first().sections.first().body
                        .take(180).trimEnd() + "…",
                    iconBg      = meta.iconBg,
                    progress    = state.progress,
                    completed   = state.completed,
                    onOpen      = { onOpenTopic(idx) }
                )
            }
        }
    }
}

// ── Topic card (expandable) ───────────────────────────────────────────────────
@Composable
fun TopicCard(
    emoji      : String,
    title      : String,
    subtitle   : String,
    description: String,
    iconBg     : Color,
    progress   : Float,
    completed  : Boolean,
    onOpen     : () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessLow
                )
            ),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
                    contentAlignment = Alignment.Center
                ) { Text(emoji, fontSize = 20.sp) }

                Column(Modifier.weight(1f)) {
                    Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Text(subtitle, fontSize = 13.sp, color = TextSecondary)
                }

                Icon(
                    imageVector        = if (completed) Icons.Default.CheckCircle
                    else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint               = if (completed) GreenPrimary else BorderColor,
                    modifier           = Modifier.size(20.dp)
                )
                Icon(
                    imageVector        = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint               = TextSecondary,
                    modifier           = Modifier.size(18.dp)
                )
            }

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                Spacer(Modifier.height(10.dp))

                Text(description, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress   = { progress.coerceIn(0f, 1f) },
                        modifier   = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(99.dp)),
                        color      = GreenPrimary,
                        trackColor = BorderColor,
                        strokeCap  = StrokeCap.Round
                    )
                    Text(
                        "${(progress * 100).toInt()}% complete",
                        fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick  = onOpen,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor   = Color.White
                    )
                ) {
                    Text(
                        text = when {
                            completed     -> "Review topic"
                            progress > 0f -> "Continue learning →"
                            else          -> "Start topic →"
                        },
                        fontSize = 13.sp, fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ── Quiz banner ───────────────────────────────────────────────────────────────
@Composable
fun QuizBanner(
    onOpenQuiz: () -> Unit = {}     // ← default keeps standalone previews happy
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(GreenPrimary)
            .clickable { onOpenQuiz() }   // ← FIX: whole banner is now tappable
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Ready to test yourself?", color = GreenLight, fontSize = 13.sp)
            Text("Take a quiz", color = GreenSurface, fontSize = 14.sp,
                fontWeight = FontWeight.Medium)
        }
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Go to quiz",
                tint = GreenSurface, modifier = Modifier.size(14.dp))
        }
    }
}

// ── Calculator banner ─────────────────────────────────────────────────────────
@Composable
fun CalculatorBanner(
    onOpenCalculator: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A6B8A))
            .clickable { onOpenCalculator() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text("Carbon Footprint", color = Color(0xFFB2E0F5), fontSize = 13.sp)
                Text("Open Calculator", color = Color.White, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium)
            }
        }
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Go to calculator",
                tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme { HomeContent() }
}