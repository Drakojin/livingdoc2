package org.livingdoc.engine.reporting

import strikt.api.Assertion

private fun CharSequence.removeWhitespace(): String {
    val result = StringBuilder(this.length)
    for (i in 0 until this.length) {
        val c = this[i]
        if (Character.isWhitespace(c)) {
            continue
        }
        result.append(c)
    }
    return result.toString()
}

fun Assertion.Builder<String>.isEqualIgnoreWhitespace(other: String) {
    assertThat("equals ignoring whitespace") {
        it.removeWhitespace() == other.removeWhitespace()
    }
}
