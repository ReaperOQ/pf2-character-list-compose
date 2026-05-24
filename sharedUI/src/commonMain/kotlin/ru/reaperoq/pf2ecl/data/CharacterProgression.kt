package ru.reaperoq.pf2ecl.data

object CharacterProgression {
    val ANCESTRY_FEAT_LEVELS = listOf(1, 5, 9, 13, 17)
    val ATTRIBUTE_BOOST_LEVELS = listOf(1, 5, 10, 15, 20)
    const val BOOSTS_PER_TIER = 4

    fun ancestryFeatSlots(level: Int): Int =
        ANCESTRY_FEAT_LEVELS.count { it <= level.coerceIn(CharacterBuilderViewModel.MIN_LEVEL, CharacterBuilderViewModel.MAX_LEVEL) }

    fun classFeatSlots(level: Int): Int =
        level.coerceIn(CharacterBuilderViewModel.MIN_LEVEL, CharacterBuilderViewModel.MAX_LEVEL)

    fun maxSpellRank(level: Int): Int =
        ((level + 1) / 2).coerceIn(1, 10)

    fun unlockedAttributeBoostTiers(level: Int): List<Int> =
        ATTRIBUTE_BOOST_LEVELS.filter { it <= level.coerceIn(CharacterBuilderViewModel.MIN_LEVEL, CharacterBuilderViewModel.MAX_LEVEL) }

    fun attributeBoostsForTier(state: CharacterState, tier: Int): Set<Attribute> =
        if (tier == 1) state.freeBoosts else state.attributeBoosts[tier].orEmpty()

    fun isAttributeBoostTierComplete(state: CharacterState, tier: Int): Boolean =
        attributeBoostsForTier(state, tier).size == BOOSTS_PER_TIER

    fun areAllAttributeBoostsComplete(state: CharacterState): Boolean =
        unlockedAttributeBoostTiers(state.level).all { isAttributeBoostTierComplete(state, it) }
}

fun CharacterState.resolvedAncestryFeats(): List<Feat> =
    selectedAncestryFeats.ifEmpty { listOfNotNull(ancestryFeat) }

fun CharacterState.resolvedClassFeats(): List<Feat> =
    selectedClassFeats.ifEmpty { listOfNotNull(classFeat) }

fun CharacterState.migrateLegacy(): CharacterState {
    var s = this
    if (s.selectedAncestryFeats.isEmpty() && s.ancestryFeat != null) {
        s = s.copy(selectedAncestryFeats = listOf(s.ancestryFeat), ancestryFeat = null)
    }
    if (s.selectedClassFeats.isEmpty() && s.classFeat != null) {
        s = s.copy(selectedClassFeats = listOf(s.classFeat), classFeat = null)
    }
    return s
}
