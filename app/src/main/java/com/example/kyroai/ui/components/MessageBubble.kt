package com.example.kyroai.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kyroai.data.local.ChatMessageEntity
import com.example.kyroai.ui.theme.KyroAccentBlue
import com.example.kyroai.ui.theme.KyroDarkBorder
import com.example.kyroai.ui.theme.KyroDarkCard
import com.example.kyroai.ui.theme.KyroTextPrimaryDark
import com.example.kyroai.ui.theme.KyroTextSecondaryDark
import java.util.Locale

@Composable
fun MessageBubble(
    message: ChatMessageEntity,
    onDelete: () -> Unit,
    onRegenerate: (() -> Unit)? = null,
    tts: TextToSpeech? = null
) {
    val isUser = message.role == "user"
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(KyroAccentBlue.copy(alpha = 0.15f))
                    .border(1.dp, KyroAccentBlue.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    color = KyroAccentBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            // Display Attached Image if present
            if (!message.imageUri.isNull_or_blank()) {
                AsyncImage(
                    model = message.imageUri,
                    contentDescription = "User Attachment",
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, KyroDarkBorder, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(if (isUser) KyroAccentBlue else KyroDarkCard)
                    .border(
                        width = 1.dp,
                        color = if (isUser) Color.Transparent else KyroDarkBorder,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                FormattedMessageText(text = message.content, isUser = isUser)
            }

            // Message Actions Bar
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Message", message.content)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = KyroTextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (!isUser && tts != null) {
                    IconButton(
                        onClick = {
                            tts.language = Locale.US
                            tts.speak(message.content, TextToSpeech.QUEUE_FLUSH, null, "KyroTTS")
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Read Aloud",
                            tint = KyroTextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (isUser && onRegenerate != null) {
                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate",
                            tint = KyroTextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = KyroTextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(10.dp))
            // User Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF27272A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = KyroTextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FormattedMessageText(text: String, isUser: Boolean) {
    if (isUser) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }

    // Parse code blocks in response
    val blocks = parseCodeBlocks(text)
    Column {
        for (block in blocks) {
            if (block.isCode) {
                CodeBlock(code = block.content, language = block.language)
            } else {
                Text(
                    text = renderFormattedText(block.content),
                    color = KyroTextPrimaryDark,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 22.sp
                    )
                )
            }
        }
    }
}

data class ParsedBlock(val content: String, val isCode: Boolean, val language: String = "")

fun parseCodeBlocks(text: String): List<ParsedBlock> {
    val blocks = mutableListOf<ParsedBlock>()
    val regex = Regex("```(\\w*)\\n([\\s\\S]*?)```")
    var lastIndex = 0

    for (match in regex.findAll(text)) {
        if (match.range.first > lastIndex) {
            val nonCode = text.substring(lastIndex, match.range.first)
            if (nonCode.isNotBlank()) {
                blocks.add(ParsedBlock(nonCode, isCode = false))
            }
        }
        val lang = match.groupValues[1].ifBlank { "code" }
        val codeContent = match.groupValues[2]
        blocks.add(ParsedBlock(codeContent, isCode = true, language = lang))
        lastIndex = match.range.last + 1
    }

    if (lastIndex < text.length) {
        val remaining = text.substring(lastIndex)
        if (remaining.isNotBlank()) {
            blocks.add(ParsedBlock(remaining, isCode = false))
        }
    }

    if (blocks.isEmpty()) {
        blocks.add(ParsedBlock(text, isCode = false))
    }

    return blocks
}

@Composable
fun renderFormattedText(text: String) = buildAnnotatedString {
    val lines = text.split("\n")
    for ((index, line) in lines.withIndex()) {
        val trimmed = line.trim()
        when {
            trimmed.startsWith("**") && trimmed.endsWith("**") && trimmed.length > 4 -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                    append(trimmed.substring(2, trimmed.length - 2))
                }
            }
            trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                append("• ")
                append(trimmed.substring(2))
            }
            else -> {
                // Parse bold segments inside line
                val parts = line.split("**")
                for (i in parts.indices) {
                    if (i % 2 == 1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append(parts[i])
                        }
                    } else {
                        append(parts[i])
                    }
                }
            }
        }
        if (index < lines.size - 1) append("\n")
    }
}

private fun String?.isNull_or_blank(): Boolean {
    return this.isNullOrBlank() || this == "null"
}
