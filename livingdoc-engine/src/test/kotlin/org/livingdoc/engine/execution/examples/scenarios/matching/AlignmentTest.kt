package org.livingdoc.engine.execution.examples.scenarios.matching

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isNotNull
import strikt.assertions.isTrue
import java.lang.String.format

class AlignmentTest {

    @Nested inner class `given a StepTemplate without variables` {

        @Test fun `aligns perfectly matching step`() {
            val alignment = align("Elvis has left the building.", "Elvis has left the building.")
            expectThat(alignment)
                .hasNoVariables()
                .alignsAs(
                    "Elvis has left the building.",
                    "Elvis has left the building."
                )
        }

        @Test fun `aligns similar step`() {
            val alignment = align("Elvis has entered the building.", "Peter has left the building.")
            expectThat(alignment)
                .hasNoVariables()
                .alignsAs(
                    "Elvis has -entered the building.",
                    "Peter has left---- the building."
                )
        }

        @Test fun `aligns empty string`() {
            val alignment = align("Elvis likes pizza.", "")
            expectThat(alignment)
                .hasNoVariables()
                .alignsAs(
                    "Elvis likes pizza.",
                    "------------------"
                )
        }
    }

    @Nested inner class `given a StepTemplate with variables` {

        @Test fun `extracts value from perfectly matching step`() {
            val alignment = align("User {username} has entered the building.", "User Peter has entered the building.")
            expectThat(alignment).hasVariables("username" to "Peter")
        }

        @Test fun `extracts value from step with slightly different text fragments`() {
            val alignment = align("User {user} has entered the building.", "A user Peter has left the building.")
            expectThat(alignment).hasVariables("user" to "Peter")
        }

        @Test fun `extracts value from step when the variable is the first part of the template`() {
            val alignment = align("{username} has entered the building.", "Peter has left the building.")
            expectThat(alignment).hasVariables("username" to "Peter")
        }

        @Test fun `extracts all of the values`() {
            val alignment = align("{username} has {action} the {object}.", "Peter has left the building.")
            expectThat(alignment).hasVariables(
                "username" to "Peter",
                "action" to "left",
                "object" to "building"
            )
        }

        @Test fun `extracts empty string as value from perfectly matching step`() {
            val alignment = align("My name is {username}.", "My name is .")
            expectThat(alignment)
                .hasVariables("username" to "")
                .alignsAs(
                    "My name is X.",
                    "My name is -."
                )
        }
    }

    @Test fun `given no matching StepTemplate, it is misaligned`() {
        val alignment = align(
            "Elvis left the building and this is a really long sentence that doesn't align with the next one at all.",
            "Peter likes pizza."
        )

        expectThat(alignment)
            .isMisaligned()
            .alignsAs(
                "Elvis left the building and this is a really long sentence that doesn't align with the next one at all.",
                "Peter likes pizza."
            )
            .hasNoVariables()
        expectThat(alignment.maxCost <= alignment.totalCost).isTrue()
    }

    @Nested inner class `given a StepTemplate with quotation characters` {

        @Test fun `extracts variable from perfectly matching step`() {
            val alignment = alignWithQuotationCharacters("Peter likes '{stuff}'.", "Peter likes 'Pizza'.")
            expectThat(alignment).hasVariables("stuff" to "Pizza")
        }

        @Test fun `extracts variable with adjacent insertion`() {
            val alignment =
                alignWithQuotationCharacters("'{user}' likes '{stuff}'.", "'Peter' likes delicious 'Pizza'.")
            expectThat(alignment).hasVariables("user" to "Peter", "stuff" to "Pizza")
        }

        @Test fun `extracts from quoted and unquoted variables in the same template`() {
            val alignment = alignWithQuotationCharacters("{user} likes '{stuff}'.", "Peter likes delicious 'Pizza'.")
            expectThat(alignment).hasVariables("user" to "Peter", "stuff" to "Pizza")
        }

        @Test fun `is misaligned, if a quotation character is missing in the step`() {
            val alignment = alignWithQuotationCharacters(
                "Peter does not like '{stuff}'.",
                "Peter does not like missing punctuation marks'."
            )
            expectThat(alignment).isMisaligned()
        }

        @Test fun `extracted variables do not contain quotation characters`() {
            val alignment = alignWithQuotationCharacters(
                "Peter does not like '{stuff}'.",
                "Peter does not like ''unnecessary quotation marks'."
            )
            expectThat(alignment.variables["stuff"]).isNotNull().not().contains("'")
            expectThat(alignment).hasVariables("stuff" to "unnecessary quotation marks")
        }

        private fun alignWithQuotationCharacters(templateString: String, step: String): Alignment {
            return Alignment(
                StepTemplate.parse(templateString, quotationCharacters = setOf('\'')),
                step,
                maxCost = maxDistance
            )
        }
    }

