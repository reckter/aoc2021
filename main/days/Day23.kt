package me.reckter.aoc.days

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.reckter.aoc.Day
import me.reckter.aoc.allPairings
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.dijkstraInt
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.uppercaseAlphabet

class Day23 : Day {
    override val day = 23

    data class Amphipod(
        val position: Cord2D<Int>, val type: Char
    )

    fun Amphipod.factor() = when (type) {
        'A' -> 1
        'B' -> 10
        'C' -> 100
        'D' -> 1000
        else -> error("invalid pod: $this")
    }

    fun getRightChamber(pod: Amphipod, map: Map<Cord2D<Int>, Boolean>): List<Cord2D<Int>> {
        return when (pod.type) {
            'A' -> map.keys.filter { it.x == 3 && it.y > 1 }
            'B' -> map.keys.filter { it.x == 5 && it.y > 1 }
            'C' -> map.keys.filter { it.x == 7 && it.y > 1 }
            'D' -> map.keys.filter { it.x == 9 && it.y > 1 }
            else -> error("invalid pod: $pod")
        }
    }

    fun getAllMoves(pod: Amphipod, map: Map<Cord2D<Int>, Boolean>, otherPods: List<Amphipod>): List<Cord2D<Int>> {
        val champer = getRightChamber(pod, map)
        if (pod.position in champer) {
            // check wether any other pod is still in the correct chamber
            if(otherPods.none { it.type != pod.type && it.position in champer }) {
                return emptyList()
            }
        }

        if (pod.position.y == 1) {
            // Wr are in the hallway, the only legal positions are the ones into the right chamber
            return champer
        }
        return map.keys.filter { it.y == 1 }.filter { it.x !in listOf(3, 5, 7, 9) } + champer
    }

    val pathCache: MutableMap<Pair<Cord2D<Int>, Cord2D<Int>>, List<Cord2D<Int>>> =
        mutableMapOf()

    fun getPath(
        from: Cord2D<Int>,
        to: Cord2D<Int>,
        map: Map<Cord2D<Int>, Boolean>,
    ): List<Cord2D<Int>> {
        val key = Pair(from, to)
        if (key in pathCache) return pathCache[key]!!
        val ret =
            dijkstraInt(from,
                to,
                { it.getNeighbors(true).filter { it in map } },
                { _, _ -> 1 }).first
        pathCache[key] = ret
        return ret
    }

    val legalMovesCache: MutableMap<Pair<Amphipod, List<Amphipod>>, Sequence<Pair<Cord2D<Int>, Int>>> =
        mutableMapOf()

    fun getLegalMoves(
        pod: Amphipod, map: Map<Cord2D<Int>, Boolean>, pods: List<Amphipod>
    ): Sequence<Pair<Cord2D<Int>, Int>> {
        val allMoves = getAllMoves(pod, map, pods - pod)
        val key = Pair(pod, pods.sortedWith(Comparator.comparing<Amphipod?, Char?> { it.type }
            .thenComparingInt { it.position.x + 20 * it.position.y }))
        if (key in legalMovesCache)
            return legalMovesCache[key]!!

        val ret = allMoves
            .asSequence()
            .map {
                it to getPath(pod.position, it, map)
            }
            .filter {
                it.second.drop(1).none { pos -> pods.any { it.position == pos } }
            }
            .filter {
                it.first.y == 1 || pods.none { other -> other.type != pod.type && other.position.x == it.first.x && other.position.y > 1 }
            }
            .filter { pos -> pos.first.x != 2 || pods.none { it.position.x == pos.first.x && it.position.y > 3 && it.type != pod.type } }
            .filter { pos -> pos.first.x != 3 || pods.none { it.position.x == pos.first.x && it.position.y > 4 && it.type != pod.type } }
            .map {
                it.first to it.second.size
            }

        legalMovesCache[key] = ret
        return ret
    }

    val minCache: MutableMap<List<Amphipod>, Int?> = mutableMapOf()

    fun findMinScore(
        pods: List<Amphipod>,
        map: Map<Cord2D<Int>, Boolean>,
        currentValue: Int = 0,
        moves: Int = 0
    ): Int? {

        val key = pods.sortedWith(Comparator.comparing<Amphipod?, Char?> { it.type }
            .thenComparingInt { it.position.x + 20 * it.position.y }
        )

        if (key in minCache) {
            return minCache[key]
        }

        GlobalScope.launch {
            print(" ".repeat(moves) + ".\r")
        }
        if (moves > 100)
            return null

        if (pods.all {
                when (it.type) {
                    'A' -> it.position.x == 3
                    'B' -> it.position.x == 5
                    'C' -> it.position.x == 7
                    'D' -> it.position.x == 9
                    else -> error("invalid pod: $it")
                } && it.position.y > 1
            }) {
            return 0
        }

        val ret = pods
            .sortedByDescending { uppercaseAlphabet.indexOf(it.type) }
            .map { pod ->
                val otherPods = pods - pod
                val allMoves = getLegalMoves(pod, map, otherPods)

                allMoves
                    .map {
                        val value= pod.factor() * (it.second -1)
                        val tmp = findMinScore(
                            pods = otherPods + pod.copy(
                                position = it.first,
                            ),
                            map = map,
                            currentValue = currentValue + value,
                            moves = moves + 1
                        ) ?: return@map null

                        val ret = tmp + value
                        if (ret < 0) null else ret
                    }
                    .filterNotNull()
                    .minOrNull()
            }
            .filterNotNull()
            .minOrNull()

        minCache[key] = ret

        return ret
    }

    var min = Int.MAX_VALUE

    fun getSolution(pods: List<Amphipod>, map: Map<Cord2D<Int>, Boolean>): Int? {
        pathCache.clear()
        minCache.clear()
        legalMovesCache.clear()
        min = Int.MAX_VALUE

        print("warm cache...")

        map
            .filter { it.value }
            .keys
            .toList()
            .allPairings()
            .forEach { getPath(it.first, it.second, map) }
        println("done")

        return findMinScore(pods, map, 1000000, 0)
    }

    override fun solvePart1() {
        val map =
            loadInput().mapIndexed { y, row ->
                row.mapIndexed { x, it -> Cord2D(x, y) to !(it == '#' || it == ' ') }
            }.flatten().filter { it.second }.toMap()

        val pods = loadInput().mapIndexed { y, row ->
            row.mapIndexed { x, it -> Cord2D(x, y) to it }
        }.flatten().filter { it.second in uppercaseAlphabet }
            .map { Amphipod(it.first, it.second) }

        getSolution(pods, map)
            .solution(1)
    }

    override fun solvePart2() {
        val map =
            loadInput(2).mapIndexed { y, row ->
                row.mapIndexed { x, it -> Cord2D(x, y) to !(it == '#' || it == ' ') }
            }.flatten().filter { it.second }.toMap()

        val pods = loadInput(2).mapIndexed { y, row ->
            row.mapIndexed { x, it -> Cord2D(x, y) to it }
        }.flatten().filter { it.second in uppercaseAlphabet }
            .map { Amphipod(it.first, it.second) }

        getSolution(pods, map)
            .solution(2)
    }
}

fun main() = solve<Day23>()
