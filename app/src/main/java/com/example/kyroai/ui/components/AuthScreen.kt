package com.example.kyroai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kyroai.ui.theme.KyroAccentBlue
import com.example.kyroai.ui.theme.KyroBlack
import com.example.kyroai.ui.theme.KyroDarkBorder
import com.example.kyroai.ui.theme.KyroDarkCard
import com.example.kyroai.ui.theme.KyroDarkSurface
import com.example.kyroai.ui.theme.KyroTextPrimaryDark
import com.example.kyroai.ui.theme.KyroTextSecondaryDark
import com.example.kyroai.ui.theme.KyroTextTertiaryDark

@Composable
fun AuthScreen(
    onLoginSuccess: (username: String) -> Unit
) {
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KyroBlack),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = KyroDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, KyroDarkBorder)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(KyroAccentBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "K",
                        color = KyroAccentBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome to Kyro AI",
                    style = MaterialTheme.typography.headlineMedium,
                    color = KyroTextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Your personal AI workspace & assistant",
                    color = KyroTextSecondaryDark,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Username field
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    placeholder = { Text("Enter your name", color = KyroTextTertiaryDark) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = KyroAccentBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KyroAccentBlue,
                        unfocusedBorderColor = KyroDarkBorder,
                        focusedContainerColor = KyroDarkCard,
                        unfocusedContainerColor = KyroDarkCard,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    placeholder = { Text("Passcode (4+ chars)", color = KyroTextTertiaryDark) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = KyroAccentBlue) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KyroAccentBlue,
                        unfocusedBorderColor = KyroDarkBorder,
                        focusedContainerColor = KyroDarkCard,
                        unfocusedContainerColor = KyroDarkCard,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val trimmedName = usernameInput.trim()
                        if (trimmedName.isBlank()) {
                            errorMessage = "Please enter your name"
                            return@Button
                        }
                        if (passwordInput.length < 4) {
                            errorMessage = "Passcode must be at least 4 characters"
                            return@Button
                        }
                        onLoginSuccess(trimmedName)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KyroAccentBlue)
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
