package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle  // ✅ FIX 1: direct import, no custom extension needed
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import my.com.a221491_amiraizatbinharith_nelson_project2.db.LeaderboardEntry
import my.com.a221491_amiraizatbinharith_nelson_project2.db.LeaderboardRepository

// ── ViewModel ─────────────────────────────────────────────────────────────────
class LeaderboardViewModel : ViewModel() {

    private val _entries = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val entries: StateFlow<List<LeaderboardEntry>> = _entries

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            _entries.value = LeaderboardRepository.getTopScores()
            _loading.value = false
        }
    }
}

// ── UI ────────────────────────────────────────────────────────────────────────
@Composable
fun LeaderboardScreen(
    lbVm     : LeaderboardViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    // ✅ FIX 2: collectAsStateWithLifecycle() is now imported directly above —
    //           the broken private extension function at the bottom has been removed.
    //           StateFlow<T>.collectAsStateWithLifecycle() is available from
    //           androidx.lifecycle.compose and works without any wrapper.
    val entries by lbVm.entries.collectAsStateWithLifecycle()
    val loading by lbVm.loading.collectAsStateWithLifecycle()

    val topicNames = listOf("Solar Energy", "Wind Energy", "Hydro Energy", "Biomass Energy")
    val gold   = Color(0xFFF59E0B)
    val silver = Color(0xFF94A3B8)
    val bronze = Color(0xFFCD7F32)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 24.dp)
        ) {
            IconButton(
                onClick  = onDismiss,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🏆", fontSize = 32.sp)
                Text(
                    "Global Leaderboard", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold, color = Color.White
                )
                Text("Top quiz scores", fontSize = 13.sp, color = GreenLight)
            }
            IconButton(
                onClick  = { lbVm.refresh() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.Refresh, null, tint = Color.White)
            }
        }

        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }

            // ✅ FIX 3: entries is List<LeaderboardEntry> — call .isEmpty() on List, not ambiguous
            entries.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎮", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No scores yet!", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = TextPrimary
                        )
                        Text(
                            "Be the first to complete a quiz!",
                            fontSize = 14.sp, color = TextSecondary
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ✅ FIX 4: itemsIndexed lambda is inside LazyListScope — correct context
                    itemsIndexed(
                        items = entries  // explicit typed list — resolves type-inference ambiguity
                    ) { idx, entry ->
                        val rankColor = when (idx) {
                            0    -> gold
                            1    -> silver
                            2    -> bronze
                            else -> TextSecondary
                        }
                        val rankEmoji = when (idx) {
                            0    -> "🥇"
                            1    -> "🥈"
                            2    -> "🥉"
                            else -> "${idx + 1}"
                        }

                        Card(
                            shape  = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (idx < 3) rankColor.copy(alpha = 0.08f) else CardBg
                            ),
                            elevation = CardDefaults.cardElevation(if (idx < 3) 3.dp else 1.dp),
                            modifier  = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier              = Modifier.padding(14.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Rank badge
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(rankColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        rankEmoji,
                                        fontSize   = if (idx < 3) 18.sp else 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = rankColor
                                    )
                                }

                                // Player info
                                // ✅ FIX 5: entry.displayName / topicIndex / difficulty /
                                //           correct / total all exist on LeaderboardEntry
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        entry.displayName,
                                        fontSize   = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color      = TextPrimary
                                    )
                                    Text(
                                        "${topicNames.getOrElse(entry.topicIndex) { "Topic ${entry.topicIndex + 1}" }}" +
                                                " · ${entry.difficulty}",
                                        fontSize = 12.sp,
                                        color    = TextSecondary
                                    )
                                    Text(
                                        "${entry.correct}/${entry.total} correct",
                                        fontSize = 11.sp,
                                        color    = TextSecondary
                                    )
                                }

                                // Score
                                // ✅ FIX 6: entry.score exists on LeaderboardEntry
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "⭐ ${entry.score}",
                                        fontSize   = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color      = rankColor
                                    )
                                    Text("pts", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ✅ FIX 7: Removed the broken private extension function that was here:
//
//   @Composable
//   private fun <T> StateFlow<T>.collectAsStateWithLifecycle() =
//       androidx.lifecycle.compose.collectAsStateWithLifecycle(this)
//
// This caused ALL the errors:
//   - The function called collectAsStateWithLifecycle() on `this` (a StateFlow) but
//     passed it as a receiver AND an argument simultaneously → type ambiguity
//   - The lambda body tried to call a @Composable inside a non-@Composable lambda
//   - The `compareTo` errors were Kotlin trying to resolve overloads on the broken call
//
// The correct import at the top of the file already provides this function directly
// from androidx.lifecycle.compose — no wrapper needed.