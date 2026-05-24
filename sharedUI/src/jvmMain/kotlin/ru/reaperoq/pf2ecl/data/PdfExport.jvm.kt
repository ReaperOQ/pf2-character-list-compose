package ru.reaperoq.pf2ecl.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO

actual fun exportPng(bitmap: ImageBitmap, fileName: String) {
    val dialog = FileDialog(null as Frame?, "Сохранить лист персонажа как PNG", FileDialog.SAVE).apply {
        file = "$fileName.png"
        isVisible = true
    }
    val dir = dialog.directory ?: return
    val name = dialog.file ?: return
    val outputFile = File(dir, if (name.endsWith(".png")) name else "$name.png")

    val awtImage = bitmap.toAwtImage()
    ImageIO.write(awtImage, "PNG", outputFile)
}

actual fun printOrExportPdf(bitmap: ImageBitmap, fileName: String) {
    val awtImage = bitmap.toAwtImage()
    val job = java.awt.print.PrinterJob.getPrinterJob()
    job.jobName = "Печать листа персонажа: $fileName"
    job.setPrintable { graphics, pageFormat, pageIndex ->
        if (pageIndex > 0) {
            java.awt.print.Printable.NO_SUCH_PAGE
        } else {
            val g2d = graphics as java.awt.Graphics2D
            g2d.translate(pageFormat.imageableX, pageFormat.imageableY)
            
            val pageWidth = pageFormat.imageableWidth
            val pageHeight = pageFormat.imageableHeight
            val imgWidth = awtImage.width.toDouble()
            val imgHeight = awtImage.height.toDouble()
            
            val scale = minOf(pageWidth / imgWidth, pageHeight / imgHeight)
            g2d.scale(scale, scale)
            g2d.drawImage(awtImage, 0, 0, null)
            java.awt.print.Printable.PAGE_EXISTS
        }
    }
    if (job.printDialog()) {
        try {
            job.print()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
