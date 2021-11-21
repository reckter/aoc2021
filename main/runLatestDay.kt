package me.reckter.aoc

import org.reflections.Reflections

fun main() {

    val reflections = Reflections("me.reckter")
    val latest = reflections.getSubTypesOf(Day::class.java)
        .toSet()
        .maxByOrNull { it.simpleName.removePrefix("Day").toInt() }
        ?: error("No day found")
    println("Running day ${latest.simpleName.removePrefix("Day")}:")
    solve(
        enablePartOne = true,
        enablePartTwo = true,
        clazz = latest as Class<Day>
    )
}
