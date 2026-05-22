package ru.reaperoq.pf2ecl.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.reaperoq.pf2ecl.data.Attribute
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.data.Translations

private val COLOR_AUTO_BG     = Color(0xFF0D2B1E)
private val COLOR_AUTO_BORDER = Color(0xFF2E7D5E)
private val COLOR_AUTO_TEXT   = Color(0xFF4DB87A)
private val COLOR_SEL_BG      = Color(0xFF2D0F0F)
private val COLOR_SEL_BORDER  = Color(0xFF912020)
private val COLOR_SEL_TEXT    = Color(0xFFE57373)

private val ALL_SKILLS = listOf(
    "acrobatics"   to Attribute.DEX,
    "arcana"       to Attribute.INT,
    "athletics"    to Attribute.STR,
    "crafting"     to Attribute.INT,
    "deception"    to Attribute.CHA,
    "diplomacy"    to Attribute.CHA,
    "intimidation" to Attribute.CHA,
    "medicine"     to Attribute.WIS,
    "nature"       to Attribute.WIS,
    "occultism"    to Attribute.INT,
    "performance"  to Attribute.CHA,
    "religion"     to Attribute.WIS,
    "society"      to Attribute.INT,
    "stealth"      to Attribute.DEX,
    "survival"     to Attribute.WIS,
    "thievery"     to Attribute.DEX
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(
    viewModel: CharacterBuilderViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val characterState by viewModel.characterState.collectAsState()
    val attributes  = viewModel.calculateAttributes()
    val autoTrained = viewModel.getAutoTrainedSkills()
    val extraCount  = viewModel.getAvailableExtraSkillsCount()
    val remaining   = maxOf(0, extraCount - characterState.extraTrainedSkills.size)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Навыки", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        if (extraCount > 0) {
                            Surface(shape = RoundedCornerShape(20.dp), color = if (remaining > 0) COLOR_SEL_BORDER else COLOR_AUTO_BORDER) {
                                Text(
                                    if (remaining > 0) "Выберите ещё $remaining" else "Готово ✓",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        SkillLegend(COLOR_AUTO_BORDER, "Авто")
                        SkillLegend(COLOR_SEL_BORDER, "Выбрано")
                        SkillLegend(MaterialTheme.colorScheme.outline, "Не обучен")
                    }
                    Button(onClick = onContinue, colors = ButtonDefaults.buttonColors(containerColor = COLOR_SEL_BORDER)) {
                        Text("Черты →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            // Количество колонок под ширину экрана
            val cols = when {
                maxWidth > 1000.dp -> 4
                maxWidth > 700.dp  -> 3
                else               -> 2
            }
            // Разбиваем навыки на строки
            val skillRows = ALL_SKILLS.chunked(cols)

            // Column + Row с weight(1f) — карточки заполняют весь экран точь-в-точь
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skillRows.forEach { rowSkills ->
                    Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSkills.forEach { (skillId, attr) ->
                            val isAuto    = skillId in autoTrained
                            val isExtra   = skillId in characterState.extraTrainedSkills
                            val isTrained = isAuto || isExtra
                            val attrVal   = attributes[attr] ?: 10
                            val attrMod   = (attrVal - 10) / 2
                            val bonus     = attrMod + if (isTrained) 3 else 0
                            val bonusTxt  = if (bonus >= 0) "+$bonus" else "$bonus"
                            val canToggle = !isAuto && (isExtra || remaining > 0)

                            SkillCard(
                                modifier  = Modifier.weight(1f).fillMaxHeight(),
                                skillId   = skillId,
                                attr      = attr,
                                bonus     = bonusTxt,
                                isAuto    = isAuto,
                                isExtra   = isExtra,
                                isTrained = isTrained,
                                canToggle = canToggle,
                                onClick   = { if (!isAuto) viewModel.toggleExtraSkill(skillId) }
                            )
                        }
                        // Заполнитель для неполной последней строки
                        repeat(cols - rowSkills.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SkillCard(
    modifier: Modifier,
    skillId: String,
    attr: Attribute,
    bonus: String,
    isAuto: Boolean,
    isExtra: Boolean,
    isTrained: Boolean,
    canToggle: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isAuto  -> COLOR_AUTO_BG
        isExtra -> COLOR_SEL_BG
        else    -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        isAuto    -> COLOR_AUTO_BORDER
        isExtra   -> COLOR_SEL_BORDER
        canToggle -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        else      -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    }
    val nameColor = when {
        isAuto    -> COLOR_AUTO_TEXT
        isExtra   -> COLOR_SEL_TEXT
        canToggle -> MaterialTheme.colorScheme.onSurface
        else      -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }
    val attrColor = when {
        isAuto  -> COLOR_AUTO_TEXT.copy(alpha = 0.6f)
        isExtra -> COLOR_SEL_TEXT.copy(alpha = 0.6f)
        else    -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (canToggle) 0.7f else 0.3f)
    }
    val bonusColor = when {
        isAuto  -> COLOR_AUTO_TEXT
        isExtra -> COLOR_SEL_TEXT
        else    -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (canToggle) 0.9f else 0.25f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(10.dp))
            .clickable(enabled = !isAuto && (isExtra || canToggle)) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // State icon top-right
        Box(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)) {
            when {
                isAuto  -> Icon(Icons.Rounded.Lock,  null, Modifier.size(14.dp), tint = COLOR_AUTO_BORDER)
                isExtra -> Icon(Icons.Rounded.Check, null, Modifier.size(14.dp), tint = COLOR_SEL_BORDER)
            }
        }

        // Centre content
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Bonus badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        when {
                            isAuto  -> COLOR_AUTO_BORDER.copy(alpha = 0.2f)
                            isExtra -> COLOR_SEL_BORDER.copy(alpha = 0.2f)
                            else    -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    bonus,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = bonusColor
                )
            }

            Text(
                Translations.translateSkill(skillId),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isTrained) FontWeight.Bold else FontWeight.Normal,
                    color = nameColor
                ),
                textAlign = TextAlign.Center
            )

            Text(
                Translations.translateAttribute(attr),
                fontSize = 12.sp,
                color = attrColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
