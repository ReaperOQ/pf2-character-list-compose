package ru.reaperoq.pf2ecl.data

import androidx.compose.ui.graphics.ImageBitmap

expect fun exportPng(bitmap: ImageBitmap, fileName: String)
expect fun printOrExportPdf(bitmap: ImageBitmap, fileName: String)
