package cz.cvut.fel.kindlma7.flashcards.ui.screen.studysession

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.cvut.fel.kindlma7.flashcards.ui.component.FlashcardFace
import cz.cvut.fel.kindlma7.flashcards.ui.component.FlashcardReview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySessionScreen(
    viewModel: StudySessionViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is StudySessionEffect.NavigateBack -> onNavigateBack()
                is StudySessionEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    val title = when (val s = uiState) {
        is StudySessionUiState.Active -> s.deckName
        is StudySessionUiState.Empty -> s.deckName
        is StudySessionUiState.Complete -> s.deckName
        else -> ""
    }
    val progress = (uiState as? StudySessionUiState.Active)?.let {
        "${it.currentIndex + 1} / ${it.totalCards}"
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(StudySessionEvent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (progress != null) {
                        Text(
                            text = progress,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(end = 16.dp),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is StudySessionUiState.Loading -> LoadingContent(Modifier.padding(innerPadding))
            is StudySessionUiState.Error -> ErrorContent(state.message, Modifier.padding(innerPadding))
            is StudySessionUiState.Empty -> EmptyContent(
                onBack = { viewModel.onEvent(StudySessionEvent.NavigateBack) },
                modifier = Modifier.padding(innerPadding),
            )
            is StudySessionUiState.Complete -> CompleteContent(
                reviewedCount = state.reviewedCount,
                onDone = { viewModel.onEvent(StudySessionEvent.NavigateBack) },
                modifier = Modifier.padding(innerPadding),
            )
            is StudySessionUiState.Active -> {
                var showRating by remember(state.currentCard.id) { mutableStateOf(false) }
                ActiveContent(
                    state = state,
                    showRating = showRating,
                    onFlipped = { showRating = true },
                    onRating = { rating -> viewModel.onEvent(StudySessionEvent.SubmitRating(rating)) },
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
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Composable
private fun EmptyContent(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No cards due for review", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack) { Text("Back") }
        }
    }
}

@Composable
private fun CompleteContent(reviewedCount: Int, onDone: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Session complete!", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                "Reviewed $reviewedCount card${if (reviewedCount == 1) "" else "s"}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onDone) { Text("Done") }
        }
    }
}

@Composable
private fun ActiveContent(
    state: StudySessionUiState.Active,
    showRating: Boolean,
    onFlipped: () -> Unit,
    onRating: (cz.cvut.fel.kindlma7.flashcards.ui.component.ReviewRating) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FlashcardFace(
            front = state.currentCard.question,
            back = state.currentCard.answer,
            onFlipped = onFlipped,
            modifier = Modifier.fillMaxWidth(),
        )
        if (showRating) {
            FlashcardReview(
                onRating = onRating,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
