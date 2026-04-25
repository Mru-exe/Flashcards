# Flashcards – Android aplikace

Flashcards aplikace se spaced repetition algoritmem SM-2. Uživatelé si mohou vytvářet vlastní sbírky kartiček nebo importovat otázky z Open Trivia Database API. Aplikace funguje offline.

**Min SDK:** 24 | **Target SDK:** 36

### In progress:

- Tohle README
- **Notifikace** – denní připomenutí na učení
- **DataStore** – preference a uživatelská nastavení
- **UI polishing** – UI/UX
- **Bug fixy** 
- 
## Build a spuštění

```bash
# Build debug APK
./gradlew assembleDebug

# Instalace na zařízení/emulátor
./gradlew installDebug

# Unit testy
./gradlew test

# Instrumented testy (vyžaduje zařízení/emulátor)
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

## Klíčové entity

| Entita         | Popis                                    |
|----------------|------------------------------------------|
| `Deck`         | Sbírka kartičky k určitému tématu       |
| `Flashcard`    | Otázka-odpověď v rámci sbírky            |
| `ReviewRecord` | Historie opakování kartičky (SM-2)       |
| `Topic`        | Kategorie z Open Trivia Database         |

## Obrazovky

| Obrazovka       | Funkcionalita                                            |
|-----------------|----------------------------------------------------------|
| Dashboard       | Přehled sbírek, pokrok, kartičky na dnes               |
| DeckList        | Správa sbírek (vytvoření/úprava/smazání)               |
| FlashcardList   | Správa kartiček v sbírce                               |
| StudySession    | Procházení kartičkami během učení                       |
| Import          | Import otázek z Open Trivia Database                   |

