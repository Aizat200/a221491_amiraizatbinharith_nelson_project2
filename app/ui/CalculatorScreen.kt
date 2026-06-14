package my.com.a221491_amiraizatbinharith_nelson_project2.ui



import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

// ── Calc colour tokens ────────────────────────────────────────────────────────
//
// WHY THESE COLORS?
// -----------------
// CalcShell (#1A2332) — Very dark navy used as the outermost body of the
//   calculator. Inspired by the dark plastic casing of Casio scientific
//   calculators. Navy (rather than pure black) feels less harsh on OLED
//   screens and gives the UI a premium, tech-device aesthetic.
//
// CalcFaceplate (#243044) — Slightly lighter navy for the inner faceplate
//   panel. The two-tone dark shell mirrors how real Casio calculators have
//   a slightly lighter inner bezel around the keypad area.
//
// CalcDisplay (#1B2D1B) — A very dark GREEN (not black) for the LCD screen
//   area. This matches the green-tinted glass of real LCD displays (e.g.
//   Casio fx-570ES). Green-black reads immediately as "screen" to the user.
//
// CalcDisplayFg (#9FE1CB) — Soft mint-green for the main display digits.
//   This is the colour of liquid-crystal segments when lit — a classic
//   retro-tech choice. It also ties into the app's overall GreenPrimary
//   palette, keeping a consistent brand identity across both screens.
//
// CalcDisplaySub (#5DCAA5) — A slightly darker mint for secondary text
//   (expression line, field labels). The contrast between #9FE1CB and
//   #5DCAA5 creates a clear hierarchy: main answer vs. working expression.
//
// CalcKeyDark (#1E2D20) — Very dark green-tinted key background for trig/
//   function keys. Green tint (rather than grey) keeps the keyboard area
//   within the same green colour family as the brand.
//
// CalcKeyMid (#2D4A30) — Medium dark-green for memory and operator keys.
//   One step lighter than CalcKeyDark so users can visually group key types.
//
// CalcKeyAccent (#1D9E75) — The same GreenPrimary from HomeScreen, used for
//   the "=" key and Calculate buttons. Reusing the brand primary creates a
//   visual anchor — the most important action key is always the brand colour.
//
// CalcKeyLight (#344A5E) — Blue-grey for digit keys (0-9). Blue-grey vs
//   green distinguishes number keys from function keys at a glance, which
//   reduces input errors — the same logic Casio uses on physical calculators.
//
// CalcKeyGray (#3A4A5E) — Slightly lighter blue-grey for secondary operators
//   (DEL, %, brackets). One shade lighter than CalcKeyLight creates a subtle
//   but readable grouping.
//
// CalcKeyRed (#8B1A1A) — Deep red reserved exclusively for the AC (All Clear)
//   key. Red = danger/reset is a universal UI convention; using it only for AC
//   prevents accidental taps of destructive actions.
//
// CalcTextLight (#E1F5EE) — Near-white with a green tint for key labels.
//   Pure white (#FFFFFF) on dark green keys can feel clinical; the green
//   tint warms the text while maintaining high contrast (> 7:1 ratio).
//
// CalcTextDark (#1A2332) — The shell colour reused as dark text on accent
//   buttons (the "=" key uses CalcKeyAccent as bg, so dark text is needed
//   for contrast). Reusing the shell colour keeps the palette lean.

private val CalcShell      = Color(0xFF1A2332)
private val CalcFaceplate  = Color(0xFF243044)
private val CalcDisplay    = Color(0xFF1B2D1B)
private val CalcDisplayFg  = Color(0xFF9FE1CB)
private val CalcDisplaySub = Color(0xFF5DCAA5)
private val CalcKeyDark    = Color(0xFF1E2D20)
private val CalcKeyMid     = Color(0xFF2D4A30)
private val CalcKeyAccent  = Color(0xFF1D9E75)
private val CalcKeyLight   = Color(0xFF344A5E)
private val CalcKeyGray    = Color(0xFF3A4A5E)
private val CalcKeyRed     = Color(0xFF8B1A1A)
private val CalcTextLight  = Color(0xFFE1F5EE)
private val CalcTextDim    = Color(0xFF9FE1CB)
private val CalcTextDark   = Color(0xFF1A2332)

