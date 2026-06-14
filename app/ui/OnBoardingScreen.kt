package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import my.com.a221491_amiraizatbinharith_nelson_project2.db.UserProfileEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OnboardingScreen(
    profileVm: ProfileViewModel = viewModel(),
    onFinish : () -> Unit = {}
) {
    var name       by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var course     by remember { mutableStateOf("") }
    var bio        by remember { mutableStateOf("") }

    // Derive initials for avatar preview
    val initials = name.split(" ").take(2)
        .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
        .ifEmpty { "?" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GreenPrimary.copy(alpha = 0.15f), SurfaceBg)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ── Header ──────────────────────────────────────────────────────
            Text("👋", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Welcome to EcoEducation!",
                fontSize    = 22.sp,
                fontWeight  = FontWeight.Bold,
                color       = GreenPrimary,
                textAlign   = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Let's set up your profile to get started.",
                fontSize  = 13.sp,
                color     = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // ── Avatar preview ───────────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = initials,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }
            Spacer(Modifier.height(4.dp))
            Text("Updates as you type", fontSize = 11.sp, color = TextSecondary)

            Spacer(Modifier.height(24.dp))

            // ── Form card ────────────────────────────────────────────────────
            Card(
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text("PERSONAL INFO", fontSize = 11.sp, color = TextSecondary,
                        letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(12.dp))

                    OnboardField(
                        value         = name,
                        onValueChange = { name = it },
                        label         = "Full Name",
                        icon          = Icons.Default.Person
                    )

                    Spacer(Modifier.height(20.dp))
                    Text("ACADEMIC INFO", fontSize = 11.sp, color = TextSecondary,
                        letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(12.dp))

                    OnboardField(
                        value         = university,
                        onValueChange = { university = it },
                        label         = "University",
                        icon          = Icons.Default.School
                    )
                    Spacer(Modifier.height(12.dp))
                    OnboardField(
                        value         = course,
                        onValueChange = { course = it },
                        label         = "Course / Programme",
                        icon          = Icons.Default.Book
                    )

                    Spacer(Modifier.height(20.dp))
                    Text("BIO", fontSize = 11.sp, color = TextSecondary,
                        letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = bio,
                        onValueChange = { bio = it },
                        label         = { Text("Tell us about yourself (optional)") },
                        modifier      = Modifier.fillMaxWidth(),
                        minLines      = 3,
                        maxLines      = 5,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            focusedLabelColor  = GreenPrimary
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    // Level chip — read-only, locked to Beginner for new users
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(GreenSurface)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Star, null,
                            tint     = GreenPrimary,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Learning Level", fontSize = 11.sp, color = TextSecondary)
                            Text("🌱 Beginner",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color      = GreenPrimary)
                        }
                        Spacer(Modifier.weight(1f))
                        Text("Auto-set", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Save & Continue button ────────────────────────────────────────
            Button(
                onClick = {
                    val joinDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        .format(Date())
                    profileVm.updateProfile(
                        UserProfile(
                            name       = name.trim(),
                            // FIX: was hardcoded "" with a comment saying it'd be
                            // filled later — nothing ever did. Pull it from the
                            // logged-in FirebaseAuth user instead, since that's
                            // the email used at sign-up.
                            email      = FirebaseAuth.getInstance().currentUser?.email ?: "",
                            university = university.trim(),
                            course     = course.trim(),
                            bio        = bio.trim(),
                            level      = "Beginner",
                            joinDate   = joinDate
                        )
                    )
                    onFinish()
                },
                enabled  = name.isNotBlank(),         // at minimum a name is required
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save & Get Started", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            // Skip option
            TextButton(onClick = onFinish) {
                Text("Skip for now", color = TextSecondary, fontSize = 13.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Reusable field ────────────────────────────────────────────────────────────
@Composable
private fun OnboardField(
    value        : String,
    onValueChange: (String) -> Unit,
    label        : String,
    icon         : androidx.compose.ui.graphics.vector.ImageVector
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        leadingIcon   = {
            Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
        },
        singleLine = true,
        modifier   = Modifier.fillMaxWidth(),
        shape      = RoundedCornerShape(10.dp),
        colors     = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenPrimary,
            focusedLabelColor  = GreenPrimary
        )
    )
}