package me.reckter.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RotateTest {

    @Test
    fun `should correctly rotate right`() {
        assertEquals(listOf(1, 2, 3), listOf(2, 3, 1).rotateRight())
        assertEquals(listOf(1, 2, 3), listOf(2, 3, 1).rotateRight(4))
        assertEquals(listOf(1, 2, 3, 4, 5), listOf(4, 5, 1, 2, 3).rotateRight(3))

        assertEquals(listOf(1, 2, 3), listOf(1, 2, 3).rotateRight(3 * 1000))
    }

    @Test
    fun `should correctly rotate left`() {
        assertEquals(listOf(1, 2, 3), listOf(3, 1, 2).rotateLeft())
        assertEquals(listOf(1, 2, 3), listOf(3, 1, 2).rotateLeft(4))
        assertEquals(listOf(1, 2, 3, 4, 5), listOf(3, 4, 5, 1, 2).rotateLeft(3))

        assertEquals(listOf(1, 2, 3), listOf(1, 2, 3).rotateLeft(3 * 1000))
    }

    @Test
    fun `should be reverse of each other`() {
        assertEquals(listOf(1, 2, 3), listOf(1, 2, 3).rotateLeft().rotateRight())
        assertEquals(listOf(1, 2, 3), listOf(1, 2, 3).rotateLeft(2).rotateRight(2))
        assertEquals(listOf(1, 2, 3), listOf(1, 2, 3).rotateLeft(1).rotateLeft(1).rotateRight(2))
    }
}
