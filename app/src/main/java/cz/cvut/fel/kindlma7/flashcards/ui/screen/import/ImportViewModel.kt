package cz.cvut.fel.kindlma7.flashcards.ui.screen.import

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.kindlma7.flashcards.data.api.TriviaRepository
import cz.cvut.fel.kindlma7.flashcards.data.csv.CsvParser
import cz.cvut.fel.kindlma7.flashcards.data.repository.DeckRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.FlashcardRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.TopicRepository
import cz.cvut.fel.kindlma7.flashcards.domain.Deck
import cz.cvut.fel.kindlma7.flashcards.domain.Flashcard
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ImportEffect {
    data class NavigateToFlashcards(val deckId: Long) : ImportEffect
    data class ShowError(val message: String) : ImportEffect
}

class ImportViewModel(
    private val deckRepository: DeckRepository,
    private val flashcardRepository: FlashcardRepository,
    private val triviaRepository: TriviaRepository,
    private val topicRepository: TopicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImportUiState>(ImportUiState.Loading)
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ImportEffect>()
    val effects: SharedFlow<ImportEffect> = _effects.asSharedFlow()

    init {
        loadTopics()
    }

    fun onEvent(event: ImportEvent) {
        when (event) {
            is ImportEvent.SelectTab -> updateContent { it.copy(selectedTab = event.tab) }
            is ImportEvent.SelectTopic -> {
                updateContent { state ->
                    val prevTopicName = state.trivia.selectedTopic?.name ?: ""
                    val currentName = state.trivia.deckName
                    val newName = if (currentName.isBlank() || currentName == prevTopicName) event.topic.name else currentName
                    state.copy(trivia = state.trivia.copy(selectedTopic = event.topic, deckName = newName))
                }
            }
            is ImportEvent.SelectDifficulty -> updateContent { it.copy(trivia = it.trivia.copy(selectedDifficulty = event.difficulty)) }
            is ImportEvent.UpdateTriviaSearch -> updateContent { it.copy(trivia = it.trivia.copy(searchQuery = event.query)) }
            is ImportEvent.UpdateTriviaDeckName -> updateContent { it.copy(trivia = it.trivia.copy(deckName = event.name)) }
            is ImportEvent.ImportFromTrivia -> importFromTrivia()
            is ImportEvent.UpdateCsvDeckName -> updateContent { it.copy(csv = it.csv.copy(deckName = event.name)) }
            is ImportEvent.CsvFileSelected -> {
                val result = CsvParser.parse(event.lines)
                updateContent {
                    it.copy(
                        csv = it.csv.copy(
                            fileUri = event.uri,
                            fileName = event.fileName,
                            rowCount = result.pairs.size,
                            lines = event.lines,
                        )
                    )
                }
            }
            is ImportEvent.ImportFromCsv -> importFromCsv()
            is ImportEvent.RetryLoadTopics -> loadTopics()
        }
    }

    private fun loadTopics() {
        _uiState.value = ImportUiState.Loading
        viewModelScope.launch {
            runCatching { triviaRepository.fetchCategories() }
                .onSuccess { topics ->
                    _uiState.value = ImportUiState.Content(
                        trivia = TriviaState(allTopics = topics)
                    )
                }
                .onFailure { e ->
                    _uiState.value = ImportUiState.Error(e.message ?: "Failed to load categories")
                }
        }
    }

    private fun importFromTrivia() {
        val content = _uiState.value as? ImportUiState.Content ?: return
        val topic = content.trivia.selectedTopic ?: return
        val deckName = content.trivia.deckName.trim().ifBlank { return }
        val difficulty = content.trivia.selectedDifficulty

        updateContent { it.copy(importing = true) }
        viewModelScope.launch {
            runCatching {
                val pairs = triviaRepository.fetchQuestions(topic.id, difficulty.apiValue)
                topicRepository.syncFromApi(listOf(topic))
                val deckId = deckRepository.insert(
                    Deck(name = deckName, topicId = topic.id, cardCount = 0, dueCount = 0)
                )
                flashcardRepository.insertAll(pairs.map { (q, a) ->
                    Flashcard(deckId = deckId, question = q, answer = a)
                })
                deckId
            }
                .onSuccess { deckId ->
                    updateContent { it.copy(importing = false) }
                    _effects.emit(ImportEffect.NavigateToFlashcards(deckId))
                }
                .onFailure { e ->
                    updateContent { it.copy(importing = false) }
                    _effects.emit(ImportEffect.ShowError(e.message ?: "Import failed"))
                }
        }
    }

    private fun importFromCsv() {
        val content = _uiState.value as? ImportUiState.Content ?: return
        val deckName = content.csv.deckName.trim().ifBlank { return }
        if (content.csv.fileUri == null) return

        updateContent { it.copy(importing = true) }
        viewModelScope.launch {
            runCatching {
                val result = CsvParser.parse(content.csv.lines)
                if (result.pairs.isEmpty()) throw IllegalStateException("No valid rows found in the CSV file")
                val deckId = deckRepository.insert(
                    Deck(name = deckName, cardCount = 0, dueCount = 0)
                )
                flashcardRepository.insertAll(result.pairs.map { (q, a) ->
                    Flashcard(deckId = deckId, question = q, answer = a)
                })
                deckId
            }
                .onSuccess { deckId ->
                    updateContent { it.copy(importing = false) }
                    _effects.emit(ImportEffect.NavigateToFlashcards(deckId))
                }
                .onFailure { e ->
                    updateContent { it.copy(importing = false) }
                    _effects.emit(ImportEffect.ShowError(e.message ?: "Import failed"))
                }
        }
    }

    private fun updateContent(transform: (ImportUiState.Content) -> ImportUiState.Content) {
        val current = _uiState.value as? ImportUiState.Content ?: return
        _uiState.value = transform(current)
    }

    companion object {
        fun factory(
            deckRepository: DeckRepository,
            flashcardRepository: FlashcardRepository,
            triviaRepository: TriviaRepository,
            topicRepository: TopicRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ImportViewModel(deckRepository, flashcardRepository, triviaRepository, topicRepository) as T
            }
    }
}
