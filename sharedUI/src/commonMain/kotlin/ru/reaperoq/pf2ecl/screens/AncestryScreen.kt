package ru.reaperoq.pf2ecl.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import org.jetbrains.compose.resources.painterResource
import pathfinder_2e_character_list.sharedui.generated.resources.Res
import pathfinder_2e_character_list.sharedui.generated.resources.allDrawableResources
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Groups
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.reaperoq.pf2ecl.data.Ancestry
import ru.reaperoq.pf2ecl.data.Attribute
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.data.Heritage
import ru.reaperoq.pf2ecl.data.Translations

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AncestryScreen(
    viewModel: CharacterBuilderViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val ancestries by viewModel.ancestries.collectAsState()
    val characterState by viewModel.characterState.collectAsState()
    val selectedAncestry = characterState.ancestry

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор Родословной", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                            text = "Выберите родословную",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Родословная определяет ваше происхождение, начальные хиты, размер, скорость и расовые особенности.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(220.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(ancestries) { ancestry ->
                                AncestryCard(
                                    ancestry = ancestry,
                                    isSelected = selectedAncestry?._id == ancestry._id,
                                    onClick = { viewModel.setAncestry(ancestry) }
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
                        if (selectedAncestry != null) {
                            AncestryDetailPanel(
                                ancestry = selectedAncestry,
                                characterState = characterState,
                                viewModel = viewModel,
                                onContinue = onContinue
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Выберите родословную слева для просмотра деталей",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                if (selectedAncestry == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Выберите родословную",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "Родословная определяет ваше происхождение, начальные хиты, размер, скорость и расовые особенности.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(150.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.heightIn(max = 600.dp)
                        ) {
                            items(ancestries) { ancestry ->
                                AncestryCard(
                                    ancestry = ancestry,
                                    isSelected = false,
                                    onClick = { viewModel.setAncestry(ancestry) }
                                )
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = { Text(Translations.translateAncestry(selectedAncestry.name)) },
                            navigationIcon = {
                                IconButton(onClick = { viewModel.reset() }) {
                                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад к списку")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            AncestryDetailPanel(
                                ancestry = selectedAncestry,
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
fun AncestryCard(
    modifier: Modifier = Modifier,
    ancestry: Ancestry,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val translatedName = Translations.translateAncestry(ancestry.name)
    val ancestryKey = Translations.getAncestryDrawableKey(ancestry.name)
    val resourceId = Res.allDrawableResources[ancestryKey]

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
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = "Выбрано",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }

            if (resourceId != null) {
                Image(
                    painter = painterResource(resourceId),
                    contentDescription = ancestry.name,
                    modifier = Modifier.size(72.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = translatedName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            if (translatedName != ancestry.name) {
                Text(
                    text = ancestry.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ancestry.system.traits.value.take(2).forEach { trait ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = Translations.translateSkill(trait),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AncestryDetailPanel(
    ancestry: Ancestry,
    characterState: ru.reaperoq.pf2ecl.data.CharacterState,
    viewModel: CharacterBuilderViewModel,
    onContinue: () -> Unit
) {
    val scrollState = rememberScrollState()
    val ancestryHeritages by viewModel.ancestryHeritages.collectAsState()
    val versatileHeritages by viewModel.versatileHeritages.collectAsState()
    val heritagesLoading by viewModel.heritagesLoading.collectAsState()
    val sizeText = when (ancestry.system.size.lowercase()) {
        "sm" -> "Маленький (Small)"
        "med" -> "Средний (Medium)"
        "lg" -> "Большой (Large)"
        else -> ancestry.system.size.uppercase()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val ancestryKey = Translations.getAncestryDrawableKey(ancestry.name)
            val resourceId = Res.allDrawableResources[ancestryKey]
            if (resourceId != null) {
                Image(
                    painter = painterResource(resourceId),
                    contentDescription = ancestry.name,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column {
                Text(
                    text = Translations.translateAncestry(ancestry.name),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Родословная • Ремастер Pathfinder 2e",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatPill(label = "Хиты (HP)", value = ancestry.system.hp.toString())
            StatPill(label = "Скорость", value = "${ancestry.system.speed} фт")
            StatPill(label = "Размер", value = sizeText)
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Параметры родословной", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            val flaws = ancestry.system.flaws?.values?.flatMap { it.value } ?: emptyList()
            if (flaws.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Штрафы (Flaws): ", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    flaws.forEach { flawId ->
                        Attribute.fromId(flawId)?.let { attr ->
                            Text(
                                text = "${Translations.translateAttribute(attr)} (-1)",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Text("Улучшения (Boosts):", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            ancestry.system.boosts?.forEach { (key, boost) ->
                val isFixed = boost.value.size == 1
                if (isFixed) {
                    val attr = Attribute.fromId(boost.value.first())
                    if (attr != null) {
                        Text(
                            text = "• ${attr.label} (+1) [Fixed]",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val selectedAttr = characterState.ancestryBoosts[key]

                    val fixedAttrs = ancestry.system.boosts
                        .filter { it.key != key && it.value.value.size == 1 }
                        .mapNotNull { Attribute.fromId(it.value.value.first()) }

                    val availableAttrs = boost.value
                        .mapNotNull { Attribute.fromId(it) }
                        .filter { it !in fixedAttrs }

                    if (availableAttrs.isEmpty()) return@forEach

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Choose a free boost:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableAttrs.forEach { attr ->
                                val isSelected = selectedAttr == attr
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setAncestryBoost(key, attr) },
                                    label = { Text(attr.label) }
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Выбор Наследия", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            if (heritagesLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (ancestryHeritages.isEmpty() && versatileHeritages.isEmpty()) {
                Text(
                    text = "Наследия для этой родословной не найдены.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                if (ancestryHeritages.isNotEmpty()) {
                    ancestryHeritages.forEach { heritage ->
                        HeritageCard(
                            heritage = heritage,
                            isSelected = characterState.heritage == heritage.name,
                            onSelect = { viewModel.updateHeritage(heritage) }
                        )
                    }
                }
                if (versatileHeritages.isNotEmpty()) {
                    if (ancestryHeritages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Versatile Heritage",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    versatileHeritages.forEach { heritage ->
                        HeritageCard(
                            heritage = heritage,
                            isSelected = characterState.heritage == heritage.name,
                            onSelect = { viewModel.updateHeritage(heritage) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val allBoostsSelected = ancestry.system.boosts?.entries?.all { (key, boost) ->
            val isFixed = boost.value.size == 1
            if (isFixed) true
            else {
                val fixedAttrs = ancestry.system.boosts
                    .filter { it.key != key && it.value.value.size == 1 }
                    .mapNotNull { Attribute.fromId(it.value.value.first()) }
                val available = boost.value.mapNotNull { Attribute.fromId(it) }.filter { it !in fixedAttrs }
                if (available.isEmpty()) true
                else characterState.ancestryBoosts[key] != null
            }
        } ?: true
        val heritageSelected = characterState.heritage != null
        val canContinue = allBoostsSelected && heritageSelected

        Button(
            onClick = onContinue,
            enabled = canContinue,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Подтвердить и продолжить")
        }
    }
}

@Composable
private fun HeritageCard(
    heritage: Heritage,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val description = remember(heritage._id) { cleanPf2eText(heritage.system.description.value) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = heritage.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                if (isSelected) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = "Выбрано",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}
