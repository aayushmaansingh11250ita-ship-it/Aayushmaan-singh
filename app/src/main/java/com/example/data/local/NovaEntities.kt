package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AssistantMode(val displayName: String) {
    CHAT("AI Chat"),
    STUDY("Study"),
    WRITING("Writing"),
    VOICE("Voice")
}

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val mode: String = AssistantMode.CHAT.name,
    val lastMessagePreview: String = "",
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val isSamplePreview: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey val id: String,
    val prompt: String,
    val style: String,
    val aspectRatio: String,
    val drawableKey: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_items")
data class StudyItemEntity(
    @PrimaryKey val id: String,
    val topic: String,
    val mode: String, // "Explain", "Flashcards", "Quiz"
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "writing_drafts")
data class WritingDraftEntity(
    @PrimaryKey val id: String,
    val title: String,
    val format: String,
    val tone: String,
    val prompt: String,
    val output: String,
    val updatedAt: Long = System.currentTimeMillis()
)
