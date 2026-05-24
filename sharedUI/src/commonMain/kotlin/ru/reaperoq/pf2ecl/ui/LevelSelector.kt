package ru.reaperoq.pf2ecl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel

@Composable
fun LevelSelector(
    level: Int,
    onLevelChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Уровень персонажа"
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onLevelChange(level - 1) },
                    enabled = level > CharacterBuilderViewModel.MIN_LEVEL
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Понизить уровень")
                }
                Text(
                    text = level.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { onLevelChange(level + 1) },
                    enabled = level < CharacterBuilderViewModel.MAX_LEVEL
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Повысить уровень")
                }
            }
        }
    }
}
