package my.com.a221491_amiraizatbinharith_nelson_project2

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import my.com.a221491_amiraizatbinharith_nelson_project2.auth.AuthViewModel
import my.com.a221491_amiraizatbinharith_nelson_project2.auth.ForgotPasswordScreen
import my.com.a221491_amiraizatbinharith_nelson_project2.auth.LoginScreen
import my.com.a221491_amiraizatbinharith_nelson_project2.auth.SignUpScreen
import my.com.a221491_amiraizatbinharith_nelson_project2.data.EcoViewModel
import my.com.a221491_amiraizatbinharith_nelson_project2.ui.*

// ── Routes ────────────────────────────────────────────────────────────────────
object Routes {
    // Auth
    const val LOGIN          = "login"
    const val SIGN_UP        = "sign_up"
    const val FORGOT_PASSWORD = "forgot_password"

    const val ONBOARDING      = "onboarding"

    // Main app
    const val HOME         = "home"
    const val PROGRESS     = "progress"
    const val CALCULATOR   = "calculator"
    const val QUIZ         = "quiz"
    const val PROFILE      = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val ECO_MAP      = "eco_map"

    const val TOPIC = "topic/{topicIndex}"
    fun topicRoute(index: Int) = "topic/$index"
}

// ── Nav tab model ─────────────────────────────────────────────────────────────
private data class NavTab(
    val route        : String,
    val label        : String,
    val icon         : ImageVector,
    val iconSelected : ImageVector = icon
)

private val NAV_TABS = listOf(
    NavTab(Routes.HOME,     "Home",     Icons.Outlined.Home,   Icons.Filled.Home),
    NavTab(Routes.PROGRESS, "Progress", Icons.Default.Calculate),
    NavTab(Routes.ECO_MAP,  "Map",      Icons.Outlined.Map,    Icons.Filled.Map),
    NavTab(Routes.QUIZ,     "Quiz",     Icons.Outlined.Star,   Icons.Filled.Star),
    NavTab(Routes.PROFILE,  "Profile",  Icons.Outlined.Person, Icons.Filled.Person)
)

private val MAIN_ROUTES = (NAV_TABS.map { it.route } + Routes.CALCULATOR).toSet()

