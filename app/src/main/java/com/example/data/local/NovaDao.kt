package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NovaDao {
    // Chat Sessions
    @Query("SELECT * FROM chat_sessions ORDER BY isPinned DESC, updatedAt DESC")
    fun observeSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: ChatSessionEntity)

    @Update
    suspend fun updateSession(session: ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllSessions()

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun observeMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getMessagesForSession(sessionId: String): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    // Generated Images
    @Query("SELECT * FROM generated_images ORDER BY createdAt DESC")
    fun observeGeneratedImages(): Flow<List<GeneratedImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedImage(image: GeneratedImageEntity)

    @Query("UPDATE generated_images SET isFavorite = NOT isFavorite WHERE id = :imageId")
    suspend fun toggleImageFavorite(imageId: String)

    @Query("DELETE FROM generated_images WHERE id = :imageId")
    suspend fun deleteGeneratedImage(imageId: String)

    @Query("DELETE FROM generated_images")
    suspend fun clearAllImages()

    // Study Items
    @Query("SELECT * FROM study_items ORDER BY createdAt DESC")
    fun observeStudyItems(): Flow<List<StudyItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyItem(item: StudyItemEntity)

    @Query("DELETE FROM study_items WHERE id = :itemId")
    suspend fun deleteStudyItem(itemId: String)

    @Query("DELETE FROM study_items")
    suspend fun clearAllStudyItems()

    // Writing Drafts
    @Query("SELECT * FROM writing_drafts ORDER BY updatedAt DESC")
    fun observeWritingDrafts(): Flow<List<WritingDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWritingDraft(draft: WritingDraftEntity)

    @Query("DELETE FROM writing_drafts WHERE id = :draftId")
    suspend fun deleteWritingDraft(draftId: String)

    @Query("DELETE FROM writing_drafts")
    suspend fun clearAllWritingDrafts()
}
