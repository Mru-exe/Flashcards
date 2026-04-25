package cz.cvut.fel.kindlma7.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cz.cvut.fel.kindlma7.flashcards.navigation.Route
import cz.cvut.fel.kindlma7.flashcards.ui.screen.dashboard.DashboardScreen
import cz.cvut.fel.kindlma7.flashcards.ui.screen.dashboard.DashboardViewModel
import cz.cvut.fel.kindlma7.flashcards.ui.screen.decklist.DeckListScreen
import cz.cvut.fel.kindlma7.flashcards.ui.screen.decklist.DeckListViewModel
import cz.cvut.fel.kindlma7.flashcards.ui.screen.studysession.StudySessionScreen
import cz.cvut.fel.kindlma7.flashcards.ui.screen.studysession.StudySessionViewModel
import cz.cvut.fel.kindlma7.flashcards.ui.theme.FlashcardsTheme

private val bottomNavItems = listOf(
    Triple(Route.Dashboard, "Dashboard", Icons.Default.Home),
    Triple(Route.DeckList, "My Decks", Icons.AutoMirrored.Filled.List),
    Triple(Route.Import, "Import", Icons.Default.Search),
)

class MainActivity : ComponentActivity() {
    private val appContainer get() = (application as FlashcardsApplication).container

    private val deckListViewModel: DeckListViewModel by viewModels {
        DeckListViewModel.factory(appContainer.deckRepository)
    }

    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModel.factory(appContainer.flashcardRepository, appContainer.reviewRecordRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = appContainer
        setContent {
            FlashcardsTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            bottomNavItems.forEach { (route, label, icon) ->
                                NavigationBarItem(
                                    selected = currentDestination?.hierarchy?.any { it.route == route.path } == true,
                                    onClick = {
                                        navController.navigate(route.path) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label) },
                                )
                            }
                        }
                    },
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.DeckList.path,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable(Route.Dashboard.path) {
                            DashboardScreen(
                                viewModel = dashboardViewModel,
                                onNavigateToReviewAll = {
                                    navController.navigate(Route.StudySession.createRoute(REVIEW_ALL_DECK_ID))
                                },
                            )
                        }

                        composable(Route.DeckList.path) {
                            DeckListScreen(
                                viewModel = deckListViewModel,
                                onNavigateToFlashcards = { deckId ->
                                    navController.navigate(Route.FlashcardList.createRoute(deckId))
                                },
                                onNavigateToStudySession = { deckId ->
                                    navController.navigate(Route.StudySession.createRoute(deckId))
                                },
                            )
                        }

                        composable(Route.Import.path) {
                            //TODO: Implement import screen
                        }

                        composable(
                            route = Route.FlashcardList.path,
                            arguments = listOf(
                                navArgument(Route.FlashcardList.ARG_DECK_ID) { type = NavType.LongType },
                            ),
                        ) { backStack ->
                            val deckId = backStack.arguments!!.getLong(Route.FlashcardList.ARG_DECK_ID)
                            val viewModel = remember(deckId) {
                                ViewModelProvider(
                                    backStack,
                                    FlashcardListViewModel.factory(
                                        appContainer.flashcardRepository,
                                        appContainer.deckRepository,
                                        deckId,
                                    )
                                )[FlashcardListViewModel::class.java]
                            }
                            FlashcardListScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToStudySession = {
                                    navController.navigate(Route.StudySession.createRoute(deckId))
                                },
                            )
                        }

                        composable(
                            route = Route.StudySession.path,
                            arguments = listOf(
                                navArgument(Route.StudySession.ARG_DECK_ID) { type = NavType.LongType },
                            ),
                        ) { backStack ->
                            val deckId = backStack.arguments!!.getLong(Route.StudySession.ARG_DECK_ID)
                            val viewModel = remember(deckId) {
                                ViewModelProvider(
                                    backStack,
                                    StudySessionViewModel.factory(
                                        appContainer.flashcardRepository,
                                        appContainer.reviewRecordRepository,
                                        appContainer.deckRepository,
                                        deckId,
                                    )
                                )[StudySessionViewModel::class.java]
                            }
                            StudySessionScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                            )
                        }
                    }
                }
            }
        }
    }
}
