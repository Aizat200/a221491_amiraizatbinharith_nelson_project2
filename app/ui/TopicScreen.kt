package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import my.com.a221491_amiraizatbinharith_nelson_project2.data.EcoViewModel

// ── Extra colour tokens ───────────────────────────────────────────────────────
private val AmberMain   = Color(0xFFF59E0B)
private val CorrectBg   = Color(0xFFE1F5EE)
private val CorrectText = Color(0xFF0F6E56)
private val WrongBg     = Color(0xFFFCEBEB)
private val WrongText   = Color(0xFFA32D2D)

// ─────────────────────────────────────────────────────────────────────────────
// ROOT — TopicScreen (works for any topic index 0-3)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TopicScreen(
    topicIndex: Int,
    onBack    : () -> Unit,
    vm        : EcoViewModel = viewModel()
) {
    val topics     by vm.topics.collectAsStateWithLifecycle()
    val topicMeta   = ALL_TOPICS.getOrNull(topicIndex) ?: return
    val topicState  = topics.getOrNull(topicIndex) ?: return
    val chapters    = topicMeta.chapters

    var currentChapter by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        TopicTopBar(
            title      = topicMeta.title,
            emoji      = topicMeta.emoji,
            chapterIdx = currentChapter,
            totalChap  = chapters.size,
            progress   = topicState.progress,
            onBack     = onBack
        )

        AnimatedContent(
            targetState   = currentChapter,
            transitionSpec = {
                if (targetState > initialState)
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                else
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
            },
            label    = "chapterSlide",
            modifier = Modifier.weight(1f)
        ) { chIdx ->
            ChapterPage(
                chapter   = chapters[chIdx],
                quizScore = topicState.quizScores.getOrNull(chIdx),
                onAnswer  = { correct ->
                    vm.markChapterComplete(
                        topicIndex        = topicIndex,
                        chapterIndex      = chIdx,
                        answeredCorrectly = correct
                    )
                },
                onRetry = {
                    vm.resetChapterQuiz(
                        topicIndex   = topicIndex,
                        chapterIndex = chIdx
                    )
                }
            )
        }

        ChapterNavBar(
            currentChapter = currentChapter,
            totalChapters  = chapters.size,
            quizScores     = topicState.quizScores,
            onPrev = { if (currentChapter > 0) currentChapter-- },
            onNext = {
                // Only advance when the current chapter quiz was answered correctly
                val passed = topicState.quizScores.getOrNull(currentChapter) == true
                if (passed && currentChapter < chapters.size - 1) currentChapter++
            }
        )
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@Composable
private fun TopicTopBar(
    title     : String,
    emoji     : String,
    chapterIdx: Int,
    totalChap : Int,
    progress  : Float,
    onBack    : () -> Unit
) {
    val animProg by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(600),
        label         = "topBarProg"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(top = 48.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = GreenSurface)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("$emoji  $title", fontSize = 17.sp,
                    fontWeight = FontWeight.Medium, color = GreenSurface
                )
                Text("Chapter ${chapterIdx + 1} of $totalChap",
                    fontSize = 12.sp, color = GreenLight
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("${(progress * 100).toInt()}%",
                    fontSize = 13.sp, fontWeight = FontWeight.Medium, color = GreenSurface
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress  = { animProg.coerceIn(0f, 1f) },
            modifier  = Modifier.fillMaxWidth().height(4.dp),
            color      = GreenSurface,
            trackColor = Color.White.copy(alpha = 0.25f),
            strokeCap  = StrokeCap.Square
        )
    }
}

// ── Chapter page ──────────────────────────────────────────────────────────────
@Composable
private fun ChapterPage(
    chapter  : ChapterData,
    quizScore: Boolean?,
    onAnswer : (Boolean) -> Unit,
    onRetry  : () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp).height(40.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(GreenPrimary)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Chapter ${chapter.number}", fontSize = 11.sp,
                    fontWeight = FontWeight.Medium, color = GreenPrimary, letterSpacing = 0.5.sp)
                Text(chapter.title, fontSize = 20.sp, fontWeight = FontWeight.Medium,
                    color = TextPrimary, lineHeight = 26.sp)
            }
        }
        Text(chapter.subtitle, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp))

        chapter.sections.forEach { section ->
            SectionBlock(section)
            Spacer(Modifier.height(20.dp))
        }

        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
        Spacer(Modifier.height(20.dp))

        QuizBlock(quiz = chapter.quiz, answered = quizScore, onAnswer = onAnswer, onRetry = onRetry)
        Spacer(Modifier.height(32.dp))
    }
}

