package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day9 : Day {
    override val day = 9

    val map by lazy {
        loadInput()
            .mapIndexed { y, row ->
                row.toCharArray().mapIndexed { x, c ->
                    Cord2D(x, y) to c.toString().toInt()
                }
            }
            .flatten()
            .toMap()
    }

    override fun solvePart1() {

        map
            .filter { (cord, height) ->
                cord.getNeighbors(true)
                    .mapNotNull { map[it] }
                    .all { height < it }
            }
            .values
            .sumOf { it + 1 }
            .solution(1)
    }

    override fun solvePart2() {
        val basins = map
                .filter { (cord, height) ->
                    cord.getNeighbors(true)
                        .mapNotNull { map[it] }
                        .all { height < it }
                }
                .map { mutableSetOf(it.key) }
        map
            .filter { it.value != 9 }
            .forEach { (cord, height) ->
            var currentCord = cord

            while(true) {
                val foundBasin = basins
                    .find { currentCord in it }

                if(foundBasin != null) {
                    foundBasin.add(cord)
                    return@forEach
                }
                currentCord = currentCord.getNeighbors(true)
                    .minByOrNull { map[it] ?: 100000  }
                    ?: error("flows no where!")

            }
        }
        basins
            .map { it.size }
            .sortedDescending()
            .take(3)
            .reduce { acc, it -> acc * it}
            .solution(2)
    }
}

fun main() = solve<Day9>()
