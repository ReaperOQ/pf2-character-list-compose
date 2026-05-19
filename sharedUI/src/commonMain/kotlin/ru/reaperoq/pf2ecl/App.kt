package ru.reaperoq.pf2ecl

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pathfinder_2e_character_list.sharedui.generated.resources.*
import ru.reaperoq.pf2ecl.theme.AppTheme
import ru.reaperoq.pf2ecl.theme.LocalThemeIsDark
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.skia.Surface
import ru.reaperoq.pf2ecl.screens.DashboardScreen

@Preview
@Composable
fun App(
    onThemeChanged: @Composable (isDark: Boolean) -> Unit = {}
) = AppTheme(onThemeChanged) {
    Surface {
        DashboardScreen()
    }
}
