package com.example.kyroai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.kyroai.ui.components.AuthScreen
import com.example.kyroai.ui.components.SplashScreen
import com.example.kyroai.ui.screens.MainChatScreen
import com.example.kyroai.ui.theme.KyroAITheme
import com.example.kyroai.ui.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeState by viewModel.userPreferences.theme.collectAsState()
            val isDarkTheme = themeState == "dark"

            KyroAITheme(darkTheme = isDarkTheme) {
                KyroApp(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun KyroApp(viewModel: ChatViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    val isLoggedIn by viewModel.userPreferences.isLoggedIn.collectAsState()

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    } else if (!isLoggedIn) {
        AuthScreen(
            onLoginSuccess = { username ->
                viewModel.userPreferences.setUsername(username)
                viewModel.userPreferences.setLoggedIn(true)
            }
        )
    } else {
        MainChatScreen(
            viewModel = viewModel,
            onLogout = {
                viewModel.userPreferences.setLoggedIn(false)
            }
        )
    }
}
