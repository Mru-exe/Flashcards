package cz.cvut.fel.kindlma7.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import cz.cvut.fel.kindlma7.flashcards.navigation.Route
import cz.cvut.fel.kindlma7.flashcards.ui.screen.flashcardlist.FlashcardListScreen
import cz.cvut.fel.kindlma7.flashcards.ui.screen.flashcardlist.FlashcardListViewModel
import cz.cvut.fel.kindlma7.flashcards.ui.theme.FlashcardsTheme

private val bottomNavItems = listOf(
    Triple(Route.Dashboard, "Dashboard", Icons.Default.Home),
    Triple(Route.DeckList, "My Decks", Icons.AutoMirrored.Filled.List),
    Triple(Route.Import, "Import", Icons.Default.Search),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as FlashcardsApplication).container
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
                            //TODO: Implement dashboard screen
                        }

                        composable(Route.DeckList.path) {
                            //TODO: Implement deck list screen
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
                            //TODO: Implement study session screen
                        }
                    }
                }
            }
        }
    }
}