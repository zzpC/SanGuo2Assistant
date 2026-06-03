package com.sanguo2.assistant.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sanguo2.assistant.ui.formation.FormationRecommendScreen
import com.sanguo2.assistant.ui.garrison.ForceNoteListScreen
import com.sanguo2.assistant.ui.garrison.ForceNoteEditScreen
import com.sanguo2.assistant.ui.garrison.ForceNoteDetailScreen
import com.sanguo2.assistant.ui.settings.SettingsScreen
import com.sanguo2.assistant.ui.soldier.SoldierQueryScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Soldier : Screen("soldier", "兵种查询", Icons.Default.Home)
    data object Formation : Screen("formation", "阵型推荐", Icons.Default.Security)
    data object Garrison : Screen("garrison", "阵容备注", Icons.Default.NoteAlt)
    data object Settings : Screen("settings", "设置", Icons.Default.Settings)
}

sealed class GarrisonRoute(val route: String) {
    data object List : GarrisonRoute("garrison_list")
    data object Add : GarrisonRoute("garrison_add")
    data object Edit : GarrisonRoute("garrison_edit/{noteId}") {
        fun createRoute(noteId: Long) = "garrison_edit/$noteId"
    }
    data object Detail : GarrisonRoute("garrison_detail/{noteId}") {
        fun createRoute(noteId: Long) = "garrison_detail/$noteId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val bottomScreens = listOf(Screen.Garrison, Screen.Formation, Screen.Soldier, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Garrison.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Soldier.route) {
                SoldierQueryScreen()
            }
            composable(Screen.Formation.route) {
                FormationRecommendScreen()
            }
            composable(Screen.Garrison.route) {
                ForceNoteListScreen(
                    onNavigateToAdd = { navController.navigate(GarrisonRoute.Add.route) },
                    onNavigateToDetail = { noteId -> navController.navigate(GarrisonRoute.Detail.createRoute(noteId)) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(GarrisonRoute.Add.route) {
                ForceNoteEditScreen(
                    isEdit = false,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = GarrisonRoute.Edit.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
                ForceNoteEditScreen(
                    noteId = noteId,
                    isEdit = true,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = GarrisonRoute.Detail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
                ForceNoteDetailScreen(
                    noteId = noteId,
                    onNavigateToEdit = { id -> navController.navigate(GarrisonRoute.Edit.createRoute(id)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
