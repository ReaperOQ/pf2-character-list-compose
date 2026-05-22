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

    init {
        loadData()
        loadSavedCharacters()
    }

    private fun loadSavedCharacters() {
        try {
            val serialized = settings.getString("characters_list", "")
            if (serialized.isNotEmpty()) {
                val list = json.decodeFromString(ListSerializer(CharacterState.serializer()), serialized)
                _savedCharacters.value = list
            }
        } catch (e: Exception) {
            Logger.e(e) { "Error loading saved characters" }
        }
    }

    fun saveCurrentCharacter() {
        viewModelScope.launch {
            try {
                val current = _characterState.value
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
        _characterState.value = character
        character.ancestry?.let { loadFeatsForAncestry(it) }
        character.classData?.let { loadFeatsForClass(it) }
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun loadData() {
        viewModelScope.launch {
            try {
                val ancestryIndexBytes = Res.readBytes("files/ancestries/index.json")
                val ancestryIndex = json.decodeFromString<IndexFile>(ancestryIndexBytes.decodeToString())
                val loadedAncestries = ancestryIndex.files.mapNotNull { entry ->
                    try {
                        val bytes = Res.readBytes("files/ancestries/${entry.fileName}")
                        json.decodeFromString<Ancestry>(bytes.decodeToString())
                    } catch (e: Exception) {
                        Logger.e(e) { "Error loading ancestry ${entry.fileName}" }
                        null
                    }
                }
                _ancestries.value = loadedAncestries

                val bgIndexBytes = Res.readBytes("files/backgrounds/index.json")
                val bgIndex = json.decodeFromString<IndexFile>(bgIndexBytes.decodeToString())
                val loadedBgs = bgIndex.files.filter { !it.fileName.startsWith("_") }.mapNotNull { entry ->
                    try {
                        val bytes = Res.readBytes("files/backgrounds/${entry.fileName}")
                        json.decodeFromString<Background>(bytes.decodeToString())
                    } catch (e: Exception) {
                        Logger.w(e) { "Error loading background ${entry.fileName}" }
                        null
                    }
                }
                _backgrounds.value = loadedBgs

                val classIndexBytes = Res.readBytes("files/classes/index.json")
                val classIndex = json.decodeFromString<IndexFile>(classIndexBytes.decodeToString())
                val loadedClasses = classIndex.files.filter { !it.fileName.startsWith("_") }.mapNotNull { entry ->
                    try {
                        val bytes = Res.readBytes("files/classes/${entry.fileName}")
                        json.decodeFromString<ClassData>(bytes.decodeToString())
                    } catch (e: Exception) {
                        Logger.w(e) { "Error loading class ${entry.fileName}" }
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
    fun loadFeatsForAncestry(ancestry: Ancestry) {
        viewModelScope.launch {
            _featsLoading.value = true
            val path = ancestry.name.lowercase().replace(" ", "-")
            try {
                val indexBytes = Res.readBytes("files/feats/ancestry/$path/level-1/index.json")
                val index = json.decodeFromString<IndexFile>(indexBytes.decodeToString())
                val feats = index.files.filter { !it.fileName.startsWith("_") && it.fileName.endsWith(".json") }.mapNotNull { entry ->
                    try {
                        val bytes = Res.readBytes("files/feats/ancestry/$path/level-1/${entry.fileName}")
                        json.decodeFromString<Feat>(bytes.decodeToString())
                    } catch (e: Exception) {
                        Logger.w(e) { "Error loading ancestry feat ${entry.fileName}" }
                        null
                    }
                }
                _ancestryFeats.value = feats
            } catch (e: Exception) {
                Logger.w(e) { "No ancestry feats for ${ancestry.name}" }
                _ancestryFeats.value = emptyList()
            }
            _featsLoading.value = false
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun loadFeatsForClass(classData: ClassData) {
        viewModelScope.launch {
            _featsLoading.value = true
            val path = classData.name.lowercase().replace(" ", "-")
            try {
                val indexBytes = Res.readBytes("files/feats/class/$path/level-1/index.json")
                val index = json.decodeFromString<IndexFile>(indexBytes.decodeToString())
                val feats = index.files.filter { !it.fileName.startsWith("_") && it.fileName.endsWith(".json") }.mapNotNull { entry ->
                    try {
                        val bytes = Res.readBytes("files/feats/class/$path/level-1/${entry.fileName}")
                        json.decodeFromString<Feat>(bytes.decodeToString())
                    } catch (e: Exception) {
                        Logger.w(e) { "Error loading class feat ${entry.fileName}" }
                        null
                    }
                }
                _classFeats.value = feats
            } catch (e: Exception) {
                Logger.w(e) { "No class feats for ${classData.name}" }
                _classFeats.value = emptyList()
            }
            _featsLoading.value = false
        }
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
                ancestryFeat = null
            )
        }
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
                extraTrainedSkills = emptySet(),
                classFeat = null
            )
        }
        loadFeatsForClass(classData)
    }

    fun updateName(name: String) {
        _characterState.update { it.copy(name = name) }
    }

    fun updateHeritage(heritage: String) {
        _characterState.update { it.copy(heritage = heritage) }
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
        _characterState.update { state ->
            val newFree = state.freeBoosts.toMutableSet()
            if (newFree.contains(attribute)) {
                newFree.remove(attribute)
            } else {
                if (newFree.size < 4) {
                    newFree.add(attribute)
                }
            }
            state.copy(freeBoosts = newFree)
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

    fun toggleExtraSkill(skillId: String) {
        _characterState.update { state ->
            val autoSkills = getAutoTrainedSkills(state)
            if (skillId in autoSkills) return@update state
            val current = state.extraTrainedSkills.toMutableSet()
            val available = getAvailableExtraSkillsCount(state)
            if (current.contains(skillId)) {
                current.remove(skillId)
            } else if (current.size < available) {
                current.add(skillId)
            }
            state.copy(extraTrainedSkills = current)
        }
    }

    fun setAncestryFeat(feat: Feat) {
        _characterState.update { it.copy(ancestryFeat = feat) }
    }

    fun setClassFeat(feat: Feat) {
        _characterState.update { it.copy(classFeat = feat) }
    }

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

    fun getAvailableExtraSkillsCount(state: CharacterState = _characterState.value): Int {
        val classAdditional = state.classData?.system?.trainedSkills?.let {
            try { json.decodeFromJsonElement<ClassTrainedSkills>(it).additional }
            catch (e: Exception) { 0 }
        } ?: 0
        val intScore = calculateAttributes(state)[Attribute.INT] ?: 10
        val intModifier = (intScore - 10) / 2
        return classAdditional + maxOf(0, intModifier)
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

        return base
    }

    fun resetBackground() {
        _characterState.update { it.copy(background = null, backgroundBoosts = emptyMap(), extraTrainedSkills = emptySet()) }
    }

    fun resetClass() {
        _characterState.update { it.copy(classData = null, classBoost = null, extraTrainedSkills = emptySet(), classFeat = null) }
        _classFeats.value = emptyList()
    }

    fun reset() {
        _characterState.value = CharacterState()
        _ancestryFeats.value = emptyList()
        _classFeats.value = emptyList()
    }
}
