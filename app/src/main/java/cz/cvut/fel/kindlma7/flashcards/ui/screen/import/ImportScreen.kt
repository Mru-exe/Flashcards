package cz.cvut.fel.kindlma7.flashcards.ui.screen.import

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    viewModel: ImportViewModel,
    onNavigateToFlashcards: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ImportEffect.NavigateToFlashcards -> onNavigateToFlashcards(effect.deckId)
                is ImportEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Import") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when (val state = uiState) {
            is ImportUiState.Loading -> LoadingContent(Modifier.padding(innerPadding))
            is ImportUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = { viewModel.onEvent(ImportEvent.RetryLoadTopics) },
                modifier = Modifier.padding(innerPadding),
            )
            is ImportUiState.Content -> ImportContent(
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
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
            TextButton(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun ImportContent(
    state: ImportUiState.Content,
    onEvent: (ImportEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabIndex = if (state.selectedTab == ImportTab.TRIVIA) 0 else 1

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            Tab(
                selected = tabIndex == 0,
                onClick = { onEvent(ImportEvent.SelectTab(ImportTab.TRIVIA)) },
                text = { Text("Trivia API") },
            )
            Tab(
                selected = tabIndex == 1,
                onClick = { onEvent(ImportEvent.SelectTab(ImportTab.CSV)) },
                text = { Text("CSV File") },
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (state.selectedTab) {
                ImportTab.TRIVIA -> TriviaTabContent(
                    state = state.trivia,
                    importing = state.importing,
                    onEvent = onEvent,
                )
                ImportTab.CSV -> CsvTabContent(
                    state = state.csv,
                    importing = state.importing,
                    onEvent = onEvent,
                )
            }

            if (state.importing) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun TriviaTabContent(
    state: TriviaState,
    importing: Boolean,
    onEvent: (ImportEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onEvent(ImportEvent.UpdateTriviaSearch(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search categories") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onEvent(ImportEvent.UpdateTriviaSearch("")) }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
        )

        val topics = state.filteredTopics
        if (topics.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text("No categories found", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(topics, key = { it.id }) { topic ->
                    ListItem(
                        headlineContent = { Text(topic.name) },
                        trailingContent = {
                            if (state.selectedTopic?.id == topic.id) {
                                Icon(Icons.Default.Check, contentDescription = "Selected")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEvent(ImportEvent.SelectTopic(topic)) },
                        tonalElevation = if (state.selectedTopic?.id == topic.id) 2.dp else 0.dp,
                    )
                    HorizontalDivider()
                }
            }
        }

        // Difficulty + topic + deck name + import button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Difficulty", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Difficulty.entries.forEach { difficulty ->
                    FilterChip(
                        selected = state.selectedDifficulty == difficulty,
                        onClick = { onEvent(ImportEvent.SelectDifficulty(difficulty)) },
                        label = { Text(difficulty.label) },
                    )
                }
            }
            OutlinedTextField(
                value = state.selectedTopic?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Topic") },
                singleLine = true,
                placeholder = { Text("Select a category above") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.deckName,
                onValueChange = { onEvent(ImportEvent.UpdateTriviaDeckName(it)) },
                label = { Text("Deck name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onEvent(ImportEvent.ImportFromTrivia) },
                enabled = state.selectedTopic != null && state.deckName.isNotBlank() && !importing,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (importing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Import")
                }
            }
        }
    }
}

@Composable
private fun CsvTabContent(
    state: CsvState,
    importing: Boolean,
    onEvent: (ImportEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val fileName = context.contentResolver.query(
            uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        } ?: uri.lastPathSegment ?: "Unknown file"

        val lines = try {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.readLines() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
        onEvent(ImportEvent.CsvFileSelected(uri, fileName, lines))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = state.deckName,
            onValueChange = { onEvent(ImportEvent.UpdateCsvDeckName(it)) },
            label = { Text("Deck name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.topic,
            onValueChange = { onEvent(ImportEvent.UpdateCsvTopic(it)) },
            label = { Text("Topic") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedButton(
            onClick = { filePickerLauncher.launch(arrayOf("text/csv", "text/plain", "*/*")) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Choose File")
        }

        if (state.fileName != null) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = state.fileName,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (state.rowCount != null) {
                    Text(
                        text = "${state.rowCount} card${if (state.rowCount == 1) "" else "s"} found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            Text(
                text = "Expected format: ',' (comma) separated values with 'question,answer' on each line.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onEvent(ImportEvent.ImportFromCsv) },
            enabled = state.fileUri != null && state.deckName.isNotBlank() && state.topic.isNotBlank() && (state.rowCount ?: 0) > 0 && !importing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (importing) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Import")
            }
        }
    }
}
