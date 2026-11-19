package com.example.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AssistantMode
import com.example.ui.chat.ChatScreen
import com.example.ui.chat.ChatViewModel
import com.example.ui.components.CosmicBackground
import com.example.ui.create.CreateScreen
import com.example.ui.create.CreateViewModel
import com.example.ui.history.HistoryScreen
import com.example.ui.history.HistoryViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.profile.ProfileScreen
import com.example.ui.profile.ProfileViewModel
import com.example.ui.profile.SettingsScreen
import com.example.ui.study.StudyScreen
import com.example.ui.study.StudyViewModel
import com.example.ui.theme.NovaLavender
import com.example.ui.theme.NovaNavyDeep
import com.example.ui.theme.NovaTextSecondary
import com.example.ui.theme.NovaViolet
import com.example.ui.voice.VoiceScreen
import com.example.ui.voice.VoiceViewModel
import com.example.ui.writing.WritingScreen
import com.example.ui.writing.WritingViewModel

object NovaRoutes {
    const val HOME = "home"
    const val CHAT = "chat"
    const val CHAT_WITH_ARGS = "chat?sessionId={sessionId}&prompt={prompt}"
    const val CREATE = "create"
    const val PROFILE = "profile"
    const val VOICE = "voice"
    const val STUDY = "study"
    const val WRITING = "writing"
    const val HISTORY = "history"
    const val SETTINGS = "settings"

    fun chatRoute(sessionId: String? = null, prompt: String? = null): String {
        val s = Uri.encode(sessionId.orEmpty())
        val p = Uri.encode(prompt.orEmpty())
        return "chat?sessionId=$s&prompt=$p"
    }
}

private data class BottomNavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

private val bottomTabs = listOf(
    BottomNavTab(NovaRoutes.HOME, "Home", Icons.Rounded.Home, Icons.Outlined.Home, "nav_home"),
    BottomNavTab(NovaRoutes.CHAT, "Chat", Icons.AutoMirrored.Rounded.Chat, Icons.AutoMirrored.Outlined.Chat, "nav_chat"),
    BottomNavTab(NovaRoutes.CREATE, "Create", Icons.Rounded.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_create"),
    BottomNavTab(NovaRoutes.PROFILE, "Profile", Icons.Rounded.Person, Icons.Outlined.Person, "nav_profile")
)

@Composable
fun NovaAppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute == NovaRoutes.HOME ||
        currentRoute?.startsWith(NovaRoutes.CHAT) == true ||
        currentRoute == NovaRoutes.CREATE ||
        currentRoute == NovaRoutes.PROFILE

    CosmicBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = NovaNavyDeep.copy(alpha = 0.96f),
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        bottomTabs.forEach { tab ->
                            val selected = if (tab.route == NovaRoutes.CHAT) {
                                currentRoute?.startsWith(NovaRoutes.CHAT) == true
                            } else {
                                currentRoute == tab.route
                            }
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    val target = if (tab.route == NovaRoutes.CHAT) {
                                        NovaRoutes.chatRoute()
                                    } else {
                                        tab.route
                                    }
                                    navController.navigate(target) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NovaLavender,
                                    selectedTextColor = NovaLavender,
                                    indicatorColor = NovaViolet.copy(alpha = 0.24f),
                                    unselectedIconColor = NovaTextSecondary,
                                    unselectedTextColor = NovaTextSecondary
                                ),
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NovaRoutes.HOME
                ) {
                    composable(NovaRoutes.HOME) {
                        val homeVm: HomeViewModel = viewModel(factory = HomeViewModel.provideFactory())
                        HomeScreen(
                            viewModel = homeVm,
                            onOpenChat = { sessionId, prompt ->
                                navController.navigate(NovaRoutes.chatRoute(sessionId, prompt))
                            },
                            onOpenCreate = {
                                navController.navigate(NovaRoutes.CREATE) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onOpenVoice = { navController.navigate(NovaRoutes.VOICE) },
                            onOpenStudy = { navController.navigate(NovaRoutes.STUDY) },
                            onOpenWriting = { navController.navigate(NovaRoutes.WRITING) },
                            onOpenHistory = { navController.navigate(NovaRoutes.HISTORY) },
                            onOpenSettings = { navController.navigate(NovaRoutes.SETTINGS) }
                        )
                    }

                    composable(
                        route = NovaRoutes.CHAT_WITH_ARGS,
                        arguments = listOf(
                            navArgument("sessionId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = ""
                            },
                            navArgument("prompt") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = ""
                            }
                        )
                    ) { entry ->
                        val sessionId = entry.arguments?.getString("sessionId")?.takeIf { it.isNotBlank() }
                        val prompt = entry.arguments?.getString("prompt")?.takeIf { it.isNotBlank() }
                        val chatVm: ChatViewModel = viewModel(
                            key = "chat_${sessionId.orEmpty()}_${prompt.orEmpty()}",
                            factory = ChatViewModel.provideFactory(
                                initialSessionId = sessionId,
                                initialPrompt = prompt,
                                initialMode = AssistantMode.CHAT
                            )
                        )
                        ChatScreen(
                            viewModel = chatVm,
                            onOpenHistory = { navController.navigate(NovaRoutes.HISTORY) }
                        )
                    }

                    composable(NovaRoutes.CREATE) {
                        val createVm: CreateViewModel = viewModel(factory = CreateViewModel.provideFactory())
                        CreateScreen(viewModel = createVm)
                    }

                    composable(NovaRoutes.PROFILE) {
                        val profileVm: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory())
                        ProfileScreen(
                            viewModel = profileVm,
                            onOpenHistory = { navController.navigate(NovaRoutes.HISTORY) },
                            onOpenSettings = { navController.navigate(NovaRoutes.SETTINGS) }
                        )
                    }

                    composable(NovaRoutes.VOICE) {
                        val voiceVm: VoiceViewModel = viewModel(factory = VoiceViewModel.provideFactory())
                        VoiceScreen(
                            viewModel = voiceVm,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(NovaRoutes.STUDY) {
                        val studyVm: StudyViewModel = viewModel(factory = StudyViewModel.provideFactory())
                        StudyScreen(
                            viewModel = studyVm,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(NovaRoutes.WRITING) {
                        val writingVm: WritingViewModel = viewModel(factory = WritingViewModel.provideFactory())
                        WritingScreen(
                            viewModel = writingVm,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(NovaRoutes.HISTORY) {
                        val historyVm: HistoryViewModel = viewModel(factory = HistoryViewModel.provideFactory())
                        HistoryScreen(
                            viewModel = historyVm,
                            onBack = { navController.popBackStack() },
                            onOpenSession = { sessionId ->
                                navController.popBackStack()
                                navController.navigate(NovaRoutes.chatRoute(sessionId = sessionId))
                            }
                        )
                    }

                    composable(NovaRoutes.SETTINGS) {
                        val profileVm: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory())
                        SettingsScreen(
                            viewModel = profileVm,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
