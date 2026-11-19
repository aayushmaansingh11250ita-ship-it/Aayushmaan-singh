package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.ChatSessionEntity
import com.example.ui.components.NovaGlassCard
import com.example.ui.components.NovaGradientButton
import com.example.ui.components.NovaNoticeBanner
import com.example.ui.theme.NovaCyanAccent
import com.example.ui.theme.NovaHeroOverlayGradient
import com.example.ui.theme.NovaIndigo
import com.example.ui.theme.NovaLavender
import com.example.ui.theme.NovaMagentaGlow
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaNavyElevated
import com.example.ui.theme.NovaPrimaryGradient
import com.example.ui.theme.NovaPurple
import com.example.ui.theme.NovaViolet

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenChat: (sessionId: String?, prompt: String?) -> Unit,
    onOpenCreate: () -> Unit,
    onOpenVoice: () -> Unit,
    onOpenStudy: () -> Unit,
    onOpenWriting: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                NovaHomeHeader(
                    displayName = state.displayName,
                    onOpenHistory = onOpenHistory,
                    onOpenSettings = onOpenSettings
                )
            }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically(animationSpec = spring()) { it / 6 }
                ) {
                    NovaHeroBanner(
                        onStartChat = { onOpenChat(null, null) }
                    )
                }
            }

            if (!state.isLiveGeminiConnected) {
                item {
                    NovaNoticeBanner()
                }
            }

            item {
                Text(
                    text = "AI Workspace",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ToolGridCard(
                            title = "AI Chat",
                            subtitle = "Multi-turn reasoning",
                            icon = Icons.AutoMirrored.Rounded.Chat,
                            accent = NovaViolet,
                            tag = "home_tool_chat",
                            onClick = { onOpenChat(null, null) },
                            modifier = Modifier.weight(1f)
                        )
                        ToolGridCard(
                            title = "Image Studio",
                            subtitle = "${state.savedImageCount} artworks",
                            icon = Icons.Rounded.Image,
                            accent = NovaMagentaGlow,
                            tag = "home_tool_create",
                            onClick = onOpenCreate,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ToolGridCard(
                            title = "Voice Orb",
                            subtitle = "Hands-free speech",
                            icon = Icons.Rounded.GraphicEq,
                            accent = NovaCyanAccent,
                            tag = "home_tool_voice",
                            onClick = onOpenVoice,
                            modifier = Modifier.weight(1f)
                        )
                        ToolGridCard(
                            title = "Study Lab",
                            subtitle = "${state.studyDeckCount} decks saved",
                            icon = Icons.Rounded.School,
                            accent = NovaIndigo,
                            tag = "home_tool_study",
                            onClick = onOpenStudy,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    WritingBannerCard(
                        onClick = onOpenWriting,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Conversations",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "View all",
                        style = MaterialTheme.typography.labelLarge,
                        color = NovaLavender,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onOpenHistory)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("home_view_all_history")
                    )
                }
            }

            if (state.recentSessions.isEmpty()) {
                item {
                    NovaGlassCard {
                        Text(
                            text = "No conversations yet",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap Start a Conversation above to launch your first Nova session.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(state.recentSessions, key = { it.id }) { session ->
                    RecentSessionRow(
                        session = session,
                        onClick = { onOpenChat(session.id, null) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NovaHomeHeader(
    displayName: String,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(NovaPrimaryGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = "Nova AI logo",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = NovaViolet.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, NovaViolet.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = "PRO UI",
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaLavender,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = "Welcome back, $displayName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = onOpenHistory,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("home_history_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.History,
                contentDescription = stringResource(R.string.history_title),
                tint = NovaLavender
            )
        }
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("home_settings_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(R.string.settings_title),
                tint = NovaLavender
            )
        }
    }
}

@Composable
private fun NovaHeroBanner(onStartChat: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        color = NovaNavyCard,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(listOf(NovaViolet.copy(alpha = 0.7f), NovaIndigo.copy(alpha = 0.3f)))
        )
    ) {
        Box(modifier = Modifier.height(210.dp)) {
            Image(
                painter = painterResource(id = R.drawable.img_nova_hero_orb),
                contentDescription = "Nova AI cosmic orb banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NovaHeroOverlayGradient)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = stringResource(R.string.home_greeting_subtitle),
                    style = MaterialTheme.typography.labelMedium,
                    color = NovaLavender
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_hero_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(14.dp))
                NovaGradientButton(
                    text = stringResource(R.string.home_hero_cta),
                    onClick = onStartChat,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.testTag("home_start_chat_button")
                )
            }
        }
    }
}

@Composable
private fun ToolGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NovaGlassCard(
        modifier = modifier.testTag(tag),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accent,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WritingBannerCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NovaGlassCard(
        modifier = modifier.testTag("home_tool_writing"),
        containerColor = NovaNavyElevated.copy(alpha = 0.85f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(NovaPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.EditNote,
                    contentDescription = "Writing Studio",
                    tint = NovaLavender,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Writing Studio",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Draft emails, essays, cover letters & crisp summaries",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RecentSessionRow(
    session: ChatSessionEntity,
    onClick: () -> Unit
) {
    NovaGlassCard(
        cornerRadius = 18.dp,
        onClick = onClick,
        modifier = Modifier.testTag("recent_session_${session.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NovaViolet.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Chat,
                    contentDescription = null,
                    tint = NovaLavender,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = session.lastMessagePreview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
