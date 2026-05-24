package ru.reaperoq.pf2ecl.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.data.CharacterProgression
import ru.reaperoq.pf2ecl.data.Feat
import ru.reaperoq.pf2ecl.data.resolvedAncestryFeats
import ru.reaperoq.pf2ecl.data.resolvedClassFeats


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
fun FeatsScreen(
    viewModel: CharacterBuilderViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val characterState by viewModel.characterState.collectAsState()
    val ancestryFeats  by viewModel.ancestryFeats.collectAsState()
    val classFeats     by viewModel.classFeats.collectAsState()
    val isLoading      by viewModel.featsLoading.collectAsState()

    // feat + kind ("ancestry" / "class") — открыт в панели деталей
    var previewed by remember { mutableStateOf<Pair<Feat, String>?>(null) }

    val selectedAncestry = characterState.resolvedAncestryFeats()
    val selectedClass = characterState.resolvedClassFeats()
    val ancestrySlots = CharacterProgression.ancestryFeatSlots(characterState.level)
    val classSlots = CharacterProgression.classFeatSlots(characterState.level)
    val selectedCount = selectedAncestry.size + selectedClass.size
    val totalSlots = ancestrySlots + classSlots
    val levelLabel = "до ур. ${characterState.level}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Черты", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        if (totalSlots > 0) {
                            Surface(shape = RoundedCornerShape(20.dp), color = if (selectedCount == totalSlots) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary) {
                                Text("$selectedCount/$totalSlots",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White)
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
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), Arrangement.End) {
                    Button(onClick = onContinue, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                        Text("Атрибуты →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary) }
            return@Scaffold
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            val isWide = maxWidth > 900.dp

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Сетка черт родословной
                    FeatGridSection(
                        modifier   = Modifier.weight(1f).fillMaxHeight(),
                        title      = "Черты родословной",
                        subtitle   = characterState.ancestry?.name?.let { "Черты $it · $levelLabel · ${selectedAncestry.size}/$ancestrySlots" } ?: "Выберите родословную",
                        feats      = ancestryFeats,
                        selected   = selectedAncestry,
                        previewed  = (previewed?.takeIf { it.second == "ancestry" })?.first,
                        onPreview  = { feat -> previewed = feat to "ancestry" }
                    )
                    // Вертикальный разделитель
                    HorizontalDivider(
                        modifier = Modifier.fillMaxHeight().width(1.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    // Сетка черт класса
                    FeatGridSection(
                        modifier   = Modifier.weight(1f).fillMaxHeight(),
                        title      = "Черты класса",
                        subtitle   = characterState.classData?.name?.let { "Черты $it · $levelLabel · ${selectedClass.size}/$classSlots" } ?: "Выберите класс",
                        feats      = classFeats,
                        selected   = selectedClass,
                        previewed  = (previewed?.takeIf { it.second == "class" })?.first,
                        onPreview  = { feat -> previewed = feat to "class" }
                    )
                    // Разделитель
                    HorizontalDivider(
                        modifier = Modifier.fillMaxHeight().width(1.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    // Панель деталей справа
                    FeatDetailPanel(
                        modifier       = Modifier.width(300.dp).fillMaxHeight(),
                        previewed      = previewed,
                        ancestrySelected = selectedAncestry,
                        classSelected  = selectedClass,
                        onToggle       = { feat, kind ->
                            if (kind == "ancestry") viewModel.toggleAncestryFeat(feat)
                            else viewModel.toggleClassFeat(feat)
                        }
                    )
                }
            } else {
                // Узкий экран — простой список с раскрывашками
                NarrowFeatsList(
                    modifier       = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    ancestryFeats  = ancestryFeats,
                    classFeats     = classFeats,
                    ancestrySelected = selectedAncestry,
                    classSelected  = selectedClass,
                    onToggleAncestry = { viewModel.toggleAncestryFeat(it) },
                    onToggleClass    = { viewModel.toggleClassFeat(it) }
                )
            }
        }
    }
}

// Сетка карточек черт (один раздел — родословная или класс)
@Composable
private fun FeatGridSection(
    modifier: Modifier,
    title: String,
    subtitle: String,
    feats: List<Feat>,
    selected: List<Feat>,
    previewed: Feat?,
    onPreview: (Feat) -> Unit
) {
    fun isFeatSelected(feat: Feat) = selected.any { s ->
        (s._id != null && feat._id != null && s._id == feat._id) || s.name == feat.name
    }
    Column(modifier = modifier) {
        // Заголовок раздела
        Column(Modifier.padding(bottom = 10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (feats.isEmpty()) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)).padding(20.dp),
                contentAlignment = Alignment.Center) {
                Text("Нет данных", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return
        }

        // 2 колонки внутри каждого раздела, строки через weight(1f) заполняют всё
        val rows = feats.chunked(2)
        Column(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { rowFeats ->
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowFeats.forEach { feat ->
                        FeatGridCard(
                            modifier   = Modifier.weight(1f).fillMaxHeight(),
                            feat       = feat,
                            isSelected = isFeatSelected(feat),
                            isPreviewed = previewed?.name == feat.name,
                            featLevel = feat.system.level?.value ?: 1,
                            onClick    = { onPreview(feat) }
                        )
                    }
                    if (rowFeats.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// Компактная карточка черты в сетке
@Composable
private fun FeatGridCard(
    modifier: Modifier,
    feat: Feat,
    isSelected: Boolean,
    isPreviewed: Boolean,
    featLevel: Int,
    onClick: () -> Unit
) {
    val traits = feat.system.traits?.value ?: emptyList()
    val cleanDesc = remember(feat.name) { cleanPf2eText(feat.system.description.value) }

    val bg = when {
        isSelected  -> MaterialTheme.colorScheme.secondaryContainer
        isPreviewed -> MaterialTheme.colorScheme.surfaceVariant
        else        -> MaterialTheme.colorScheme.surface
    }
    val border = when {
        isSelected  -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
        isPreviewed -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        else        -> BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(border, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Левая полоса для выбранной
        if (isSelected) {
            Box(Modifier.width(3.dp).fillMaxHeight().background(MaterialTheme.colorScheme.secondary).align(Alignment.CenterStart))
        }

        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Трейты и галочка
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (isSelected) {
                    Icon(Icons.Rounded.Check, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                traits.take(2).forEach { FeatTraitPill(it) }
            }

            Spacer(Modifier.height(6.dp))

            // Название
            Text(
                feat.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                "Ур. $featLevel",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(4.dp))

            // Краткое описание
            Text(
                cleanDesc.take(80).replace("\n", " ") + if (cleanDesc.length > 80) "…" else "",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Панель деталей справа — показывает полное описание выбранной/просматриваемой черты
@Composable
private fun FeatDetailPanel(
    modifier: Modifier,
    previewed: Pair<Feat, String>?,
    ancestrySelected: List<Feat>,
    classSelected: List<Feat>,
    onToggle: (Feat, String) -> Unit
) {
    fun isSelected(feat: Feat, kind: String): Boolean {
        val list = if (kind == "ancestry") ancestrySelected else classSelected
        return list.any { (it._id != null && feat._id != null && it._id == feat._id) || it.name == feat.name }
    }
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (previewed == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(
                    "Нажмите на черту\nдля просмотра",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val (feat, kind) = previewed
            val selected = isSelected(feat, kind)
            val traits    = feat.system.traits?.value ?: emptyList()
            val prereqs   = feat.system.prerequisites?.value?.filter { it.value.isNotBlank() } ?: emptyList()
            val cleanDesc = remember(feat.name) { cleanPf2eText(feat.system.description.value) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Скроллируемое описание
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Трейты
                    if (traits.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            traits.forEach { FeatTraitPill(it) }
                        }
                    }

                    // Название
                    Text(
                        feat.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Тип черты
                    Text(
                        when (kind) {
                            "ancestry" -> "Черта родословной · Ур. ${feat.system.level?.value ?: 1}"
                            else -> "Черта класса · Ур. ${feat.system.level?.value ?: 1}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (prereqs.isNotEmpty()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                        Text(
                            "Требования: ${prereqs.joinToString("; ") { it.value }}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))

                    // Описание
                    Text(
                        cleanDesc,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Кнопка выбора
                Button(
                    onClick = { onToggle(feat, kind) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (selected)
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(0.3f), contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    else
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    if (selected) Icon(Icons.Rounded.Check, null, Modifier.size(16.dp))
                    if (selected) Spacer(Modifier.width(6.dp))
                    Text(
                        if (selected) "Снять выбор" else "Выбрать эту черту",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// Узкий экран — простой аккордеон
@Composable
private fun NarrowFeatsList(
    modifier: Modifier,
    ancestryFeats: List<Feat>,
    classFeats: List<Feat>,
    ancestrySelected: List<Feat>,
    classSelected: List<Feat>,
    onToggleAncestry: (Feat) -> Unit,
    onToggleClass: (Feat) -> Unit
) {
    fun isAncestrySelected(feat: Feat) = ancestrySelected.any { (it._id != null && feat._id != null && it._id == feat._id) || it.name == feat.name }
    fun isClassSelected(feat: Feat) = classSelected.any { (it._id != null && feat._id != null && it._id == feat._id) || it.name == feat.name }
    var expandedId by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(0.dp)) {
        NarrowSectionHeader("Черта родословной")
        ancestryFeats.forEach { feat ->
            val key = (feat._id ?: feat.name) + "_a"
            NarrowFeatRow(feat, isAncestrySelected(feat), expandedId == key,
                onToggle = { expandedId = if (expandedId == key) null else key },
                onSelect = { onToggleAncestry(feat) })
        }
        Spacer(Modifier.height(16.dp))
        NarrowSectionHeader("Черта класса")
        classFeats.forEach { feat ->
            val key = (feat._id ?: feat.name) + "_c"
            NarrowFeatRow(feat, isClassSelected(feat), expandedId == key,
                onToggle = { expandedId = if (expandedId == key) null else key },
                onSelect = { onToggleClass(feat) })
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun NarrowSectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary),
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
}

@Composable
private fun NarrowFeatRow(feat: Feat, isSelected: Boolean, isExpanded: Boolean, onToggle: () -> Unit, onSelect: () -> Unit) {
    val traits    = feat.system.traits?.value ?: emptyList()
    val cleanDesc = remember(feat.name) { cleanPf2eText(feat.system.description.value) }
    val prereqs   = feat.system.prerequisites?.value?.filter { it.value.isNotBlank() } ?: emptyList()

    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
            .border(BorderStroke(if (isSelected) 1.5.dp else 0.5.dp,
                if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(0.2f)), RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) { Icon(Icons.Rounded.Check, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer); Spacer(Modifier.width(4.dp)) }
            Text(feat.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface), modifier = Modifier.weight(1f))
            traits.take(2).forEach { FeatTraitPill(it) }
        }
        if (isExpanded) {
            Spacer(Modifier.height(8.dp))
            if (prereqs.isNotEmpty()) Text("Требования: ${prereqs.joinToString("; ") { it.value }}",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
            Text(cleanDesc.take(500) + if (cleanDesc.length > 500) "…" else "",
                fontSize = 13.sp, lineHeight = 19.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onSelect, modifier = Modifier.fillMaxWidth(),
                colors = if (isSelected) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary.copy(0.3f), MaterialTheme.colorScheme.onSecondaryContainer)
                else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary, Color.White),
                shape = RoundedCornerShape(6.dp)) {
                Text(if (isSelected) "Выбрано ✓" else "Выбрать", fontWeight = FontWeight.Bold)
            }
        } else {
            Text(cleanDesc.take(80).replace("\n", " ") + "…",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun FeatTraitPill(trait: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(0.15f))
            .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.secondary.copy(0.5f)), RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(trait.replaceFirstChar { it.uppercase() }, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSecondaryContainer, maxLines = 1)
    }
}
