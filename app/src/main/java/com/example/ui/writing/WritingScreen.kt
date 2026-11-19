package com.example.ui.writing

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ai.WritingFormat
import com.example.data.ai.WritingTone
import com.example.ui.components.NovaGlassCard
import com.example.ui.components.NovaGradientButton
import com.example.ui.components.NovaNoticeBanner
import com.example.ui.components.NovaTopBar
import com.example.ui.components.SamplePreviewBadge
import com.example.ui.theme.NovaLavender
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaOutline
import com.example.ui.theme.NovaViolet

@Composable
fun WritingScreen(
    viewModel: WritingViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
        ) {
            NovaTopBar(
                title = stringResource(R.string.writing_title),
                subtitle = "Format & tone-tuned AI drafts",
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { NovaNoticeBanner() }

                item {
                    NovaGlassCard {
                        Text(
                            text = "Format",
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
                            WritingFormat.entries.forEach { format ->
                                val selected = state.selectedFormat == format
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.selectFormat(format) },
                                    label = { Text(format.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NovaViolet.copy(alpha = 0.28f),
                                        selectedLabelColor = NovaLavender,
                                        containerColor = NovaNavyCard
                                    ),
                                    modifier = Modifier.testTag("writing_format_${format.name.lowercase()}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tone",
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
                            WritingTone.entries.forEach { tone ->
                                val selected = state.selectedTone == tone
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.selectTone(tone) },
                                    label = { Text(tone.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NovaViolet.copy(alpha = 0.28f),
                                        selectedLabelColor = NovaLavender,
                                        containerColor = NovaNavyCard
                                    ),
                                    modifier = Modifier.testTag("writing_tone_${tone.name.lowercase()}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = state.prompt,
                            onValueChange = viewModel::updatePrompt,
                            placeholder = { Text("What do you want to write about?") },
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NovaViolet,
                                unfocusedBorderColor = NovaOutline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("writing_prompt_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        NovaGradientButton(
                            text = if (state.isGenerating) "Drafting..." else "Generate Draft",
                            enabled = !state.isGenerating && state.prompt.isNotBlank(),
                            onClick = { viewModel.generateDraft() },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.EditNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.testTag("writing_generate_button")
                        )
                    }
                }

                if (state.generatedOutput.isNotBlank()) {
                    item {
                        NovaGlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SamplePreviewBadge()
                                IconButton(
                                    onClick = { clipboard.setText(AnnotatedString(state.generatedOutput)) },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ContentCopy,
                                        contentDescription = stringResource(R.string.cd_copy),
                                        tint = NovaLavender
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.generatedOutput,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (state.savedDrafts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Saved Drafts (${state.savedDrafts.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    items(state.savedDrafts, key = { it.id }) { draft ->
                        NovaGlassCard(
                            cornerRadius = 18.dp,
                            onClick = { viewModel.loadDraft(draft) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = draft.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${draft.format} • ${draft.tone}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NovaLavender
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteDraft(draft.id) },
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
                }
            }
        }
    }
}
