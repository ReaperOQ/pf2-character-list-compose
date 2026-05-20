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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pathfinder_2e_character_list.sharedui.generated.resources.Res
import pathfinder_2e_character_list.sharedui.generated.resources.allDrawableResources
import ru.reaperoq.pf2ecl.data.Attribute
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.data.ClassData
import ru.reaperoq.pf2ecl.data.Translations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassScreen(
    viewModel: CharacterBuilderViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val classes by viewModel.classes.collectAsState()
    val characterState by viewModel.characterState.collectAsState()
    val selectedClass = characterState.classData
    var searchQuery by remember { mutableStateOf("") }

    val filteredClasses = remember(searchQuery, classes) {
        if (searchQuery.isBlank()) classes
        else classes.filter { cls ->
            val ruName = Translations.translateClass(cls.name)
            cls.name.contains(searchQuery, ignoreCase = true) || ruName.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор Класса", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                            text = "Выберите класс",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Класс определяет ваш стиль боя, ключевую характеристику, хиты, спасброски и классовые способности.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Поиск классов...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(220.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredClasses) { classData ->
                                ClassGridCard(
                                    classData = classData,
                                    isSelected = selectedClass?._id == classData._id,
                                    onClick = { viewModel.setClassData(classData) }
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
                        if (selectedClass != null) {
                            ClassDetailPanel(
                                classData = selectedClass,
                                characterState = characterState,
                                viewModel = viewModel,
                                onContinue = onContinue
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Выберите класс слева для просмотра деталей",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                if (selectedClass == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Выберите класс",
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

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(150.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredClasses) { classData ->
                                ClassGridCard(
                                    classData = classData,
                                    isSelected = false,
                                    onClick = { viewModel.setClassData(classData) }
                                )
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = { Text(Translations.translateClass(selectedClass.name)) },
                            navigationIcon = {
                                IconButton(onClick = { viewModel.resetClass() }) {
                                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад к списку")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            ClassDetailPanel(
                                classData = selectedClass,
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
fun ClassGridCard(
    modifier: Modifier = Modifier,
    classData: ClassData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val translatedName = Translations.translateClass(classData.name)
    val classKey = classData.name.lowercase()
    val resourceId = Res.allDrawableResources["class_$classKey"]

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
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
                    contentDescription = classData.name,
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
                        Icons.Rounded.Person,
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
            if (translatedName != classData.name) {
                Text(
                    text = classData.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassDetailPanel(
    classData: ClassData,
    characterState: ru.reaperoq.pf2ecl.data.CharacterState,
    viewModel: CharacterBuilderViewModel,
    onContinue: () -> Unit
) {
    val scrollState = rememberScrollState()
    val classKey = classData.name.lowercase()
    val resourceId = Res.allDrawableResources["class_$classKey"]

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
            if (resourceId != null) {
                Image(
                    painter = painterResource(resourceId),
                    contentDescription = classData.name,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = Translations.translateClass(classData.name),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Класс • Ремастер Pathfinder 2e",
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
            StatPill(label = "Хиты (HP/ур)", value = "${classData.system.hp}")
            StatPill(label = "Восприятие", value = "Ранг ${classData.system.perception}")
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ключевая характеристика", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                text = "Ключевая характеристика добавляет +1 к модификатору параметра и определяет точность ваших атак или заклинаний.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            classData.system.keyAbility?.let { keyAbility ->
                val availableAttrs = keyAbility.value.mapNotNull { Attribute.fromId(it) }
                val isChoice = availableAttrs.size > 1

                if (!isChoice) {
                    val attr = availableAttrs.first()
                    Text(
                        text = "• ${Translations.translateAttribute(attr)} (+1) [Фиксированная]",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val selectedAttr = characterState.classBoost
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Выберите ключевую характеристику:",
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
                                    onClick = { viewModel.setClassBoost(attr) },
                                    label = { Text(Translations.translateAttribute(attr)) }
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Спасброски (Saving Throws)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val saves = classData.system.savingThrows
                SavePill(label = "Стойкость", value = saves.fortitude)
                SavePill(label = "Реакция", value = saves.reflex)
                SavePill(label = "Воля", value = saves.will)
            }
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Боевая подготовка", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            
            val weaponText = listOfNotNull(
                "Простое оружие: Ранг ${classData.system.attacks.simple}",
                "Воинское оружие: Ранг ${classData.system.attacks.martial}",
                classData.system.attacks.advanced.let { "Особое оружие: Ранг $it" },
                "Без оружия: Ранг ${classData.system.attacks.unarmed}"
            ).joinToString("\n")

            val defense = classData.system.defenses
            val armorText = listOfNotNull(
                "Без доспехов: Ранг ${defense.unarmored}",
                "Легкие доспехи: Ранг ${defense.light}",
                "Средние доспехи: Ранг ${defense.medium}",
                "Тяжелые доспехи: Ранг ${defense.heavy}"
            ).joinToString("\n")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Оружие:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text(weaponText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Доспехи:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text(armorText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val keyAbilitySelected = characterState.classBoost != null

        Button(
            onClick = onContinue,
            enabled = keyAbilitySelected,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Подтвердить и продолжить")
        }
    }
}

@Composable
fun SavePill(label: String, value: Int) {
    val rankText = when (value) {
        0 -> "Н"
        1 -> "Т"
        2 -> "Э"
        3 -> "М"
        4 -> "Л"
        else -> value.toString()
    }
    val rankColor = when (value) {
        0 -> MaterialTheme.colorScheme.outline
        1 -> MaterialTheme.colorScheme.primary
        2 -> Color(0xFF1E88E5)
        3 -> Color(0xFF8E24AA)
        4 -> Color(0xFFE53935)
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.width(90.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(rankColor, shape = MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    rankText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}
