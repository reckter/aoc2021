package me.reckter.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class PermutationsTest {

    @Test
    fun `should correctly calculate all permutations`() {
        val input = listOf(1, 2, 3)
        val permutations = input.permutations()

        assertTrue("Should correctly calculate all permutations, got ${permutations.toList()}") {
            permutations.toList() == listOf(
                listOf(1, 2, 3),
                listOf(2, 1, 3),
                listOf(2, 3, 1),

                listOf(1, 3, 2),
                listOf(3, 1, 2),
                listOf(3, 2, 1)
            )
        }
    }
}