// ── Section block ─────────────────────────────────────────────────────────────
@Composable
private fun SectionBlock(section: SectionData) {
    Column {
        Text(section.heading, fontSize = 15.sp, fontWeight = FontWeight.Medium,
            color = TextPrimary, modifier = Modifier.padding(bottom = 8.dp))
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
        Spacer(Modifier.height(10.dp))
        Text(section.body, fontSize = 13.sp, color = TextSecondary, lineHeight = 21.sp)

        section.flowSteps?.forEach { row ->
            Spacer(Modifier.height(12.dp))
            FlowDiagram(steps = row)
        }
        section.callout?.let {
            Spacer(Modifier.height(12.dp))
            CalloutBox(it)
        }
        section.highlights?.let {
            Spacer(Modifier.height(12.dp))
            HighlightGrid(it)
        }
        section.stats?.let {
            Spacer(Modifier.height(12.dp))
            StatsGrid(it)
        }
    }
}

@Composable
private fun FlowDiagram(steps: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { i, step ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GreenSurface)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(step, fontSize = 11.sp, color = GreenPrimary,
                        fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
                        lineHeight = 15.sp)
                }
                if (i < steps.lastIndex) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                        tint = GreenLight, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
private fun CalloutBox(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AmberSurface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("💡", fontSize = 16.sp)
        Text(text, fontSize = 12.sp, color = Color(0xFF78350F), lineHeight = 18.sp)
    }
}

@Composable
private fun HighlightGrid(items: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (label, desc) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceBg)
                            .padding(10.dp)
                    ) {
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                            color = TextPrimary, lineHeight = 17.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatsGrid(items: List<Pair<String, String>>) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (value, label) ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GreenSurface)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = GreenPrimary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(2.dp))
                Text(label, fontSize = 10.sp, color = TextSecondary,
                    textAlign = TextAlign.Center, lineHeight = 13.sp)
            }
        }
    }
}

