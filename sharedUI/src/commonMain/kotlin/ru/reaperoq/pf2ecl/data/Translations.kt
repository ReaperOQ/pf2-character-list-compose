package ru.reaperoq.pf2ecl.data

import pathfinder_2e_character_list.sharedui.generated.resources.Res
import pathfinder_2e_character_list.sharedui.generated.resources.allDrawableResources

object Translations {
    val ancestries = mapOf(
        "Athamaru" to "Атамару",
        "Automaton" to "Автоматон",
        "Awakened Animal" to "Пробуждённое животное",
        "Catfolk" to "Кошачий народ (Кэтфолк)",
        "Centaur" to "Кентавр",
        "Dragonet" to "Драконет",
        "Dwarf" to "Дворф",
        "Elf" to "Эльф",
        "Gnome" to "Гном",
        "Goblin" to "Гоблин",
        "Halfling" to "Полурослик",
        "Hobgoblin" to "Хобгоблин",
        "Human" to "Человек",
        "Jotunborn" to "Йотунборн",
        "Kholo" to "Холо (Гнолл)",
        "Kobold" to "Кобольд",
        "Leshy" to "Леший",
        "Lizardfolk" to "Ящеролюд (Лизардфолк)",
        "Merfolk" to "Мерфолк (Тритон)",
        "Minotaur" to "Минотавр",
        "Orc" to "Орк",
        "Ratfolk" to "Крысолюд (Рэтфолк)",
        "Samsaran" to "Самсаран",
        "Sarangay" to "Сарангай",
        "Surki" to "Сурки",
        "Tanuki" to "Тануки",
        "Tengu" to "Тенгу",
        "Tripkee" to "Трипки (Гриппли)",
        "Wayang" to "Вайанг",
        "Yaksha" to "Якша",
        "Yaoguai" to "Яогуай"
    )

    val classes = mapOf(
        "Alchemist" to "Алхимик",
        "Animist" to "Анимист",
        "Barbarian" to "Варвар",
        "Bard" to "Бард",
        "Champion" to "Чемпион",
        "Cleric" to "Жрец",
        "Commander" to "Командир",
        "Druid" to "Друид",
        "Exemplar" to "Экземпляр",
        "Fighter" to "Воин",
        "Guardian" to "Страж",
        "Gunslinger" to "Стрелок",
        "Inventor" to "Изобретатель",
        "Investigator" to "Следователь",
        "Kineticist" to "Кинетик",
        "Monk" to "Монах",
        "Oracle" to "Оракул",
        "Psychic" to "Псионик",
        "Ranger" to "Рейнджер",
        "Rogue" to "Плут",
        "Sorcerer" to "Чародей",
        "Summoner" to "Призыватель",
        "Swashbuckler" to "Сорвиголова",
        "Thaumaturge" to "Тауматург",
        "Witch" to "Ведьма",
        "Wizard" to "Волшебник"
    )

    val skills = mapOf(
        "acrobatics" to "Акробатика",
        "arcana" to "Аркана",
        "athletics" to "Атлетика",
        "crafting" to "Ремесло",
        "deception" to "Обман",
        "diplomacy" to "Дипломатия",
        "intimidation" to "Запугивание",
        "medicine" to "Медицина",
        "nature" to "Природа",
        "occultism" to "Оккультизм",
        "performance" to "Выступление",
        "religion" to "Религия",
        "society" to "Общество",
        "stealth" to "Скрытность",
        "survival" to "Выживание",
        "thievery" to "Воровство"
    )

    val attributeNames = mapOf(
        Attribute.STR to "Сила",
        Attribute.DEX to "Ловкость",
        Attribute.CON to "Телосложение",
        Attribute.INT to "Интеллект",
        Attribute.WIS to "Мудрость",
        Attribute.CHA to "Харизма"
    )
    fun translateAncestry(name: String): String = name
    fun translateClass(name: String): String = name
    fun translateSkill(id: String): String = skills[id.lowercase()] ?: id.replaceFirstChar { it.uppercase() }
    fun translateAttribute(attr: Attribute): String = attributeNames[attr] ?: attr.ruLabel
    fun translateBackground(name: String): String = name

