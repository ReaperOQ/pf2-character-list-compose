package ru.reaperoq.pf2ecl.data

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.browser.window

actual fun exportBitmap(bitmap: ImageBitmap, fileName: String) {
    window.print()
}
