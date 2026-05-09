package cz.cvut.fel.kindlma7.flashcards.ui.screen.decklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.cvut.fel.kindlma7.flashcards.R
import cz.cvut.fel.kindlma7.flashcards.domain.Deck
import cz.cvut.fel.kindlma7.flashcards.ui.component.DeckCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    viewModel: DeckListViewModel,
    onNavigateToFlashcards: (Long) -> Unit,
    onNavigateToStudySession: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DeckListEffect.NavigateToFlashcards -> onNavigateToFlashcards(effect.deckId)
                is DeckListEffect.NavigateToStudySession -> onNavigateToStudySession(effect.deckId)
                is DeckListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.screen_deck_list_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState is DeckListUiState.Content) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(DeckListEvent.ShowCreateDeckDialog) },
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.deck_list_fab_cd))
                }
            }
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is DeckListUiState.Loading -> LoadingContent(Modifier.padding(innerPadding))
            is DeckListUiState.Error -> ErrorContent(state.message, Modifier.padding(innerPadding))
            is DeckListUiState.Content -> {
                DeckListContent(
                    state = state,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.padding(innerPadding),
                )
            }
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
private fun DeckListContent(
    state: DeckListUiState.Content,
    onEvent: (DeckListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedDeckId by remember { mutableStateOf<Long?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onEvent(DeckListEvent.UpdateSearchQuery(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.deck_list_search_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onEvent(DeckListEvent.UpdateSearchQuery("")) }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_clear_search))
                    }
                }
            },
            singleLine = true,
        )

        if (state.decks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (state.searchQuery.isNotBlank()) {
                        Text(stringResource(R.string.label_no_results, state.searchQuery), style = MaterialTheme.typography.titleMedium)
                    } else {
                        Text(stringResource(R.string.deck_list_empty_title), style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(R.string.deck_list_empty_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.decks, key = { it.id }) { deck ->
                    DeckCard(
                        name = deck.name,
                        topic = deck.topic,
                        cardCount = deck.cardCount,
                        dueCount = deck.dueCount,
                        onClick = { onEvent(DeckListEvent.OpenFlashcards(deck)) },
                        onMenuClick = { expandedDeckId = deck.id },
                        dropdownMenu = {
                            DeckDropdownMenu(
                                expanded = expandedDeckId == deck.id,
                                onDismiss = { expandedDeckId = null },
                                onStudy = {
                                    expandedDeckId = null
                                    onEvent(DeckListEvent.OpenStudySession(deck))
                                },
                                onRename = {
                                    expandedDeckId = null
                                    onEvent(DeckListEvent.ShowEditDeckDialog(deck))
                                },
                                onDelete = {
                                    expandedDeckId = null
                                    onEvent(DeckListEvent.ShowDeleteConfirmation(deck))
                                },
                            )
                        },
                    )
            }
        }
    }

    }

    when (val dialog = state.dialog) {
        is DeckListUiState.DialogState.CreateDeck -> CreateDeckDialog(
            onConfirm = { name, topic -> onEvent(DeckListEvent.SubmitCreateDeck(name, topic)) },
            onDismiss = { onEvent(DeckListEvent.DismissDialog) },
        )
        is DeckListUiState.DialogState.EditDeck -> EditDeckDialog(
            deck = dialog.deck,
            onConfirm = { newName -> onEvent(DeckListEvent.SubmitRenameDeck(dialog.deck, newName)) },
            onDismiss = { onEvent(DeckListEvent.DismissDialog) },
        )
        is DeckListUiState.DialogState.ConfirmDelete -> ConfirmDeleteDialog(
            deck = dialog.deck,
            onConfirm = { onEvent(DeckListEvent.ConfirmDeleteDeck(dialog.deck)) },
            onDismiss = { onEvent(DeckListEvent.DismissDialog) },
        )
        null -> Unit
    }
}

@Composable
private fun DeckDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onStudy: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.deck_menu_study)) },
            leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
            onClick = onStudy,
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.action_rename)) },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            onClick = onRename,
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.action_delete)) },
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
private fun CreateDeckDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.deck_create_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.label_deck_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text(stringResource(R.string.label_topic)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, topic) }, enabled = name.isNotBlank() && topic.isNotBlank()) {
                Text(stringResource(R.string.action_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}

@Composable
private fun EditDeckDialog(
    deck: Deck,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(deck.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.deck_rename_dialog_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.label_deck_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank() && name != deck.name,
            ) {
                Text(stringResource(R.string.action_rename))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}

@Composable
private fun ConfirmDeleteDialog(
    deck: Deck,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.deck_delete_dialog_title)) },
        text = { Text(stringResource(R.string.deck_delete_dialog_message, deck.name)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}
