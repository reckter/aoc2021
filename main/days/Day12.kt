package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day12 : Day {
    override val day = 12

    data class Cave(
        val isSmall: Boolean,
        val name: String,
        val connectedTo: MutableList<Cave> = mutableListOf()
    )

    private val caves by lazy {
        val caves = mutableMapOf<String, Cave>()
        loadInput()
            .parseWithRegex("(.*?)-(.*?)")
            .map { (first, second) ->
                val firstCave = caves.getOrPut(first) { Cave(first[0].isLowerCase(), first) }
                val secondCave = caves.getOrPut(second) { Cave(second[0].isLowerCase(), second) }
                firstCave.connectedTo.add(secondCave)
                secondCave.connectedTo.add(firstCave)
            }
        caves
    }

    private fun findAllPath(path: List<Cave>, allowASingleSmallCave: Boolean = false): List<List<Cave>> {
        if (path.last().name == "end") return listOf(path)
        return path.last()
            .connectedTo
            .filter { it.name != "start" }
            .filter { !it.isSmall || it !in path || allowASingleSmallCave }
            .flatMap {
                findAllPath(
                    path + it,
                    allowASingleSmallCave && (!it.isSmall || it !in path)
                )
            }
    }

    override fun solvePart1() {
        findAllPath(listOf(caves["start"]!!))
            .count()
            .solution(1)
    }

    override fun solvePart2() {
        findAllPath(listOf(caves["start"]!!), true)
            .count()
            .solution(1)
    }
}

fun main() = solve<Day12>()
