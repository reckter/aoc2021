package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day14 : Day {
    override val day = 14

    val starter by lazy {
        loadInput()
            .first()
    }

    val rules by lazy {
        loadInput()
            .drop(1)
            .parseWithRegex("(.)(.) -> (.)")
            .map { (first, second, insert) ->
                (first[0] to second[0]) to insert[0]
            }
    }

    fun onePolyStep(poly: List<Char>): List<Char> {
        return (poly.windowed(2)
            .flatMap { (current, next) ->
                val rule = rules
                    .find { (rule, replace) ->
                        rule.first == current && rule.second == next
                    }

                if (rule == null) {
                    listOf(current)
                } else {
                    listOf(current, rule.second)
                }
            }
                + poly.last())
    }

    override fun solvePart1() {
        getValueOfString(starter.toCharArray().toList(), 10)
            .entries
            .sortedBy { it.value }
            .let { it.last().value - it.first().value }
            .solution(1)
    }

    fun Map<Char, Long>.plus(other: Map<Char, Long>): Map<Char, Long> {
        return (this.keys + other.keys)
            .map { it to (this.getOrDefault(it, 0L) + other.getOrDefault(it, 0L)) }
            .toMap()
    }

    val cache = mutableMapOf<Pair<List<Char>, Int>, Map<Char, Long>>()

    fun getValueOfString(input: List<Char>, level: Int = 40): Map<Char, Long> {

        if((input to level) in cache) {
            return cache[input to level]!!
        }

        if (level == 0) {
            return input.groupBy { it }
                .mapValues { it.value.size.toLong() }
        }

        val count = input
            .windowed(2)
            .map {
                val next = onePolyStep(it)
                val count = next
                    .windowed(2)
                    .map {
                        val nextLevel = level - 1
                        val value = getValueOfString(it, nextLevel)
                        cache[it to  nextLevel] = value

                        value

                    }
                    .reduce { acc, cur -> acc.plus(cur) }
                    .plus(mapOf(next[1] to -1))

                count
            }
            .reduce { acc, cur -> acc.plus(cur) }

        return count.plus(input.drop(1).dropLast(1).groupBy { it }.mapValues { -it.value.size.toLong() })
    }

    override fun solvePart2() {
        getValueOfString(starter.toCharArray().toList())
            .entries
            .sortedBy { it.value }
            .let { it.last().value - it.first().value }
            .solution(1)
    }
}

fun main() = solve<Day14>()
