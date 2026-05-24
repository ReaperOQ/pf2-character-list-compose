package ru.reaperoq.pf2ecl.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Image as SkiaImage
import org.jetbrains.skia.EncodedImageFormat
import kotlinx.browser.document
import kotlinx.browser.window
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLIFrameElement
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.FileReader
import org.w3c.dom.url.URL

actual fun exportPng(bitmap: ImageBitmap, fileName: String) {
    try {
        val skBitmap = bitmap.asSkiaBitmap()
        val pngData = SkiaImage.makeFromBitmap(skBitmap).encodeToData(EncodedImageFormat.PNG)
            ?: throw Exception("Failed to encode to PNG")

        val bytes = pngData.bytes
        val jsArray = Uint8Array(bytes.size)
        for (i in bytes.indices) {
            jsArray[i] = bytes[i]
        }
        val blob = Blob(arrayOf(jsArray), BlobPropertyBag(type = "image/png"))
        val url = URL.createObjectURL(blob)

        val anchor = document.createElement("a") as HTMLAnchorElement
        anchor.href = url
        anchor.download = "$fileName.png"
        document.body?.appendChild(anchor)
        anchor.click()
        document.body?.removeChild(anchor)
        URL.revokeObjectURL(url)
    } catch (e: Exception) {
        console.error("Failed to export PNG: ", e)
    }
}

actual fun printOrExportPdf(bitmap: ImageBitmap, fileName: String) {
    try {
        val skBitmap = bitmap.asSkiaBitmap()
        val pngData = SkiaImage.makeFromBitmap(skBitmap).encodeToData(EncodedImageFormat.PNG)
            ?: throw Exception("Failed to encode to PNG")

        val bytes = pngData.bytes
        val jsArray = Uint8Array(bytes.size)
        for (i in bytes.indices) {
            jsArray[i] = bytes[i]
        }
        val blob = Blob(arrayOf(jsArray), BlobPropertyBag(type = "image/png"))

        val reader = FileReader()
        reader.onload = {
            val dataUrl = reader.result.toString()
            printImageViaIframe(dataUrl)
        }
        reader.readAsDataURL(blob)
    } catch (e: Exception) {
        console.error("Failed to print/export PDF: ", e)
        window.print()
    }
}

private fun printImageViaIframe(dataUrl: String) {
    val iframe = document.createElement("iframe") as HTMLIFrameElement
    iframe.style.position = "fixed"
    iframe.style.right = "0"
    iframe.style.bottom = "0"
    iframe.style.width = "0"
    iframe.style.height = "0"
    iframe.style.border = "0"
    document.body?.appendChild(iframe)

    val doc = iframe.contentWindow?.document
    if (doc != null) {
        doc.open()
        doc.write("""
            <html>
            <head>
                <style>
                    @page { size: A4; margin: 0; }
                    body { margin: 0; padding: 0; }
                    img { width: 100%; height: auto; display: block; }
                </style>
            </head>
            <body>
                <img src="$dataUrl" onload="window.print()" />
            </body>
            </html>
        """.trimIndent())
        doc.close()
    }

    iframe.contentWindow?.addEventListener("afterprint", {
        document.body?.removeChild(iframe)
    })
}
