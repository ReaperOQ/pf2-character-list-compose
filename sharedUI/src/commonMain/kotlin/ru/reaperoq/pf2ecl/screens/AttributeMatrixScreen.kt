package ru.reaperoq.pf2ecl.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.reaperoq.pf2ecl.data.Attribute
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.data.CharacterProgression
import ru.reaperoq.pf2ecl.data.Translations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttributeMatrixScreen(
    viewModel: CharacterBuilderViewModel,
    onBack: () -> Unit,
    onGenerate: () -> Unit
) {
    val characterState by viewModel.characterState.collectAsState()
    val attributes = viewModel.calculateAttributes()
    val boostTiers = CharacterProgression.unlockedAttributeBoostTiers(characterState.level)
    val allBoostsComplete = CharacterProgression.areAllAttributeBoostsComplete(characterState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Матрица Атрибутов", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Правила Pathfinder 2e Remaster",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "На 1, 5, 10, 15 и 20 уровнях персонаж получает 4 свободных улучшения характеристик. Каждую характеристику можно улучшить только один раз в рамках одного набора.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                boostTiers.forEach { tier ->
                    val tierBoosts = CharacterProgression.attributeBoostsForTier(characterState, tier)
                    val tierComplete = tierBoosts.size == CharacterProgression.BOOSTS_PER_TIER

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (tier == 1) "Свободные улучшения 1-го уровня" else "Свободные улучшения $tier-го уровня",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text("Выбрано: ${tierBoosts.size} из ${CharacterProgression.BOOSTS_PER_TIER}") },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (tierComplete) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val columnsCount = if (isWideScreen) 3 else 1
                    val chunkedAttributes = Attribute.entries.chunked(columnsCount)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        chunkedAttributes.forEach { rowAttrs ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                rowAttrs.forEach { attr ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        val finalMod = attributes[attr] ?: 0
                                        val isTierBoosted = tierBoosts.contains(attr)

                                        val hasAncestryBoost = characterState.ancestryBoosts.values.contains(attr)
                                        val hasAncestryFlaw = characterState.ancestry?.system?.flaws?.values
                                            ?.flatMap { it.value }
                                            ?.mapNotNull { Attribute.fromId(it) }
                                            ?.contains(attr) == true
                                        val hasBackgroundBoost = characterState.backgroundBoosts.values.contains(attr)
                                        val isClassKey = characterState.classBoost == attr ||
                                            (characterState.classData?.system?.keyAbility?.value?.size == 1 &&
                                                Attribute.fromId(characterState.classData?.system?.keyAbility?.value?.first() ?: "") == attr)

                                        AttributeDetailCard(
                                            attribute = attr,
                                            finalValue = finalMod,
                                            hasAncestryBoost = hasAncestryBoost,
                                            hasAncestryFlaw = hasAncestryFlaw,
                                            hasBackgroundBoost = hasBackgroundBoost,
                                            isClassKey = isClassKey,
                                            isCustomBoosted = isTierBoosted,
                                            tierLabel = if (tier == 1) "Свободное улучшение (+1)" else "Улучшение $tier-го уровня (+1)",
                                            showBreakdown = tier == boostTiers.first(),
                                            hasFreeBoost1 = characterState.freeBoosts.contains(attr),
                                            levelBoostTiers = if (tier == boostTiers.first()) {
                                                characterState.attributeBoosts.filterKeys { it in boostTiers && it != 1 }
                                            } else emptyMap(),
                                            onToggleBoost = {
                                                viewModel.toggleAttributeBoostTier(tier, attr)
                                            }
                                        )
                                    }
                                }
                                if (rowAttrs.size < columnsCount) {
                                    repeat(columnsCount - rowAttrs.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    if (tier != boostTiers.last()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onBack) {
                        Text("Назад")
                    }
                    Button(
                        onClick = onGenerate,
                        enabled = allBoostsComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Сгенерировать лист персонажа")
                    }
                }
            }
        }
    }
}

@Composable
fun AttributeDetailCard(
    attribute: Attribute,
    finalValue: Int,
    hasAncestryBoost: Boolean,
    hasAncestryFlaw: Boolean,
    hasBackgroundBoost: Boolean,
    isClassKey: Boolean,
    isCustomBoosted: Boolean,
    tierLabel: String = "Свободное улучшение (+1)",
    showBreakdown: Boolean = true,
    hasFreeBoost1: Boolean = false,
    levelBoostTiers: Map<Int, Set<Attribute>> = emptyMap(),
    onToggleBoost: () -> Unit
) {
    val finalSign = if (finalValue >= 0) "+$finalValue" else "$finalValue"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Translations.translateAttribute(attribute),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = attribute.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = if (finalValue > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = finalSign,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            if (showBreakdown) {
                HorizontalDivider()

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Источники улучшения:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    BreakdownRow(label = "Базовое значение", value = "+0")
                    if (hasAncestryBoost) BreakdownRow(label = "Родословная (Ancestry)", value = "+1", isBoost = true)
                    if (hasAncestryFlaw) BreakdownRow(label = "Штраф родословной", value = "-1", isFlaw = true)
                    if (hasBackgroundBoost) BreakdownRow(label = "Предыстория (Background)", value = "+1", isBoost = true)
                    if (isClassKey) BreakdownRow(label = "Ключевая характеристика класса", value = "+1", isBoost = true)
                    if (hasFreeBoost1) BreakdownRow(label = "Свободное улучшение 1-го уровня", value = "+1", isBoost = true)
                    levelBoostTiers.forEach { (tier, attrs) ->
                        if (attrs.contains(attribute)) {
                            BreakdownRow(label = "Улучшение $tier-го уровня", value = "+1", isBoost = true)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleBoost() }
                    .background(
                        color = if (isCustomBoosted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    )
                    .border(
                        width = 1.dp,
                        color = if (isCustomBoosted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tierLabel,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Checkbox(
                        checked = isCustomBoosted,
                        onCheckedChange = { onToggleBoost() }
                    )
                }
            }
        }
    }
}

@Composable
fun BreakdownRow(label: String, value: String, isBoost: Boolean = false, isFlaw: Boolean = false) {
    val color = when {
        isBoost -> MaterialTheme.colorScheme.primary
        isFlaw -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = color)
    }
}
