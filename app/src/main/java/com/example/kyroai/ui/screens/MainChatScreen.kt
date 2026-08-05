package com.example.kyroai.ui.screens

import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kyroai.R
import com.example.kyroai.ui.components.DrawerContent
import com.example.kyroai.ui.components.MessageBubble
import com.example.kyroai.ui.components.ProfileModal
import com.example.kyroai.ui.components.SettingsModal
import com.example.kyroai.ui.theme.KyroAccentBlue
import com.example.kyroai.ui.theme.KyroBlack
import com.example.kyroai.ui.theme.KyroDarkBorder
import com.example.kyroai.ui.theme.KyroDarkCard
import com.example.kyroai.ui.theme.KyroDarkSurface
import com.example.kyroai.ui.theme.KyroSuccess
import com.example.kyroai.ui.theme.KyroTextPrimaryDark
import com.example.kyroai.ui.theme.KyroTextSecondaryDark
import com.example.kyroai.ui.theme.KyroTextTertiaryDark
import com.example.kyroai.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MainChatScreen(
    viewModel: ChatViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val sessions by viewModel.sessions.collectAsState()
    val currentSessionId by viewModel.currentSessionId.collectAsState()
    val currentMessages by viewModel.currentMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val attachedUri by viewModel.attachedImageUri.collectAsState()

    val showSettings by viewModel.showSettingsModal.collectAsState()
    val showProfile by viewModel.showProfileModal.collectAsState()

    val useDemoMode by viewModel.userPreferences.useDemoMode.collectAsState()
    val username by viewModel.userPreferences.username.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Initialize Text-To-Speech engine
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialized successfully
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine.stop()
            ttsEngine.shutdown()
        }
    }

    // Photo picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setAttachedImageUri(it.toString()) }
    }

    // Auto-scroll when new messages arrive
    LaunchedEffect(currentMessages.size) {
        if (currentMessages.isNotEmpty()) {
            listState.animateScrollToItem(currentMessages.size - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = KyroDarkSurface,
                modifier = Modifier.width(280.dp)
            ) {
                DrawerContent(
                    sessions = sessions,
                    currentSessionId = currentSessionId,
                    onSelectSession = { id ->
                        viewModel.selectSession(id)
                        scope.launch { drawerState.close() }
                    },
                    onNewChat = {
                        viewModel.createNewChat()
                        scope.launch { drawerState.close() }
                    },
                    onPinSession = { id, pinned -> viewModel.pinSession(id, pinned) },
                    onDeleteSession = { id -> viewModel.deleteSession(id) },
                    onRenameSession = { id, title -> viewModel.updateSessionTitle(id, title) },
                    onOpenSettings = {
                        viewModel.toggleSettingsModal(true)
                        scope.launch { drawerState.close() }
                    },
                    onOpenProfile = {
                        viewModel.toggleProfileModal(true)
                        scope.launch { drawerState.close() }
                    },
                    username = username
                )
            }
        }
    ) {
        Scaffold(
            containerColor = KyroBlack,
            topBar = {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(KyroDarkSurface)
                        .border(1.dp, KyroDarkBorder)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = KyroTextPrimaryDark)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            val activeTitle = sessions.find { it.sessionId == currentSessionId }?.title ?: "Kyro AI"
                            Text(
                                text = activeTitle,
                                color = KyroTextPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 180.dp)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(KyroSuccess)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (useDemoMode) "Kyro Thinking (Demo)" else "Kyro Thinking (Gemini)",
                                    color = KyroTextTertiaryDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Row {
                        IconButton(onClick = { viewModel.createNewChat() }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "New Chat", tint = KyroAccentBlue)
                        }
                        IconButton(onClick = { viewModel.toggleSettingsModal(true) }) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = KyroTextSecondaryDark)
                        }
                        IconButton(onClick = { viewModel.toggleProfileModal(true) }) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = KyroTextSecondaryDark)
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom Input Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KyroBlack)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Show Attached Image Preview Tag if present
                    if (attachedUri != null) {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(KyroDarkCard)
                                .border(1.dp, KyroDarkBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = attachedUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Image Attached",
                                color = KyroTextPrimaryDark,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.setAttachedImageUri(null) },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = KyroTextSecondaryDark)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(KyroDarkSurface)
                            .border(1.dp, KyroDarkBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach", tint = KyroTextSecondaryDark)
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ask Kyro AI anything...", color = KyroTextTertiaryDark, fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = KyroTextPrimaryDark,
                                unfocusedTextColor = KyroTextPrimaryDark
                            ),
                            maxLines = 4
                        )

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp),
                                color = KyroAccentBlue,
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        viewModel.sendMessage(inputText.trim())
                                        inputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (inputText.isNotBlank()) KyroAccentBlue else KyroDarkCard)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (inputText.isNotBlank()) Color.White else KyroTextTertiaryDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
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
                if (currentMessages.isEmpty()) {
                    // Empty state welcome view
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
                            // Hero image banner
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_banner_1785904959278),
                                contentDescription = "Hero Banner",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "How can I help you today?",
                                style = MaterialTheme.typography.headlineMedium,
                                color = KyroTextPrimaryDark,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "I can code, write, analyze, and process multimodal ideas.",
                                color = KyroTextSecondaryDark,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            // Quick Prompt Action Cards
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    QuickActionCard(
                                        title = "Explain Topic",
                                        desc = "Learn quantum computing simply",
                                        icon = Icons.Default.Lightbulb,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            inputText = "Explain quantum computing in simple terms"
                                            viewModel.sendMessage(inputText)
                                            inputText = ""
                                        }
                                    )
                                    QuickActionCard(
                                        title = "Code Help",
                                        desc = "Write Kotlin / Python logic",
                                        icon = Icons.Default.Code,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            inputText = "Write a clean Kotlin function to sort a list"
                                            viewModel.sendMessage(inputText)
                                            inputText = ""
                                        }
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    QuickActionCard(
                                        title = "Write Content",
                                        desc = "Draft emails, essays, stories",
                                        icon = Icons.Default.EditNote,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            inputText = "Write a professional project update email"
                                            viewModel.sendMessage(inputText)
                                            inputText = ""
                                        }
                                    )
                                    QuickActionCard(
                                        title = "Deep Reason",
                                        desc = "Analyze complex problems",
                                        icon = Icons.Default.Psychology,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            inputText = "Analyze the pros and cons of remote vs hybrid work"
                                            viewModel.sendMessage(inputText)
                                            inputText = ""
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Chat message feed
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        itemsIndexed(
                            items = currentMessages,
                            key = { _, msg -> msg.id }
                        ) { _, msg ->
                            MessageBubble(
                                message = msg,
                                onDelete = { viewModel.deleteMessage(msg.id) },
                                onRegenerate = if (msg.role == "user") {
                                    { viewModel.sendMessage(msg.content) }
                                } else null,
                                tts = tts
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showSettings) {
        SettingsModal(
            userPreferences = viewModel.userPreferences,
            onDismiss = { viewModel.toggleSettingsModal(false) }
        )
    }

    if (showProfile) {
        ProfileModal(
            userPreferences = viewModel.userPreferences,
            onDismiss = { viewModel.toggleProfileModal(false) },
            onLogout = {
                viewModel.toggleProfileModal(false)
                onLogout()
            }
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KyroDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, KyroDarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = KyroAccentBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = KyroTextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = desc,
                color = KyroTextSecondaryDark,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
