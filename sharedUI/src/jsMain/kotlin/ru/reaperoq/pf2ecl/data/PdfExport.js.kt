package ru.reaperoq.pf2ecl.data

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.browser.window

actual fun exportBitmap(bitmap: ImageBitmap, fileName: String) {
//    try {
//        val skBitmap = bitmap.asSkiaBitmap()
//        val pngData = SkiaImage.makeFromBitmap(skBitmap).encodeToData(EncodedImageFormat.PNG)
//            ?: run { window.print(); return }
//
//        val bytes = pngData.bytes
//        val jsArray = js("new Uint8Array(bytes.length)")
//        for (i in bytes.indices) {
//            jsArray[i] = bytes[i]
//        }
//        val blob = Blob(arrayOf(jsArray), BlobPropertyBag(type = "image/png"))
//        val url = window.URL.createObjectURL(blob)
//        window.
//
//        val anchor = document.createElement("a").unsafeCast<HTMLAnchorElement>()
//        anchor.href = url
//        anchor.download = "$fileName.png"
//        document.body?.appendChild(anchor)
//        anchor.click()
//        document.body?.removeChild(anchor)
//        window.URL.revokeObjectURL(url)
//    } catch (e: Exception) {
//        // Fallback to print dialog if encoding fails
//        window.print()
//    }
    window.print()
}
