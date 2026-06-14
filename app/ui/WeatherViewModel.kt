package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class WeatherData(
    val tempC      : Float,
    val humidity   : Int,
    val windKph    : Float,
    val weatherCode: Int,
    val isDay      : Boolean
) {
    val emoji: String get() = when {
        weatherCode == 0  && isDay  -> "☀️"
        weatherCode == 0            -> "🌙"
        weatherCode in 1..3         -> "⛅"
        weatherCode in 45..48       -> "🌫️"
        weatherCode in 51..67       -> "🌧️"
        weatherCode in 71..77       -> "❄️"
        weatherCode in 80..82       -> "🌦️"
        weatherCode in 95..99       -> "⛈️"
        else                        -> "🌤️"
    }
    val description: String get() = when {
        weatherCode == 0  && isDay  -> "Clear sky"
        weatherCode == 0            -> "Clear night"
        weatherCode in 1..3         -> "Partly cloudy"
        weatherCode in 45..48       -> "Foggy"
        weatherCode in 51..67       -> "Rainy"
        weatherCode in 71..77       -> "Snowy"
        weatherCode in 80..82       -> "Showers"
        weatherCode in 95..99       -> "Thunderstorm"
        else                        -> "Cloudy"
    }
}

sealed class WeatherState {
    object Idle    : WeatherState()
    object Loading : WeatherState()
    data class Success(val data: WeatherData, val city: String) : WeatherState()
    data class Error(val msg: String) : WeatherState()
}

class WeatherViewModel : ViewModel() {

    private val _state = MutableStateFlow<WeatherState>(WeatherState.Idle)
    val state: StateFlow<WeatherState> = _state

    fun fetchWeather(lat: Double, lon: Double, cityName: String) {
        viewModelScope.launch {
            _state.value = WeatherState.Loading
            try {
                val url = "https://api.open-meteo.com/v1/forecast" +
                        "?latitude=$lat&longitude=$lon" +
                        "&current=temperature_2m,relative_humidity_2m," +
                        "wind_speed_10m,weather_code,is_day" +
                        "&wind_speed_unit=kmh&timezone=auto"

                val json = withContext(Dispatchers.IO) {
                    JSONObject(URL(url).readText())
                }

                val cur  = json.getJSONObject("current")
                val data = WeatherData(
                    tempC       = cur.getDouble("temperature_2m").toFloat(),
                    humidity    = cur.getInt("relative_humidity_2m"),
                    windKph     = cur.getDouble("wind_speed_10m").toFloat(),
                    weatherCode = cur.getInt("weather_code"),
                    isDay       = cur.getInt("is_day") == 1
                )
                _state.value = WeatherState.Success(data, cityName)
            } catch (e: Exception) {
                _state.value = WeatherState.Error("Could not load weather.")
            }
        }
    }

    fun clear() { _state.value = WeatherState.Idle }
}