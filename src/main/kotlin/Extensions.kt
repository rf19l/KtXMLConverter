package com.rf.foster.ktxml

import java.util.regex.Pattern

fun String.toCamelCase(): String {
    if (this.isEmpty()) return this
    // Replace dots with underscores
    var formatted = this.replace(".", "_").removePrefix("android:")

    // Remove trailing underscores
    formatted = formatted.trimEnd('_')

    // Replace multiple underscores with a single one
    formatted = formatted.replace(Regex("_{2,}"), "_")

    // Convert to camel case
    val p = Pattern.compile("(_)(\\w)")
    var m = p.matcher(formatted)
    var sb = StringBuilder()
    while (m.find()) {
        m.appendReplacement(sb, m.group(2).toUpperCase())
    }
    m.appendTail(sb)

    // Make sure the first character is lowercase
    return sb[0].toLowerCase() + sb.substring(1)
}
