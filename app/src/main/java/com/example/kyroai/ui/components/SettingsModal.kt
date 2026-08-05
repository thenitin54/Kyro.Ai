package com.example.kyroai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kyroai.data.local.UserPreferences
import com.example.kyroai.ui.theme.KyroAccentBlue
import com.example.kyroai.ui.theme.KyroDarkBorder
import com.example.kyroai.ui.theme.KyroDarkCard
import com.example.kyroai.ui.theme.KyroDarkSurface
import com.example.kyroai.ui.theme.KyroTextPrimaryDark
import com.example.kyroai.ui.theme.KyroTextSecondaryDark
import com.example.kyroai.ui.theme.KyroTextTertiaryDark

@Composable
fun SettingsModal(
    userPreferences: UserPreferences,
    onDismiss: () -> Unit
) {
    val apiKey by userPreferences.apiKey.collectAsState()
    val useDemoMode by userPreferences.useDemoMode.collectAsState()
    val selectedModel by userPreferences.selectedModel.collectAsState()
    val selectedLanguage by userPreferences.selectedLanguage.collectAsState()
    val systemPersona by userPreferences.systemPersona.collectAsState()
    val theme by userPreferences.theme.collectAsState()

    var apiKeyInput by remember { mutableStateOf(apiKey) }
    var personaInput by remember { mutableStateOf(systemPersona) }

    val models = listOf(
        "gemini-3.5-flash" to "Gemini 3.5 Flash (Fast, General)",
        "gemini-3.1-pro-preview" to "Gemini 3.1 Pro (Complex Reasoning)",
        "gemini-2.5-flash-image" to "Gemini 2.5 Flash Image (Multimodal)"
    )

    val languages = listOf(
        "en" to "English",
        "hi" to "Hindi",
        "hinglish" to "Hinglish",
        "es" to "Spanish",
        "fr" to "French",
        "de" to "German"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = KyroDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, KyroDarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kyro AI Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = KyroTextPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = KyroTextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mode Section: Demo vs Gemini API
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(KyroDarkCard)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Demo Mode", color = KyroTextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(text = "Use local simulated AI without API key", color = KyroTextSecondaryDark, fontSize = 12.sp)
                    }
                    Switch(
                        checked = useDemoMode,
                        onCheckedChange = { userPreferences.setUseDemoMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = KyroAccentBlue)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gemini API Key Section
                if (!useDemoMode) {
                    Text(text = "GEMINI API KEY", color = KyroTextTertiaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = {
                            apiKeyInput = it
                            userPreferences.setApiKey(it)
                        },
                        placeholder = { Text("Paste your Gemini API key here...", color = KyroTextTertiaryDark, fontSize = 13.sp) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = KyroAccentBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
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
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // AI Model Selection
                Text(text = "SELECT AI MODEL", color = KyroTextTertiaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for ((modelId, modelName) in models) {
                        val isSelected = selectedModel == modelId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) KyroAccentBlue.copy(alpha = 0.15f) else KyroDarkCard)
                                .border(1.dp, if (isSelected) KyroAccentBlue else KyroDarkBorder, RoundedCornerShape(10.dp))
                                .clickable { userPreferences.setSelectedModel(modelId) }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = if (isSelected) KyroAccentBlue else KyroTextSecondaryDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = modelName,
                                    color = if (isSelected) KyroAccentBlue else KyroTextPrimaryDark,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Persona / System Instruction
                Text(text = "CUSTOM SYSTEM PERSONA", color = KyroTextTertiaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = personaInput,
                    onValueChange = {
                        personaInput = it
                        userPreferences.setSystemPersona(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KyroAccentBlue,
                        unfocusedBorderColor = KyroDarkBorder,
                        focusedContainerColor = KyroDarkCard,
                        unfocusedContainerColor = KyroDarkCard,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Language Selection
                Text(text = "RESPONSE LANGUAGE", color = KyroTextTertiaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for ((code, name) in languages.take(3)) {
                        val isSelected = selectedLanguage == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) KyroAccentBlue else KyroDarkCard)
                                .clickable { userPreferences.setSelectedLanguage(code) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                color = if (isSelected) Color.White else KyroTextSecondaryDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KyroAccentBlue)
                ) {
                    Text("Save & Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
