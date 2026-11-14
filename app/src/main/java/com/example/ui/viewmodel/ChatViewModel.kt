package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.UUID

private const val TAG = "ChatViewModel"

data class ChatMessageItem(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false
)

data class ChatUiState(
    val messages: List<ChatMessageItem> = listOf(
        ChatMessageItem(
            isUser = false,
            text = "¡Hola! Soy el asistente psicoeducativo de Psicología para Todos. Puedo ayudarte a comprender conceptos sobre emociones, manejo del estrés, hábitos de bienestar y referencias educativas basadas en el DSM-5-TR, OMS y CIE-11.\n\nRecuerda que mis respuestas tienen fines exclusivamente educativos y no sustituyen una valoración ni atención psicológica profesional."
        )
    ),
    val isGenerating: Boolean = false,
    val errorMessage: String? = null
)

class ChatViewModel : ViewModel() {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    Eres el asistente psicoeducativo de la plataforma "Psicología para Todos", proyecto educativo desarrollado por Roberto Martínez Paternina, estudiante de noveno semestre de Psicología.
                    Reglas obligatorias:
                    1. Responde siempre en español claro, empático, respetuoso y accesible para todo público.
                    2. Basa tus explicaciones en fuentes científicas verificadas como los 22 capítulos actualizados del DSM-5-TR (APA, 2022, incluyendo el trastorno de duelo prolongado y los códigos de atención clínica), la Organización Mundial de la Salud (OMS), la CIE-11 y la psicología basada en evidencia. No inventes citas ni estadísticas.
                    3. Fines estrictamente psicoeducativos: NUNCA emitas diagnósticos clínicos, NUNCA prescribas medicamentos ni tratamientos personalizados, y NUNCA afirmes que el creador es psicólogo profesional titulado.
                    4. Si la persona expresa malestar intenso, crisis emocional o ideación de autolesión, responde con empatía inmediata e indícale acudir de inmediato a servicios de urgencias, líneas de atención en salud mental locales o un profesional de la salud.
                    5. Mantén respuestas estructuradas, concisas y fáciles de leer en pantalla móvil.
                    """.trimIndent()
                )
            }
        )
    }

    private var activeChat: Chat? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private fun getOrCreateChat(): Chat {
        return activeChat ?: model.startChat().also { activeChat = it }
    }

    fun sendMessage(rawText: String) {
        val text = rawText.trim()
        if (text.isEmpty() || _uiState.value.isGenerating) return

        val userMsg = ChatMessageItem(isUser = true, text = text)
        val assistantMsgId = UUID.randomUUID().toString()
        val placeholderAssistant = ChatMessageItem(
            id = assistantMsgId,
            isUser = false,
            text = "",
            isStreaming = true
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg + placeholderAssistant,
                isGenerating = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val accumulated = StringBuilder()
            try {
                val chat = getOrCreateChat()
                withTimeout(60_000L) {
                    chat.sendMessageStream(text).collect { chunk ->
                        val part = chunk.text.orEmpty()
                        if (part.isNotEmpty()) {
                            accumulated.append(part)
                            val currentText = accumulated.toString()
                            _uiState.update { state ->
                                state.copy(
                                    messages = state.messages.map { item ->
                                        if (item.id == assistantMsgId) {
                                            item.copy(text = currentText, isStreaming = true)
                                        } else {
                                            item
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                val finalResponse = accumulated.toString().trim()
                if (finalResponse.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages.map { item ->
                                if (item.id == assistantMsgId) {
                                    item.copy(text = finalResponse, isStreaming = false)
                                } else {
                                    item
                                }
                            },
                            isGenerating = false
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages.filterNot { it.id == assistantMsgId },
                            isGenerating = false,
                            errorMessage = "El asistente no devolvió contenido. Intenta formular nuevamente tu pregunta."
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Chat streaming timed out", e)
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.filterNot { it.id == assistantMsgId && it.text.isBlank() }
                            .map { if (it.id == assistantMsgId) it.copy(isStreaming = false) else it },
                        isGenerating = false,
                        errorMessage = "La respuesta tardó más de 60 segundos. Verifica tu conexión e intenta de nuevo."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI chat failed", e)
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.filterNot { it.id == assistantMsgId && it.text.isBlank() }
                            .map { if (it.id == assistantMsgId) it.copy(isStreaming = false) else it },
                        isGenerating = false,
                        errorMessage = e.localizedMessage ?: "No se pudo conectar con el asistente en este momento."
                    )
                }
            }
        }
    }

    fun resetConversation() {
        activeChat = null
        _uiState.value = ChatUiState()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
