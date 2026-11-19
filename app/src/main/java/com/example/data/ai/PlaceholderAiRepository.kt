package com.example.data.ai

import com.example.data.local.AssistantMode
import com.example.data.local.ChatMessageEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.abs

/**
 * Placeholder implementation of [AiRepository] returning clearly labeled sample responses.
 * Swap this binding with a Firebase AI Logic implementation once Firebase is enabled.
 */
class PlaceholderAiRepository : AiRepository {
    override val isLiveGeminiConnected: Boolean = false

    override fun streamChatReply(
        prompt: String,
        history: List<ChatMessageEntity>,
        mode: AssistantMode,
        creativity: Float,
        streamEnabled: Boolean
    ): Flow<String> = flow {
        val cleanPrompt = prompt.trim()
        val fullResponse = buildSampleChatResponse(cleanPrompt, history.size, mode, creativity)

        if (!streamEnabled) {
            delay(450L)
            emit(fullResponse)
            return@flow
        }

        val words = fullResponse.split(" ")
        val builder = StringBuilder()
        for ((index, word) in words.withIndex()) {
            if (index > 0) builder.append(' ')
            builder.append(word)
            emit(builder.toString())
            delay(28L)
        }
    }

    override suspend fun generateStudyMaterial(
        topic: String,
        studyType: StudyType
    ): StudyGenerationResult {
        delay(650L)
        val cleanTopic = topic.trim().ifEmpty { "Quantum Computing" }
        return when (studyType) {
            StudyType.EXPLAIN -> StudyGenerationResult(
                topic = cleanTopic,
                type = studyType,
                summary = buildString {
                    appendLine("[Sample Preview — Connect Firebase AI Logic for live Gemini]")
                    appendLine()
                    appendLine("Core Intuition for $cleanTopic:")
                    appendLine("• What it is: $cleanTopic organizes complex relationships into clear, repeatable building blocks so you can reason from first principles.")
                    appendLine("• How it works: Start with the fundamental inputs, trace how constraints transform them step-by-step, and verify the output against real examples.")
                    appendLine("• Key mental model: Think of $cleanTopic as a layered system—master the base layer first before optimizing edge cases.")
                }.trim()
            )
            StudyType.FLASHCARDS -> StudyGenerationResult(
                topic = cleanTopic,
                type = studyType,
                summary = "[Sample Preview] Generated 4 interactive flashcards for $cleanTopic.",
                flashcards = listOf(
                    FlashcardItem(
                        front = "What is the foundational principle behind $cleanTopic?",
                        back = "It breaks down a complex domain into structured states and predictable transitions you can test independently."
                    ),
                    FlashcardItem(
                        front = "Why is $cleanTopic important in practice?",
                        back = "It improves clarity, reduces avoidable errors, and gives learners a repeatable framework for solving novel problems."
                    ),
                    FlashcardItem(
                        front = "What is a common mistake when learning $cleanTopic?",
                        back = "Memorizing surface formulas or terminology before understanding the underlying cause-and-effect mechanism."
                    ),
                    FlashcardItem(
                        front = "How can you quickly verify mastery of $cleanTopic?",
                        back = "Explain the core concept in 3 plain sentences without jargon and solve one unfamiliar edge-case scenario."
                    )
                )
            )
            StudyType.QUIZ -> StudyGenerationResult(
                topic = cleanTopic,
                type = studyType,
                summary = "[Sample Preview] Generated a 3-question checkup quiz for $cleanTopic.",
                quizQuestions = listOf(
                    QuizQuestionItem(
                        question = "Which approach builds the strongest long-term intuition for $cleanTopic?",
                        options = listOf(
                            "Connecting core principles to concrete examples",
                            "Memorizing isolated definitions without context",
                            "Skipping foundational edge cases"
                        ),
                        correctIndex = 0,
                        explanation = "Connecting fundamentals to real examples makes concepts transferable across unfamiliar problems."
                    ),
                    QuizQuestionItem(
                        question = "When analyzing a problem in $cleanTopic, what should you identify first?",
                        options = listOf(
                            "Visual styling preferences",
                            "Constraints, inputs, and desired outcomes",
                            "Random trial-and-error guesses"
                        ),
                        correctIndex = 1,
                        explanation = "Defining inputs, boundaries, and target outputs prevents wasted work and clarifies assumptions."
                    ),
                    QuizQuestionItem(
                        question = "What is the fastest way to spot a misconception about $cleanTopic?",
                        options = listOf(
                            "Avoid testing your assumptions",
                            "Reread the title twice",
                            "Test the idea on a boundary or counterexample"
                        ),
                        correctIndex = 2,
                        explanation = "Boundary cases immediately reveal whether a mental model holds or breaks."
                    )
                )
            )
        }
    }