    private fun align(templateString: String, step: String): Alignment {
        return Alignment(StepTemplate.parse(templateString), step, maxCost = maxDistance)
    }

    private val maxDistance = 40 // allow for 20 insertions (a bit much, but useful for the examples in this test)
}

private fun Assertion.Builder<Alignment>.isMisaligned(): Assertion.Builder<Alignment> =
    assertThat("is misaligned") {
        it.isMisaligned()
    }

private fun Assertion.Builder<Alignment>.alignsAs(template: String, step: String): Assertion.Builder<Alignment> =
    assert("aligns as\n\t(template) $template\n\t(step) $step") {
        if (it.alignedStrings == Pair(template, step)) {
            pass()
        } else {
            val reason = when {
                it.alignedStrings.first != template -> "the template is aligned differently"
                it.alignedStrings.second != step -> "the step is aligned differently"
                else -> "both template and step are aligned differently"
            }
            fail(
                description = String.format(
                    "Expected alignment\n\t(template) %s\n\t    (step) %s\n" +
                            "to be equal to\n\t(template) %s\n\t    (step) %s\nbut %s.",
                    it.alignedStrings.first, it.alignedStrings.second, template, step, reason
                ),
                actual = it
            )
        }
    }

private fun formatVariablesX(variables: Map<String, String>) =
    variables.asIterable().joinToString(separator = "\n\t", prefix = "\t") { "${it.key} = ${it.value}" }

private fun Assertion.Builder<Alignment>.hasNoVariables(): Assertion.Builder<Alignment> =
    assert("has no variables") {
        if (it.variables.isEmpty()) {
            pass()
        } else {
            fail(
                description = "Expected alignment with no variables, but there are:\n${formatVariablesX(it.variables)}"
            )
        }
    }

private fun Assertion.Builder<Alignment>.hasVariables(vararg variables: Pair<String, String>): Assertion.Builder<Alignment> =
    assert("has variables $variables") { actual ->
        val variablesToValues = variables.toMap()
        val missingVariables = variablesToValues.keys.subtract(actual.variables.keys)
        val unexpectedVariables = actual.variables.keys.subtract(variablesToValues.keys)
        val wrongValues = variablesToValues.keys
            .intersect(actual.variables.keys)
            .filter { variablesToValues[it] != actual.variables[it] }

        if (missingVariables.isNotEmpty() || unexpectedVariables.isNotEmpty() || wrongValues.isNotEmpty()) {
            val reasons = mutableListOf<String>()
            if (missingVariables.isNotEmpty())
                reasons.add("it is missing " + missingVariables.joinToString { "'$it'" })
            if (unexpectedVariables.isNotEmpty())
                reasons.add("it unexpectedly also yields " + unexpectedVariables.joinToString { "'$it'" })
            if (wrongValues.isNotEmpty())
                wrongValues.forEach {
                    reasons.add("the value of '$it' was '${actual.variables[it]}', not '${variablesToValues[it]}'")
                }

            val description = if (actual.variables.isEmpty())
                "with no variables "
            else
                String.format("yielding the variables\n%s\n", formatVariablesX(actual.variables))
            fail(
                description = format(
                    "Expected alignment %sto yield\n%s\nbut %s.",
                    description,
                    formatVariablesX(variablesToValues),
                    reasons.joinToString(separator = "\n\tand ")),
                actual = actual
            )
        }
        pass()
    }
