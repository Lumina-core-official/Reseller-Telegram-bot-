package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MessageEntity
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.TerminalCodeBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiTerminalView(
    messages: List<MessageEntity>,
    inputText: String,
    isLoading: Boolean,
    onInputChange: (String) -> Unit,
    onSendPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    val aiMessages = messages.filter { it.type == "AI" }

    val promptChips = listOf(
        "Diagnose WebRTC ICE latency",
        "Explain Coroutine Channel pipeline",
        "P2P Encryption & Security Audit",
        "Optimize 1080p stream bitrate"
    )

    LaunchedEffect(aiMessages.size, isLoading) {
        if (aiMessages.isNotEmpty()) {
            listState.animateScrollToItem(aiMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .imePadding()
    ) {
        // Terminal Header Banner
        Surface(
            color = CyberSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Terminal",
                        tint = ElectricPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DIAGNOSTIC AI TERMINAL",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini Engine",
                        tint = ElectricPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Gemini 3.5 Flash",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricPurple,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Terminal Messages Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (aiMessages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ElectricPurple.copy(alpha = 0.15f))
                                .border(1.dp, ElectricPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Engine",
                                tint = ElectricPurple,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "NEXUS INTELLIGENCE TERMINAL",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ask any code, architecture, WebRTC, or general knowledge question.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }

            items(aiMessages, key = { it.id }) { msg ->
                val isUser = msg.sender == "Operator"
                AiTerminalBubble(
                    message = msg,
                    isUser = isUser,
                    onCopy = { clipboardManager.setText(AnnotatedString(msg.content)) }
                )
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ElectricPurple,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GEMINI AI THINKING & COMPILING RESPONSE...",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricPurple,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Quick Action Prompt Chips
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            promptChips.forEach { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberSurfaceDark)
                        .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                        .clickable { onSendPrompt(chip) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("prompt_chip_${chip.replace(" ", "_")}")
                ) {
                    Text(
                        text = "⚡ $chip",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Text Input Bar
        Surface(
            color = CyberSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            text = "Type diagnostic prompt or code query...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_terminal_input"),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = ObsidianBackground,
                        unfocusedContainerColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendPrompt(inputText) }),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { onSendPrompt(inputText) },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (inputText.isNotBlank()) ElectricPurple else CyberSurfaceVariant)
                        .testTag("ai_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Prompt",
                        tint = if (inputText.isNotBlank()) ObsidianBackground else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun AiTerminalBubble(
    message: MessageEntity,
    isUser: Boolean,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = if (isUser) "OPERATOR" else "GEMINI-AI",
                style = MaterialTheme.typography.labelSmall,
                color = if (isUser) CyberCyan else ElectricPurple,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Text",
                    tint = TextMuted,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .background(if (isUser) CyberSurfaceVariant else TerminalCodeBg)
                .border(
                    1.dp,
                    if (isUser) CyberCyan.copy(alpha = 0.4f) else ElectricPurple.copy(alpha = 0.4f),
                    RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            RenderFormattedMarkdownText(text = message.content)
        }
    }
}

@Composable
fun RenderFormattedMarkdownText(text: String) {
    val annotatedString = buildAnnotatedString {
        val lines = text.split("\n")
        var inCodeBlock = false

        lines.forEach { line ->
            when {
                line.startsWith("```") -> {
                    inCodeBlock = !inCodeBlock
                    if (!inCodeBlock) append("\n")
                }
                inCodeBlock -> {
                    withStyle(SpanStyle(color = NeonGreen, fontFamily = FontFamily.Monospace, fontSize = 13.sp)) {
                        append(line + "\n")
                    }
                }
                line.startsWith("## ") -> {
                    withStyle(SpanStyle(color = ElectricPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append(line.substring(3) + "\n")
                    }
                }
                line.startsWith("### ") -> {
                    withStyle(SpanStyle(color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                        append(line.substring(4) + "\n")
                    }
                }
                line.startsWith("- ") -> {
                    withStyle(SpanStyle(color = TextPrimary)) {
                        append("  • " + line.substring(2) + "\n")
                    }
                }
                else -> {
                    withStyle(SpanStyle(color = TextPrimary, fontSize = 14.sp)) {
                        append(line + "\n")
                    }
                }
            }
        }
    }

    Text(
        text = annotatedString,
        lineHeight = 20.sp
    )
}
