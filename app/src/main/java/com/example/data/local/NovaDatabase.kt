package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatSessionEntity::class,
        ChatMessageEntity::class,
        GeneratedImageEntity::class,
        StudyItemEntity::class,
        WritingDraftEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NovaDatabase : RoomDatabase() {
    abstract fun novaDao(): NovaDao

    companion object {
        @Volatile
        private var INSTANCE: NovaDatabase? = null

        fun getInstance(context: Context): NovaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): NovaDatabase {
            return Room.databaseBuilder(
                context,
                NovaDatabase::class.java,
                "nova_ai_database"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                            seedInitialData(getInstance(context).novaDao())
                        }
                    }
                })
                .build()
        }

        private suspend fun seedInitialData(dao: NovaDao) {
            val now = System.currentTimeMillis()
            val session1 = ChatSessionEntity(
                id = "seed_session_1",
                title = "Product Launch Brainstorm",
                mode = AssistantMode.CHAT.name,
                lastMessagePreview = "Here is a 3-phase launch roadmap tailored for a mobile AI companion...",
                isPinned = true,
                createdAt = now - 3_600_000L,
                updatedAt = now - 3_500_000L
            )
            val session2 = ChatSessionEntity(
                id = "seed_session_2",
                title = "Kotlin Coroutines & Flow",
                mode = AssistantMode.STUDY.name,
                lastMessagePreview = "StateFlow holds a hot observable value, while Flow is cold until collected...",
                isPinned = false,
                createdAt = now - 86_400_000L,
                updatedAt = now - 86_000_000L
            )
            dao.upsertSession(session1)
            dao.upsertSession(session2)

            dao.insertMessage(
                ChatMessageEntity(
                    id = "seed_msg_1",
                    sessionId = "seed_session_1",
                    role = "user",
                    content = "Help me plan a 3-phase product launch for a creative mobile app.",
                    isSamplePreview = false,
                    timestamp = now - 3_600_000L
                )
            )
            dao.insertMessage(
                ChatMessageEntity(
                    id = "seed_msg_2",
                    sessionId = "seed_session_1",
                    role = "assistant",
                    content = "[Sample Preview] Here is a structured 3-phase launch roadmap:\n\n1. Teaser & Waitlist (Weeks 1–2): Share short motion previews of your core workflow and collect early testers.\n2. Closed Beta (Weeks 3–4): Gather feedback on onboarding speed, voice latency, and daily utility.\n3. Public Launch (Week 5): Publish a concise demo reel, highlight real user outcomes, and ship v1.1 quick fixes within 48 hours.",
                    isSamplePreview = true,
                    timestamp = now - 3_500_000L
                )
            )

            dao.insertMessage(
                ChatMessageEntity(
                    id = "seed_msg_3",
                    sessionId = "seed_session_2",
                    role = "user",
                    content = "Compare Flow and StateFlow in Jetpack Compose.",
                    isSamplePreview = false,
                    timestamp = now - 86_400_000L
                )
            )
            dao.insertMessage(
                ChatMessageEntity(
                    id = "seed_msg_4",
                    sessionId = "seed_session_2",
                    role = "assistant",
                    content = "[Sample Preview] StateFlow is a hot, state-holder observable that always has a current value and replays the latest state to new collectors—ideal for ViewModel UiState. Standard Flow is cold, executing its producer block independently for each collector, which works great for Room queries and streaming tokens.",
                    isSamplePreview = true,
                    timestamp = now - 86_000_000L
                )
            )

            dao.insertGeneratedImage(
                GeneratedImageEntity(
                    id = "seed_img_1",
                    prompt = "Neon violet cyberpunk observatory above dark basalt cliffs under starry cosmos",
                    style = "Cyberpunk",
                    aspectRatio = "1:1",
                    drawableKey = "cyberpunk_observatory",
                    isFavorite = true,
                    createdAt = now - 7_200_000L
                )
            )
            dao.insertGeneratedImage(
                GeneratedImageEntity(
                    id = "seed_img_2",
                    prompt = "Bioluminescent crystalline lotus glowing in amethyst purple inside obsidian cavern",
                    style = "3D Render",
                    aspectRatio = "1:1",
                    drawableKey = "crystal_lotus",
                    isFavorite = false,
                    createdAt = now - 14_400_000L
                )
            )

            dao.insertStudyItem(
                StudyItemEntity(
                    id = "seed_study_1",
                    topic = "Neural Networks Basics",
                    mode = "Explain",
                    content = "[Sample Preview] A neural network learns patterns by passing inputs through weighted layers, applying non-linear activation functions, and adjusting weights via backpropagation to minimize prediction error.",
                    createdAt = now - 43_200_000L
                )
            )

            dao.insertWritingDraft(
                WritingDraftEntity(
                    id = "seed_draft_1",
                    title = "Q4 Design Sync Invite",
                    format = "Email",
                    tone = "Professional",
                    prompt = "Invite the mobile team to review the new dark cosmic design system on Thursday.",
                    output = "[Sample Preview]\nSubject: Thursday Sync: Reviewing the Nova Dark Cosmic UI System\n\nHi team,\n\nI'd love to gather everyone this Thursday at 10:00 AM to walk through our updated obsidian-navy surfaces, violet gradient tokens, and interactive component specs. Please bring any edge-case feedback on tablet responsiveness.\n\nBest,\nAlex",
                    updatedAt = now - 21_600_000L
                )
            )
        }
    }
}
