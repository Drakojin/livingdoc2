package org.livingdoc.engine.algo

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DamerauLevenshteinTest {

    private val cut = DamerauLevenshtein()

    @ParameterizedTest
    @ValueSource(strings = ["", "a", "LivingDoc"])
    fun identicalStringsHaveDistanceOfZero(a: String) {
        expectThat(cut.distance(a, a)).isEqualTo(0)
    }

    @Test
    fun insertionsAndDeletionsAreCountedCorrectly() {
        expect {
            that(cut.distance("", "LivingDoc")).isEqualTo(9)
            that(cut.distance("LivingDoc", "")).isEqualTo(9)
            that(cut.distance("Living", "LivingDoc")).isEqualTo(3)
            that(cut.distance("LivingDoc", "Living")).isEqualTo(3)
        }
    }

    @Test
    fun substitutionsAreCountedCorrectly() {
        expect {
            that(cut.distance("Test-It", "Tast-It")).isEqualTo(1)
            that(cut.distance("Test-It", "Tast-Et")).isEqualTo(2)
        }
    }

    @Test
    fun swappingAdjacentCharactersCountsOnlyOnce() {
        expect {
            that(cut.distance("LivingDoc", "LviingDoc")).isEqualTo(1)
            that(cut.distance("LivingDoc", "LviingoDc")).isEqualTo(2)
        }
    }

    @Test
    fun returnsCutoffDistanceWhenDistanceIsTooLarge() {
        expectThat(
            cut.distance(
                "he adds '{}' to his shopping cart",
                "the user '{}' is logged into the shop"
            )
        ).isEqualTo(25)
        val withCutoffDistance = DamerauLevenshtein(cutoffDistance = 12)
        expectThat(
            withCutoffDistance.distance(
                "he adds '{}' to his shopping cart",
                "the user '{}' is logged into the shop"
            )
        ).isEqualTo(12)
    }

    @Test
    fun canConfigureWeightsOfInsertionsAndDeletions() {
        expectThat(cut.distance("Living", "ingDoc")).isEqualTo(6)
        val withExpensiveInsertionsAndDeletions = DamerauLevenshtein(weightDeletion = 2, weightInsertion = 2)
        expectThat(withExpensiveInsertionsAndDeletions.distance("Living", "ingDoc")).isEqualTo(6)
    }

    @Test
    fun canConfigureWeightOfSubstitutions() {
        expectThat(cut.distance("Living", "Loving")).isEqualTo(1)
        val withExpensiveSubstitutions = DamerauLevenshtein(
            weightDeletion = 3,
            weightInsertion = 3,
            weightSubstitution = 2
        )
        expectThat(withExpensiveSubstitutions.distance("Living", "Loving")).isEqualTo(2)
    }

    @Test
    fun canConfigureWeightOfTranspositions() {
        expectThat(cut.distance("Living", "Livign")).isEqualTo(1)
        val withExpensiveTranspositions = DamerauLevenshtein(
            weightDeletion = 3,
            weightInsertion = 3,
            weightSubstitution = 3,
            weightTransposition = 2
        )
        expectThat(withExpensiveTranspositions.distance("Living", "Livign")).isEqualTo(2)
    }
}
