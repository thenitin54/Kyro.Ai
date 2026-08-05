package com.example.kyroai.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.kyroai.data.local.ChatDao
import com.example.kyroai.data.local.ChatMessageEntity
import com.example.kyroai.data.local.ChatSessionEntity
import com.example.kyroai.data.local.UserPreferences
import com.example.kyroai.data.remote.Content
import com.example.kyroai.data.remote.GenerateContentRequest
import com.example.kyroai.data.remote.GenerationConfig
import com.example.kyroai.data.remote.InlineData
import com.example.kyroai.data.remote.Part
import com.example.kyroai.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID

class ChatRepository(
    private val chatDao: ChatDao,
    private val userPreferences: UserPreferences,
    private val context: Context
) {
    val allSessions: Flow<List<ChatSessionEntity>> = chatDao.getAllSessions()

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForSession(sessionId)
    }

    suspend fun createNewSession(title: String = "New Chat"): String {
        val sessionId = UUID.randomUUID().toString()
        val session = ChatSessionEntity(
            sessionId = sessionId,
            title = title
        )
        chatDao.insertSession(session)
        return sessionId
    }

    suspend fun updateSessionTitle(sessionId: String, title: String) {
        chatDao.updateSessionTitle(sessionId, title)
    }

    suspend fun togglePinSession(sessionId: String, currentPinned: Boolean) {
        chatDao.updateSessionPin(sessionId, !currentPinned)
    }

    suspend fun deleteSession(sessionId: String) {
        chatDao.deleteSessionAndMessages(sessionId)
    }

    suspend fun deleteMessage(messageId: Long) {
        chatDao.deleteMessageById(messageId)
    }

    suspend fun sendMessage(
        sessionId: String,
        userText: String,
        imageUriString: String? = null,
        history: List<ChatMessageEntity>
    ) {
        // 1. Save user message to Room DB
        val userMsg = ChatMessageEntity(
            sessionId = sessionId,
            role = "user",
            content = userText,
            imageUri = imageUriString
        )
        chatDao.insertMessage(userMsg)

        // Update session title if this is the first message
        if (history.isEmpty()) {
            val titleSnippet = if (userText.length > 28) userText.take(28) + "..." else userText
            chatDao.updateSessionTitle(sessionId, titleSnippet)
        }

        // 2. Fetch parameters
        val apiKey = userPreferences.apiKey.value
        val isDemo = userPreferences.useDemoMode.value
        val model = userPreferences.selectedModel.value
        val language = userPreferences.selectedLanguage.value
        val persona = userPreferences.systemPersona.value

        if (isDemo || apiKey.isBlank()) {
            generateDemoResponse(sessionId, userText)
            return
        }

        try {
            // Build Gemini request
            val contents = mutableListOf<Content>()

            // Add history context (up to last 10 turns)
            val recentHistory = history.takeLast(10)
            for (msg in recentHistory) {
                contents.add(
                    Content(
                        role = if (msg.role == "user") "user" else "model",
                        parts = listOf(Part(text = msg.content))
                    )
                )
            }

            // Current message parts
            val currentParts = mutableListOf<Part>()
            currentParts.add(Part(text = userText))

            // Check if there's an attached image
            if (!imageUriString.isNull_or_empty_uri()) {
                val base64Image = uriToBase64(Uri.parse(imageUriString))
                if (base64Image != null) {
                    currentParts.add(
                        Part(
                            inlineData = InlineData(
                                mimeType = "image/jpeg",
                                data = base64Image
                            )
                        )
                    )
                }
            }

            contents.add(Content(role = "user", parts = currentParts))

            val systemInstruction = Content(
                parts = listOf(Part(text = "$persona Always reply in $language language unless requested otherwise."))
            )

            val request = GenerateContentRequest(
                contents = contents,
                generationConfig = GenerationConfig(temperature = 0.7f),
                systemInstruction = systemInstruction
            )

            val response = withContext(Dispatchers.IO) {
                RetrofitClient.service.generateContent(
                    model = model,
                    apiKey = apiKey,
                    request = request
                )
            }

            val assistantReply = response.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text
                ?: "I couldn't generate a response. Please check your query or model configuration."

            val assistantMsg = ChatMessageEntity(
                sessionId = sessionId,
                role = "assistant",
                content = assistantReply
            )
            chatDao.insertMessage(assistantMsg)

        } catch (e: Exception) {
            val errorReply = "**Error calling Gemini API:** ${e.localizedMessage ?: "Unknown network error"}\n\n*Tip: You can switch to Demo Mode in Settings or update your Gemini API key.*"
            val assistantMsg = ChatMessageEntity(
                sessionId = sessionId,
                role = "assistant",
                content = errorReply
            )
            chatDao.insertMessage(assistantMsg)
        }
    }

    private suspend fun generateDemoResponse(sessionId: String, query: String) {
        delay(1200) // Simulated AI delay
        val lowercase = query.lowercase()
        val responseText = when {
            lowercase.contains("hello") || lowercase.contains("hi") || lowercase.contains("kyro") -> {
                "Hello! I am **Kyro AI**, your intelligent assistant. How can I help you today?\n\n- Write code & debug\n- Summarize documents\n- Answer complex questions"
            }
            lowercase.contains("code") || lowercase.contains("python") || lowercase.contains("kotlin") -> {
                "Here is an example snippet matching your request:\n\n```kotlin\nfun main() {\n    val greeting = \"Hello from Kyro AI!\"\n    println(greeting)\n}\n```\n\nLet me know if you need explanations or additional functions!"
            }
            lowercase.contains("explain") || lowercase.contains("quantum") -> {
                "**Quantum Computing Explained Simply:**\n\nImagine a standard coin that is either Heads (1) or Tails (0). A quantum coin can spin in mid-air, being **both** Heads and Tails at the same time until caught (*superposition*).\n\nThis allows quantum computers to analyze massive amounts of possibilities simultaneously."
            }
            else -> {
                "I've processed your query: *\"$query\"*.\n\nAs Kyro AI, I can assist you with writing, code analysis, brainstorming, or technical research. Feel free to refine your question or ask for step-by-step guidance!"
            }
        }

        val assistantMsg = ChatMessageEntity(
            sessionId = sessionId,
            role = "assistant",
            content = responseText
        )
        chatDao.insertMessage(assistantMsg)
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    private fun String?.isNull_or_empty_uri(): Boolean {
        return this.isNullOrBlank() || this == "null"
    }
}
