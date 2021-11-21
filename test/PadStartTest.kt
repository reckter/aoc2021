package me.reckter.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class PadStartTest {

    @Test
    fun `should pad correctly`() {
        val result = listOf(1, 2)
            .padStart(5, 0)

        assertTrue("received $result") {
            result == listOf(0, 0, 0, 1, 2)
        }
    }

    @Test
    fun `should pad correctly when its the right size`() {
        val result = listOf(1, 2, 3, 4, 5)
            .padStart(5, 0)

        assertTrue("received $result") {
            result == listOf(1, 2, 3, 4, 5)
        }
    }

    @Test
    fun `should not pad, when list is longer`() {
        val result = listOf(1, 2, 3, 4)
            .padStart(2, 0)

        assertTrue("received $result") {
            result == listOf(1, 2, 3, 4)
        }
    }
}
