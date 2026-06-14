package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import my.com.a221491_amiraizatbinharith_nelson_project2.auth.AuthViewModel
import my.com.a221491_amiraizatbinharith_nelson_project2.data.EcoViewModel

@Composable
fun ProfileScreen(
    modifier     : Modifier         = Modifier,
    profileVm    : ProfileViewModel  = viewModel(),
    ecoVm        : EcoViewModel      = viewModel(),
    authVm       : AuthViewModel     = viewModel(),
    photoVm      : PhotoViewModel    = viewModel(),
    onEditProfile: () -> Unit        = {},
    onLogout     : () -> Unit        = {}
) {
    val profile       by profileVm.profile.collectAsStateWithLifecycle()
    val topics        by ecoVm.topics.collectAsStateWithLifecycle()
    val photoUrl      by photoVm.photoUrl.collectAsStateWithLifecycle()
    val photoBase64   by photoVm.photoBase64.collectAsStateWithLifecycle()
    val uploading     by photoVm.uploading.collectAsStateWithLifecycle()
    val uploadError   by photoVm.uploadError.collectAsStateWithLifecycle()
    val uploadSuccess by photoVm.uploadSuccess.collectAsStateWithLifecycle()

    // FIX: observe the cacheBuster so ProfileAvatar recomposes after every upload
    // Without this the avatar would keep showing the stale Coil-cached image.
    var cacheBuster by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(photoUrl) {
        // Each time photoUrl changes (after upload), bump the buster
        cacheBuster = System.currentTimeMillis()
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPhotoPicker  by remember { mutableStateOf(false) }

    // FIX: show a brief green banner when upload succeeds
    var showUploadBanner by remember { mutableStateOf(false) }
    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess) {
            showUploadBanner = true
            delay(2500)
            showUploadBanner = false
            photoVm.clearSuccess()
        }
    }

    val completedCount  = topics.count { it.completed }
    val averageProgress = if (topics.isEmpty()) 0f
    else topics.sumOf { it.progress.toDouble() }.toFloat() / topics.size

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon  = { Icon(Icons.Default.Logout, null, tint = Color(0xFFEF4444)) },
            title = { Text("Sign Out?", fontWeight = FontWeight.Bold) },
            text  = { Text("You will be returned to the login screen.") },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; authVm.logout(); onLogout() },
                    colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) { Text("Sign Out") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showPhotoPicker) {
        PhotoPickerSheet(photoVm = photoVm, onDismiss = { showPhotoPicker = false })
    }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        // ── Upload success banner ─────────────────────────────────────────────
        // FIX: visible feedback so user knows the photo actually saved
        if (showUploadBanner) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.CheckCircle, null,
                    tint = GreenLight, modifier = Modifier.size(18.dp))
                Text("Profile photo updated!", color = GreenSurface, fontSize = 13.sp)
            }
        }

        // ── Upload error banner ───────────────────────────────────────────────
        // FIX: visible error feedback instead of silent failure
        if (!uploadError.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEDED))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.ErrorOutline, null,
                    tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                Text(uploadError!!, color = Color(0xFFEF4444),
                    fontSize = 13.sp, modifier = Modifier.weight(1f))
                IconButton(
                    onClick  = { photoVm.clearError() },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.Close, null,
                        tint     = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 32.dp)
        ) {
            IconButton(onClick = { showLogoutDialog = true },
                modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(Icons.Default.Logout, "Sign Out",
                    tint = Color.White.copy(alpha = 0.85f))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.fillMaxWidth()
            ) {
                // ── Avatar with camera badge ──────────────────────────────────
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier.size(84.dp).shadow(8.dp, CircleShape)) {
                        val initials = profile.name.split(" ").take(2)
                            .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
                            .ifEmpty { "?" }

                        // FIX: pass cacheBuster so Coil fetches the new photo after upload
                        ProfileAvatar(
                            photoUrl    = photoUrl,
                            initials    = initials,
                            size        = 84.dp,
                            onClick     = { showPhotoPicker = true },
                            cacheBuster = cacheBuster,
                            photoBase64 = photoBase64
                        )
                    }

                    // Camera badge — shows spinner while uploading
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uploading) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(14.dp),
                                color       = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.CameraAlt, null,
                                tint     = Color.White,
                                modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    profile.name.ifEmpty { "New User" },
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.White.copy(alpha = 0.20f))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text("🌱  ${profile.level.ifEmpty { "Beginner" }}",
                        fontSize = 12.sp, color = GreenSurface)
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick  = onEditProfile,
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit Profile", fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatChipP(Modifier.weight(1f), "📚", "${topics.size}",  "Topics", AmberSurface)
            StatChipP(Modifier.weight(1f), "✅", "$completedCount", "Done",   GreenSurface)
            StatChipP(Modifier.weight(1f), "📈",
                "${(averageProgress * 100).toInt()}%", "Avg", BlueSurface)
        }
        Spacer(Modifier.height(20.dp))

        SectionLabelP("ABOUT")
        Card(
            modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(14.dp),
            colors    = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier             = Modifier.padding(16.dp),
                verticalArrangement  = Arrangement.spacedBy(14.dp)
            ) {
                ProfileRowP(Icons.Default.Email,
                    BlueSurface,  Color(0xFF3B82F6), "Email",      profile.email)
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                ProfileRowP(Icons.Default.School,
                    AmberSurface, Color(0xFFF59E0B), "University", profile.university)
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                ProfileRowP(Icons.Default.Book,
                    RedSurface,   Color(0xFFEF4444), "Course",     profile.course)
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                ProfileRowP(Icons.Default.CalendarMonth,
                    GreenSurface, GreenPrimary,      "Joined",     profile.joinDate)
            }
        }

        Spacer(Modifier.height(16.dp))
        SectionLabelP("BIO")
        Card(
            modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(14.dp),
            colors    = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Text(
                text     = profile.bio.ifEmpty { "No bio yet. Tap Edit Profile to add one." },
                fontSize = 14.sp,
                color    = TextSecondary,
                lineHeight = 22.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick  = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(50.dp),
            shape    = RoundedCornerShape(12.dp),
            border   = BorderStroke(1.dp, Color(0xFFEF4444)),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
        ) {
            Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Sign Out", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ── Small helpers ─────────────────────────────────────────────────────────────
@Composable
private fun SectionLabelP(text: String) {
    Text(
        text          = text,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Medium,
        color         = TextSecondary,
        letterSpacing = 1.sp,
        modifier      = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun StatChipP(
    modifier: Modifier,
    emoji   : String,
    value   : String,
    label   : String,
    bg      : Color
) {
    Column(
        modifier              = modifier
            .shadow(3.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(12.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(bg),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 16.sp) }
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun ProfileRowP(
    icon    : ImageVector,
    iconBg  : Color,
    iconTint: Color,
    label   : String,
    value   : String
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(
                value.ifEmpty { "—" },
                fontSize   = 14.sp,
                color      = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF1A2332)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme { ProfileScreen() }
}