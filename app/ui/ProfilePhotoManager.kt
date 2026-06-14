package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.a221491_amiraizatbinharith_nelson_project2.db.EcoDatabase
import java.io.ByteArrayOutputStream
import java.io.File

// ── ViewModel ─────────────────────────────────────────────────────────────────
// Stores profile photo as Base64 in Room — no Firebase Storage needed.
// Photo is kept on-device only (no cloud sync for photo).
class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = EcoDatabase.getInstance(application)

    // Base64 string of the current photo (empty = no photo)
    private val _photoBase64 = MutableStateFlow<String?>(null)
    val photoBase64: StateFlow<String?> = _photoBase64

    private val _uploading = MutableStateFlow(false)
    val uploading: StateFlow<Boolean> = _uploading

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError: StateFlow<String?> = _uploadError

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess: StateFlow<Boolean> = _uploadSuccess

    // Kept for API compatibility with ProfileScreen — always null now
    val photoUrl: StateFlow<String?> = MutableStateFlow(null)

    init {
        loadPhoto()
    }

    // ── Reset for account switch ──────────────────────────────────────────────
    fun resetForNewSession() {
        _photoBase64.value   = null
        _uploadError.value   = null
        _uploadSuccess.value = false
        loadPhoto()
    }

    // ── Load photo from Room ──────────────────────────────────────────────────
    fun loadPhoto() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entity = db.userProfileDao().get()
                _photoBase64.value = entity?.photoBase64?.ifEmpty { null }
            } catch (e: Exception) {
                _photoBase64.value = null
            }
        }
    }

    // ── Save photo picked from gallery or camera ──────────────────────────────
    fun uploadPhoto(uri: Uri) {
        val context = getApplication<Application>()
        viewModelScope.launch {
            _uploading.value     = true
            _uploadError.value   = null
            _uploadSuccess.value = false
            try {
                val base64 = withContext(Dispatchers.IO) {
                    encodeUriToBase64(context, uri)
                }
                // Save Base64 into the existing Room profile row
                val current = db.userProfileDao().get()
                if (current != null) {
                    db.userProfileDao().upsert(current.copy(photoBase64 = base64))
                }
                _photoBase64.value   = base64
                _uploadSuccess.value = true
            } catch (e: Exception) {
                _uploadError.value = "Failed to save photo: ${e.localizedMessage ?: "unknown error"}"
            } finally {
                _uploading.value = false
            }
        }
    }

    fun clearError()   { _uploadError.value   = null  }
    fun clearSuccess() { _uploadSuccess.value = false }

    // ── Encode URI → Base64 JPEG (works on all API levels, no deprecated calls) ──
    private fun encodeUriToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open image")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        // Scale down to 400×400 to keep the Room row small (~40 KB)
        val scaled = android.graphics.Bitmap.createScaledBitmap(bitmap, 400, 400, true)
        val stream = ByteArrayOutputStream()
        scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 75, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }
}

// ── Creates a temp file URI for the camera capture intent ─────────────────────
fun createTempImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "profile_photo_temp.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

// ── Decode Base64 → ImageBitmap (cached by remember) ─────────────────────────
@Composable
fun rememberBase64Bitmap(base64: String?): ImageBitmap? {
    return remember(base64) {
        if (base64.isNullOrEmpty()) null
        else try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}

// ── Reusable avatar composable ────────────────────────────────────────────────
@Composable
fun ProfileAvatar(
    photoUrl    : String?       = null,   // kept for API compatibility — ignored
    initials    : String,
    size        : Dp            = 84.dp,
    onClick     : (() -> Unit)? = null,
    cacheBuster : Long          = 0L,     // kept for API compatibility — ignored
    photoBase64 : String?       = null
) {
    val bitmap = rememberBase64Bitmap(photoBase64)

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(GreenLight)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap             = bitmap,
                contentDescription = "Profile photo",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text       = initials.ifEmpty { "?" },
                fontSize   = (size.value * 0.33f).sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
        }
    }
}

// ── Bottom sheet: Camera or Gallery picker ────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerSheet(
    photoVm  : PhotoViewModel,
    onDismiss: () -> Unit
) {
    val context     = LocalContext.current
    val uploadError by photoVm.uploadError.collectAsState()
    val uploading   by photoVm.uploading.collectAsState()
    var cameraUri   by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { photoVm.uploadPhoto(it) }
        onDismiss()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraUri?.let { photoVm.uploadPhoto(it) }
        onDismiss()
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Change Profile Photo",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )

            if (!uploadError.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFEDED))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, null,
                        tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    Text(
                        text     = uploadError!!,
                        color    = Color(0xFFEF4444),
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
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

            Spacer(Modifier.height(4.dp))

            // Camera option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenSurface)
                    .clickable(enabled = !uploading) {
                        val uri = createTempImageUri(context)
                        cameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CameraAlt, null,
                    tint     = GreenPrimary,
                    modifier = Modifier.size(22.dp))
                Column {
                    Text("Take a Photo", fontSize = 15.sp,
                        fontWeight = FontWeight.Medium, color = TextPrimary)
                    Text("Use your camera", fontSize = 12.sp, color = TextSecondary)
                }
            }

            // Gallery option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BlueSurface)
                    .clickable(enabled = !uploading) { galleryLauncher.launch("image/*") }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PhotoLibrary, null,
                    tint     = Color(0xFF3B82F6),
                    modifier = Modifier.size(22.dp))
                Column {
                    Text("Choose from Gallery", fontSize = 15.sp,
                        fontWeight = FontWeight.Medium, color = TextPrimary)
                    Text("Pick an existing photo", fontSize = 12.sp, color = TextSecondary)
                }
            }

            TextButton(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = TextSecondary)
            }
        }
    }
}