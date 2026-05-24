package ru.reaperoq.pf2ecl.data

internal fun ByteArray.decodeToJsonString(): String {
    val start = if (size >= 3 &&
        this[0] == 0xEF.toByte() &&
        this[1] == 0xBB.toByte() &&
        this[2] == 0xBF.toByte()
    ) {
        3
    } else {
        0
    }
    return copyOfRange(start, size).decodeToString()
}
