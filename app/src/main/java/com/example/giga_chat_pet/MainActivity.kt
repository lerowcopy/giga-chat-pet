package com.example.giga_chat_pet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.giga_chat_pet.navigation.AppNavGraph
import com.example.giga_chat_pet.navigation.Screen
import com.example.giga_chat_pet.ui.theme.GigachatpetTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GigachatpetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var isLoggedIn by remember { mutableStateOf(false) }
                    var isChecked by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                        isChecked = true
                    }

                    if (isChecked) {
                        val startDestination = if (isLoggedIn) Screen.ChatList.route else Screen.Login.route
                        AppNavGraph(navController = navController, startDestination = startDestination)
                    }
                }
            }
        }
    }
}
