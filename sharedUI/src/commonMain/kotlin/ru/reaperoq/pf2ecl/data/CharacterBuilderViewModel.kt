package ru.reaperoq.pf2ecl.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.builtins.ListSerializer
import org.jetbrains.compose.resources.ExperimentalResourceApi
import pathfinder_2e_character_list.sharedui.generated.resources.Res
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings

class CharacterBuilderViewModel : ViewModel() {
    companion object {
        const val MIN_LEVEL = 1
        const val MAX_LEVEL = 20

        fun proficiencyBonus(level: Int): Int = level.coerceIn(MIN_LEVEL, MAX_LEVEL) + 2

        fun calculateMaxHp(state: CharacterState, conMod: Int): Int {
            val ancestryHp = state.ancestry?.system?.hp ?: 0
            val classHp = state.classData?.system?.hp ?: 0
            return ancestryHp + state.level * classHp + state.level * conMod
        }
    }

    private val settings = Settings()
    private val json = Json { ignoreUnknownKeys = true }

    private val _characterState = MutableStateFlow(CharacterState())
    val characterState: StateFlow<CharacterState> = _characterState.asStateFlow()

    private val _ancestries = MutableStateFlow<List<Ancestry>>(emptyList())
    val ancestries: StateFlow<List<Ancestry>> = _ancestries.asStateFlow()

    private val _backgrounds = MutableStateFlow<List<Background>>(emptyList())
    val backgrounds: StateFlow<List<Background>> = _backgrounds.asStateFlow()

    private val _classes = MutableStateFlow<List<ClassData>>(emptyList())
    val classes: StateFlow<List<ClassData>> = _classes.asStateFlow()

    private val _savedCharacters = MutableStateFlow<List<CharacterState>>(emptyList())
    val savedCharacters: StateFlow<List<CharacterState>> = _savedCharacters.asStateFlow()

    private val _ancestryFeats = MutableStateFlow<List<Feat>>(emptyList())
    val ancestryFeats: StateFlow<List<Feat>> = _ancestryFeats.asStateFlow()

    private val _classFeats = MutableStateFlow<List<Feat>>(emptyList())
    val classFeats: StateFlow<List<Feat>> = _classFeats.asStateFlow()

    private val _featsLoading = MutableStateFlow(false)
    val featsLoading: StateFlow<Boolean> = _featsLoading.asStateFlow()

    private val _heritagesLoading = MutableStateFlow(false)
    val heritagesLoading: StateFlow<Boolean> = _heritagesLoading.asStateFlow()

    private val _ancestryHeritages = MutableStateFlow<List<Heritage>>(emptyList())
    val ancestryHeritages: StateFlow<List<Heritage>> = _ancestryHeritages.asStateFlow()

    private val _versatileHeritages = MutableStateFlow<List<Heritage>>(emptyList())
    val versatileHeritages: StateFlow<List<Heritage>> = _versatileHeritages.asStateFlow()

    private val _cantripSpells = MutableStateFlow<List<Spell>>(emptyList())
    val cantripSpells: StateFlow<List<Spell>> = _cantripSpells.asStateFlow()

    private val _spellsByRank = MutableStateFlow<Map<Int, List<Spell>>>(emptyMap())
    val spellsByRank: StateFlow<Map<Int, List<Spell>>> = _spellsByRank.asStateFlow()

    private val _spellsLoading = MutableStateFlow(false)
    val spellsLoading: StateFlow<Boolean> = _spellsLoading.asStateFlow()

    init {
        loadData()
        loadSavedCharacters()
    }

    private fun loadSavedCharacters() {
        try {
            val serialized = settings.getString("characters_list", "")
            if (serialized.isNotEmpty()) {
                val list = json.decodeFromString(ListSerializer(CharacterState.serializer()), serialized)
                    .map { it.migrateLegacy() }
                _savedCharacters.value = list
            }
        } catch (e: Exception) {
            Logger.e(e) { "Error loading saved characters" }
        }
    }

    fun saveCurrentCharacter() {
        viewModelScope.launch {
            try {
                val current = _characterState.value.migrateLegacy()
                val list = _savedCharacters.value.toMutableList()

                val idx = list.indexOfFirst { it.name == current.name }
                if (idx >= 0) {
                    list[idx] = current
                } else {
                    list.add(current)
                }

                _savedCharacters.value = list
                val serialized = json.encodeToString(ListSerializer(CharacterState.serializer()), list)
                settings.putString("characters_list", serialized)
            } catch (e: Exception) {
                Logger.e(e) { "Error saving current character" }
            }
        }
    }

