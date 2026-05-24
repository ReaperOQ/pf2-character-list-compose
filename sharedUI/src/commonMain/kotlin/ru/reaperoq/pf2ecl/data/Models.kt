package ru.reaperoq.pf2ecl.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class IndexFile(
    val directories: List<String> = emptyList(),
    val files: List<IndexEntry> = emptyList()
)

@Serializable
data class IndexEntry(
    val fileName: String,
    val id: String? = null,
    val name: String? = null,
    val img: String? = null,
    val type: String? = null,
    val level: Int? = null,
    val category: String? = null,
    val data: JsonElement? = null
)

@Serializable
data class Ancestry(
    val _id: String,
    val name: String,
    val img: String? = null,
    val slug: String? = null,
    val system: AncestrySystem
) {
    @Serializable
    data class AncestrySystem(
        val hp: Int,
        val size: String,
        val speed: Int,
        val description: Description,
        val traits: Traits,
        val boosts: Map<String, AttributeBoost>? = null,
        val flaws: Map<String, AttributeBoost>? = null
    )
}

@Serializable
data class AttributeBoost(
    val value: List<String>
)

@Serializable
data class Description(
    val value: String
)

@Serializable
data class Traits(
    val value: List<String>
)

@Serializable
data class Background(
    val _id: String,
    val name: String,
    val system: BackgroundSystem
) {
    @Serializable
    data class BackgroundSystem(
        val description: Description,
        val boosts: Map<String, AttributeBoost>? = null,
        val trainedSkills: JsonElement? = null
    )
}

@Serializable
data class BackgroundTrainedSkills(
    val value: List<String> = emptyList(),
    val lore: List<String> = emptyList()
)

@Serializable
data class ClassData(
    val _id: String,
    val name: String,
    val img: String? = null,
    val slug: String? = null,
    val system: ClassSystem
) {
    @Serializable
    data class ClassSystem(
        val description: Description,
        val keyAbility: AttributeBoost? = null,
        val hp: Int,
        val perception: Int,
        val savingThrows: SaveProficiencies,
        val attacks: AttackProficiencies,
        val defenses: DefenseProficiencies,
        val trainedSkills: JsonElement? = null
    )
}

@Serializable
data class ClassTrainedSkills(
    val value: List<String> = emptyList(),
    val additional: Int = 0
)

@Serializable
data class SaveProficiencies(
    val fortitude: Int,
    val reflex: Int,
    val will: Int
)

@Serializable
data class AttackProficiencies(
    val simple: Int,
    val martial: Int,
    val advanced: Int,
    val unarmed: Int
)

@Serializable
data class DefenseProficiencies(
    val unarmored: Int,
    val light: Int,
    val medium: Int,
    val heavy: Int
)

enum class Attribute(val label: String, val ruLabel: String) {
    STR("Strength", "СИЛ"),
    DEX("Dexterity", "ЛОВ"),
    CON("Constitution", "ТЕЛ"),
    INT("Intelligence", "ИНТ"),
    WIS("Wisdom", "МУД"),
    CHA("Charisma", "ХАР");

    companion object {
        fun fromId(id: String): Attribute? = entries.find { it.name.equals(id, ignoreCase = true) }
    }
}

@Serializable
data class Heritage(
    val _id: String,
    val name: String,
    val system: HeritageSystem
) {
    @Serializable
    data class HeritageSystem(
        val description: Description,
        val ancestry: HeritageAncestryRef? = null
    )

    @Serializable
    data class HeritageAncestryRef(
        val name: String? = null,
        val slug: String? = null
    )
}

@Serializable
data class Spell(
    val _id: String,
    val name: String,
    val img: String? = null,
    val slug: String? = null,
    val system: SpellSystem
) {
    @Serializable
    data class SpellSystem(
        val description: Description,
        val level: SpellLevel? = null,
        val traits: SpellTraits? = null,
        val time: SpellValue? = null,
        val range: SpellValue? = null
    )

    @Serializable
    data class SpellLevel(val value: Int = 0)

    @Serializable
    data class SpellTraits(
        val value: List<String> = emptyList(),
        val traditions: List<String> = emptyList()
    )

    @Serializable
    data class SpellValue(val value: String = "")
}

@Serializable
data class SpellRef(
    val id: String,
    val name: String,
    val rankLabel: String
)

@Serializable
data class Feat(
    val _id: String? = null,
    val name: String,
    val system: FeatSystem
) {
    @Serializable
    data class FeatSystem(
        val description: Description,
        val level: FeatLevel? = null,
        val category: String? = null,
        val traits: Traits? = null,
        val prerequisites: Prerequisites? = null
    )

    @Serializable
    data class FeatLevel(
        val value: Int = 0
    )

    @Serializable
    data class Prerequisites(
        val value: List<PrerequisiteEntry> = emptyList()
    )

    @Serializable
    data class PrerequisiteEntry(
        val value: String = ""
    )
}

@Serializable
data class CharacterState(
    val name: String = "Новый герой",
    val level: Int = 1,
    val ancestry: Ancestry? = null,
    val heritage: String? = null,
    val background: Background? = null,
    val classData: ClassData? = null,
    val ancestryBoosts: Map<String, Attribute> = emptyMap(),
    val backgroundBoosts: Map<String, Attribute> = emptyMap(),
    val classBoost: Attribute? = null,
    val freeBoosts: Set<Attribute> = emptySet(),
    val attributeBoosts: Map<Int, Set<Attribute>> = emptyMap(),
    val extraTrainedSkills: Set<String> = emptySet(),
    val selectedAncestryFeats: List<Feat> = emptyList(),
    val selectedClassFeats: List<Feat> = emptyList(),
    val ancestryFeat: Feat? = null,
    val classFeat: Feat? = null,
    val selectedCantrips: List<SpellRef> = emptyList(),
    val selectedSpells: List<SpellRef> = emptyList(),
    val enabledWidgets: Set<String> = setOf("attributes", "defenses", "skills", "strikes", "feats", "spells")
)