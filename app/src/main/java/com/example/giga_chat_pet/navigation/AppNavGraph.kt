package com.example.giga_chat_pet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.giga_chat_pet.ui.chat.ChatScreen
import com.example.giga_chat_pet.ui.login.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            ChatScreen(navController = navController)
        }
    }
}
