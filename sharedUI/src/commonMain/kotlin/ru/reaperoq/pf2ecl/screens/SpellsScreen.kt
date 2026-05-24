package ru.reaperoq.pf2ecl.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import org.jetbrains.compose.resources.painterResource
import pathfinder_2e_character_list.sharedui.generated.resources.Res
import pathfinder_2e_character_list.sharedui.generated.resources.allDrawableResources
import ru.reaperoq.pf2ecl.data.Translations
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.data.CharacterProgression
import ru.reaperoq.pf2ecl.data.Spell

private fun cleanPf2eText(raw: String): String = raw
    .replace(Regex("@[A-Za-z]+\\[[^\\]]*\\]\\{([^}]*)\\}"), "$1")
    .replace(Regex("@[A-Za-z]+\\[[^\\]]*\\]"), "")
    .replace(Regex("<p>|</p>"), "\n")
    .replace(Regex("<br\\s*/?>"), "\n")
    .replace(Regex("</?strong>|</?b>"), "")
    .replace(Regex("</?em>|</?i>"), "")
    .replace("<li>", "• ").replace(Regex("</li>|</?ul>|</?ol>"), "\n")
    .replace(Regex("<h[1-6]>(.*?)</h[1-6]>"), "$1\n")
    .replace(Regex("<[^>]+>"), "")
    .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
    .replace("&nbsp;", " ").replace("&#13;", "\n")
    .replace(Regex("[ \\t]+"), " ").replace(Regex("\\n{3,}"), "\n\n")
    .trim()

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SpellsScreen(
    viewModel: CharacterBuilderViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val characterState by viewModel.characterState.collectAsState()
    val cantripSpells by viewModel.cantripSpells.collectAsState()
    val spellsByRank by viewModel.spellsByRank.collectAsState()
    val isLoading by viewModel.spellsLoading.collectAsState()

    val maxRank = CharacterProgression.maxSpellRank(characterState.level)
    val rankTabs = remember(maxRank) { (1..maxRank).toList() }
    val tabLabels = remember(characterState, maxRank) {
        listOf("Cantrips (${characterState.selectedCantrips.size})") +
            rankTabs.map { rank ->
                val count = characterState.selectedSpells.count { it.rankLabel == "Rank $rank" }
                "Rank $rank ($count)"
            }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var previewSpell by remember { mutableStateOf<Spell?>(null) }

    LaunchedEffect(characterState.level) {
        viewModel.loadBuilderSpells()
    }

    LaunchedEffect(maxRank) {
        if (selectedTab > maxRank) selectedTab = 0
    }

    val selectedCount = characterState.selectedCantrips.size + characterState.selectedSpells.size
    val isCantripTab = selectedTab == 0
    val currentRank = if (isCantripTab) null else rankTabs.getOrNull(selectedTab - 1)
    val currentSpells = if (isCantripTab) cantripSpells else spellsByRank[currentRank] ?: emptyList()
    val filteredSpells = remember(currentSpells, searchQuery) {
        if (searchQuery.isBlank()) currentSpells
        else currentSpells.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Заклинания", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        if (selectedCount > 0) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    "$selectedCount",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Черты →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            @Suppress("deprecation")
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabLabels.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index; previewSpell = null },
                        text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                label = { Text("Поиск") },
                singleLine = true
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(if (previewSpell != null) 1f else 1f)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredSpells, key = { it._id }) { spell ->
                            val isSelected = if (isCantripTab) {
                                characterState.selectedCantrips.any { it.id == spell._id }
                            } else {
                                characterState.selectedSpells.any { it.id == spell._id }
                            }
                            SpellListItem(
                                spell = spell,
                                isSelected = isSelected,
                                isPreviewed = previewSpell?._id == spell._id,
                                onPreview = { previewSpell = spell },
                                onToggle = {
                                    if (isCantripTab) viewModel.toggleCantrip(spell)
                                    else currentRank?.let { viewModel.toggleRankSpell(spell, it) }
                                }
                            )
                        }
                    }

                    previewSpell?.let { spell ->
                        SpellDetailPanel(
                            spell = spell,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .padding(end = 16.dp, bottom = 8.dp),
                            isSelected = if (isCantripTab) {
                                characterState.selectedCantrips.any { it.id == spell._id }
                            } else {
                                characterState.selectedSpells.any { it.id == spell._id }
                            },
                            onToggle = {
                                if (isCantripTab) viewModel.toggleCantrip(spell)
                                else currentRank?.let { viewModel.toggleRankSpell(spell, it) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellListItem(
    spell: Spell,
    isSelected: Boolean,
    isPreviewed: Boolean,
    onPreview: () -> Unit,
    onToggle: () -> Unit
) {
    val spellKey = Translations.getSpellDrawableKey(spell.name)
    val resourceId = Res.allDrawableResources[spellKey]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPreview),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isPreviewed -> MaterialTheme.colorScheme.secondaryContainer
                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (resourceId != null) {
                    Image(
                        painter = painterResource(resourceId),
                        contentDescription = spell.name,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.AutoStories,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = spell.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onToggle, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = "Select",
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpellDetailPanel(
    spell: Spell,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val description = remember(spell._id) { cleanPf2eText(spell.system.description.value) }
    val traditions = spell.system.traits?.traditions.orEmpty()
    val traits = spell.system.traits?.value.orEmpty().filter { it != "cantrip" }
    val castTime = spell.system.time?.value?.takeIf { it.isNotBlank() }?.let { "$it actions" }
    val range = spell.system.range?.value?.takeIf { it.isNotBlank() }

    val spellKey = Translations.getSpellDrawableKey(spell.name)
    val resourceId = Res.allDrawableResources[spellKey]

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (resourceId != null) {
                    Image(
                        painter = painterResource(resourceId),
                        contentDescription = spell.name,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column {
                    Text(
                        text = spell.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Заклинание • Pathfinder 2e",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                traditions.forEach { tradition ->
                    FilterChip(selected = false, onClick = {}, label = { Text(tradition, style = MaterialTheme.typography.labelSmall) })
                }
                traits.forEach { trait ->
                    FilterChip(selected = false, onClick = {}, label = { Text(trait, style = MaterialTheme.typography.labelSmall) })
                }
            }

            listOfNotNull(castTime, range).forEach { meta ->
                Text(meta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (description.isNotBlank()) {
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isSelected) "Убрать" else "Добавить")
            }
        }
    }
}
