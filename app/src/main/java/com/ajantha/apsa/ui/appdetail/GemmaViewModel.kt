package com.ajantha.apsa.ui.appdetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajantha.apsa.model.AppUiModel
import com.ajantha.apsa.ui.appdetail.util.buildAppSummaryPrompt
import com.ajantha.apsa.ui.appdetail.util.buildComponentPrompt
import com.ajantha.apsa.ui.appdetail.util.buildPermissionPrompt
import com.ajantha.apsa.util.removeMarkdown
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.seconds

class GemmaViewModel : ViewModel() {

    private var generationJob: Job? = null
    private var engine: Engine? = null
    private var conversation: Conversation? = null

    private val _uiState = MutableStateFlow<GemmaUiState>(GemmaUiState.Idle)
    val uiState: StateFlow<GemmaUiState> = _uiState.asStateFlow()

    private val _isModelInitialized = MutableStateFlow(false)
    val isModelInitialized: StateFlow<Boolean> = _isModelInitialized.asStateFlow()

    private val _analysisTitle = MutableStateFlow("")
    val analysisTitle: StateFlow<String> = _analysisTitle.asStateFlow()

    fun initializeModel(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val modelFile = File(
                    context.getExternalFilesDir(null),
                    MODEL_FILE_NAME
                )

                // 4. Verify the private file now exists
                if (!modelFile.exists()) {
                    _isModelInitialized.update { false }
                    _uiState.update { GemmaUiState.ModelMissing }
                    return@launch
                }

                _uiState.update { GemmaUiState.Loading }

                cleanupResourcesInternal()

                // 5. Initialize LiteRT-LM using the private file path
                val config = EngineConfig(
                    modelPath = modelFile.absolutePath,
                    backend = Backend.CPU()
                )

                val newEngine = Engine(config)
                newEngine.initialize()

                engine = newEngine

                _isModelInitialized.update { true }
                _uiState.update { GemmaUiState.Ready }

            } catch (e: Exception) {
                _isModelInitialized.update { false }
                _uiState.update {
                    GemmaUiState.Error(
                        "Failed to initialize LiteRT-LM: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun analyzeAppSummary(model: AppUiModel) {
        askGemma("App Privacy and Security", buildAppSummaryPrompt(model))
    }

    fun analyzePermission(appName: String, permission: String) {
        askGemma("Permission Explanation", buildPermissionPrompt(appName, permission))
    }

    fun analyzeComponent(appName: String, componentType: String, componentName: String) {
        askGemma(
            "$componentType Explanation",
            buildComponentPrompt(appName, componentType, componentName)
        )
    }

    fun askGemma(title: String, prompt: String) {
        val currentEngine = engine ?: run {
            _uiState.update { GemmaUiState.Error("Engine not initialized") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Immediately signal Native C++ Engine to stop current generation
            stopNativeGeneration()

            // 2. Clear UI instantly
            _analysisTitle.update { title }
            _uiState.update { GemmaUiState.Thinking("") }

            // 3. Launch a new isolation job
            generationJob = launch {
                var currentConversation: Conversation? = null

                try {
                    // Always instantiate a clean conversation for isolated requests
                    currentConversation = currentEngine.createConversation()
                    conversation = currentConversation

                    val builder = StringBuilder()

                    currentConversation.sendMessageAsync(prompt).collect { partial ->
                        ensureActive()

                        builder.append(partial)
                        val text = builder.toString().cleanOutput()

                        _uiState.update { GemmaUiState.Thinking(text) }
                    }

                    ensureActive()

                    val finalText = builder.toString().cleanOutput()
                    _uiState.update { GemmaUiState.Success(finalText) }

                } catch (_: CancellationException) {
                    // Interrupted by stop or new request
                } catch (e: Exception) {
                    _uiState.update {
                        GemmaUiState.Error("Generation failed: ${e.localizedMessage ?: "Unknown error"}")
                    }
                } finally {
                    delay(1.seconds)
                    // Safe cleanup of conversation object
                    try {
                        currentConversation?.close()
                    } catch (_: Exception) {
                    }
                    if (conversation === currentConversation) {
                        conversation = null
                    }
                }
            }
        }
    }

    fun stopGeneration() {
        viewModelScope.launch(Dispatchers.IO) {
            stopNativeGeneration()
            _uiState.update {
                if (_isModelInitialized.value) GemmaUiState.Ready else GemmaUiState.Idle
            }
        }
    }

    fun resetState() {
        stopGeneration()
    }

    /**
     * Halts C++ inference mid-stream and joins the running coroutine cleanly.
     */
    private suspend fun stopNativeGeneration() {
        try {
            // Tell LiteRT native runtime to stop generating tokens right now
            conversation?.cancelProcess()
        } catch (_: Exception) {
        }

        // Join the Kotlin coroutine subscriber so it terminates safely
        generationJob?.cancelAndJoin()
        generationJob = null

        try {
            conversation?.close()
        } catch (_: Exception) {
        }
        conversation = null
    }

    private fun cleanupResourcesInternal() {
        try {
            conversation?.cancelProcess()
        } catch (_: Exception) {
        }
        try {
            conversation?.close()
        } catch (_: Exception) {
        }
        try {
            engine?.close()
        } catch (_: Exception) {
        }
        conversation = null
        engine = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            cleanupResourcesInternal()
        }
    }

    private fun String.cleanOutput(): String = this.removeMarkdown().trim()

    companion object {
        private const val MODEL_FILE_NAME = "gemma-4-E2B-it.litertlm"
    }
}

sealed interface GemmaUiState {
    data object Idle : GemmaUiState
    data object ModelMissing : GemmaUiState
    data object Ready : GemmaUiState
    data object Loading : GemmaUiState
    data class Thinking(val partialText: String) : GemmaUiState
    data class Success(val answer: String) : GemmaUiState
    data class Error(val message: String) : GemmaUiState
}

val GemmaUiState.shouldShowDialog: Boolean
    get() = when (this) {
        is GemmaUiState.Thinking,
        is GemmaUiState.Success,
        is GemmaUiState.Error -> true

        else -> false
    }