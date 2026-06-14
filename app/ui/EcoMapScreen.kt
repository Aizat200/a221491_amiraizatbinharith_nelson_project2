package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

// ═══════════════════════════════════════════════════════════════════════════════
// EcoMapScreen  —  entry point (ViewModel injected here)
// ═══════════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EcoMapScreen(
    mapViewModel: EcoMapViewModel = viewModel()
) {
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    val weatherVm = remember { WeatherViewModel() }

    val locationPermissions = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    )

    val malaysiaCenter = GeoPoint(3.9, 108.0)

    Box(modifier = Modifier.fillMaxSize().background(SurfaceBg)) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ───────────────────────────────────────────────────────
            EcoMapTopBar(
                activeFilter  = uiState.activeFilter,
                filteredCount = uiState.filteredSites.size,
                onFilterClick = { mapViewModel.setFilter(it) },
                onSdg7Click   = { mapViewModel.showSdg7Sheet() }
            )

            // ── Map ───────────────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {

                if (!locationPermissions.allPermissionsGranted) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LocationPermissionBanner(
                            onAllow = { locationPermissions.launchMultiplePermissionRequest() }
                        )
                        StableMapView(
                            modifier       = Modifier.fillMaxSize(),
                            center         = malaysiaCenter,
                            sites          = uiState.filteredSites,
                            showMyLocation = false,
                            onMarkerClick  = { mapViewModel.selectSite(it) }
                        )
                    }
                } else {
                    StableMapView(
                        modifier       = Modifier.fillMaxSize(),
                        center         = malaysiaCenter,
                        sites          = uiState.filteredSites,
                        showMyLocation = true,
                        onMarkerClick  = { mapViewModel.selectSite(it) }
                    )
                }

                MapLegend(
                    activeFilter = uiState.activeFilter,
                    modifier     = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                )

                if (uiState.selectedSite == null) {
                    TapHintBanner(modifier = Modifier.align(Alignment.BottomCenter))
                }
            }
        }

        // ── Sheets ────────────────────────────────────────────────────────────
        if (uiState.showSdg7Sheet) {
            Sdg7Sheet(onDismiss = { mapViewModel.hideSdg7Sheet() })
        }

        uiState.selectedSite?.let { site ->
            SiteDetailSheet(
                site      = site,
                weatherVm = weatherVm,
                onDismiss = { mapViewModel.clearSelectedSite() }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// EcoMapTopBar
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun EcoMapTopBar(
    activeFilter  : EnergyType,
    filteredCount : Int,
    onFilterClick : (EnergyType) -> Unit,
    onSdg7Click   : () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(GreenPrimary, Color(0xFF0A5C45))))
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 16.dp)
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "🇲🇾 Malaysia Energy Map",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Text(
                        "${MALAYSIA_ENERGY_SITES.size} sites · tap marker for details",
                        fontSize = 12.sp,
                        color    = Color.White.copy(alpha = 0.75f)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { onSdg7Click() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", fontSize = 16.sp)
                        Text(
                            "SDG 7", fontSize = 10.sp,
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding        = PaddingValues(end = 8.dp)
            ) {
                items(EnergyType.values()) { type ->
                    val isActive = activeFilter == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isActive) Color.White else Color.White.copy(alpha = 0.15f))
                            .clickable { onFilterClick(type) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(type.emoji, fontSize = 12.sp)
                            Text(
                                type.label, fontSize = 12.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color      = if (isActive) GreenPrimary else Color.White
                            )
                            if (isActive && type != EnergyType.ALL) {
                                Text(
                                    "($filteredCount)", fontSize = 10.sp,
                                    color = GreenPrimary.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// LocationPermissionBanner
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun LocationPermissionBanner(onAllow: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3CD))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "📍 Enable location to show your position",
            fontSize = 12.sp,
            color    = Color(0xFF856404)
        )
        TextButton(onClick = onAllow) {
            Text("Allow", fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TapHintBanner
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun TapHintBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(bottom = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("📍 Tap a marker to explore site details", fontSize = 12.sp, color = Color.White)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// StableMapView  —  OSMDroid AndroidView wrapper
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun StableMapView(
    modifier      : Modifier,
    center        : GeoPoint,
    sites         : List<MalaysianEnergySite>,
    showMyLocation: Boolean,
    onMarkerClick : (MalaysianEnergySite) -> Unit
) {
    val onMarkerClickRef = rememberUpdatedState(onMarkerClick)

    AndroidView(
        modifier = modifier,
        factory  = { ctx ->
            Configuration.getInstance().userAgentValue = ctx.packageName
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(6.0)
                controller.setCenter(center)
                if (showMyLocation) {
                    val myLoc = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    myLoc.enableMyLocation()
                    overlays.add(0, myLoc)
                }
            }
        },
        update = { mapView ->
            mapView.overlays.removeAll(mapView.overlays.filterIsInstance<Marker>())
            sites.forEach { site ->
                Marker(mapView).apply {
                    position = GeoPoint(site.lat, site.lon)
                    title    = site.name
                    snippet  = "${site.type.emoji} ${site.type.label} · ${site.capacity}"
                    setOnMarkerClickListener { _, _ ->
                        onMarkerClickRef.value(site)
                        true
                    }
                    mapView.overlays.add(this)
                }
            }
            mapView.invalidate()
        }
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// SiteDetailSheet
// ═══════════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SiteDetailSheet(
    site      : MalaysianEnergySite,
    weatherVm : WeatherViewModel,
    onDismiss : () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val tc         = site.type.color

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = SurfaceBg,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderColor)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                        .background(tc.copy(alpha = 0.15f)),
                    Alignment.Center
                ) { Text(site.type.emoji, fontSize = 26.sp) }

                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier.clip(RoundedCornerShape(4.dp))
                                .background(tc.copy(alpha = 0.12f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                site.type.label.uppercase(), fontSize = 9.sp,
                                color = tc, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                            )
                        }
                        Text(site.commissioned, fontSize = 10.sp, color = TextSecondary)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        site.name, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = TextPrimary, lineHeight = 24.sp
                    )
                    Text("📍 ${site.state}", fontSize = 12.sp, color = TextSecondary)
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = TextSecondary)
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Stats ─────────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox(Modifier.weight(1f), Icons.Default.ElectricBolt, "Capacity", site.capacity, tc)
                StatBox(Modifier.weight(1f), Icons.Default.CalendarMonth, "Commissioned", site.commissioned, tc)
            }

            Spacer(Modifier.height(16.dp))

            // ── Photo ─────────────────────────────────────────────────────────
            SitePhoto(site = site, typeColor = tc)

            Spacer(Modifier.height(16.dp))

            // ── Weather ───────────────────────────────────────────────────────
            SectionHeader("🌤️ Live Weather at This Site")
            Spacer(Modifier.height(8.dp))
            WeatherCard(lat = site.lat, lon = site.lon, cityName = "${site.name}, ${site.state}", weatherVm = weatherVm)

            Spacer(Modifier.height(18.dp))
            SectionHeader("📖 About This Facility")
            Spacer(Modifier.height(8.dp))
            Text(site.fullDesc, fontSize = 14.sp, color = TextPrimary, lineHeight = 23.sp)

            Spacer(Modifier.height(18.dp))
            SectionHeader("⚙️ How It Works")
            Spacer(Modifier.height(8.dp))
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = tc.copy(alpha = 0.06f)),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Text(site.function, fontSize = 14.sp, color = TextPrimary,
                    lineHeight = 23.sp, modifier = Modifier.padding(14.dp))
            }

            Spacer(Modifier.height(18.dp))
            SectionHeader("✅ Benefits")
            Spacer(Modifier.height(8.dp))
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    site.benefits.forEach { benefit ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment     = Alignment.Top
                        ) {
                            Box(Modifier.padding(top = 5.dp).size(7.dp).clip(CircleShape).background(tc))
                            Text(benefit, fontSize = 13.sp, color = TextPrimary, lineHeight = 20.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            SectionHeader("🌍 SDG 7 Connection")
            Spacer(Modifier.height(8.dp))
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("⚡", fontSize = 20.sp)
                    Text(site.sdg7Link, fontSize = 13.sp, color = Color(0xFF1B5E20), lineHeight = 21.sp)
                }
            }

            Spacer(Modifier.height(18.dp))
            SectionHeader("📊 Technical Facts")
            Spacer(Modifier.height(8.dp))
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = SurfaceBg),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    site.facts.forEachIndexed { i, fact ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment     = Alignment.Top
                        ) {
                            Box(
                                Modifier.size(20.dp).clip(RoundedCornerShape(6.dp))
                                    .background(tc.copy(alpha = 0.15f)),
                                Alignment.Center
                            ) {
                                Text("${i+1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tc)
                            }
                            Text(fact, fontSize = 13.sp, color = TextPrimary,
                                lineHeight = 20.sp, modifier = Modifier.weight(1f))
                        }
                        if (i < site.facts.size - 1)
                            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SitePhoto + Fallback
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun SitePhoto(site: MalaysianEnergySite, typeColor: Color) {
    val context = LocalContext.current
    val resId = remember(site.id) {
        SITE_IMAGE_DRAWABLES[site.id]?.let {
            context.resources.getIdentifier(it, "drawable", context.packageName)
        } ?: 0
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, typeColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
    ) {
        if (resId != 0) {
            Image(
                painter            = painterResource(id = resId),
                contentDescription = "${site.name} — ${site.type.label} facility photo",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        } else {
            SitePhotoFallback(site = site, typeColor = typeColor)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth().height(64.dp).align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart).fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(site.type.emoji, fontSize = 13.sp)
                Text(
                    "${site.name} · ${site.state}",
                    fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                    color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                site.shortDesc, fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun SitePhotoFallback(site: MalaysianEnergySite, typeColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    listOf(typeColor.copy(alpha = 0.22f), typeColor.copy(alpha = 0.06f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(site.type.emoji, fontSize = 40.sp)
            Text(
                site.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                color = typeColor, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text("Image unavailable offline", fontSize = 10.sp, color = TextSecondary)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Sdg7Sheet
// ═══════════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Sdg7Sheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceBg,
        dragHandle = {
            Box(
                Modifier.padding(vertical = 12.dp).size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp)).background(BorderColor)
            )
        }
    ) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(bottom = 40.dp)
        ) {
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFFC107), Color(0xFFFFA000))))
                    .padding(20.dp)
            ) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚡", fontSize = 36.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("SDG Goal 7", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(
                        "Affordable and Clean Energy", fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "The United Nations SDG 7 ensures universal access to affordable, reliable, sustainable, and modern energy by 2030. Energy is central to nearly every major global challenge.",
                fontSize = 14.sp, color = TextPrimary, lineHeight = 23.sp
            )

            Spacer(Modifier.height(20.dp))
            SectionHeader("🎯 SDG 7 Targets")
            Spacer(Modifier.height(10.dp))
            listOf(
                "7.1" to "Universal access to affordable, reliable and modern energy by 2030",
                "7.2" to "Substantially increase the share of renewable energy in the global energy mix",
                "7.3" to "Double the global rate of improvement in energy efficiency",
                "7.a" to "Enhance international cooperation for clean energy technology access",
                "7.b" to "Expand and upgrade infrastructure for modern sustainable energy services"
            ).forEach { (num, text) ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment     = Alignment.Top
                ) {
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFC107).copy(alpha = 0.2f))
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(num, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D6E00))
                    }
                    Text(text, fontSize = 13.sp, color = TextPrimary,
                        lineHeight = 20.sp, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionHeader("🇲🇾 Malaysia's SDG 7 Progress")
            Spacer(Modifier.height(10.dp))
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        Triple("☀️", "1,933 MW", "Solar capacity by end of 2023"),
                        Triple("💧", "6,400+ MW", "Hydropower installed capacity"),
                        Triple("🌿", "500+ MW", "Biomass and biogas capacity"),
                        Triple("🎯", "31%", "RE target by 2025 (NETR)"),
                        Triple("⚡", "40%", "Carbon intensity reduction by 2030 (NDC)"),
                        Triple("🔋", "4 GW", "Solar capacity target by 2030")
                    ).forEach { (e, v, l) ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(e, fontSize = 18.sp)
                            Column(Modifier.weight(1f)) {
                                Text(v, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                                Text(l, fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionHeader("📚 Learn More")
            Spacer(Modifier.height(8.dp))
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "UN SDGs: sdgs.un.org/goals/goal7",
                        "Malaysia NETR: www.petra.gov.my",
                        "Energy Commission: www.st.gov.my",
                        "Sarawak Energy: www.sarawakenergy.com",
                        "TNB Sustainability: www.tnb.com.my"
                    ).forEach { link ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Link, null, tint = GreenPrimary,
                                modifier = Modifier.size(14.dp))
                            Text(link, fontSize = 12.sp, color = Color(0xFF1B5E20))
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Small reusable composables
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun MapLegend(activeFilter: EnergyType, modifier: Modifier = Modifier) {
    val types = if (activeFilter == EnergyType.ALL) EnergyType.values().drop(1)
    else listOf(activeFilter)
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(10.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("Legend", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            types.forEach { type ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(type.color))
                    Text(type.label, fontSize = 9.sp, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
        color = TextPrimary, letterSpacing = 0.2.sp)
}

@Composable
private fun StatBox(
    modifier: Modifier = Modifier,
    icon    : ImageVector,
    label   : String,
    value   : String,
    tint    : Color
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(tint.copy(alpha = 0.08f))
            .padding(10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        Column {
            Text(label, fontSize = 10.sp, color = TextSecondary)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = tint,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}