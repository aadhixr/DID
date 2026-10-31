package com.diskwala.recreated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.diskwala.recreated.ui.screens.*
import com.diskwala.recreated.ui.theme.DarkBackground
import com.diskwala.recreated.ui.theme.DarkThemeColors
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = DarkThemeColors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Stream : Screen("stream", "Stream")
    object Downloads : Screen("downloads", "Downloads")
    object You : Screen("you", "History")
    object Player : Screen("player/{mediaId}", "Player") {
        fun createRoute(mediaId: String): String {
            val encoded = URLEncoder.encode(mediaId, StandardCharsets.UTF_8.toString())
            return "player/$encoded"
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(Screen.Stream, Screen.Downloads, Screen.You)
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Text(screen.title.take(1)) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Stream.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Stream.route) {
                StreamInputScreen(onStreamUrl = { mediaTarget ->
                    navController.navigate(Screen.Player.createRoute(mediaTarget))
                })
            }
            composable(Screen.Downloads.route) {
                DownloadsScreen(downloads = emptyList(), onPlayLocal = { uri ->
                    navController.navigate(Screen.Player.createRoute(uri))
                })
            }
            composable(Screen.You.route) {
                YouScreen(recentFiles = emptyList(), onClearHistory = {})
            }
            composable(Screen.Player.route) { backStackEntry ->
                val encodedId = backStackEntry.arguments?.getString("mediaId") ?: ""
                val mediaId = try {
                    URLDecoder.decode(encodedId, StandardCharsets.UTF_8.toString())
                } catch (e: Exception) {
                    encodedId
                }
                CustomPlayerScreen(
                    mediaId = mediaId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