// ── Mode enum ─────────────────────────────────────────────────────────────────
enum class CalcMode(val label: String) {
    SCIENTIFIC("Scientific"),
    ENERGY_KWH("Energy (kWh)"),
    SOLAR("Solar Power"),
    WIND("Wind Power"),
    CO2("CO₂ Savings")
}

enum class AngleMode { DEG, RAD }

// ═════════════════════════════════════════════════════════════════════════════
// ROOT SCREEN
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var mode         by remember { mutableStateOf(CalcMode.SCIENTIFIC) }
    var angleMode    by remember { mutableStateOf(AngleMode.DEG) }
    var showModeMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalcShell)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Brand bar ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "EcoCalc FX-570ES",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalcDisplayFg,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    "NATURAL-V.P.A.M.",
                    fontSize = 12.sp,
                    color = CalcDisplaySub,
                    fontFamily = FontFamily.Monospace
                )
            }
            Box {
                OutlinedButton(
                    onClick = { showModeMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CalcDisplayFg)
                ) {
                    Text(mode.label, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
                DropdownMenu(
                    expanded = showModeMenu,
                    onDismissRequest = { showModeMenu = false },
                    modifier = Modifier.background(CalcFaceplate)
                ) {
                    CalcMode.entries.forEach { m ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    m.label,
                                    fontSize = 14.sp,
                                    color = if (m == mode) CalcKeyAccent else CalcTextLight,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            onClick = { mode = m; showModeMenu = false }
                        )
                    }
                }
            }
        }

        // ── Faceplate with shadow ──────────────────────────────────────────
        // WHY shadow here?
        // The faceplate is the main "body" of the calculator device. A strong
        // shadow (elevation = 12.dp) makes it look like a physical object
        // lifted off the screen — reinforcing the skeuomorphic (real-device)
        // design intent. Higher elevation than the individual keys (2.dp) so
        // the whole device reads as one cohesive raised slab.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = 4.dp, bottomEnd = 4.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = 4.dp, bottomEnd = 4.dp
                    )
                )
                .background(CalcFaceplate)
                .padding(bottom = 16.dp)
        ) {
            // WHY AnimatedContent?
            // When the user switches modes (Scientific → Solar etc.), the new
            // screen fades in while the old one fades out. Without animation the
            // swap is a jarring instant cut. fadeIn + fadeOut (200ms) is fast
            // enough to not slow the user down but smooth enough to feel polished.
            AnimatedContent(
                targetState = mode,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "mode_transition"
            ) { current ->
                when (current) {
                    CalcMode.SCIENTIFIC -> ScientificCalculator(angleMode) { angleMode = it }
                    CalcMode.ENERGY_KWH -> EnergyKwhCalculator()
                    CalcMode.SOLAR      -> SolarCalculator()
                    CalcMode.WIND       -> WindCalculator()
                    CalcMode.CO2        -> Co2Calculator()
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// SCIENTIFIC CALCULATOR  (Casio fx-570ES Plus layout)
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun ScientificCalculator(angleMode: AngleMode, onAngleModeChange: (AngleMode) -> Unit) {
    var expression by remember { mutableStateOf("") }
    var display    by remember { mutableStateOf("0") }
    var memory     by remember { mutableStateOf(0.0) }
    var isError    by remember { mutableStateOf(false) }
    var shiftOn    by remember { mutableStateOf(false) }
    var alphaOn    by remember { mutableStateOf(false) }

    fun calc(expr: String): String {
        if (expr.isBlank()) return "0"
        return try {
            val v = evalExpression(expr, angleMode)
            if (v.isNaN() || v.isInfinite()) throw ArithmeticException()
            if (v % 1.0 == 0.0 && abs(v) < 1e15) v.toLong().toString()
            else "%.10f".format(v).trimEnd('0').trimEnd('.')
        } catch (_: Exception) { "Math Error" }
    }

    fun press(token: String) {
        if (isError && token != "AC") { expression = ""; display = "0"; isError = false }
        when (token) {
            "="   -> {
                val res = calc(expression.ifBlank { display })
                isError = res == "Math Error"
                display = res
                if (!isError) expression = res
            }
            "AC"  -> { expression = ""; display = "0"; isError = false }
            "DEL" -> {
                if (expression.isNotEmpty()) {
                    val fnSuffix = listOf("sin(","cos(","tan(","asin(","acos(","atan(","log(","ln(","√(")
                        .firstOrNull { expression.endsWith(it) }
                    expression = if (fnSuffix != null) expression.dropLast(fnSuffix.length)
                    else expression.dropLast(1)
                }
                display = expression.ifEmpty { "0" }
            }
            "M+"  -> memory += display.toDoubleOrNull() ?: 0.0
            "M-"  -> memory -= display.toDoubleOrNull() ?: 0.0
            "MR"  -> {
                val ms = if (memory % 1.0 == 0.0) memory.toLong().toString()
                else "%.10f".format(memory).trimEnd('0')
                expression += ms; display = expression
            }
            "MC"  -> memory = 0.0
            "Ans" -> { expression += display; display = expression }
            else  -> { expression += token; display = expression }
        }
        if (token !in listOf("SHIFT", "ALPHA")) { shiftOn = false; alphaOn = false }
    }

    Column(Modifier.fillMaxWidth()) {
        // LCD with shadow — the screen needs to look recessed/inset into the
        // faceplate. shadow() here uses a lower elevation (4.dp) than the
        // faceplate so it reads as a sub-element, not a separate object.
        LcdDisplay(expression, display, angleMode, shiftOn, alphaOn, memory != 0.0)
        Spacer(Modifier.height(6.dp))

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallToggle("SHIFT", shiftOn, CalcKeyDark) { shiftOn = !shiftOn; alphaOn = false }
            SmallToggle("ALPHA", alphaOn, CalcKeyDark) { alphaOn = !alphaOn; shiftOn = false }
            SmallToggle(angleMode.name, true, CalcKeyMid) {
                onAngleModeChange(if (angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "M=${if (memory % 1.0 == 0.0) memory.toLong() else "%.3f".format(memory)}",
                fontSize = 9.sp, color = CalcDisplaySub, fontFamily = FontFamily.Monospace
            )
        }
        Spacer(Modifier.height(6.dp))

        // Trig row
        CalcRow(Modifier.padding(horizontal = 10.dp)) {
            SciBtn(if (shiftOn) "asin(" else "sin(",  if (shiftOn) "sin⁻¹" else "sin",  CalcKeyDark, CalcTextLight) { press(it) }
            SciBtn(if (shiftOn) "acos(" else "cos(",  if (shiftOn) "cos⁻¹" else "cos",  CalcKeyDark, CalcTextLight) { press(it) }
            SciBtn(if (shiftOn) "atan(" else "tan(",  if (shiftOn) "tan⁻¹" else "tan",  CalcKeyDark, CalcTextLight) { press(it) }
            SciBtn(if (shiftOn) "10^(" else "log(",   if (shiftOn) "10^x"  else "log",  CalcKeyDark, CalcTextLight) { press(it) }
            SciBtn(if (shiftOn) "e^("  else "ln(",    if (shiftOn) "eˣ"    else "ln",   CalcKeyDark, CalcTextLight) { press(it) }
        }
        Spacer(Modifier.height(4.dp))

        // Powers & constants
        CalcRow(Modifier.padding(horizontal = 10.dp)) {
            SciBtn("²",  "x²", CalcKeyDark, CalcTextLight) { press(it) }
            SciBtn("³",  "x³", CalcKeyDark, CalcTextLight) { press(it) }
            SciBtn("√(", "√(", CalcKeyDark, CalcTextLight) { press(it) }
            SciBtn("π",  "π",  CalcKeyMid,  CalcTextLight) { press(it) }
            SciBtn("e",  "e",  CalcKeyMid,  CalcTextLight) { press(it) }
        }
        Spacer(Modifier.height(4.dp))

        // Brackets & memory
        CalcRow(Modifier.padding(horizontal = 10.dp)) {
            SciBtn("(",  "(",  CalcKeyGray, CalcTextLight) { press(it) }
            SciBtn(")",  ")",  CalcKeyGray, CalcTextLight) { press(it) }
            SciBtn("M+", "M+", CalcKeyMid,  CalcTextLight) { press(it) }
            SciBtn("M-", "M-", CalcKeyMid,  CalcTextLight) { press(it) }
            SciBtn("MR", "MR", CalcKeyMid,  CalcTextLight) { press(it) }
        }
        Spacer(Modifier.height(4.dp))

        // AC DEL % ÷ EXP
        CalcRow(Modifier.padding(horizontal = 10.dp)) {
            SciBtn("AC",  "AC",  CalcKeyRed,    CalcTextLight) { press(it) }
            SciBtn("DEL", "DEL", CalcKeyGray,   CalcTextLight) { press(it) }
            SciBtn("%",   "%",   CalcKeyGray,   CalcTextLight) { press(it) }
            SciBtn("÷",   "÷",   CalcKeyAccent, CalcTextDark)  { press(it) }
            SciBtn("E",   "EXP", CalcKeyGray,   CalcTextLight) { press(it) }
        }
        Spacer(Modifier.height(4.dp))

        // Digit rows
        listOf(
            listOf("7","8","9","×"),
            listOf("4","5","6","-"),
            listOf("1","2","3","+"),
            listOf("0",".","Ans","=")
        ).forEach { row ->
            CalcRow(Modifier.padding(horizontal = 10.dp)) {
                row.forEach { label ->
                    SciBtn(
                        token = label,
                        displayLabel = label,
                        bg = when (label) {
                            "="         -> CalcKeyAccent
                            "×","-","+" -> CalcKeyMid
                            "Ans"       -> CalcKeyGray
                            else        -> CalcKeyLight
                        },
                        fg = if (label == "=") CalcTextDark else CalcTextLight
                    ) { press(it) }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// ENERGY kWh
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun EnergyKwhCalculator() {
    var watts  by remember { mutableStateOf("") }
    var hours  by remember { mutableStateOf("") }
    var days   by remember { mutableStateOf("30") }
    var tariff by remember { mutableStateOf("0.40") }

    data class Res(
        val kwhDay: Double, val kwhMonth: Double, val kwhYear: Double,
        val costDay: Double, val costMonth: Double, val costYear: Double,
        val co2Year: Double
    )
    var result by remember { mutableStateOf<Res?>(null) }

    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        EnergyHeader("⚡ kWh Calculator", "Appliance electricity cost & usage")
        EnergyField("Power (Watts)",     watts,  "e.g. 1500")      { watts  = it }
        EnergyField("Hours per day",     hours,  "e.g. 8")         { hours  = it }
        EnergyField("Days per month",    days,   "e.g. 30")        { days   = it }
        EnergyField("Tariff (MYR/kWh)", tariff, "TNB avg ≈ 0.40") { tariff = it }
        Spacer(Modifier.height(10.dp))
        EnergyBtn("Calculate") {
            val w = watts.toDoubleOrNull()  ?: return@EnergyBtn
            val h = hours.toDoubleOrNull()  ?: return@EnergyBtn
            val d = days.toDoubleOrNull()   ?: return@EnergyBtn
            val t = tariff.toDoubleOrNull() ?: return@EnergyBtn
            val kd = w / 1000.0 * h
            val km = kd * d
            val ky = km * 12.0
            result = Res(kd, km, ky, kd*t, km*t, ky*t, ky*0.585)
        }

        // WHY AnimatedVisibility for the result card?
        // The result card only appears after the user taps Calculate. Without
        // animation it pops in abruptly, which can feel jarring and confuse
        // users into thinking the layout shifted. expandVertically + fadeIn
        // (300ms) smoothly reveals the card so the user's eye follows
        // the content rather than being startled by it.
        AnimatedVisibility(
            visible = result != null,
            enter = expandVertically(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(tween(300)),
            exit  = shrinkVertically() + fadeOut(tween(200))
        ) {
            result?.let { r ->
                Column {
                    Spacer(Modifier.height(16.dp))
                    ResultCard {
                        ResultRow("Daily usage",   "%.3f kWh".format(r.kwhDay))
                        ResultRow("Monthly usage", "%.2f kWh".format(r.kwhMonth))
                        ResultRow("Yearly usage",  "%.1f kWh".format(r.kwhYear))
                        HorizontalDivider(color = CalcKeyMid, thickness = 0.5.dp)
                        ResultRow("Daily cost",    "RM %.2f".format(r.costDay))
                        ResultRow("Monthly cost",  "RM %.2f".format(r.costMonth), true)
                        ResultRow("Yearly cost",   "RM %.2f".format(r.costYear))
                        HorizontalDivider(color = CalcKeyMid, thickness = 0.5.dp)
                        ResultRow("CO₂ per year",  "%.1f kg".format(r.co2Year), sub = true)
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// SOLAR
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun SolarCalculator() {
    var panelW by remember { mutableStateOf("") }
    var panels by remember { mutableStateOf("") }
    var sunHrs by remember { mutableStateOf("5.5") }
    var eff    by remember { mutableStateOf("80") }
    var tariff by remember { mutableStateOf("0.40") }
    var result by remember { mutableStateOf("") }

    Column(Modifier.padding(horizontal = 14.dp)) {
        EnergyHeader("☀️ Solar Power Calculator", "PV system output & savings")
        EnergyField("Panel rating (W)",       panelW, "e.g. 400")         { panelW = it }
        EnergyField("Number of panels",       panels, "e.g. 10")          { panels = it }
        EnergyField("Peak sun hours/day",     sunHrs, "Malaysia ≈ 5.5")   { sunHrs = it }
        EnergyField("System efficiency (%)",  eff,    "e.g. 80")          { eff    = it }
        EnergyField("Tariff (MYR/kWh)",       tariff, "e.g. 0.40")        { tariff = it }
        Spacer(Modifier.height(10.dp))
        EnergyBtn("Calculate Solar Output") {
            val pw = panelW.toDoubleOrNull() ?: return@EnergyBtn
            val np = panels.toDoubleOrNull() ?: return@EnergyBtn
            val sh = sunHrs.toDoubleOrNull() ?: return@EnergyBtn
            val ef = (eff.toDoubleOrNull() ?: 80.0) / 100.0
            val t  = tariff.toDoubleOrNull() ?: return@EnergyBtn
            val kWp       = pw * np / 1000.0
            val dailyKwh  = kWp * sh * ef
            val monthlyKwh = dailyKwh * 30
            val yearlyKwh  = dailyKwh * 365
            result = buildString {
                appendLine("System size:       %.2f kWp".format(kWp))
                appendLine("Daily output:      %.2f kWh".format(dailyKwh))
                appendLine("Monthly output:    %.1f kWh".format(monthlyKwh))
                appendLine("Yearly output:     %.0f kWh".format(yearlyKwh))
                appendLine("───────────────────────────")
                appendLine("Yearly savings:    RM %.2f".format(yearlyKwh * t))
                appendLine("CO₂ offset/year:   %.0f kg".format(yearlyKwh * 0.585))
            }
        }
        AnimatedVisibility(
            visible = result.isNotEmpty(),
            enter = expandVertically(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(tween(300)),
            exit  = shrinkVertically() + fadeOut(tween(200))
        ) {
            Column {
                Spacer(Modifier.height(14.dp))
                MonoBox(result)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// WIND
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun WindCalculator() {
    var bladeM by remember { mutableStateOf("") }
    var windMs by remember { mutableStateOf("") }
    var cpPct  by remember { mutableStateOf("40") }
    var result by remember { mutableStateOf("") }

    Column(Modifier.padding(horizontal = 14.dp)) {
        EnergyHeader("💨 Wind Power Calculator", "P = ½ρAv³ · Cₚ")
        EnergyField("Blade length (m)", bladeM, "e.g. 30")           { bladeM = it }
        EnergyField("Wind speed (m/s)", windMs, "e.g. 8")            { windMs = it }
        EnergyField("Efficiency Cₚ (%)", cpPct, "Practical ≈ 40%")   { cpPct  = it }
        Spacer(Modifier.height(10.dp))
        EnergyBtn("Calculate Wind Power") {
            val r  = bladeM.toDoubleOrNull() ?: return@EnergyBtn
            val v  = windMs.toDoubleOrNull()  ?: return@EnergyBtn
            val cp = (cpPct.toDoubleOrNull()  ?: 40.0) / 100.0
            val area   = PI * r * r
            val powerW = 0.5 * 1.225 * area * v.pow(3) * cp
            val kW     = powerW / 1000.0
            val daily  = kW * 24
            val yearly = daily * 365
            result = buildString {
                appendLine("Rotor area:        %.1f m²".format(area))
                appendLine("Power output:      %.2f kW".format(kW))
                appendLine("Daily generation:  %.1f kWh".format(daily))
                appendLine("Yearly generation: %.0f kWh".format(yearly))
                appendLine("───────────────────────────")
                appendLine("CO₂ offset/year:   %.0f kg".format(yearly * 0.585))
            }
        }
        AnimatedVisibility(
            visible = result.isNotEmpty(),
            enter = expandVertically(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(tween(300)),
            exit  = shrinkVertically() + fadeOut(tween(200))
        ) {
            Column {
                Spacer(Modifier.height(14.dp))
                MonoBox(result)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// CO₂ SAVINGS
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun Co2Calculator() {
    var kwh    by remember { mutableStateOf("") }
    var gf     by remember { mutableStateOf("0.585") }
    var result by remember { mutableStateOf("") }

    Column(Modifier.padding(horizontal = 14.dp)) {
        EnergyHeader("🌿 CO₂ Savings Calculator", "Quantify your green impact")
        EnergyField("Renewable energy (kWh)",          kwh, "e.g. 5000")       { kwh = it }
        EnergyField("Grid emission factor (kg/kWh)",   gf,  "Malaysia = 0.585") { gf  = it }
        Spacer(Modifier.height(10.dp))
        EnergyBtn("Calculate CO₂ Savings") {
            val k = kwh.toDoubleOrNull() ?: return@EnergyBtn
            val g = gf.toDoubleOrNull()  ?: return@EnergyBtn
            val co2Kg = k * g
            result = buildString {
                appendLine("kWh renewable:     %.0f kWh".format(k))
                appendLine("Emission factor:   ${g} kg/kWh")
                appendLine("───────────────────────────")
                appendLine("CO₂ avoided:       %.1f kg".format(co2Kg))
                appendLine("CO₂ in tonnes:     %.3f t".format(co2Kg / 1000.0))
                appendLine("───────────────────────────")
                appendLine("≈ trees planted:   %.0f".format(co2Kg / 21.77))
                appendLine("≈ cars off road:   %.2f".format(co2Kg / 4600.0))
            }
        }
        AnimatedVisibility(
            visible = result.isNotEmpty(),
            enter = expandVertically(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(tween(300)),
            exit  = shrinkVertically() + fadeOut(tween(200))
        ) {
            Column {
                Spacer(Modifier.height(14.dp))
                MonoBox(result)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// SHARED UI COMPONENTS
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun LcdDisplay(
    expression: String, display: String,
    angleMode: AngleMode, shiftOn: Boolean, alphaOn: Boolean, memOn: Boolean
) {
    // WHY shadow on the LCD?
    // A real LCD is recessed into the calculator body. shadow() at elevation
    // 4.dp with the dark CalcDisplay background creates this "sunken screen"
    // illusion — the shadow renders underneath the box edges, making it look
    // pressed inward compared to the raised faceplate around it.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(CalcDisplay)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("S" to shiftOn, "A" to alphaOn, "M" to memOn, angleMode.name to true)
                    .forEach { (lbl, on) ->
                        Text(
                            lbl, fontSize = 9.sp,
                            color = if (on) CalcDisplayFg else CalcDisplay,
                            fontFamily = FontFamily.Monospace
                        )
                    }
            }
            Spacer(Modifier.height(2.dp))
            Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                Text(
                    expression.ifBlank { " " }, fontSize = 13.sp,
                    color = CalcDisplaySub, fontFamily = FontFamily.Monospace, maxLines = 1
                )
            }
            Spacer(Modifier.height(2.dp))
            Box(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    display, fontSize = 40.sp, color = CalcDisplayFg,
                    fontFamily = FontFamily.Monospace, textAlign = TextAlign.End, maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CalcRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

// ── SciBtn with press-scale animation ────────────────────────────────────────
// WHY animateFloatAsState for scale?
// Physical calculator keys depress when pressed. animateFloatAsState shrinks
// the button to 92% when pressed (pressed = true) then springs back to 100%.
// spring(stiffness = High) makes the return snappy — like a mechanical key
// bouncing back. This micro-animation gives tactile feedback on a touchscreen.
//
// WHY animateColorAsState for background?
// When pressed, the background lightens slightly (blended with white 20%) to
// mimic the visual highlight of a key being depressed under light. The 80ms
// tween is fast so it does not feel sluggish — it matches the speed of a
// real button press.
@Composable
fun RowScope.SciBtn(
    token: String, displayLabel: String,
    bg: Color, fg: Color,
    onClick: (String) -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "btn_scale"
    )
    val bgAnimated by animateColorAsState(
        targetValue = if (pressed) bg.copy(alpha = 0.75f) else bg,
        animationSpec = tween(80),
        label = "btn_color"
    )

    Button(
        onClick = {
            pressed = true
            onClick(token)
            pressed = false
        },
        modifier = Modifier
            .weight(1f)
            .height(60.dp)
            .scale(scale),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgAnimated, contentColor = fg),
        // WHY elevation = 2.dp on keys?
        // Each key sits 2.dp above the faceplate, just like a physical key
        // standing proud of the keyboard surface. Lower than the faceplate
        // shadow (12.dp) so the hierarchy is: device > key > label.
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(displayLabel, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
    }
}

// ── SmallToggle with animated background ─────────────────────────────────────
// WHY animate the toggle color?
// SHIFT and ALPHA are mode-indicator buttons. When they turn on, the
// background animates from dark (inactive) to CalcKeyAccent (active) over
// 150ms. This makes the state change unmistakable — the user always knows
// whether SHIFT is active without reading a label.
@Composable
fun SmallToggle(label: String, active: Boolean, bg: Color, onClick: () -> Unit) {
    val bgAnim by animateColorAsState(
        targetValue = if (active) CalcKeyAccent else bg,
        animationSpec = tween(150),
        label = "toggle_bg"
    )
    val fgAnim by animateColorAsState(
        targetValue = if (active) CalcTextDark else CalcTextDim,
        animationSpec = tween(150),
        label = "toggle_fg"
    )
    Button(
        onClick = onClick,
        modifier = Modifier.height(26.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgAnim, contentColor = fgAnim),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
    ) {
        Text(label, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun EnergyHeader(title: String, subtitle: String) {
    // WHY shadow on the header?
    // The header acts as the "label panel" on a real calculator mode section.
    // shadow(4.dp) lifts it slightly above the white form area below,
    // visually marking it as a title block rather than just another card.
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(CalcDisplay)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                title, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = CalcDisplayFg, fontFamily = FontFamily.Monospace
            )
            Text(
                subtitle, fontSize = 12.sp,
                color = CalcDisplaySub, fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun EnergyField(label: String, value: String, placeholder: String, onValueChange: (String) -> Unit) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(
            label, fontSize = 12.sp, color = CalcDisplaySub,
            fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder, fontSize = 14.sp, color = Color(0xFF4A6A5A),
                    fontFamily = FontFamily.Monospace
                )
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            textStyle = TextStyle(
                fontSize = 16.sp, fontFamily = FontFamily.Monospace,
                color = CalcDisplayFg
            ),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = CalcKeyAccent,
                unfocusedBorderColor = CalcKeyMid,
                focusedContainerColor   = CalcDisplay,
                unfocusedContainerColor = CalcDisplay,
                cursorColor = CalcDisplayFg
            )
        )
    }
}

@Composable
fun EnergyBtn(label: String, onClick: () -> Unit) {
    // WHY shadow on the calculate button?
    // This is the primary action button on each energy calculator. shadow(6.dp)
    // makes it the most elevated element on the form, drawing the eye and
    // communicating "tap me" without needing extra colour emphasis beyond the
    // CalcKeyAccent already applied.
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CalcKeyAccent,
            contentColor   = CalcTextDark
        )
    ) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun ResultCard(content: @Composable ColumnScope.() -> Unit) {
    // WHY shadow on the result card?
    // The result card appears dynamically after calculation. shadow(8.dp) makes
    // it pop forward — drawing attention to the new content. Higher than the
    // input fields (0.dp) so it reads as the "answer" floating above the form.
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(CalcDisplay)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        content = content
    )
}

@Composable
fun ResultRow(label: String, value: String, highlight: Boolean = false, sub: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            fontSize = if (sub) 10.sp else 12.sp,
            color = CalcDisplaySub,
            fontFamily = FontFamily.Monospace
        )
        Text(
            value,
            fontSize = if (highlight) 15.sp else 12.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) CalcKeyAccent else CalcDisplayFg,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun MonoBox(text: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(CalcDisplay)
            .padding(14.dp)
    ) {
        Text(
            text.trimEnd(), fontSize = 12.sp, color = CalcDisplayFg,
            fontFamily = FontFamily.Monospace, lineHeight = 20.sp
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// EXPRESSION EVALUATOR  (shunting-yard + function resolver)
// ═════════════════════════════════════════════════════════════════════════════
internal fun evalExpression(raw: String, angleMode: AngleMode): Double {
    fun toRad(v: Double)   = if (angleMode == AngleMode.DEG) Math.toRadians(v) else v
    fun fromRad(v: Double) = if (angleMode == AngleMode.DEG) Math.toDegrees(v) else v

    var e = raw.trim()
        .replace("×", "*").replace("÷", "/")
        .replace("π", Math.PI.toString())

    e = Regex("(?<![0-9.])e(?![0-9+\\-])").replace(e, Math.E.toString())
    e = e.replace(Regex("([0-9.])\\("), "$1*(")
    e = e.replace(Regex("\\)([0-9.(])"), ")*$1")

    val fnRegex = Regex("(sin|cos|tan|asin|acos|atan|ln|log|√|abs|n!)\\(([^()]+)\\)")
    var iterations = 0
    while (fnRegex.containsMatchIn(e) && iterations++ < 50) {
        e = fnRegex.replace(e) { m ->
            val fn  = m.groupValues[1]
            val arg = evalSimple(m.groupValues[2])
            val res = when (fn) {
                "sin"  -> sin(toRad(arg))
                "cos"  -> cos(toRad(arg))
                "tan"  -> tan(toRad(arg))
                "asin" -> fromRad(asin(arg))
                "acos" -> fromRad(acos(arg))
                "atan" -> fromRad(atan(arg))
                "ln"   -> ln(arg)
                "log"  -> log10(arg)
                "√"    -> sqrt(arg)
                "abs"  -> abs(arg)
                "n!"   -> (1..arg.toInt()).fold(1L) { a, i -> a * i }.toDouble()
                else   -> arg
            }
            res.toString()
        }
    }

    e = e.replace("²", "^2").replace("³", "^3")
    e = Regex("([0-9.]+)[Ee]([+\\-]?[0-9]+)").replace(e) { m ->
        (m.groupValues[1].toDouble() * 10.0.pow(m.groupValues[2].toInt())).toString()
    }

    return evalSimple(e)
}

private fun evalSimple(expr: String): Double {
    val tokens = tokenizeSimple(expr.trim())
    val output = ArrayDeque<Double>()
    val ops    = ArrayDeque<String>()

    fun prec(op: String) = when (op) { "+","-" -> 1; "*","/" -> 2; "^" -> 3; else -> 0 }
    fun applyOp() {
        val op = ops.removeLast()
        val b  = output.removeLast()
        val a  = if (output.isEmpty()) 0.0 else output.removeLast()
        output.addLast(when (op) {
            "+" -> a + b;  "-" -> a - b
            "*" -> a * b
            "/" -> { if (b == 0.0) throw ArithmeticException("Div by zero"); a / b }
            "^" -> a.pow(b)
            else -> b
        })
    }

    for (tok in tokens) {
        when {
            tok.toDoubleOrNull() != null -> output.addLast(tok.toDouble())
            tok == "(" -> ops.addLast("(")
            tok == ")" -> {
                while (ops.isNotEmpty() && ops.last() != "(") applyOp()
                if (ops.isNotEmpty()) ops.removeLast()
            }
            tok in listOf("+","-","*","/","^") -> {
                while (ops.isNotEmpty() && ops.last() != "(" && prec(ops.last()) >= prec(tok))
                    applyOp()
                ops.addLast(tok)
            }
        }
    }
    while (ops.isNotEmpty()) applyOp()
    return output.lastOrNull() ?: 0.0
}

private fun tokenizeSimple(expr: String): List<String> {
    val out = mutableListOf<String>()
    var i = 0
    while (i < expr.length) {
        val ch = expr[i]
        when {
            ch.isDigit() || ch == '.' -> {
                var j = i
                while (j < expr.length && (expr[j].isDigit() || expr[j] == '.')) j++
                out.add(expr.substring(i, j)); i = j
            }
            ch == '-' && (out.isEmpty() || out.last() in listOf("(","^","*","/","+","-")) -> {
                var j = i + 1
                while (j < expr.length && (expr[j].isDigit() || expr[j] == '.')) j++
                out.add(expr.substring(i, j)); i = j
            }
            ch == '%' -> { out.add("*"); out.add("0.01"); i++ }
            else -> { out.add(ch.toString()); i++ }
        }
    }
    return out
}

// ── Preview ────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF1A2332)
@Composable
fun CalculatorScreenPreview() {
    MaterialTheme { CalculatorScreen() }
}