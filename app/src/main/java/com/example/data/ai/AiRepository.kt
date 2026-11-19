package com.example.data.ai

import com.example.data.local.AssistantMode
import com.example.data.local.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

enum class StudyType(val label: String) {
    EXPLAIN("Explain Simply"),
    FLASHCARDS("Flashcards"),
    QUIZ("Practice Quiz")
}

data class FlashcardItem(
    val front: String,
    val back: String
)

data class QuizQuestionItem(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class StudyGenerationResult(
    val topic: String,
    val type: StudyType,
    val summary: String,
    val flashcards: List<FlashcardItem> = emptyList(),
    val quizQuestions: List<QuizQuestionItem> = emptyList(),
    val isSamplePreview: Boolean = true
)

enum class WritingFormat(val label: String) {
    EMAIL("Email"),
    ESSAY("Essay"),
    BLOG("Blog Post"),
    COVER_LETTER("Cover Letter"),
    SUMMARY("Executive Summary")
}

enum class WritingTone(val label: String) {
    PROFESSIONAL("Professional"),
    CREATIVE("Creative"),
    CONCISE("Concise"),
    ACADEMIC("Academic"),
    FRIENDLY("Friendly")
}

enum class ImageStyle(val label: String) {
    CYBERPUNK("Cyberpunk"),
    PHOTOREALISTIC("Photoreal"),
    RENDER_3D("3D Render"),
    ANIME("Anime"),
    MINIMALIST("Minimalist")
}

enum class ImageAspectRatio(val label: String) {
    SQUARE("1:1"),
    LANDSCAPE("16:9"),
    PORTRAIT("3:4")
}

data class ImageGenerationResult(
    val prompt: String,
    val style: ImageStyle,
    val aspectRatio: ImageAspectRatio,
    val drawableKey: String,
    val caption: String,
    val isSamplePreview: Boolean = true
)

/**
 * Single AI repository abstraction for all Nova AI generative features.
 * Currently backed by [PlaceholderAiRepository] until Firebase AI Logic is enabled.
 */
interface AiRepository {
    val isLiveGeminiConnected: Boolean

    fun streamChatReply(
        prompt: String,
        history: List<ChatMessageEntity>,
        mode: AssistantMode,
        creativity: Float,
        streamEnabled: Boolean
    ): Flow<String>

    suspend fun generateStudyMaterial(
        topic: String,
        studyType: StudyType
    ): StudyGenerationResult

    suspend fun generateWriting(
        prompt: String,
        format: WritingFormat,
        tone: WritingTone
    ): String

    suspend fun generateImagePreview(
        prompt: String,
        style: ImageStyle,
        aspectRatio: ImageAspectRatio
    ): ImageGenerationResult

    suspend fun generateVoiceReply(transcript: String): String
}