    override suspend fun generateWriting(
        prompt: String,
        format: WritingFormat,
        tone: WritingTone
    ): String {
        delay(650L)
        val cleanPrompt = prompt.trim().ifEmpty { "Launching our next-generation mobile assistant" }
        return when (format) {
            WritingFormat.EMAIL -> """
                [Sample Preview — Connect Firebase AI Logic for live Gemini]

                Subject: Update on $cleanPrompt

                Hi there,

                I wanted to share a quick, ${tone.label.lowercase()} note regarding $cleanPrompt. We have aligned on the primary milestones, tightened the timeline, and outlined clear next steps so everyone stays in sync.

                Could you review the draft goals and share any feedback by Thursday afternoon?

                Warm regards,
                Nova Workspace
            """.trimIndent()

            WritingFormat.ESSAY -> """
                [Sample Preview — Connect Firebase AI Logic for live Gemini]

                Exploring $cleanPrompt

                In a rapidly evolving landscape, $cleanPrompt stands out as a defining catalyst for thoughtful innovation. Adopting a ${tone.label.lowercase()} lens reveals two essential pillars: clarity of purpose and disciplined execution.

                First, foundational design choices compound over time. Second, continuous feedback loops ensure solutions remain grounded in real human needs rather than fleeting trends.

                Ultimately, approaching $cleanPrompt with intention turns ambitious ideas into lasting impact.
            """.trimIndent()

            WritingFormat.BLOG -> """
                [Sample Preview — Connect Firebase AI Logic for live Gemini]

                Why $cleanPrompt Matters More Than Ever

                Let's break down $cleanPrompt in a ${tone.label.lowercase()} way you can actually apply today:

                1. Start with the smallest high-leverage workflow.
                2. Keep interfaces calm, legible, and responsive.
                3. Measure real-world outcomes instead of vanity metrics.

                What part of $cleanPrompt are you exploring next?
            """.trimIndent()

            WritingFormat.COVER_LETTER -> """
                [Sample Preview — Connect Firebase AI Logic for live Gemini]

                Dear Hiring Team,

                I am excited to express my interest in contributing to $cleanPrompt. With a ${tone.label.lowercase()} approach to collaboration, architecture, and craft, I thrive on turning ambiguous product goals into polished experiences users genuinely enjoy.

                Thank you for your time and consideration—I would welcome the opportunity to discuss how my background aligns with your roadmap.

                Sincerely,
                Applicant
            """.trimIndent()

            WritingFormat.SUMMARY -> """
                [Sample Preview — Connect Firebase AI Logic for live Gemini]

                Executive Summary (${tone.label} Tone)
                Topic: $cleanPrompt

                • Objective: Deliver measurable value around $cleanPrompt with minimal operational friction.
                • Key Insight: Focused iteration outperforms broad, unvalidated scope expansion.
                • Recommended Next Step: Run a targeted 2-week pilot and review adoption metrics.
            """.trimIndent()
        }
    }

    override suspend fun generateImagePreview(
        prompt: String,
        style: ImageStyle,
        aspectRatio: ImageAspectRatio
    ): ImageGenerationResult {
        delay(1100L)
        val cleanPrompt = prompt.trim().ifEmpty { "Cosmic violet crystal nebula" }
        val keys = listOf("cyberpunk_observatory", "crystal_lotus", "cosmic_orb")
        val selectedKey = when {
            cleanPrompt.contains("lotus", ignoreCase = true) ||
                cleanPrompt.contains("crystal", ignoreCase = true) ||
                style == ImageStyle.RENDER_3D -> "crystal_lotus"
            cleanPrompt.contains("orb", ignoreCase = true) ||
                cleanPrompt.contains("space", ignoreCase = true) ||
                style == ImageStyle.MINIMALIST -> "cosmic_orb"
            else -> keys[abs(cleanPrompt.hashCode()) % keys.size]
        }

        return ImageGenerationResult(
            prompt = cleanPrompt,
            style = style,
            aspectRatio = aspectRatio,
            drawableKey = selectedKey,
            caption = "[Sample Preview] Styled in ${style.label} (${aspectRatio.label}). Connect Firebase AI Logic for live Gemini image synthesis.",
            isSamplePreview = true
        )
    }

    override suspend fun generateVoiceReply(transcript: String): String {
        delay(550L)
        val clean = transcript.trim().ifEmpty { "Hello Nova" }
        return "[Sample Preview] I heard: \"$clean\". Here is a quick voice summary: keep your priorities focused on one deep work block, one creative sprint, and a quick review before wrapping up. Connect Firebase AI Logic for live Gemini voice reasoning."
    }

    private fun buildSampleChatResponse(
        prompt: String,
        historyCount: Int,
        mode: AssistantMode,
        creativity: Float
    ): String {
        val lower = prompt.lowercase()
        val toneHint = if (creativity > 0.7f) "imaginative" else if (creativity < 0.35f) "direct" else "balanced"
        val contextLine = if (historyCount > 2) {
            "Building on our conversation so far ($historyCount messages in context), "
        } else {
            ""
        }

        return when {
            "code" in lower || "kotlin" in lower || "compose" in lower -> """
                [Sample Preview — Connect Firebase AI Logic for live Gemini]

                ${contextLine}here is a clean $toneHint Kotlin pattern for your request ("$prompt"):

                ```kotlin
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                ```

                Keep UI state immutable, stream updates through `StateFlow`, and isolate side effects inside a repository interface.
            """.trimIndent()

            "plan" in lower || "idea" in lower || "brainstorm" in lower -> """
                [Sample Preview — Connect Firebase AI Logic for live Gemini]

                ${contextLine}here is a $toneHint 3-step blueprint for "$prompt":

                1. Clarify the core outcome you want within the next 48 hours.
                2. Break the execution into 25-minute focused milestones.
                3. Review results and iterate on the highest-impact bottleneck.
            """.trimIndent()

            else -> """
                [Sample Preview — Connect Firebase AI Logic for live Gemini]

                ${contextLine}I received your ${mode.displayName} prompt: "$prompt" (style: $toneHint).

                Key takeaways:
                • Structure: Start with a clear goal, validate assumptions early, and keep your workflow modular.
                • Next Action: Try asking for a step-by-step checklist, code snippet, or study breakdown—and connect Firebase AI Logic whenever you're ready for live Gemini responses.
            """.trimIndent()
        }
    }
}
