package io.github.rf19l.ktxml.utils

import java.util.regex.Pattern

/**
 * Converts various formats of names in xml to a camelCase representation
 */
fun String.toCamelCase(): String {
    if (this.isEmpty()) return this
    var formatted = this.replace(".", "_").removePrefix("android:")
    formatted = formatted.trimEnd('_')
    formatted = formatted.replace(Regex("_{2,}"), "_")
    val p = Pattern.compile("(_)(\\w)")
    val m = p.matcher(formatted)
    val sb = StringBuilder()
    while (m.find()) {
        m.appendReplacement(sb, m.group(2).toUpperCase())
    }
    m.appendTail(sb)
    return sb[0].toLowerCase() + sb.substring(1)
}