// ── Root composable ───────────────────────────────────────────────────────────
@Composable
fun AppNavigation(
    authVm   : AuthViewModel    = viewModel(),
    ecoVm    : EcoViewModel     = viewModel(),
    profileVm: ProfileViewModel = viewModel(),
    photoVm  : PhotoViewModel   = viewModel()   // FIX: hoisted here so it can be reset on account switch
) {
    val currentUser  by authVm.currentUser.collectAsStateWithLifecycle()
    val sessionEpoch by authVm.sessionEpoch.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val currentEntry  by navController.currentBackStackEntryAsState()
    val currentRoute  = currentEntry?.destination?.route

    // ── FIX: react to account switches (login / signup / logout) ─────────────
    // ProfileViewModel and PhotoViewModel are long-lived singletons scoped to
    // AppNavigation's lifecycle — they are NOT recreated when a different user
    // logs in. Without this effect, the new account would keep showing the
    // PREVIOUS account's cached name, university, course, bio, and profile
    // photo (Room's user_profile table is a single shared row, and
    // PhotoViewModel._photoUrl is in-memory only).
    //
    // AuthViewModel bumps sessionEpoch:
    //   - after login()  succeeds
    //   - after signUp() succeeds
    //   - after logout()
    //   - on initial app launch if already logged in
    //
    // Each bump:
    //   1. profileVm.syncAfterLogin() — re-reads Room (now cleared/repopulated
    //      by AuthViewModel's repo.clearLocalUserData() + syncFromCloud())
    //   2. photoVm.resetForNewSession() — clears the stale cached photo URL and
    //      reloads from Firestore for whichever uid is now active
    LaunchedEffect(sessionEpoch) {
        if (sessionEpoch > 0) {
            profileVm.syncAfterLogin()
            photoVm.resetForNewSession()
        }
    }

    // FIX: lock the start destination on FIRST composition only.
    // `if (currentUser != null) HOME else LOGIN` was recomputed on every
    // recomposition. signUp() sets _currentUser.value BEFORE _authState
    // becomes Success, so this composable recomposes, startDest flips
    // LOGIN -> HOME. NavHost's startDestination then resolves to HOME,
    // racing with / overriding the ONBOARDING navigation that
    // SignUpScreen's LaunchedEffect was about to perform.
    // remember{} freezes this for the lifetime of this composable instance.
    // All later navigation happens explicitly via navController.navigate(...).
    val startDest = remember { if (currentUser != null) Routes.HOME else Routes.LOGIN }

    Scaffold(
        containerColor = SurfaceBg,
        bottomBar = {
            if (currentRoute in MAIN_ROUTES) {
                AppBottomNavBar(
                    currentRoute  = currentRoute,
                    onTabSelected = { route ->
                        if (currentRoute !in NAV_TABS.map { it.route }) {
                            navController.popBackStack()
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = startDest,
            modifier         = Modifier.padding(innerPadding)
        ) {

            // ── AUTH SCREENS ──────────────────────────────────────────────────

            composable(Routes.LOGIN) {
                LoginScreen(
                    authVm = authVm,
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(Routes.SIGN_UP)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Routes.FORGOT_PASSWORD)
                    }
                )
            }

            composable(Routes.SIGN_UP) {
                SignUpScreen(
                    authVm = authVm,
                    onSignUpSuccess = {
                        navController.navigate(Routes.ONBOARDING) {   // ← was Routes.HOME
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    profileVm = profileVm,
                    onFinish  = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    authVm = authVm,
                    onBack = { navController.popBackStack() }
                )
            }

            // ── MAIN APP SCREENS ──────────────────────────────────────────────

            composable(Routes.HOME) {
                HomeContent(
                    vm = ecoVm,
                    onOpenTopic      = { idx -> navController.navigate(Routes.topicRoute(idx)) },
                    onOpenQuiz       = {
                        navController.navigate(Routes.QUIZ) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    onOpenCalculator = {
                        navController.navigate(Routes.CALCULATOR) { launchSingleTop = true }
                    }
                )
            }

            composable(Routes.PROGRESS) {
                ProgressScreen(
                    vm = ecoVm,
                    onOpenTopic = { idx -> navController.navigate(Routes.topicRoute(idx)) }
                )
            }

            composable(Routes.ECO_MAP) { EcoMapScreen() }

            composable(Routes.QUIZ) {
                QuizScreen(
                    vm = ecoVm,
                    onOpenTopic = { idx -> navController.navigate(Routes.topicRoute(idx)) }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    profileVm = profileVm,
                    ecoVm     = ecoVm,
                    authVm    = authVm,
                    photoVm   = photoVm,   // FIX: pass the SAME hoisted instance — never a fresh viewModel()
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.EDIT_PROFILE) {
                EditProfileScreen(
                    profileVm = profileVm,
                    onBack    = { navController.popBackStack() }
                )
            }

            composable(Routes.CALCULATOR) { CalculatorScreen() }

            composable(Routes.TOPIC) { backStack ->
                val topicIndex = backStack.arguments
                    ?.getString("topicIndex")?.toIntOrNull() ?: 0
                TopicScreen(
                    topicIndex = topicIndex,
                    vm         = ecoVm,
                    onBack     = { navController.popBackStack() }
                )
            }
        }
    }
}

// ── Bottom Navigation Bar ─────────────────────────────────────────────────────
@Composable
fun AppBottomNavBar(
    currentRoute : String?,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(containerColor = CardBg, tonalElevation = 0.dp) {
        NAV_TABS.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick  = { onTabSelected(tab.route) },
                icon = {
                    Icon(if (selected) tab.iconSelected else tab.icon,
                        contentDescription = tab.label)
                },
                label = { Text(tab.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = GreenPrimary,
                    selectedTextColor   = GreenPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor      = Color.Transparent
                )
            )
        }
    }
}