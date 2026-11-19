package com.example.ui.voice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.NovaGlassCard
import com.example.ui.components.NovaNoticeBanner
import com.example.ui.components.NovaPlasmaOrb
import com.example.ui.components.NovaTopBar
import com.example.ui.components.SamplePreviewBadge
import com.example.ui.theme.NovaCyanAccent
import com.example.ui.theme.NovaLavender
import com.example.ui.theme.NovaNavyCard
import com.example.ui.theme.NovaOutline
import com.example.ui.theme.NovaPrimaryGradient
import com.example.ui.theme.NovaViolet
import java.util.Locale

@Composable
fun VoiceScreen(
    viewModel: VoiceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember(context) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale.getDefault()
                ttsReady = true
            }
        }
        instance
    }

    val speakReply: (String) -> Unit = remember(tts, ttsReady) {
        { text ->
            if (ttsReady) {
                viewModel.onSpeakingStarted()
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        viewModel.onSpeakingFinished()
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        viewModel.onSpeakingFinished()
                    }
                })
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nova_voice_${System.currentTimeMillis()}")
            } else {
                viewModel.onSpeakingFinished()
            }
        }
    }

    val speechRecognizer = remember(context) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    DisposableEffect(speechRecognizer, tts) {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                viewModel.onListeningStarted()
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                val msg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Didn't catch that. Tap the orb or try a quick prompt below."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out. Tap the orb to try again."
                    else -> "Speech recognition unavailable on this device ($error). Use a quick prompt below."
                }
                viewModel.onSpeechError(msg)
            }
            override fun onResults(results: Bundle?) {
                val spoken = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                if (spoken.isNotBlank()) {
                    viewModel.submitTranscript(spoken, speakReply)
                } else {
                    viewModel.onSpeechError("No speech detected.")
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                if (partial.isNotBlank()) {
                    viewModel.onPartialTranscript(partial)
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        onDispose {
            speechRecognizer?.destroy()
            tts.stop()
            tts.shutdown()
        }
    }

    val startListening: () -> Unit = {
        if (speechRecognizer != null) {
            tts.stop()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
            viewModel.onListeningStarted()
            speechRecognizer.startListening(intent)
        } else {
            viewModel.onSpeechError("On-device speech recognizer isn't available here. Tap a quick voice prompt below.")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startListening()
        } else {
            viewModel.onSpeechError("Microphone permission denied. You can still test Nova Voice using the quick prompts below.")
        }
    }

    val handleOrbTap: () -> Unit = {
        when (state.phase) {
            VoicePhase.LISTENING -> {
                speechRecognizer?.stopListening()
                viewModel.onSpeakingFinished()
            }
            VoicePhase.SPEAKING -> {
                tts.stop()
                viewModel.onSpeakingFinished()
            }
            VoicePhase.THINKING -> Unit
            VoicePhase.IDLE -> {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    startListening()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }

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
                title = stringResource(R.string.voice_title),
                subtitle = "On-device speech & TTS assistant",
                onBackClick = onBack,
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleAutoSpeak(!state.autoSpeakEnabled) },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("voice_autospeak_toggle")
                    ) {
                        Icon(
                            imageVector = if (state.autoSpeakEnabled) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                            contentDescription = "Toggle spoken replies",
                            tint = NovaLavender
                        )
                    }
                }
            )

            if (!state.isLiveGeminiConnected) {
                NovaNoticeBanner(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .clip(CircleShape)
                        .clickable(onClick = handleOrbTap)
                        .testTag("voice_orb_button"),
                    contentAlignment = Alignment.Center
                ) {
                    NovaPlasmaOrb(
                        isActive = state.phase == VoicePhase.LISTENING || state.phase == VoicePhase.THINKING,
                        isSpeaking = state.phase == VoicePhase.SPEAKING,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(NovaPrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.phase == VoicePhase.LISTENING || state.phase == VoicePhase.SPEAKING) {
                                Icons.Rounded.Stop
                            } else {
                                Icons.Rounded.Mic
                            },
                            contentDescription = stringResource(R.string.cd_voice_orb),
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val statusLabel = when (state.phase) {
                    VoicePhase.IDLE -> stringResource(R.string.voice_tap_to_speak)
                    VoicePhase.LISTENING -> stringResource(R.string.voice_listening)
                    VoicePhase.THINKING -> stringResource(R.string.voice_thinking)
                    VoicePhase.SPEAKING -> stringResource(R.string.voice_speaking)
                }
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (state.phase == VoicePhase.SPEAKING) NovaCyanAccent else NovaLavender
                )

                if (state.liveTranscript.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${state.liveTranscript}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }

                state.statusNotice?.let { notice ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = notice,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Quick voice prompt chips (helpful on emulators without microphone hardware)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val quickPrompts = listOf(
                    "Plan my day",
                    "Give me a focus tip"
                )
                quickPrompts.forEachIndexed { idx, prompt ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = NovaNavyCard,
                        border = BorderStroke(1.dp, NovaOutline),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.submitTranscript(prompt, speakReply) }
                            .testTag("voice_quick_prompt_$idx")
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelMedium,
                            color = NovaLavender,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.turns, key = { it.id }) { turn ->
                    NovaGlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "You: ${turn.userTranscript}",
                                style = MaterialTheme.typography.titleSmall,
                                color = NovaLavender,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            SamplePreviewBadge()
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = turn.assistantReply,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