// ── Quiz block ────────────────────────────────────────────────────────────────
@Composable
private fun QuizBlock(
    quiz    : QuizData,
    answered: Boolean?,
    onAnswer: (Boolean) -> Unit,
    onRetry : () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(AmberSurface),
                contentAlignment = Alignment.Center
            ) { Text("🧠", fontSize = 14.sp) }
            Text("Chapter Quiz", fontSize = 14.sp,
                fontWeight = FontWeight.Medium, color = TextPrimary
            )
            if (answered != null) {
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(if (answered) CorrectBg else WrongBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(if (answered) "Correct ✓" else "Incorrect",
                        fontSize = 11.sp, color = if (answered) CorrectText else WrongText,
                        fontWeight = FontWeight.Medium)
                }
            }
        }

        Text(quiz.question, fontSize = 14.sp, fontWeight = FontWeight.Medium,
            color = TextPrimary, lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 14.dp))

        quiz.options.forEachIndexed { idx, option ->
            val isCorrect  = idx == quiz.correctIndex
            val isSelected = answered != null && isCorrect

            val bgCol     = when {
                answered == null -> SurfaceBg
                isCorrect        -> CorrectBg
                else             -> WrongBg
            }
            val borderCol = when {
                answered == null -> BorderColor
                isCorrect        -> GreenPrimary
                else             -> Color(0xFFEF4444)
            }
            val textCol   = when {
                answered == null -> TextPrimary
                isCorrect        -> CorrectText
                else             -> WrongText
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgCol)
                    .border(0.5.dp, borderCol, RoundedCornerShape(10.dp))
                    .then(
                        if (answered == null)
                            Modifier.clickable { onAnswer(isCorrect) }
                        else Modifier
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .border(0.5.dp, borderCol, CircleShape)
                        .background(if (answered != null && isCorrect) GreenPrimary else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (answered != null && isCorrect) {
                        Icon(Icons.Default.CheckCircle, null,
                            tint = Color.White, modifier = Modifier.size(14.dp))
                    } else {
                        Text(('A' + idx).toString(), fontSize = 10.sp,
                            color = if (answered == null) TextSecondary else borderCol,
                            fontWeight = FontWeight.Medium)
                    }
                }
                Text(option, fontSize = 13.sp, color = textCol,
                    lineHeight = 19.sp, modifier = Modifier.weight(1f))
            }
        }

        if (answered != null) {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (answered) CorrectBg else WrongBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    if (answered) "Great work! Your progress has been saved. ✓"
                    else          "Not quite — the correct answer is highlighted. Review the section above.",
                    fontSize = 12.sp,
                    color    = if (answered) CorrectText else WrongText,
                    lineHeight = 18.sp
                )
            }

            // Show Try Again button only when answered incorrectly
            if (!answered) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick  = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(9.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Try Again", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "You must answer correctly to proceed to the next chapter.",
                    fontSize = 11.sp,
                    color = WrongText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ── Bottom chapter navigation bar ─────────────────────────────────────────────
@Composable
private fun ChapterNavBar(
    currentChapter: Int,
    totalChapters : Int,
    quizScores    : List<Boolean?>,
    onPrev        : () -> Unit,
    onNext        : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp)
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Dot progress strip
        Row(
            modifier              = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            repeat(totalChapters) { idx ->
                val isDone   = quizScores.getOrNull(idx) == true
                val isWrong  = quizScores.getOrNull(idx) == false
                val isActive = idx == currentChapter
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isActive) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isDone   -> GreenPrimary
                                isWrong  -> WrongText
                                isActive -> AmberMain
                                else     -> BorderColor
                            }
                        )
                )
            }
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick  = onPrev,
                enabled  = currentChapter > 0,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (currentChapter > 0) GreenSurface else SurfaceBg)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous",
                    tint = if (currentChapter > 0) GreenPrimary else BorderColor,
                    modifier = Modifier.size(18.dp))
            }

            Row(
                modifier              = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(totalChapters) { idx ->
                    val isDone        = quizScores.getOrNull(idx) == true
                    val isActive      = idx == currentChapter
                    val lastAnswered  = quizScores.indexOfLast { it != null }
                    val isLocked      = idx > lastAnswered + 1 && !isActive

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(when {
                                isActive -> GreenPrimary
                                isDone   -> GreenSurface
                                else     -> SurfaceBg
                            })
                            .border(0.5.dp, when {
                                isActive -> GreenPrimary
                                isDone   -> GreenLight
                                else     -> BorderColor
                            }, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isDone && !isActive -> Icon(Icons.Default.CheckCircle,
                                "Done", tint = GreenPrimary, modifier = Modifier.size(16.dp))
                            isLocked -> Icon(Icons.Default.Lock,
                                "Locked", tint = BorderColor, modifier = Modifier.size(14.dp))
                            else -> Text("${idx + 1}", fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isActive) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            val canGoNext = currentChapter < totalChapters - 1 &&
                    quizScores.getOrNull(currentChapter) == true
            IconButton(
                onClick  = onNext,
                enabled  = canGoNext,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (canGoNext) GreenPrimary else SurfaceBg)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next",
                    tint = if (canGoNext) Color.White else BorderColor,
                    modifier = Modifier.size(18.dp))
            }
        }
    }
}