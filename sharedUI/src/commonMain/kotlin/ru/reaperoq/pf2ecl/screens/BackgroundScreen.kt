package ru.reaperoq.pf2ecl.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.reaperoq.pf2ecl.data.Attribute
import ru.reaperoq.pf2ecl.data.Background
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.data.Translations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundScreen(
    viewModel: CharacterBuilderViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val backgrounds by viewModel.backgrounds.collectAsState()
    val characterState by viewModel.characterState.collectAsState()
    val selectedBackground = characterState.background
    var searchQuery by remember { mutableStateOf("") }

    val filteredBackgrounds = remember(searchQuery, backgrounds) {
        if (searchQuery.isBlank()) backgrounds
        else backgrounds.filter { bg ->
            val ruName = Translations.translateBackground(bg.name)
            bg.name.contains(searchQuery, ignoreCase = true) || ruName.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор Предыстории", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            val isWideScreen = maxWidth > 720.dp

            if (isWideScreen) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Выберите предысторию",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Предыстория отражает прошлое вашего персонажа, дарует два улучшения характеристик, черту и обученность навыку.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Поиск по названию предыстории...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredBackgrounds) { bg ->
                                BackgroundCard(
                                    background = bg,
                                    isSelected = selectedBackground?._id == bg._id,
                                    onClick = { viewModel.setBackground(bg) }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        if (selectedBackground != null) {
                            BackgroundDetailPanel(
                                background = selectedBackground,
                                characterState = characterState,
                                viewModel = viewModel,
                                onContinue = onContinue
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Выберите предысторию слева для просмотра деталей",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                if (selectedBackground == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Выберите предысторию",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Поиск...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredBackgrounds) { bg ->
                                BackgroundCard(
                                    background = bg,
                                    isSelected = false,
                                    onClick = { viewModel.setBackground(bg) }
                                )
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = { Text(Translations.translateBackground(selectedBackground.name)) },
                            navigationIcon = {
                                IconButton(onClick = { viewModel.resetBackground() }) {
                                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад к списку")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            BackgroundDetailPanel(
                                background = selectedBackground,
                                characterState = characterState,
                                viewModel = viewModel,
                                onContinue = onContinue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackgroundCard(
    modifier: Modifier = Modifier,
    background: Background,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val translatedName = Translations.translateBackground(background.name)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
        ) else CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = translatedName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (translatedName != background.name) {
                        Text(
                            text = background.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (isSelected) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = "Выбрано",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val descriptionClean = background.system.description.value
                .replace(Regex("<[^>]*>"), "")
                .trim()
            Text(
                text = descriptionClean,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackgroundDetailPanel(
    background: Background,
    characterState: ru.reaperoq.pf2ecl.data.CharacterState,
    viewModel: CharacterBuilderViewModel,
    onContinue: () -> Unit
) {
    val scrollState = rememberScrollState()
    val descriptionClean = background.system.description.value
        .replace(Regex("<[^>]*>"), "")
        .replace(Regex("&nbsp;"), " ")
        .trim()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column {
            Text(
                text = Translations.translateBackground(background.name),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Предыстория • Pathfinder 2e Remaster",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Описание предыстории", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                text = descriptionClean,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Улучшения предыстории", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                text = "Предыстория дает два улучшения характеристик. Одно из них должно быть из двух предложенных параметров, второе — абсолютно свободное.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            background.system.boosts?.forEach { (key, boost) ->
                val selectedAttr = characterState.backgroundBoosts[key]

                val filteredAttrs = if (key == "1") {
                    val firstBoostSelection = characterState.backgroundBoosts["0"]
                    boost.value.mapNotNull { Attribute.fromId(it) }.filter { it != firstBoostSelection }
                } else {
                    boost.value.mapNotNull { Attribute.fromId(it) }
                }

                val title = if (key == "0") "Параметр предыстории:" else "Свободный параметр предыстории:"

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                        .padding(12.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredAttrs.forEach { attr ->
                            val isSelected = selectedAttr == attr
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setBackgroundBoost(key, attr) },
                                label = { Text(Translations.translateAttribute(attr)) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val allBoostsSelected = background.system.boosts?.keys?.all { characterState.backgroundBoosts[it] != null } ?: true

        Button(
            onClick = onContinue,
            enabled = allBoostsSelected,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Подтвердить и продолжить к Классу")
        }
    }
}