    fun deleteCharacter(character: CharacterState) {
        try {
            val list = _savedCharacters.value.filter { it.name != character.name }
            _savedCharacters.value = list
            val serialized = json.encodeToString(ListSerializer(CharacterState.serializer()), list)
            settings.putString("characters_list", serialized)
        } catch (e: Exception) {
            Logger.e(e) { "Error deleting character" }
        }
    }

    fun loadCharacter(character: CharacterState) {
        _characterState.value = character.migrateLegacy()
        character.ancestry?.let {
            loadHeritagesForAncestry(it)
            loadFeatsForAncestry(it)
        }
        character.classData?.let { loadFeatsForClass(it) }
        loadBuilderSpells()
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun loadData() {
        viewModelScope.launch {
            try {
                val ancestryIndexBytes = Res.readBytes("files/ancestries/index.json")
                val ancestryIndex = json.decodeFromString<IndexFile>(ancestryIndexBytes.decodeToString())
                val loadedAncestries = ancestryIndex.files.mapNotNull { entry ->
                    try {
                        entry.data?.let { json.decodeFromJsonElement<Ancestry>(it) }
                    } catch (e: Exception) {
                        Logger.e(e) { "Error decoding ancestry ${entry.fileName} from index" }
                        null
                    }
                }
                _ancestries.value = loadedAncestries

                val bgIndexBytes = Res.readBytes("files/backgrounds/index.json")
                val bgIndex = json.decodeFromString<IndexFile>(bgIndexBytes.decodeToString())
                val loadedBgs = bgIndex.files.filter { !it.fileName.startsWith("_") }.mapNotNull { entry ->
                    try {
                        entry.data?.let { json.decodeFromJsonElement<Background>(it) }
                    } catch (e: Exception) {
                        Logger.w(e) { "Error decoding background ${entry.fileName} from index" }
                        null
                    }
                }
                _backgrounds.value = loadedBgs

                val classIndexBytes = Res.readBytes("files/classes/index.json")
                val classIndex = json.decodeFromString<IndexFile>(classIndexBytes.decodeToString())
                val loadedClasses = classIndex.files.filter { !it.fileName.startsWith("_") }.mapNotNull { entry ->
                    try {
                        entry.data?.let { json.decodeFromJsonElement<ClassData>(it) }
                    } catch (e: Exception) {
                        Logger.w(e) { "Error decoding class ${entry.fileName} from index" }
                        null
                    }
                }
                _classes.value = loadedClasses

            } catch (e: Exception) {
                Logger.e(e) { "Error loading JSON data indexes" }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun loadHeritagesForAncestry(ancestry: Ancestry) {
        viewModelScope.launch {
            _heritagesLoading.value = true
            val slug = ancestrySlug(ancestry.name)
            _ancestryHeritages.value = loadHeritagesFromFolder("files/heritages/$slug")
            _versatileHeritages.value = loadHeritagesFromFolder("files/heritages/versatile-heritages")
            _heritagesLoading.value = false
        }
    }

    private fun ancestrySlug(name: String): String = name.lowercase().replace(" ", "-")

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadHeritagesFromFolder(resourcePath: String): List<Heritage> {
        return try {
            val indexBytes = Res.readBytes("$resourcePath/index.json")
            val index = json.decodeFromString<IndexFile>(indexBytes.decodeToJsonString())
            index.files
                .filter { it.fileName.endsWith(".json") && !it.fileName.startsWith("_") }
                .mapNotNull { entry ->
                    try {
                        entry.data?.let { json.decodeFromJsonElement<Heritage>(it) }
                    } catch (e: Exception) {
                        Logger.w(e) { "Error decoding heritage ${entry.fileName} from index at $resourcePath" }
                        null
                    }
                }
                .sortedBy { it.name }
        } catch (e: Exception) {
            Logger.w(e) { "No heritages at $resourcePath" }
            emptyList()
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun loadBuilderSpells() {
        viewModelScope.launch {
            _spellsLoading.value = true
            val level = _characterState.value.level
            val maxRank = CharacterProgression.maxSpellRank(level)
            _cantripSpells.value = loadSpellsFromFolder("files/spells/spells/cantrip")
            val byRank = mutableMapOf<Int, List<Spell>>()
            for (rank in 1..maxRank) {
                byRank[rank] = loadSpellsFromFolder("files/spells/spells/rank-$rank")
            }
            _spellsByRank.value = byRank
            _spellsLoading.value = false
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadSpellsFromFolder(resourcePath: String): List<Spell> {
        return try {
            val indexBytes = Res.readBytes("$resourcePath/index.json")
            val index = json.decodeFromString<IndexFile>(indexBytes.decodeToJsonString())
            index.files
                .filter { it.fileName.endsWith(".json") && !it.fileName.startsWith("_") }
                .mapNotNull { entry ->
                    try {
                        entry.data?.let { json.decodeFromJsonElement<Spell>(it) }
                    } catch (e: Exception) {
                        Logger.w(e) { "Error decoding spell ${entry.fileName} from index at $resourcePath" }
                        null
                    }
                }
                .sortedBy { it.name }
        } catch (e: Exception) {
            Logger.w(e) { "No spells at $resourcePath" }
            emptyList()
        }
    }

    fun toggleCantrip(spell: Spell) {
        toggleSpellSelection(spell, "Cantrip", isCantrip = true)
    }

    fun toggleRank1Spell(spell: Spell) {
        toggleSpellSelection(spell, "Rank 1", isCantrip = false)
    }

    fun toggleRankSpell(spell: Spell, rank: Int) {
        toggleSpellSelection(spell, "Rank $rank", isCantrip = false)
    }

    private fun toggleSpellSelection(spell: Spell, rankLabel: String, isCantrip: Boolean) {
        _characterState.update { state ->
            val ref = SpellRef(id = spell._id, name = spell.name, rankLabel = rankLabel)
            if (isCantrip) {
                val current = state.selectedCantrips.toMutableList()
                val idx = current.indexOfFirst { it.id == ref.id }
                if (idx >= 0) current.removeAt(idx) else current.add(ref)
                state.copy(selectedCantrips = current.sortedBy { it.name })
            } else {
                val current = state.selectedSpells.toMutableList()
                val idx = current.indexOfFirst { it.id == ref.id }
                if (idx >= 0) current.removeAt(idx) else current.add(ref)
                state.copy(selectedSpells = current.sortedBy { it.name })
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun loadFeatsForAncestry(ancestry: Ancestry) {
        viewModelScope.launch {
            _featsLoading.value = true
            val path = ancestry.name.lowercase().replace(" ", "-")
            val maxLevel = _characterState.value.level
            _ancestryFeats.value = loadFeatsUpToLevel("files/feats/ancestry/$path", maxLevel)
            _featsLoading.value = false
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun loadFeatsForClass(classData: ClassData) {
        viewModelScope.launch {
            _featsLoading.value = true
            val path = classData.name.lowercase().replace(" ", "-")
            val maxLevel = _characterState.value.level
            _classFeats.value = loadFeatsUpToLevel("files/feats/class/$path", maxLevel)
            _featsLoading.value = false
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadFeatsUpToLevel(basePath: String, maxLevel: Int): List<Feat> {
        val feats = mutableListOf<Feat>()
        for (lvl in MIN_LEVEL..maxLevel.coerceIn(MIN_LEVEL, MAX_LEVEL)) {
            try {
                val indexBytes = Res.readBytes("$basePath/level-$lvl/index.json")
                val index = json.decodeFromString<IndexFile>(indexBytes.decodeToString())
                index.files
                    .filter { !it.fileName.startsWith("_") && it.fileName.endsWith(".json") }
                    .forEach { entry ->
                        try {
                            entry.data?.let { feats.add(json.decodeFromJsonElement<Feat>(it)) }
                        } catch (e: Exception) {
                            Logger.w(e) { "Error decoding feat ${entry.fileName} from index at $basePath/level-$lvl" }
                        }
                    }
            } catch (_: Exception) {
                // No feats folder for this level
            }
        }
        return feats.sortedWith(compareBy({ it.system.level?.value ?: 1 }, { it.name }))
    }

    fun setAncestry(ancestry: Ancestry) {
        _characterState.update { state ->
            val fixedBoosts = mutableMapOf<String, Attribute>()
            ancestry.system.boosts?.forEach { (key, boost) ->
                if (boost.value.size == 1) {
                    Attribute.fromId(boost.value.first())?.let { attr ->
                        fixedBoosts[key] = attr
                    }
                }
            }
            state.copy(
                ancestry = ancestry,
                ancestryBoosts = fixedBoosts,
                heritage = null,
                freeBoosts = emptySet(),
                attributeBoosts = emptyMap(),
                selectedAncestryFeats = emptyList(),
                ancestryFeat = null
            )
        }
        loadHeritagesForAncestry(ancestry)
        loadFeatsForAncestry(ancestry)
    }

    fun setBackground(background: Background) {
        _characterState.update { state ->
            val fixedBoosts = mutableMapOf<String, Attribute>()
            background.system.boosts?.forEach { (key, boost) ->
                if (boost.value.size == 1) {
                    Attribute.fromId(boost.value.first())?.let { attr ->
                        fixedBoosts[key] = attr
                    }
                }
            }
            state.copy(
                background = background,
                backgroundBoosts = fixedBoosts,
                freeBoosts = emptySet(),
                attributeBoosts = emptyMap(),
                extraTrainedSkills = emptySet()
            )
        }
    }

    fun setClassData(classData: ClassData) {
        _characterState.update { state ->
            val keyBoost = classData.system.keyAbility?.value?.firstOrNull()?.let { Attribute.fromId(it) }
            state.copy(
                classData = classData,
                classBoost = keyBoost,
                freeBoosts = emptySet(),
                attributeBoosts = emptyMap(),
                extraTrainedSkills = emptySet(),
                selectedClassFeats = emptyList(),
                classFeat = null
            )
        }
        loadFeatsForClass(classData)
    }

    fun updateName(name: String) {
        _characterState.update { it.copy(name = name) }
    }

    fun setLevel(level: Int) {
        val clamped = level.coerceIn(MIN_LEVEL, MAX_LEVEL)
        _characterState.update { state -> trimStateForLevel(state, clamped) }
        _characterState.value.ancestry?.let { loadFeatsForAncestry(it) }
        _characterState.value.classData?.let { loadFeatsForClass(it) }
        loadBuilderSpells()
    }

    private fun trimStateForLevel(state: CharacterState, level: Int): CharacterState {
        val maxSpellRank = CharacterProgression.maxSpellRank(level)
        val validBoostTiers = CharacterProgression.ATTRIBUTE_BOOST_LEVELS.filter { it <= level && it != 1 }.toSet()
        return state.copy(
            level = level,
            selectedAncestryFeats = state.resolvedAncestryFeats()
                .take(CharacterProgression.ancestryFeatSlots(level)),
            selectedClassFeats = state.resolvedClassFeats()
                .take(CharacterProgression.classFeatSlots(level)),
            attributeBoosts = state.attributeBoosts.filterKeys { it in validBoostTiers },
            selectedSpells = state.selectedSpells.filter { spellRef ->
                val rank = spellRef.rankLabel.removePrefix("Rank ").toIntOrNull() ?: 1
                rank <= maxSpellRank
            },
            ancestryFeat = null,
            classFeat = null
        )
    }

    fun updateHeritage(heritage: Heritage) {
        _characterState.update { it.copy(heritage = heritage.name) }
    }

    fun setAncestryBoost(key: String, attribute: Attribute) {
        _characterState.update { state ->
            val newBoosts = state.ancestryBoosts.toMutableMap()
            newBoosts[key] = attribute
            state.copy(ancestryBoosts = newBoosts)
        }
    }

    fun setBackgroundBoost(key: String, attribute: Attribute) {
        _characterState.update { state ->
            val newBoosts = state.backgroundBoosts.toMutableMap()
            newBoosts[key] = attribute
            state.copy(backgroundBoosts = newBoosts)
        }
    }

    fun setClassBoost(attribute: Attribute) {
        _characterState.update { it.copy(classBoost = attribute) }
    }

    fun toggleFreeBoost(attribute: Attribute) {
        toggleAttributeBoostTier(1, attribute)
    }

    fun toggleAttributeBoostTier(tier: Int, attribute: Attribute) {
        _characterState.update { state ->
            if (tier == 1) {
                val newFree = state.freeBoosts.toMutableSet()
                if (newFree.contains(attribute)) {
                    newFree.remove(attribute)
                } else if (newFree.size < CharacterProgression.BOOSTS_PER_TIER) {
                    newFree.add(attribute)
                }
                state.copy(freeBoosts = newFree)
            } else {
                val current = state.attributeBoosts[tier].orEmpty().toMutableSet()
                if (current.contains(attribute)) {
                    current.remove(attribute)
                } else if (current.size < CharacterProgression.BOOSTS_PER_TIER) {
                    current.add(attribute)
                }
                val newMap = state.attributeBoosts.toMutableMap()
                if (current.isEmpty()) newMap.remove(tier) else newMap[tier] = current
                state.copy(attributeBoosts = newMap)
            }
        }
    }

    fun toggleWidget(widget: String) {
        _characterState.update { state ->
            val newWidgets = state.enabledWidgets.toMutableSet()
            if (newWidgets.contains(widget)) {
                newWidgets.remove(widget)
            } else {
                newWidgets.add(widget)
            }
            state.copy(enabledWidgets = newWidgets)
        }
    }

    fun toggleAncestryFeat(feat: Feat) {
        _characterState.update { state ->
            val current = state.resolvedAncestryFeats().toMutableList()
            val idx = current.indexOfFirst { it.sameFeatAs(feat) }
            if (idx >= 0) {
                current.removeAt(idx)
            } else {
                val max = CharacterProgression.ancestryFeatSlots(state.level)
                if (current.size < max) current.add(feat)
            }
            state.copy(selectedAncestryFeats = current, ancestryFeat = null)
        }
    }

    fun toggleClassFeat(feat: Feat) {
        _characterState.update { state ->
            val current = state.resolvedClassFeats().toMutableList()
            val idx = current.indexOfFirst { it.sameFeatAs(feat) }
            if (idx >= 0) {
                current.removeAt(idx)
            } else {
                val max = CharacterProgression.classFeatSlots(state.level)
                if (current.size < max) current.add(feat)
            }
            state.copy(selectedClassFeats = current, classFeat = null)
        }
    }

    private fun Feat.sameFeatAs(other: Feat): Boolean =
        (_id != null && other._id != null && _id == other._id) || name == other.name

    fun getAutoTrainedSkills(state: CharacterState = _characterState.value): Set<String> {
        val bgSkills = state.background?.system?.trainedSkills?.let {
            try { json.decodeFromJsonElement<BackgroundTrainedSkills>(it).value.map { s -> s.lowercase() }.toSet() }
            catch (e: Exception) { emptySet() }
        } ?: emptySet()
        val classSkills = state.classData?.system?.trainedSkills?.let {
            try { json.decodeFromJsonElement<ClassTrainedSkills>(it).value.map { s -> s.lowercase() }.toSet() }
            catch (e: Exception) { emptySet() }
        } ?: emptySet()
        return bgSkills + classSkills
    }

    fun calculateAttributes(state: CharacterState = _characterState.value): Map<Attribute, Int> {
        val base = Attribute.entries.associateWith { 10 }.toMutableMap()

        fun applyBoost(attribute: Attribute, amount: Int) {
            base[attribute] = (base[attribute] ?: 10) + amount
        }

        state.ancestry?.system?.flaws?.values?.forEach { flaw ->
            val attr = Attribute.fromId(flaw.value.firstOrNull() ?: "")
            if (attr != null) applyBoost(attr, -2)
        }

        state.ancestryBoosts.values.forEach { attr ->
            applyBoost(attr, 2)
        }

        state.backgroundBoosts.values.forEach { attr ->
            applyBoost(attr, 2)
        }

        state.classBoost?.let { attr ->
            applyBoost(attr, 2)
        }

        state.freeBoosts.forEach { attr ->
            applyBoost(attr, 2)
        }

        state.attributeBoosts.values.flatten().forEach { attr ->
            applyBoost(attr, 2)
        }

        return base
    }

    fun resetBackground() {
        _characterState.update { it.copy(background = null, backgroundBoosts = emptyMap(), extraTrainedSkills = emptySet()) }
    }

    fun resetClass() {
        _characterState.update { it.copy(classData = null, classBoost = null, extraTrainedSkills = emptySet(), selectedClassFeats = emptyList(), classFeat = null) }
        _classFeats.value = emptyList()
    }

    fun reset() {
        _characterState.value = CharacterState()
        _ancestryFeats.value = emptyList()
        _classFeats.value = emptyList()
        _ancestryHeritages.value = emptyList()
        _versatileHeritages.value = emptyList()
        _cantripSpells.value = emptyList()
        _spellsByRank.value = emptyMap()
    }
}
