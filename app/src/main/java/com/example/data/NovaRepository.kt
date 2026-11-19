package com.example.data

import com.example.data.local.AssistantMode
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.GeneratedImageEntity
import com.example.data.local.NovaDao
import com.example.data.local.StudyItemEntity
import com.example.data.local.WritingDraftEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class NovaRepository(private val dao: NovaDao) {
    fun observeSessions(): Flow<List<ChatSessionEntity>> = dao.observeSessions()

    fun observeMessages(sessionId: String): Flow<List<ChatMessageEntity>> =
        dao.observeMessagesForSession(sessionId)

    fun observeGeneratedImages(): Flow<List<GeneratedImageEntity>> = dao.observeGeneratedImages()

    fun observeStudyItems(): Flow<List<StudyItemEntity>> = dao.observeStudyItems()

    fun observeWritingDrafts(): Flow<List<WritingDraftEntity>> = dao.observeWritingDrafts()

    suspend fun getSession(sessionId: String): ChatSessionEntity? = dao.getSessionById(sessionId)

    suspend fun getMessages(sessionId: String): List<ChatMessageEntity> =
        dao.getMessagesForSession(sessionId)

    suspend fun createOrUpdateSession(
        sessionId: String? = null,
        title: String,
        mode: AssistantMode,
        lastPreview: String
    ): ChatSessionEntity {
        val now = System.currentTimeMillis()
        val existing = if (sessionId != null) dao.getSessionById(sessionId) else null
        val session = existing?.copy(
            title = if (existing.title == "New Conversation" && title.isNotBlank()) title else existing.title,
            lastMessagePreview = lastPreview.take(120),
            updatedAt = now
        ) ?: ChatSessionEntity(
            id = sessionId ?: UUID.randomUUID().toString(),
            title = title.ifBlank { "New Conversation" }.take(48),
            mode = mode.name,
            lastMessagePreview = lastPreview.take(120),
            isPinned = false,
            createdAt = now,
            updatedAt = now
        )
        dao.upsertSession(session)
        return session
    }

    suspend fun addMessage(
        sessionId: String,
        role: String,
        content: String,
        isSamplePreview: Boolean
    ) {
        dao.insertMessage(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                role = role,
                content = content,
                isSamplePreview = isSamplePreview,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun togglePinSession(session: ChatSessionEntity) {
        dao.updateSession(session.copy(isPinned = !session.isPinned))
    }

    suspend fun deleteSession(sessionId: String) {
        dao.deleteSession(sessionId)
    }

    suspend fun saveGeneratedImage(
        prompt: String,
        style: String,
        aspectRatio: String,
        drawableKey: String
    ) {
        dao.insertGeneratedImage(
            GeneratedImageEntity(
                id = UUID.randomUUID().toString(),
                prompt = prompt,
                style = style,
                aspectRatio = aspectRatio,
                drawableKey = drawableKey,
                isFavorite = false,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleImageFavorite(imageId: String) = dao.toggleImageFavorite(imageId)

    suspend fun deleteGeneratedImage(imageId: String) = dao.deleteGeneratedImage(imageId)

    suspend fun saveStudyItem(topic: String, mode: String, content: String) {
        dao.insertStudyItem(
            StudyItemEntity(
                id = UUID.randomUUID().toString(),
                topic = topic,
                mode = mode,
                content = content,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteStudyItem(itemId: String) = dao.deleteStudyItem(itemId)

    suspend fun saveWritingDraft(
        title: String,
        format: String,
        tone: String,
        prompt: String,
        output: String
    ) {
        dao.insertWritingDraft(
            WritingDraftEntity(
                id = UUID.randomUUID().toString(),
                title = title.take(60),
                format = format,
                tone = tone,
                prompt = prompt,
                output = output,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteWritingDraft(draftId: String) = dao.deleteWritingDraft(draftId)

    suspend fun clearAllLocalData() {
        dao.clearAllSessions()
        dao.clearAllImages()
        dao.clearAllStudyItems()
        dao.clearAllWritingDrafts()
    }
}
