package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day10 : Day {
    override val day = 10

    data class Chunk(
        val openingBracket: Char,
        var closingBracket: Char? = null,
        val children: MutableList<Chunk> = mutableListOf()
    ) {
        fun isValidItself() = when {
            closingBracket == null -> true
            openingBracket == '(' && closingBracket == ')' -> true
            openingBracket == '{' && closingBracket == '}' -> true
            openingBracket == '[' && closingBracket == ']' -> true
            openingBracket == '<' && closingBracket == '>' -> true
            else -> false
        }

        fun hasValidTree(): Boolean = isValidItself() && children.all { it.hasValidTree() }
    }

    override fun solvePart1() {
        val illegals = mutableMapOf<Char, Int>()
        loadInput()
            .map {
                it.toCharArray()
                    .fold(listOf<Chunk>()) { stack, cur ->
                        val last = stack.lastOrNull()
                        if (cur in listOf('(', '{', '[', '<')) {
                            val next = Chunk(cur)
                            last?.children?.add(next)
                            stack + next
                        } else {
                            last!!.closingBracket = cur
                            if (!last.isValidItself()) {
                                illegals[cur] = illegals.getOrDefault(
                                    cur, 0
                                ) + 1
                            }

                            stack.dropLast(1)
                        }
                    }
            }
        (
            ((illegals[')'] ?: 0) * 3) +
                ((illegals[']'] ?: 0) * 57) +
                ((illegals['}'] ?: 0) * 1197) +
                ((illegals['>'] ?: 0) * 25137)
            )
            .solution(1)
    }

    fun Chunk.getCompletionString(): String {
        return if (this.closingBracket != null) ""
        else children.fold("") { acc, cur -> acc + cur.getCompletionString() } + when (this.openingBracket) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            else -> error("invalid opening bracket! $this")
        }
    }

    override fun solvePart2() {
        loadInput()
            .mapNotNull {
                it.toCharArray()
                    .fold(listOf<Chunk>()) { stack, cur ->
                        val last = stack.lastOrNull()
                        if (cur in listOf('(', '{', '[', '<')) {
                            val next = Chunk(cur)
                            last?.children?.add(next)
                            stack + next
                        } else {
                            last!!.closingBracket = cur
                            if (!last.isValidItself()) {
                                return@mapNotNull null
                            }

                            stack.dropLast(1)
                        }
                    }
            }
            .mapNotNull { it.first() }
            .map { it.getCompletionString() }
            .map {
                it.fold(0L) { acc, cur ->
                    acc * 5L + when (cur) {
                        ')' -> 1L
                        ']' -> 2L
                        '}' -> 3L
                        '>' -> 4L
                        else -> error("wrong closing bracket $cur")
                    }
                }
            }
            .sorted()
            .print("all scores")
            .let { it[it.size / 2] }
            .solution(2)
    }
}

fun main() = solve<Day10>()
