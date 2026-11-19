package com.example.ui.profile

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.NovaGlassCard
import com.example.ui.components.NovaTopBar
import com.example.ui.theme.NovaLavender
import com.example.ui.theme.NovaViolet

@Composable
fun SettingsScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings
    var showClearConfirm by remember { mutableStateOf(false) }

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
                title = stringResource(R.string.settings_title),
                subtitle = "Preferences saved automatically",
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    NovaGlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Response Creativity",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${(settings.responseCreativity * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = NovaLavender
                            )
                        }
                        Slider(
                            value = settings.responseCreativity,
                            onValueChange = viewModel::updateCreativity,
                            colors = SliderDefaults.colors(
                                thumbColor = NovaViolet,
                                activeTrackColor = NovaViolet
                            ),
                            modifier = Modifier.testTag("settings_creativity_slider")
                        )
                        Text(
                            text = "Higher values encourage imaginative brainstorming; lower values stay concise.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    NovaGlassCard {
                        SettingSwitchRow(
                            title = "Stream AI Responses",
                            subtitle = "Reveal chat tokens progressively",
                            checked = settings.streamResponses,
                            onCheckedChange = viewModel::updateStreamResponses,
                            tag = "settings_stream_switch"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingSwitchRow(
                            title = "Voice Auto-Speak",
                            subtitle = "Read voice assistant replies aloud with TTS",
                            checked = settings.voiceAutoSpeak,
                            onCheckedChange = viewModel::updateVoiceAutoSpeak,
                            tag = "settings_autospeak_switch"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingSwitchRow(
                            title = "Dark Cosmic Theme",
                            subtitle = "Signature obsidian-navy & violet palette",
                            checked = settings.darkCosmicMode,
                            onCheckedChange = viewModel::updateDarkCosmicMode,
                            tag = "settings_theme_switch"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingSwitchRow(
                            title = "Haptic Feedback",
                            subtitle = "Subtle tactile confirmation on actions",
                            checked = settings.hapticFeedback,
                            onCheckedChange = viewModel::updateHapticFeedback,
                            tag = "settings_haptics_switch"
                        )
                    }
                }

                item {
                    NovaGlassCard {
                        Text(
                            text = "Local Storage",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Clear all locally stored chats, study decks, image gallery entries, and writing drafts.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showClearConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_clear_data_button")
                        ) {
                            Text("Clear All Local History")
                        }
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear Local Data?") },
            text = { Text("This permanently deletes your saved conversations, gallery previews, study decks, and writing drafts on this device.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllLocalData()
                        showClearConfirm = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NovaLavender,
                checkedTrackColor = NovaViolet
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}
