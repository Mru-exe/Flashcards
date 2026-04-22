package cz.cvut.fel.kindlma7.flashcards.navigation

sealed class Route(val path: String) {
    data object Dashboard : Route("dashboard")
    data object DeckList : Route("deck_list")
    data object Import : Route("import")

    data object FlashcardList : Route("flashcard_list/{deckId}") {
        fun createRoute(deckId: Long) = "flashcard_list/$deckId"
        const val ARG_DECK_ID = "deckId"
    }

    data object StudySession : Route("study_session/{deckId}") {
        fun createRoute(deckId: Long) = "study_session/$deckId"
        const val ARG_DECK_ID = "deckId"
    }
}
