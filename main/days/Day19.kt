package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.allPairings
import me.reckter.aoc.cords.d3.Cord3D
import me.reckter.aoc.cords.d3.manhattenDistance
import me.reckter.aoc.cords.d3.plus
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAt

typealias Cord = Cord3D<Int>

private fun Cord.rotate(): Cord = Cord(y, z, x)

class Day19 : Day {
    override val day = 19

    operator fun Cord.minus(other: Cord): Cord = Cord(x - other.x, y - other.y, z - other.z)

    val headings: List<(Cord) -> Cord> = listOf(
        { it },
        { it.rotate() },
        { it.rotate().rotate() },
    )

    val rotations: List<(Cord) -> Cord> = listOf(
        { it },
        { Cord(it.x, it.z, -it.y) },
        { Cord(it.x, -it.y, -it.z) },
        { Cord(it.x, -it.z, it.y) },

        { Cord(-it.x, it.y, -it.z) },
        { Cord(-it.x, -it.z, -it.y) },
        { Cord(-it.x, -it.y, it.z) },
        { Cord(-it.x, it.z, it.y) },
    )

    val allDirections =
        headings
            .flatMap { heading ->
                rotations.map { rot ->
                    { cord: Cord -> rot((heading(cord))) }
                }
            }

    data class Scanner(
        val beacons: List<Cord>,
        val rotatedBeacons: List<Pair<(Cord) -> Cord, List<Map<Cord, Cord>>>>,
        val id: String,
        var position: Cord? = null,
        var foundRotation: List<Cord>? = null,
        var foundMatrix: List<Map<Cord, Cord>>? = null
    )

    fun buildMatrix(cords: List<Cord>): List<Map<Cord, Cord>> {
        return cords
            .map { base ->
                cords.associateBy { base - it }
            }
    }

    fun matches(a: Map<Cord, Cord>, b: Map<Cord, Cord>): Boolean {
        return a.keys.count { it in b } >= 12
    }

    val zero = Cord(0, 0, 0)

    fun matches(a: List<Map<Cord,Cord>>, b: List<Map<Cord,Cord>>): Pair<Cord, Cord>? {
        a
            .forEach { a ->
                val match = b
                    .find { matches(a, it) }
                    ?.get(zero)
                if (match != null) return a[zero]!! to match
            }

        return null
    }

    data class Match(
        val sameScanner: Pair<Cord, Cord>,
        val direction: (Cord) -> Cord
    )

    fun findMatch(truth: List<Map<Cord, Cord>>, needle: Scanner): Match? {
        val match = needle.rotatedBeacons
            .map {
                it to matches(truth, it.second)
            }
            .find { it.second != null } ?: return null

        val matchedBeacon = match.second!!
        val (dir, rotated) = match.first

        return Match(matchedBeacon, dir)
    }

    val scanner by lazy {
        val scanner = loadInput( trim = false)
            .splitAt { it.isEmpty() }
            .map {
                val beacons = it.drop(1)
                    .parseWithRegex("(-?\\d+),(-?\\d+),(-?\\d+)")
                    .map { (xStr, yStr, zStr) -> Cord(xStr.toInt(), yStr.toInt(), zStr.toInt()) }
                val id = "--- scanner (\\d+) ---".toRegex().matchEntire(it.first())!!.groupValues[1]
                Scanner(
                    beacons = beacons,
                    rotatedBeacons = allDirections
                        .map { dir ->
                            dir to buildMatrix(beacons.map { dir(it) })
                        },
                    id = id
                )
            }

        val queue = ArrayDeque(scanner)

        val found = mutableSetOf<Scanner>(queue.removeFirst().let {
            it.position = Cord(0, 0, 0)
            it.foundRotation = it.beacons
            it.foundMatrix = buildMatrix(it.beacons)

            it
        })

        val checkedSets = mutableMapOf<String, Set<String>>()

        scanner.forEach { checkedSets[it.id] = setOf() }

        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()

            val match = found
                .asSequence()
                .filter { it.id !in checkedSets[next.id]!! }
                .map { findMatch(it.foundMatrix!!, next)?.let { m -> it to m } }
                .find { it != null }

            if (match == null) {
                queue.add(next)
                checkedSets[next.id] = found.map { it.id }.toSet()
                continue
            }

            val (scanner, matched) = match

            next.position =
                matched.sameScanner.first - matched.sameScanner.second
            next.foundRotation = next.beacons.map { matched.direction(it) + next.position!! }
            next.foundMatrix = buildMatrix(next.foundRotation!!)
            found.add(next)
        }
        scanner
    }
    override fun solvePart1() {
        scanner
            .flatMap { it.foundRotation ?: emptyList() }
            .distinct()
            .count()
            .solution(1)
    }

    override fun solvePart2() {
        scanner
            .map { it.position!! }
            .allPairings(includeSelf = false, bothDirections = false)
            .maxOf { (a,b) -> a.manhattenDistance(b) }
            .solution(2)
    }
}

fun main() = solve<Day19>()
