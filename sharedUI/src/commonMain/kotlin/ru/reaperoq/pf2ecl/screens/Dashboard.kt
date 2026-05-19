package ru.reaperoq.pf2ecl.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.content.contentReceiver
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Ваши персонажи")
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        FlowRow(
            modifier = Modifier.padding(paddingValues).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CharacterCard(
                name = "Brog",
                level = 3,
                cls = "Goblin",
                background = "Rogue"
            )
            CharacterCard(
                name = "Brog",
                level = 3,
                cls = "Goblin",
                background = "Rogue"
            )
            CharacterCard(
                name = "Brog",
                level = 3,
                cls = "Goblin",
                background = "Rogue"
            )
            CharacterCard(
                name = "Brog",
                level = 3,
                cls = "Goblin",
                background = "Rogue"
            )
        }
    }
}

@Composable
fun CharacterCard(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    name: String,
    level: Int,
    cls: String,
    background: String
) {
    Card(
        modifier = modifier.padding(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon?.let {
                Image(
                    modifier = Modifier.size(128.dp),
                    imageVector = icon,
                    contentDescription = null
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Уровень $level")
                Text(name)
                Text("$background $cls")
            }
        }
    }
}