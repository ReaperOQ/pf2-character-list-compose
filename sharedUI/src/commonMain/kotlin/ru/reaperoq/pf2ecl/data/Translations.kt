package ru.reaperoq.pf2ecl.data

import kotlinx.serialization.Serializable

@Serializable
data class HeritageInfo(
    val id: String,
    val nameRu: String,
    val nameEn: String,
    val description: String
)

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

    val heritages = mapOf(
        "Dwarf" to listOf(
            HeritageInfo("ancient_dwarf", "Древний дворф", "Ancient Dwarf", "Вы получаете одну дополнительную черту класса дворфа 1-го уровня."),
            HeritageInfo("death_warden_dwarf", "Дворф-страж смерти", "Death Warden Dwarf", "Вы получаете +2 к спасброскам против эффектов некромантии и смерти."),
            HeritageInfo("forge_dwarf", "Кузнечный дворф", "Forge Dwarf", "Вы получаете сопротивление огню, равное половине вашего уровня (минимум 1)."),
            HeritageInfo("rock_dwarf", "Скалистый дворф", "Rock Dwarf", "Вы получаете +2 к спасброскам или КД против попыток сбить вас с ног или переместить.")
        ),
        "Elf" to listOf(
            HeritageInfo("arctic_elf", "Арктический эльф", "Arctic Elf", "Вы получаете сопротивление холоду, равное половине вашего уровня (минимум 1)."),
            HeritageInfo("cavern_elf", "Пещерный эльф", "Cavern Elf", "Вы получаете темновидение (Darkvision) вместо сумеречного зрения."),
            HeritageInfo("seer_elf", "Эльф-провидец", "Seer Elf", "Вы можете использовать заклинание обнаружения магии (Detect Magic) как врождённый фокус."),
            HeritageInfo("whisper_elf", "Шепчущий эльф", "Whisper Elf", "Вы получаете +2 к проверкам Восприятия, основанным на слухе.")
        ),
        "Human" to listOf(
            HeritageInfo("skilled_human", "Опытный человек", "Skilled Heritage", "Вы становитесь обученным в одном дополнительном навыке по вашему выбору."),
            HeritageInfo("versatile_human", "Разносторонний человек", "Versatile Heritage", "Вы получаете одну дополнительную общую черту (General Feat) 1-го уровня.")
        ),
        "Goblin" to listOf(
            HeritageInfo("charhide_goblin", "Угленосый гоблин", "Charhide Goblin", "Вы получаете сопротивление огню, равное половине вашего уровня (минимум 1)."),
            HeritageInfo("irongut_goblin", "Железнобрюхий гоблин", "Irongut Goblin", "Вы получаете +2 к спасброскам против болезней и ядов, съеденных вами."),
            HeritageInfo("razortooth_goblin", "Острозубый гоблин", "Razortooth Goblin", "Вы получаете челюстную атаку, наносящую 1d6 колющего урона."),
            HeritageInfo("unbreakable_goblin", "Несокрушимый гоблин", "Unbreakable Goblin", "Ваши максимальные хиты (HP) увеличиваются на 4.")
        ),
        "Halfling" to listOf(
            HeritageInfo("gutsy_halfling", "Смелый полурослик", "Gutsy Halfling", "Вы получаете +2 к спасброскам против эффектов страха."),
            HeritageInfo("hillock_halfling", "Пригорный полурослик", "Hillock Halfling", "Вы восстанавливаете в два раза больше хитов во время отдыха на природе."),
            HeritageInfo("nomad_halfling", "Полурослик-кочевник", "Nomad Halfling", "Вы получаете +2 к проверкам Выживания в дикой природе.")
        )
    )

    val backgroundNames = mapOf(
        "Acolyte" to "Аколит",
        "Acrobat" to "Акробат",
        "Artisan" to "Ремесленник",
        "Artist" to "Художник",
        "Barkeep" to "Трактирщик",
        "Bounty Hunter" to "Охотник за головами",
        "Criminal" to "Преступник",
        "Detective" to "Детектив",
        "Entertainer" to "Артист",
        "Farmhand" to "Батрак",
        "Field Medic" to "Полевой медик",
        "Gladiator" to "Гладиатор",
        "Guard" to "Стражник",
        "Herbalist" to "Травник",
        "Hermit" to "Отшельник",
        "Hunter" to "Охотник",
        "Laborer" to "Рабочий",
        "Merchant" to "Торговец",
        "Noble" to "Дворянин",
        "Nomad" to "Кочевник",
        "Prisoner" to "Заключенный",
        "Sailor" to "Моряк",
        "Scholar" to "Ученый",
        "Scout" to "Разведчик",
        "Street Urchin" to "Беспризорник",
        "Tinker" to "Жестянщик",
        "Warrior" to "Воин"
    )

    fun translateAncestry(name: String): String = ancestries[name] ?: name
    fun translateClass(name: String): String = classes[name] ?: name
    fun translateSkill(id: String): String = skills[id.lowercase()] ?: id.replaceFirstChar { it.uppercase() }
    fun translateAttribute(attr: Attribute): String = attributeNames[attr] ?: attr.ruLabel
    fun translateBackground(name: String): String = backgroundNames[name] ?: name
}
