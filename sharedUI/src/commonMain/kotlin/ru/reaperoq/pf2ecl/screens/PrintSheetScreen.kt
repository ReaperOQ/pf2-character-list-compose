package ru.reaperoq.pf2ecl.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isFinite
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.reaperoq.pf2ecl.data.Attribute
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.data.Translations
import ru.reaperoq.pf2ecl.data.exportBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintSheetScreen(
    viewModel: CharacterBuilderViewModel,
    onBack: () -> Unit
) {
    val characterState by viewModel.characterState.collectAsState()
    val attributes = viewModel.calculateAttributes()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val graphicsLayer = rememberGraphicsLayer()

    val a4Width = 794.dp
    val a4Height = 1123.dp

    val conMod = attributes[Attribute.CON] ?: 0
    val dexMod = attributes[Attribute.DEX] ?: 0
    val level = 1
    val profTrained = 2 + level

    val baseHp = (characterState.ancestry?.system?.hp ?: 0) + (characterState.classData?.system?.hp ?: 0) + conMod

    val fortSave = conMod + ((characterState.classData?.system?.savingThrows?.fortitude ?: 0) * profTrained)
    val refSave = dexMod + ((characterState.classData?.system?.savingThrows?.reflex ?: 0) * profTrained)
    val willSave = (attributes[Attribute.WIS] ?: 0) + ((characterState.classData?.system?.savingThrows?.will ?: 0) * profTrained)

    val acValue = 10 + dexMod + profTrained

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Печать и Экспорт", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
            val isWideScreen = maxWidth > 850.dp

            if (isWideScreen) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "Настройки Листа",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )

                        OutlinedTextField(
                            value = characterState.name,
                            onValueChange = { viewModel.updateName(it) },
                            label = { Text("Имя персонажа") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        HorizontalDivider()

                        Text("Выбор виджетов на листе:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                        WidgetToggleRow(
                            label = "Характеристики",
                            checked = characterState.enabledWidgets.contains("attributes"),
                            onCheckedChange = { viewModel.toggleWidget("attributes") }
                        )

                        WidgetToggleRow(
                            label = "Спасброски и Защита",
                            checked = characterState.enabledWidgets.contains("defenses"),
                            onCheckedChange = { viewModel.toggleWidget("defenses") }
                        )

                        WidgetToggleRow(
                            label = "Навыки",
                            checked = characterState.enabledWidgets.contains("skills"),
                            onCheckedChange = { viewModel.toggleWidget("skills") }
                        )

                        WidgetToggleRow(
                            label = "Атаки (Удары)",
                            checked = characterState.enabledWidgets.contains("strikes"),
                            onCheckedChange = { viewModel.toggleWidget("strikes") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    val name = characterState.name.ifBlank { "hero" }.replace(" ", "_")
                                    val bitmap = graphicsLayer.toImageBitmap()
                                    withContext(Dispatchers.Default) {
                                        exportBitmap(bitmap, name)
                                    }
                                    snackbarHostState.showSnackbar("Экспорт завершен!")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Rounded.Print, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Печать / Экспорт в PNG")
                        }
                    }

                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1.8f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val previewScale = remember(maxWidth, maxHeight) {
                            val heightScale = if (maxHeight.isFinite) maxHeight / a4Height else 1f
                            minOf(maxWidth / a4Width, heightScale).coerceAtMost(1f)
                        }

                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = previewScale
                                    scaleY = previewScale
                                }
                        ) {
                            Surface(
                                modifier = Modifier.size(a4Width, a4Height),
                                color = Color.White,
                                shadowElevation = 8.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    A4SheetContent(
                                        characterState = characterState,
                                        attributes = attributes,
                                        baseHp = baseHp,
                                        acValue = acValue,
                                        fortSave = fortSave,
                                        refSave = refSave,
                                        willSave = willSave,
                                        profTrained = profTrained
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = characterState.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Имя персонажа") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                val name = characterState.name.ifBlank { "hero" }.replace(" ", "_")
                                val bitmap = graphicsLayer.toImageBitmap()
                                withContext(Dispatchers.Default) {
                                    exportBitmap(bitmap, name)
                                }
                                snackbarHostState.showSnackbar("Экспорт завершен!")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Печать / Экспорт в PDF")
                    }

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val previewScale = remember(maxWidth, maxHeight) {
                            val heightScale = if (maxHeight.isFinite) maxHeight / a4Height else 1f
                            minOf(maxWidth / a4Width, heightScale).coerceAtMost(1f)
                        }

                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = previewScale
                                    scaleY = previewScale
                                }
                        ) {
                            Surface(
                                modifier = Modifier.size(a4Width, a4Height),
                                color = Color.White
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp)
                                ) {
                                    A4SheetContent(
                                        characterState = characterState,
                                        attributes = attributes,
                                        baseHp = baseHp,
                                        acValue = acValue,
                                        fortSave = fortSave,
                                        refSave = refSave,
                                        willSave = willSave,
                                        profTrained = profTrained
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(a4Width, a4Height)
                    .zIndex(-1f)
                    .graphicsLayer { alpha = 0f }
                    .drawWithContent {
                        graphicsLayer.record { this@drawWithContent.drawContent() }
                        drawContent()
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    A4SheetContent(
                        characterState = characterState,
                        attributes = attributes,
                        baseHp = baseHp,
                        acValue = acValue,
                        fortSave = fortSave,
                        refSave = refSave,
                        willSave = willSave,
                        profTrained = profTrained
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun A4SheetContent(
    characterState: ru.reaperoq.pf2ecl.data.CharacterState,
    attributes: Map<Attribute, Int>,
    baseHp: Int,
    acValue: Int,
    fortSave: Int,
    refSave: Int,
    willSave: Int,
    profTrained: Int
) {
    val classLabel = characterState.classData?.name?.let { Translations.translateClass(it) } ?: "Без класса"
    val ancestryLabel = characterState.ancestry?.name?.let { Translations.translateAncestry(it) } ?: "Без родословной"
    val bgLabel = characterState.background?.name?.let { Translations.translateBackground(it) } ?: "Без предыстории"
    val strMod = attributes[Attribute.STR] ?: 0
    val dexMod = attributes[Attribute.DEX] ?: 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF8B0000), RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Text(
                text = characterState.name.ifBlank { "НОВЫЙ ГЕРОЙ" }.uppercase(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Класс: $classLabel", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                Text("Родословная: $ancestryLabel", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                Text("Предыстория: $bgLabel", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                Text("Уровень: 1", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
            if (characterState.heritage != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text("Наследие: ${characterState.heritage}", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
        }

        if (characterState.enabledWidgets.contains("attributes")) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Attribute.entries.forEach { attr ->
                        val modifierValue = attributes[attr] ?: 0
                        val modifierText = if (modifierValue >= 0) "+$modifierValue" else "$modifierValue"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = Translations.translateAttribute(attr).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color.White,
                                border = BorderStroke(1.dp, Color.Black),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = modifierText,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (characterState.enabledWidgets.contains("defenses")) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("КЛАСС ДОСПЕХА (AC)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$acValue", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("10 + DEX + Обучен", fontSize = 9.sp, color = Color.Gray)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("МАКС. ХИТЫ (HP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$baseHp", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Род. + Класс + CON", fontSize = 9.sp, color = Color.Gray)
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SaveValueBox(name = "Стойкость (FORT)", value = fortSave, formula = "CON + Проф.")
                    SaveValueBox(name = "Реакция (REF)", value = refSave, formula = "DEX + Проф.")
                    SaveValueBox(name = "Воля (WILL)", value = willSave, formula = "WIS + Проф.")
                }
            }
        }

        if (characterState.enabledWidgets.contains("strikes")) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ОРУЖЕЙНЫЕ АТАКИ (УДАРЫ)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    StrikeRow(name = "Ближний бой (Кулак / Оружие)", modifier = strMod + profTrained, damage = Attribute.STR.ruLabel)
                    StrikeRow(name = "Дальний бой (Метательное / Лук)", modifier = dexMod + profTrained, damage = "Свободно")
                }
            }
        }

        if (characterState.enabledWidgets.contains("skills")) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("НАВЫКИ ПЕРСОНАЖА (SKILLS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val skillList = listOf(
                        "Acrobatics" to Attribute.DEX,
                        "Arcana" to Attribute.INT,
                        "Athletics" to Attribute.STR,
                        "Crafting" to Attribute.INT,
                        "Deception" to Attribute.CHA,
                        "Diplomacy" to Attribute.CHA,
                        "Intimidation" to Attribute.CHA,
                        "Medicine" to Attribute.WIS,
                        "Nature" to Attribute.WIS,
                        "Occultism" to Attribute.INT,
                        "Performance" to Attribute.CHA,
                        "Religion" to Attribute.WIS,
                        "Society" to Attribute.INT,
                        "Stealth" to Attribute.DEX,
                        "Survival" to Attribute.WIS,
                        "Thievery" to Attribute.DEX
                    )

                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(8),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(skillList) { (skillId, attr) ->
                            val attrVal = attributes[attr] ?: 0
                            val isTrained = true
                            val finalSkillVal = attrVal + (if (isTrained) profTrained else 0)
                            val finalSign = if (finalSkillVal >= 0) "+$finalSkillVal" else "$finalSkillVal"

                            Row(
                                modifier = Modifier.width(200.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${Translations.translateSkill(skillId)} (${Translations.translateAttribute(attr)})",
                                    fontSize = 11.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = finalSign,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SaveValueBox(name: String, value: Int, formula: String) {
    val valSign = if (value >= 0) "+$value" else "$value"
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(name.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = Color.White,
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(valSign, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(formula, fontSize = 8.sp, color = Color.Gray)
    }
}

@Composable
fun StrikeRow(name: String, modifier: Int, damage: String) {
    val modSign = if (modifier >= 0) "+$modifier" else "$modifier"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Атака: $modSign", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Урон: 1d6 + $damage", fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}
