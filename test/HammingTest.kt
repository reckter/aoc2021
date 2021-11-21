package me.reckter.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class HammingTest {

    @Test
    fun `should return 0 on same strings`() {
        val result = hammingDistance("aohutns,.hgcuaokd.gcl,aou.htka", "aohutns,.hgcuaokd.gcl,aou.htka")
        assertTrue { result == 0 }
    }

    @Test
    fun `should return 1 on one char difference`() {
        val result = hammingDistance("a", "b")
        assertTrue { result == 1 }
    }

    @Test
    fun `should return errors correctli size strings`() {
        val result = hammingDistance("ba", "a")
        assertTrue { result == 2 }
    }
}
