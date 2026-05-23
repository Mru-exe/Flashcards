package cz.cvut.fel.kindlma7.flashcards.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cz.cvut.fel.kindlma7.flashcards.R

/** (minutes, labelRes) pairs. Values < 15 min use OneTimeWorkRequest (test only). */
private data class IntervalOption(val minutes: Int, val labelRes: Int)

private val INTERVAL_OPTIONS = listOf(
    IntervalOption(1,    R.string.settings_interval_option_test),
    IntervalOption(360,  R.string.settings_interval_option_hours),
    IntervalOption(720,  R.string.settings_interval_option_hours),
    IntervalOption(1440, R.string.settings_interval_option_hours),
    IntervalOption(2880, R.string.settings_interval_option_hours),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.screen_settings_title)) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.settings_notification_interval_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.settings_notification_interval_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()

            Column(modifier = Modifier.selectableGroup()) {
                INTERVAL_OPTIONS.forEach { option ->
                    val label = when (option.labelRes) {
                        R.string.settings_interval_option_test ->
                            stringResource(R.string.settings_interval_option_test)
                        else ->
                            stringResource(R.string.settings_interval_option_hours, option.minutes / 60)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = uiState.notificationIntervalMinutes == option.minutes,
                                onClick = { viewModel.setNotificationInterval(option.minutes) },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = uiState.notificationIntervalMinutes == option.minutes,
                            onClick = null, // handled by Row selectable
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                }
            }
        }
    }
}
