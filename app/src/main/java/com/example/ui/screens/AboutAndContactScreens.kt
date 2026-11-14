package com.example.ui.screens

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.DisclaimerBanner

@Composable
fun AboutCreatorScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.img_emotional_balance_1794685033252),
                            contentDescription = stringResource(R.string.cd_balance_illustration),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                        )
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = stringResource(R.string.creator_name),
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.semantics { heading() }
                                    )
                                    Text(
                                        text = "Estudiante de noveno semestre de Psicología",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }

                            Text(
                                text = stringResource(R.string.creator_presentation_p1),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(R.string.creator_presentation_p2),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Fundamento científico y ético",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.semantics { heading() }
                            )
                        }
                        Text(
                            text = "• Referencias principales: Manual Diagnóstico y Estadístico de los Trastornos Mentales (DSM-5 y DSM-5-TR) de la Asociación Americana de Psiquiatría (APA).\n• Complemento internacional: Organización Mundial de la Salud (OMS) y Clasificación Internacional de Enfermedades (CIE-11).\n• Ética y divulgación: Este proyecto es estrictamente educativo; no ofrece atención clínica ni atribuye títulos profesionales habilitantes.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item { DisclaimerBanner() }
        }
    }
}

@Composable
fun ContactScreen(
    onSubmitMessage: (name: String, email: String, subject: String, message: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }
    var sentSuccess by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.MailOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Contacto educativo",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.semantics { heading() }
                            )
                        }
                        Text(
                            text = "Envía sugerencias de temas psicoeducativos, dudas sobre los recursos publicados o comentarios académicos. Por privacidad, no se publican datos personales del administrador.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (sentSuccess) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = "Tu mensaje ha sido enviado y registrado correctamente para revisión en el panel administrativo.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }

                        if (!errorMsg.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = errorMsg!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; errorMsg = null },
                            label = { Text("Nombre") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_name_input")
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMsg = null },
                            label = { Text("Correo electrónico") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_email_input")
                        )

                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it; errorMsg = null },
                            label = { Text("Asunto") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_subject_input")
                        )

                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it; errorMsg = null },
                            label = { Text("Mensaje") },
                            minLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_message_input")
                        )

                        Button(
                            onClick = {
                                when {
                                    name.isBlank() -> errorMsg = "Por favor ingresa tu nombre."
                                    !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                                        errorMsg = "Ingresa un correo electrónico válido."
                                    subject.isBlank() -> errorMsg = "Por favor escribe un asunto."
                                    message.trim().length < 10 ->
                                        errorMsg = "El mensaje debe tener al menos 10 caracteres."
                                    else -> {
                                        onSubmitMessage(name, email, subject, message)
                                        name = ""
                                        email = ""
                                        subject = ""
                                        message = ""
                                        errorMsg = null
                                        sentSuccess = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("contact_submit_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(8.dp))
                            Text("Enviar mensaje")
                        }
                    }
                }
            }

            item { DisclaimerBanner() }
        }
    }
}
