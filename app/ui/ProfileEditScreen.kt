package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

// ── Edit Profile Screen ───────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profileVm: ProfileViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val profile by profileVm.profile.collectAsStateWithLifecycle()

    // Local mutable state mirrors the current profile
    var name       by remember(profile) { mutableStateOf(profile.name) }
    var email      by remember(profile) { mutableStateOf(profile.email) }
    var university by remember(profile) { mutableStateOf(profile.university) }
    var course     by remember(profile) { mutableStateOf(profile.course) }
    var bio        by remember(profile) { mutableStateOf(profile.bio) }
    var level      by remember(profile) { mutableStateOf(profile.level) }

    var showSaved  by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            profileVm.updateProfile(
                                profile.copy(
                                    name       = name.trim(),
                                    email      = email.trim(),
                                    university = university.trim(),
                                    course     = course.trim(),
                                    bio        = bio.trim(),
                                    level      = level.trim()
                                )
                            )
                            showSaved = true
                        }
                    ) {
                        Text("Save", color = GreenLight, fontWeight = FontWeight.Bold,
                            fontSize = 15.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = SurfaceBg
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Saved snackbar banner ─────────────────────────────────────────
            if (showSaved) {
                LaunchedEffect(Unit) {
                    delay(2000)
                    showSaved = false
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GreenPrimary)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null,
                        tint = GreenLight, modifier = Modifier.size(18.dp))
                    Text("Profile saved successfully!",
                        color = GreenSurface, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Avatar preview ────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name
                            .split(" ")
                            .take(2)
                            .joinToString("") { it.firstOrNull()?.uppercase() ?: "" },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Initials update as you type",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Form fields ───────────────────────────────────────────────────
            EditSectionLabel("PERSONAL INFO")

            EditCard {
                EcoTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    leadingIcon = Icons.Default.Person
                )
                Spacer(Modifier.height(12.dp))
                EcoTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    leadingIcon = Icons.Default.Email
                )
            }

            Spacer(Modifier.height(16.dp))
            EditSectionLabel("ACADEMIC INFO")

            EditCard {
                EcoTextField(
                    value = university,
                    onValueChange = { university = it },
                    label = "University",
                    leadingIcon = Icons.Default.School
                )
                Spacer(Modifier.height(12.dp))
                EcoTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = "Course / Programme",
                    leadingIcon = Icons.Default.Book
                )
                Spacer(Modifier.height(12.dp))
                EcoTextField(
                    value = level,
                    onValueChange = { level = it },
                    label = "Learning Level",
                    leadingIcon = Icons.Default.Star
                )
            }

            Spacer(Modifier.height(16.dp))
            EditSectionLabel("BIO")

            EditCard {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("About yourself") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        focusedLabelColor  = GreenPrimary
                    )
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Save button ───────────────────────────────────────────────────
            Button(
                onClick = {
                    profileVm.updateProfile(
                        profile.copy(
                            name       = name.trim(),
                            email      = email.trim(),
                            university = university.trim(),
                            course     = course.trim(),
                            bio        = bio.trim(),
                            level      = level.trim()
                        )
                    )
                    showSaved = true
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null,
                    modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Changes", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Small helpers ─────────────────────────────────────────────────────────────
@Composable
private fun EditSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary,
        letterSpacing = 1.sp,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun EditCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun EcoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = leadingIcon, contentDescription = null,
                tint = GreenPrimary, modifier = Modifier.size(20.dp))
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenPrimary,
            focusedLabelColor  = GreenPrimary
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileEditScreenPreview() {
    EditProfileScreen()
}