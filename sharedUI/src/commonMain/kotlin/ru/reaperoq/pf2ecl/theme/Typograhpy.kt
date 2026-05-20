package ru.reaperoq.pf2ecl.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import pathfinder_2e_character_list.sharedui.generated.resources.Literata_Black
import pathfinder_2e_character_list.sharedui.generated.resources.Literata_Bold
import pathfinder_2e_character_list.sharedui.generated.resources.Literata_ExtraBold
import pathfinder_2e_character_list.sharedui.generated.resources.Literata_ExtraLight
import pathfinder_2e_character_list.sharedui.generated.resources.Literata_Light
import pathfinder_2e_character_list.sharedui.generated.resources.Literata_Medium
import pathfinder_2e_character_list.sharedui.generated.resources.Literata_Regular
import pathfinder_2e_character_list.sharedui.generated.resources.Literata_SemiBold
import pathfinder_2e_character_list.sharedui.generated.resources.Res

@Composable
fun LiterataTypography(): Typography {
    val literataFont = FontFamily(
        Font(Res.font.Literata_ExtraLight, FontWeight.ExtraLight),
        Font(Res.font.Literata_Light, FontWeight.Light),
        Font(Res.font.Literata_Regular, FontWeight.Normal),
        Font(Res.font.Literata_Medium, FontWeight.Medium),
        Font(Res.font.Literata_SemiBold, FontWeight.SemiBold),
        Font(Res.font.Literata_Bold, FontWeight.Bold),
        Font(Res.font.Literata_ExtraBold, FontWeight.ExtraBold),
        Font(Res.font.Literata_Black, FontWeight.Black)
    )

    return with(MaterialTheme.typography) {
        copy(
            displayLarge = displayLarge.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            displayMedium = displayMedium.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            displaySmall = displaySmall.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            headlineLarge = headlineLarge.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            headlineMedium = headlineMedium.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            headlineSmall = headlineSmall.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            titleLarge = titleLarge.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            titleMedium = titleMedium.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            titleSmall = titleSmall.copy(fontFamily = literataFont, fontWeight = FontWeight.Bold),
            labelLarge = labelLarge.copy(fontFamily = literataFont, fontWeight = FontWeight.Normal),
            labelMedium = labelMedium.copy(fontFamily = literataFont, fontWeight = FontWeight.Normal),
            labelSmall = labelSmall.copy(fontFamily = literataFont, fontWeight = FontWeight.Normal),
        )
    }
}