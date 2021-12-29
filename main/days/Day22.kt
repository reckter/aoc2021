package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.allPairings
import me.reckter.aoc.cords.d3.Cord3D
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.lang.Integer.max
import kotlin.math.min

class Day22 : Day {
    override val day = 22

    data class Operation(
        val operation: Operation,
        val min: Cord3D<Int>,
        val max: Cord3D<Int>,
    ) {
        enum class Operation {
            on,
            off
        }
    }

    fun Map<Cord3D<Int>, Boolean>.apply(operation: Operation): Map<Cord3D<Int>, Boolean> {
        return this + (operation.min.x..operation.max.x).flatMap { x ->
            (operation.min.y..operation.max.y).flatMap { y ->
                (operation.min.z..operation.max.z).map { z ->
                    Cord3D(x, y, z)
                }
            }
        }
            .map {
                it to (operation.operation == Operation.Operation.on)
            }
    }

    fun overlap(from: Operation, to: Operation): Pair<Cord3D<Int>, Cord3D<Int>>? {
        if (from.min.x > to.max.x || from.min.y > to.max.y || from.min.z > to.max.z) return null
        if (to.min.x > from.max.x || to.min.y > from.max.y || to.min.z > from.max.z) return null

        // we have a definitie overlap somewhere on some axis

        val minOverlap = Cord3D(
            max(from.min.x, to.min.x),
            max(from.min.y, to.min.y),
            max(from.min.z, to.min.z),
        )

        val maxOverlap = Cord3D(
            min(from.max.x, to.max.x),
            min(from.max.y, to.max.y),
            min(from.max.z, to.max.z),
        )


        return minOverlap to maxOverlap
    }

    override fun solvePart1() {
        val ops = loadInput()
            .parseWithRegex("(.*?) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
            .map { (opStr, xMinStr, xMaxStr, yMinStr, yMaxStr, zMinStr, zMaxStr) ->
                Operation(
                    operation = if (opStr == "on") Operation.Operation.on else Operation.Operation.off,
                    min = Cord3D(xMinStr.toInt(), yMinStr.toInt(), zMinStr.toInt()),
                    max = Cord3D(xMaxStr.toInt(), yMaxStr.toInt(), zMaxStr.toInt()),
                )
            }

        val finished = ops
            .asSequence()
            .filter { it.min.x <= 50 }
            .filter { it.min.y <= 50 }
            .filter { it.min.z <= 50 }
            .filter { it.max.x >= -50 }
            .filter { it.max.y >= -50 }
            .filter { it.max.z >= -50 }
            .fold(emptyMap<Cord3D<Int>, Boolean>()) { map, op ->
                map.apply(op)
            }

        finished
            .filter { it.key.x in -50..50 }
            .filter { it.key.y in -50..50 }
            .filter { it.key.z in -50..50 }
            .filter { it.value }
            .count()
            .solution(1)
    }

    fun extractPart(existing: Operation, overlap: Pair<Cord3D<Int>, Cord3D<Int>>): List<Operation> {
        val parts = listOf(
            // big rectangles
            existing.min to Cord3D(existing.max.x, overlap.first.y - 1, existing.max.z),
            Cord3D(existing.min.x, overlap.second.y + 1, existing.min.z) to existing.max,

            // small rectangles
            Cord3D(existing.min.x, overlap.first.y, existing.min.z) to Cord3D(
                overlap.first.x - 1,
                overlap.second.y,
                existing.max.z
            ),
            Cord3D(overlap.second.x + 1, overlap.first.y, existing.min.z) to Cord3D(
                existing.max.x,
                overlap.second.y,
                existing.max.z
            ),

            // left overs
            Cord3D(overlap.first.x, overlap.first.y, existing.min.z) to Cord3D(
                overlap.second.x,
                overlap.second.y,
                overlap.first.z - 1
            ),
            Cord3D(overlap.first.x, overlap.first.y, overlap.second.z + 1) to Cord3D(
                overlap.second.x,
                overlap.second.y,
                existing.max.z
            )
        )
            .filter { it.first.x <= it.second.x }
            .filter { it.first.y <= it.second.y }
            .filter { it.first.z <= it.second.z }


        return parts.map {
            Operation(existing.operation, it.first, it.second)
        }
    }

    fun fixOverlaps(existing: List<Operation>, adding: Operation): List<Operation> {
        val withoutAdding = existing
            .flatMap {
                val overlap = overlap(it, adding)
                if (overlap == null) {
                    listOf(it)
                } else {
                    extractPart(it, overlap)
                }
            }

        return if (adding.operation == Operation.Operation.on) {
            withoutAdding + adding
        } else {
            withoutAdding
        }
    }

    fun Operation.area(): Long {
        return (if (operation == Operation.Operation.on) 1L else -1L) * (max.x - min.x + 1).toLong() * (max.y - min.y + 1).toLong() * (max.z - min.z + 1).toLong()
    }

    override fun solvePart2() {
        val initOps = loadInput()
            .parseWithRegex("(.*?) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
            .map { (opStr, xMinStr, xMaxStr, yMinStr, yMaxStr, zMinStr, zMaxStr) ->
                Operation(
                    operation = if (opStr == "on") Operation.Operation.on else Operation.Operation.off,
                    min = Cord3D(xMinStr.toInt(), yMinStr.toInt(), zMinStr.toInt()),
                    max = Cord3D(xMaxStr.toInt(), yMaxStr.toInt(), zMaxStr.toInt()),
                )
            }

        val areas = (initOps).fold(emptyList<Operation>()) { counted, current ->
            fixOverlaps(counted, current)
        }

        areas
            .sumOf { it.area() }
            .solution(2)
    }
}

fun main() = solve<Day22>()
