package com.example.giga_chat_pet.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.giga_chat_pet.presentation.chat.ChatMessageUiModel
import com.example.giga_chat_pet.presentation.chat.MessageStatusUiModel
import com.example.giga_chat_pet.presentation.renderer.MarkdownRenderer
import com.example.giga_chat_pet.ui.viewmodel.ChatViewModel
import android.widget.TextView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    conversationId: Long,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Чат") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_revert),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.updateInputText(it) },
                    modifier = Modifier
                        .weight(1f),
                    placeholder = { Text("Сообщение...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    enabled = !uiState.isLoading
                )
                IconButton(
                    onClick = { viewModel.sendMessage() },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = 8.dp),
                    enabled = uiState.inputText.isNotBlank() && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(4.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_send),
                            contentDescription = "Отправить",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = paddingValues
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        onRetryClick = { viewModel.retryFailedMessage(message.id, message.text) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessageUiModel,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<ChatViewModel>()
    val markdownRenderer = viewModel.getMarkdownRenderer()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
        ) {
            AndroidView(
                factory = { ctx ->
                    TextView(ctx).apply {
                        setPadding(35, 35, 35, 35)
                        setLineSpacing(0f, 1.3f)
                    }
                },
                update = { textView ->
                    textView.text = markdownRenderer.render(context, message.text)
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
                },
                modifier = Modifier
                    .widthIn(max = 250.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                            bottomEnd = if (message.isFromMe) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (message.isFromMe) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
            )
            if (message.isFromMe) {
                MessageStatusIcon(status = message.status, onRetryClick = onRetryClick)
            }
        }
    }
}

@Composable
private fun MessageStatusIcon(
    status: MessageStatusUiModel,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit = {}
) {
    val icon = when (status) {
        MessageStatusUiModel.SENDING -> "⏳"
        MessageStatusUiModel.SENT -> "✓"
        MessageStatusUiModel.ERROR -> "⟳"
    }

    if (status == MessageStatusUiModel.ERROR) {
        IconButton(onClick = onRetryClick) {
            Text(
                text = icon,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        Text(
            text = icon,
            modifier = modifier.padding(top = 4.dp, end = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = when (status) {
                MessageStatusUiModel.SENDING -> MaterialTheme.colorScheme.onSurfaceVariant
                MessageStatusUiModel.SENT -> MaterialTheme.colorScheme.onPrimary
                MessageStatusUiModel.ERROR -> MaterialTheme.colorScheme.error
            }
        )
    }
}
