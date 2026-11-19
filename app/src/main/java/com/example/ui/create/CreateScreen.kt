package com.example.ui.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ai.ImageAspectRatio
import com.example.data.ai.ImageStyle
import com.example.data.local.GeneratedImageEntity
import com.example.ui.components.NovaGlassCard
import com.example.ui.components.NovaGradientButton
import com.example.ui.components.NovaNoticeBanner
import com.example.ui.components.SamplePreviewBadge
import com.example.ui.components.drawableForKey
import com.example.ui.theme.NovaLavender
import com.example.ui.theme.NovaMagentaGlow
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaOutline
import com.example.ui.theme.NovaViolet

@Composable
fun CreateScreen(
    viewModel: CreateViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                Column {
                    Text(
                        text = "Image Studio",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Synthesize visual concepts with style & aspect presets",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!state.isLiveGeminiConnected) {
                item { NovaNoticeBanner() }
            }

            item {
                NovaGlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prompt",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = CircleShape,
                            color = NovaViolet.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, NovaViolet.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { viewModel.inspireMe() }
                                .testTag("create_inspire_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Casino,
                                    contentDescription = "Inspire prompt",
                                    tint = NovaLavender,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Inspire me",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NovaLavender
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.prompt,
                        onValueChange = viewModel::updatePrompt,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.create_prompt_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        minLines = 3,
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NovaViolet,
                            unfocusedBorderColor = NovaOutline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_prompt_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Art Style",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ImageStyle.entries.forEach { style ->
                            val selected = state.selectedStyle == style
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.selectStyle(style) },
                                label = { Text(style.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NovaViolet.copy(alpha = 0.28f),
                                    selectedLabelColor = NovaLavender,
                                    containerColor = NovaNavyCard
                                ),
                                modifier = Modifier.testTag("create_style_${style.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Aspect Ratio",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImageAspectRatio.entries.forEach { ratio ->
                            val selected = state.selectedRatio == ratio
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.selectRatio(ratio) },
                                label = { Text(ratio.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NovaViolet.copy(alpha = 0.28f),
                                    selectedLabelColor = NovaLavender,
                                    containerColor = NovaNavyCard
                                ),
                                modifier = Modifier.testTag("create_ratio_${ratio.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    NovaGradientButton(
                        text = if (state.isGenerating) "Synthesizing..." else stringResource(R.string.create_generate_button),
                        enabled = !state.isGenerating && state.prompt.isNotBlank(),
                        onClick = { viewModel.generateImage() },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("create_generate_button")
                    )
                }
            }

            if (state.isGenerating) {
                item {
                    NovaGlassCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = NovaViolet,
                                modifier = Modifier.size(26.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Rendering cosmic preview...",
                                style = MaterialTheme.typography.titleSmall,
                                color = NovaLavender
                            )
                        }
                    }
                }
            }

            state.latestResult?.let { result ->
                item {
                    NovaGlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Latest Preview",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            SamplePreviewBadge()
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        val ratioFloat = when (result.aspectRatio) {
                            ImageAspectRatio.SQUARE -> 1f
                            ImageAspectRatio.LANDSCAPE -> 16f / 9f
                            ImageAspectRatio.PORTRAIT -> 3f / 4f
                        }
                        Image(
                            painter = painterResource(id = drawableForKey(result.drawableKey)),
                            contentDescription = result.prompt,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratioFloat)
                                .clip(RoundedCornerShape(18.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = result.caption,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Saved Gallery (${state.gallery.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(state.gallery, key = { it.id }) { item ->
                GalleryImageCard(
                    item = item,
                    onUsePrompt = { viewModel.updatePrompt(item.prompt) },
                    onToggleFavorite = { viewModel.toggleFavorite(item.id) },
                    onDelete = { viewModel.deleteImage(item.id) }
                )
            }
        }
    }
}

@Composable
private fun GalleryImageCard(
    item: GeneratedImageEntity,
    onUsePrompt: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    NovaGlassCard(
        cornerRadius = 20.dp,
        onClick = onUsePrompt
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = drawableForKey(item.drawableKey)),
                contentDescription = item.prompt,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.prompt,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.style} • ${item.aspectRatio}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NovaLavender
                )
            }
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (item.isFavorite) NovaMagentaGlow else NovaLavender
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
