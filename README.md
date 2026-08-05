# Kyro AI - Android Application

Kyro AI is a modern, multimodal AI Assistant application built natively for Android using Kotlin and Jetpack Compose.

## Features

- **Multimodal AI Conversations**: Direct integration with Gemini models (`gemini-3.5-flash`, `gemini-3.1-pro-preview`, `gemini-2.5-flash-image`) via REST API or Demo Mode.
- **Rich Markdown & Code Rendering**: Formatted text rendering with syntax-highlighted code blocks, copy code buttons, bold text, and lists.
- **Image Attachments**: Attach photos or images for visual AI processing.
- **Chat Session Management**: Create, rename, pin, search, and delete chat sessions persisted locally in a Room database.
- **Custom System Persona**: Custom system instructions and response language preferences.
- **Text-to-Speech (TTS)**: Built-in voice readout for AI responses.
- **Security & Privacy**: Offline-first encrypted session storage with secure key configuration via BuildConfig and user preferences.

## Tech Stack

- **UI Framework**: Jetpack Compose (Material Design 3)
- **Language**: Kotlin 2.0
- **Database**: Room Database (KSP)
- **Networking**: Retrofit2 & KotlinX Serialization
- **Image Loading**: Coil Compose
