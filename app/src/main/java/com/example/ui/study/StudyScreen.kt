package com.example.ui.study

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material.icons.rounded.School
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ai.FlashcardItem
import com.example.data.ai.QuizQuestionItem
import com.example.data.ai.StudyType
import com.example.ui.components.NovaGlassCard
import com.example.ui.components.NovaGradientButton
import com.example.ui.components.NovaNoticeBanner
import com.example.ui.components.NovaTopBar
import com.example.ui.components.SamplePreviewBadge
import com.example.ui.theme.NovaLavender
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaNavyElevated
import com.example.ui.theme.NovaOutline
import com.example.ui.theme.NovaSuccess
import com.example.ui.theme.NovaViolet

@Composable
fun StudyScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                title = stringResource(R.string.study_title),
                subtitle = "Explanations, Flashcards & Practice Quizzes",
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
                        OutlinedTextField(
                            value = state.topicInput,
                            onValueChange = viewModel::updateTopic,
                            placeholder = {
                                Text("Enter a topic (e.g., Quantum Entanglement)")
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NovaViolet,
                                unfocusedBorderColor = NovaOutline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("study_topic_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StudyType.entries.forEach { type ->
                                val selected = state.selectedType == type
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.selectType(type) },
                                    label = { Text(type.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NovaViolet.copy(alpha = 0.28f),
                                        selectedLabelColor = NovaLavender,
                                        containerColor = NovaNavyCard
                                    ),
                                    modifier = Modifier.testTag("study_type_${type.name.lowercase()}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        NovaGradientButton(
                            text = if (state.isLoading) "Preparing Study Deck..." else "Generate Study Material",
                            enabled = !state.isLoading && state.topicInput.isNotBlank(),
                            onClick = { viewModel.generateStudyMaterial() },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.School,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.testTag("study_generate_button")
                        )
                    }
                }

                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = NovaViolet)
                        }
                    }
                }

                state.result?.let { result ->
                    item {
                        NovaGlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${result.topic} • ${result.type.label}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                SamplePreviewBadge()
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = result.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (result.flashcards.isNotEmpty()) {
                        items(result.flashcards) { card ->
                            FlashcardRow(card = card)
                        }
                    }

                    if (result.quizQuestions.isNotEmpty()) {
                        items(result.quizQuestions.size) { index ->
                            QuizQuestionCard(
                                index = index,
                                question = result.quizQuestions[index],
                                selectedOption = state.selectedQuizAnswers[index],
                                onSelectOption = { opt -> viewModel.selectQuizAnswer(index, opt) }
                            )
                        }
                    }
                }

                if (state.savedItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "Saved Study Library (${state.savedItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    items(state.savedItems, key = { it.id }) { item ->
                        NovaGlassCard(
                            cornerRadius = 18.dp,
                            onClick = { viewModel.generateStudyMaterial(item.topic) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.topic,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.mode,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NovaLavender
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteSavedItem(item.id) },
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

@Composable
private fun FlashcardRow(card: FlashcardItem) {
    var flipped by remember { mutableStateOf(false) }
    NovaGlassCard(
        containerColor = if (flipped) NovaNavyElevated else NovaNavyCard,
        onClick = { flipped = !flipped }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (flipped) "ANSWER" else "QUESTION",
                style = MaterialTheme.typography.labelSmall,
                color = NovaLavender
            )
            Icon(
                imageVector = Icons.Rounded.Flip,
                contentDescription = "Flip flashcard",
                tint = NovaLavender,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (flipped) card.back else card.front,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun QuizQuestionCard(
    index: Int,
    question: QuizQuestionItem,
    selectedOption: Int?,
    onSelectOption: (Int) -> Unit
) {
    NovaGlassCard {
        Text(
            text = "Q${index + 1}. ${question.question}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))
        question.options.forEachIndexed { optIdx, optionText ->
            val isSelected = selectedOption == optIdx
            val isCorrect = optIdx == question.correctIndex
            val borderColor = when {
                selectedOption == null -> NovaOutline
                isCorrect -> NovaSuccess
                isSelected -> MaterialTheme.colorScheme.error
                else -> NovaOutline
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) NovaViolet.copy(alpha = 0.2f) else NovaNavyElevated.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectOption(optIdx) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (selectedOption != null && isCorrect) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Correct option",
                            tint = NovaSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        if (selectedOption != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = NovaLavender
            )
        }
    }
}