    fun getFilenameKey(img: String?): String? {
        if (img.isNullOrBlank()) return null
        val fileName = img.substringAfterLast('/').substringBeforeLast('.')
        return fileName.lowercase()
            .replace("'", "")
            .replace("-", "_")
            .replace(" ", "_")
            .replace(",", "")
            .replace(":", "")
            .replace("(", "")
            .replace(")", "")
    }

    fun getClassDrawableKey(classData: ClassData): String {
        val slug = classData.slug
        if (slug != null) {
            val key = "class_${slug.lowercase().replace("-", "_")}"
            if (Res.allDrawableResources.containsKey(key)) {
                return key
            }
        }
        val imgFilename = getFilenameKey(classData.img)
        if (imgFilename != null) {
            val key = "class_$imgFilename"
            if (Res.allDrawableResources.containsKey(key)) {
                return key
            }
        }
        val englishName = classes.entries.find { it.value.equals(classData.name, ignoreCase = true) }?.key
            ?: classes.entries.find { it.key.equals(classData.name, ignoreCase = true) }?.key
            ?: classData.name
        return "class_${englishName.lowercase()}"
    }

    fun getAncestryDrawableKey(ancestry: Ancestry): String {
        val slug = ancestry.slug
        if (slug != null) {
            val key = "ancestry_${slug.lowercase().replace("-", "_")}"
            if (Res.allDrawableResources.containsKey(key)) {
                return key
            }
        }
        val imgFilename = getFilenameKey(ancestry.img)
        if (imgFilename != null) {
            val key = "ancestry_$imgFilename"
            if (Res.allDrawableResources.containsKey(key)) {
                return key
            }
        }
        val englishName = ancestries.entries.find { it.value.equals(ancestry.name, ignoreCase = true) }?.key
            ?: ancestries.entries.find { it.key.equals(ancestry.name, ignoreCase = true) }?.key
            ?: ancestry.name
        return "ancestry_" + englishName.lowercase()
            .replace("'", "")
            .replace("-", "_")
            .replace(" ", "_")
            .replace(",", "")
            .replace(":", "")
            .replace("(", "")
            .replace(")", "")
    }

    fun getAncestryDrawableKey(name: String): String {
        return "ancestry_" + name.lowercase()
            .replace("'", "")
            .replace("-", "_")
            .replace(" ", "_")
            .replace(",", "")
            .replace(":", "")
            .replace("(", "")
            .replace(")", "")
    }

    fun getSpellDrawableKey(spell: Spell): String {
        val slug = spell.slug
        if (slug != null) {
            val key = "spell_${slug.lowercase().replace("-", "_")}"
            if (Res.allDrawableResources.containsKey(key)) {
                return key
            }
        }
        val imgFilename = getFilenameKey(spell.img)
        if (imgFilename != null) {
            val key = "spell_$imgFilename"
            if (Res.allDrawableResources.containsKey(key)) {
                return key
            }
        }
        return "spell_" + spell.name.lowercase()
            .replace("'", "")
            .replace("-", "_")
            .replace(" ", "_")
            .replace(",", "")
            .replace(":", "")
            .replace("(", "")
            .replace(")", "")
    }

    fun getSpellDrawableKey(name: String): String {
        return "spell_" + name.lowercase()
            .replace("'", "")
            .replace("-", "_")
            .replace(" ", "_")
            .replace(",", "")
            .replace(":", "")
            .replace("(", "")
            .replace(")", "")
    }

    fun getAncestrySlug(ancestry: Ancestry): String {
        val slug = ancestry.slug
        if (slug != null) return slug.lowercase()
        val englishName = ancestries.entries.find { it.value.equals(ancestry.name, ignoreCase = true) }?.key
            ?: ancestries.entries.find { it.key.equals(ancestry.name, ignoreCase = true) }?.key
            ?: ancestry.name
        return englishName.lowercase().replace(" ", "-")
    }

    fun getClassSlug(classData: ClassData): String {
        val slug = classData.slug
        if (slug != null) return slug.lowercase()
        val englishName = classes.entries.find { it.value.equals(classData.name, ignoreCase = true) }?.key
            ?: classes.entries.find { it.key.equals(classData.name, ignoreCase = true) }?.key
            ?: classData.name
        return englishName.lowercase().replace(" ", "-")
    }
}
