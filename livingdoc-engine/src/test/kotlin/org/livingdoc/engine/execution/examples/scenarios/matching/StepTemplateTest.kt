package org.livingdoc.engine.execution.examples.scenarios.matching

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.containsExactly
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class StepTemplateTest {

    @Nested
    inner class `when building a new template from a string` {

        @Test
        fun `throws when parsing an empty string`() {
            expectThrows<IllegalFormatException> {
                StepTemplate.parse("")
            }
        }

        @Test
        fun `parses StepTemplates without variable`() {
            val cut = StepTemplate.parse("Peter enters the building.")
            expectThat(cut.fragments).containsExactly(Text("Peter enters the building."))
        }

        @Test
        fun `throws if StepTemplate contains illegal curly braces`() {
            expectThrows<IllegalFormatException> {
                StepTemplate.parse("This is {{invalid}, because it has 2 opening braces before the first closing one.")
            }
            expectThrows<IllegalFormatException> {
                StepTemplate.parse("This is also {invalid}, because} it has 2 closing braces without an open one in between.")
            }
        }

        @Test
        fun `parses StepTemplates with one variable`() {
            val cut = StepTemplate.parse("{user} enters the building.")
            expectThat(cut.fragments).containsExactly(
                Variable("user"),
                Text(" enters the building.")
            )
        }

        @Test
        fun `accepts optional set of quotation characters`() {
            val cut = StepTemplate.parse("{user} enters the building.", setOf('\''))
            expectThat(cut.quotationCharacters).containsExactly('\'')
        }

        @Test
        fun `parses StepTemplates with multiple variables`() {
            val cut = StepTemplate.parse("The user {user} enters a value for {variable}: {value}")
            expectThat(cut.fragments).containsExactly(
                Text("The user "),
                Variable("user"),
                Text(" enters a value for "),
                Variable("variable"),
                Text(": "),
                Variable("value")
            )
        }

        @Test
        fun `parses StepTemplates with escaped curly braces`() {
            val cut = StepTemplate.parse("Literal \\{curly braces\\} have to be escaped with backslashes.")
            expectThat(cut.fragments).containsExactly(
                Text("Literal \\{curly braces\\} have to be escaped with backslashes.")
            )
        }

        @Test
        fun `throws if StepTemplate contains two consecutive variables`() {
            expectThrows<IllegalFormatException> {
                StepTemplate.parse("Two {consecutive}{variables} cannot be separated by a matcher.")
            }
        }
    }

    @Test
    fun `aligns with scenario step descriptions`() {
        val cut = StepTemplate.parse("My name is {username}.")
        val result = cut.alignWith("My name is Peter.")
        expectThat(result).isA<Alignment>()
    }

    @Test
    fun `round-trip serializes to String`() {
        val cut = StepTemplate.parse("My name is {username}.")
        expectThat(cut.toString()).isEqualTo("My name is {username}.")
    }
}
