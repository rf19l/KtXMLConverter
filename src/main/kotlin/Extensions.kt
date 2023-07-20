package com.rf.foster.ktxml

import java.util.regex.Pattern

internal fun String.toCamelCase():String {
    val p = Pattern.compile("(_)(\\w)")
    var m = p.matcher(this.toLowerCase())
    var sb = StringBuilder()
    while (m.find()) {
        m.appendReplacement(sb, m.group(2).toUpperCase())
    }
    m.appendTail(sb)

    // Convert PascalCase to camelCase
    return sb[0].toLowerCase() + sb.substring(1)
}