package cz.cvut.fel.kindlma7.flashcards.ui.screen.flashcardlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.cvut.fel.kindlma7.flashcards.domain.Flashcard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardListScreen(
    viewModel: FlashcardListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToStudySession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FlashcardListEffect.NavigateBack -> onNavigateBack()
                is FlashcardListEffect.NavigateToStudySession -> onNavigateToStudySession()
                is FlashcardListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    val deckName = (uiState as? FlashcardListUiState.Content)?.deck?.name ?: ""

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(deckName) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(FlashcardListEvent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(FlashcardListEvent.StartStudySession) }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start study session")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState is FlashcardListUiState.Content) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(FlashcardListEvent.ShowCreateFlashcardDialog) },
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add flashcard")
                }
            }
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is FlashcardListUiState.Loading -> LoadingContent(Modifier.padding(innerPadding))
            is FlashcardListUiState.Error -> ErrorContent(state.message, Modifier.padding(innerPadding))
            is FlashcardListUiState.Content -> FlashcardListContent(
                state = state,
                onEvent = viewModel::onEvent,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun FlashcardListContent(
    state: FlashcardListUiState.Content,
    onEvent: (FlashcardListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedCardId by remember { mutableStateOf<Long?>(null) }

    if (state.flashcards.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No flashcards yet", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Tap + to add your first card",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.flashcards, key = { it.id }) { flashcard ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    FlashcardItemCard(
                        flashcard = flashcard,
                        onMenuClick = { expandedCardId = flashcard.id },
                    )
                    FlashcardDropdownMenu(
                        expanded = expandedCardId == flashcard.id,
                        onDismiss = { expandedCardId = null },
                        onEdit = {
                            expandedCardId = null
                            onEvent(FlashcardListEvent.ShowEditFlashcardDialog(flashcard))
                        },
                        onDelete = {
                            expandedCardId = null
                            onEvent(FlashcardListEvent.ShowDeleteConfirmation(flashcard))
                        },
                    )
                }
            }
        }
    }

    when (val dialog = state.dialog) {
        is FlashcardListUiState.DialogState.CreateFlashcard -> CreateFlashcardDialog(
            onConfirm = { q, a -> onEvent(FlashcardListEvent.SubmitCreateFlashcard(q, a)) },
            onDismiss = { onEvent(FlashcardListEvent.DismissDialog) },
        )
        is FlashcardListUiState.DialogState.EditFlashcard -> EditFlashcardDialog(
            flashcard = dialog.flashcard,
            onConfirm = { q, a -> onEvent(FlashcardListEvent.SubmitEditFlashcard(dialog.flashcard, q, a)) },
            onDismiss = { onEvent(FlashcardListEvent.DismissDialog) },
        )
        is FlashcardListUiState.DialogState.ConfirmDelete -> ConfirmDeleteDialog(
            flashcard = dialog.flashcard,
            onConfirm = { onEvent(FlashcardListEvent.ConfirmDeleteFlashcard(dialog.flashcard)) },
            onDismiss = { onEvent(FlashcardListEvent.DismissDialog) },
        )
        null -> Unit
    }
}

@Composable
private fun FlashcardItemCard(
    flashcard: Flashcard,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = flashcard.question,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = flashcard.answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Card options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FlashcardDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text("Edit") },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            onClick = onEdit,
        )
        DropdownMenuItem(
            text = { Text("Delete") },
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            onClick = onDelete,
        )
    }
}

@Composable
private fun CreateFlashcardDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New flashcard") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(question, answer) },
                enabled = question.isNotBlank() && answer.isNotBlank(),
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun EditFlashcardDialog(
    flashcard: Flashcard,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var question by remember { mutableStateOf(flashcard.question) }
    var answer by remember { mutableStateOf(flashcard.answer) }
    val changed = question != flashcard.question || answer != flashcard.answer
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit flashcard") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(question, answer) },
                enabled = question.isNotBlank() && answer.isNotBlank() && changed,
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun ConfirmDeleteDialog(
    flashcard: Flashcard,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete flashcard") },
        text = { Text("Delete this card? This cannot be undone.\n\n\"${flashcard.question.take(60)}\"") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
