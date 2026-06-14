package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Drop this composable anywhere in EcoMapScreen's detail panel.
 *
 * Usage — inside the site detail Column, after the stats row:
 *
 *   WeatherCard(
 *       lat      = site.lat,
 *       lon      = site.lon,
 *       cityName = "${site.name}, ${site.state}"
 *   )
 */
@Composable
fun WeatherCard(
    lat      : Double,
    lon      : Double,
    cityName : String,
    weatherVm: WeatherViewModel = viewModel()
) {
    val state by weatherVm.state.collectAsStateWithLifecycle()

    // Auto-fetch whenever the site changes
    LaunchedEffect(lat, lon) {
        weatherVm.fetchWeather(lat, lon, cityName)
    }

    AnimatedContent(
        targetState   = state,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label         = "weatherAnim"
    ) { ws ->
        when (ws) {
            is WeatherState.Loading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBg)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(18.dp),
                        color     = GreenPrimary,
                        strokeWidth = 2.dp
                    )
                    Text("Loading weather…", fontSize = 13.sp, color = TextSecondary)
                }
            }

            is WeatherState.Success -> {
                val d = ws.data
                Card(
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier  = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {

                        // Header
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.WbSunny, null,
                                tint = GreenPrimary, modifier = Modifier.size(14.dp))
                            Text(
                                "Live Weather · ${ws.city}",
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color      = TextSecondary
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        // Main temp row
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(d.emoji, fontSize = 32.sp)
                                Column {
                                    Text(
                                        "${d.tempC.toInt()}°C",
                                        fontSize   = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = TextPrimary
                                    )
                                    Text(d.description, fontSize = 12.sp, color = TextSecondary)
                                }
                            }

                            // Mini stats
                            Column(horizontalAlignment = Alignment.End) {
                                WeatherStat(Icons.Default.WaterDrop, "${d.humidity}%", "Humidity")
                                Spacer(Modifier.height(4.dp))
                                WeatherStat(Icons.Default.Air, "${d.windKph.toInt()} km/h", "Wind")
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Solar context tip
                        val solarTip = when {
                            d.weatherCode == 0 && d.isDay ->
                                "☀️ Excellent solar generation conditions right now."
                            d.weatherCode in 1..3 && d.isDay ->
                                "⛅ Good solar generation — partial cloud cover."
                            d.weatherCode in 51..99 ->
                                "🌧️ Reduced solar output expected during rain."
                            !d.isDay ->
                                "🌙 Solar generation offline — nighttime."
                            else ->
                                "🌤️ Moderate solar generation conditions."
                        }
                        Text(
                            solarTip,
                            fontSize   = 11.sp,
                            color      = Color(0xFF166534),
                            modifier   = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenSurface)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            is WeatherState.Error -> {
                Text(
                    "⚠️ ${ws.msg}",
                    fontSize = 12.sp,
                    color    = TextSecondary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun WeatherStat(icon: androidx.compose.ui.graphics.vector.ImageVector,
                        value: String, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(12.dp))
        Column {
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(label, fontSize = 10.sp, color = TextSecondary)
        }
    }
}
