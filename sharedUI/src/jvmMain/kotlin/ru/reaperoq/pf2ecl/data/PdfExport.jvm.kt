package ru.reaperoq.pf2ecl.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO

actual fun exportBitmap(bitmap: ImageBitmap, fileName: String) {
    val dialog = FileDialog(null as Frame?, "Сохранить лист персонажа", FileDialog.SAVE).apply {
        file = "$fileName.png"
        isVisible = true
    }
    val dir = dialog.directory ?: return
    val name = dialog.file ?: return
    val outputFile = File(dir, if (name.endsWith(".png")) name else "$name.png")

    val awtImage = bitmap.toAwtImage()
    ImageIO.write(awtImage, "PNG", outputFile)
}
