package cz.cvut.fel.kindlma7.flashcards.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.cvut.fel.kindlma7.flashcards.domain.DeckRetentionStat
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToReviewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DashboardEffect.NavigateToReviewAll -> onNavigateToReviewAll()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Dashboard") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ReviewAllCard(dueCount = uiState.dueCount, onReviewAll = viewModel::onReviewAll)
            StatisticsSection(
                reviewedThisMonth = uiState.reviewedThisMonth,
                reviewedLifetime = uiState.reviewedLifetime,
            )
            if (uiState.top3Decks.isNotEmpty()) {
                TopDecksSection(decks = uiState.top3Decks)
            }
        }
    }
}

@Composable
private fun ReviewAllCard(dueCount: Int, onReviewAll: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Quick Action", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("Review All Due Cards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (dueCount > 0) "$dueCount card${if (dueCount != 1) "s" else ""} due today"
                       else "No cards due right now",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onReviewAll,
                enabled = dueCount > 0,
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Start Review")
            }
        }
    }
}

@Composable
private fun StatisticsSection(reviewedThisMonth: Int, reviewedLifetime: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Statistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                label = "This Month",
                value = reviewedThisMonth.toString(),
                subtitle = "reviews",
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                label = "Lifetime",
                value = reviewedLifetime.toString(),
                subtitle = "reviews",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TopDecksSection(decks: List<DeckRetentionStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Top Decks by Retention",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            decks.forEachIndexed { index, deck ->
                if (index > 0) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                RetentionRow(deck = deck, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun RetentionRow(deck: DeckRetentionStat, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                deck.deckName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Text(
                "${(deck.retentionRate * 100).roundToInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = retentionColor(deck.retentionRate),
            )
        }
        LinearProgressIndicator(
            progress = { deck.retentionRate },
            modifier = Modifier.fillMaxWidth(),
            color = retentionColor(deck.retentionRate),
        )
        Text(
            "${deck.reviewCount} review${if (deck.reviewCount != 1) "s" else ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun retentionColor(rate: Float) = when {
    rate >= 0.8f -> MaterialTheme.colorScheme.primary
    rate >= 0.6f -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.error
}
