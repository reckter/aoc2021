package me.reckter.aoc

import org.reflections.Reflections

class RegressionTest {

//    @ParameterizedTest
//    @MethodSource("getDays")
    fun `should still result in the same solutions`(clazz: Class<Day>) {
        Context.day = clazz.getDeclaredConstructor().newInstance().day
        Context.testMode = true

        println(Context)
        println()
        solve(enablePartOne = true, enablePartTwo = true, clazz = clazz)
    }

    companion object {

        @JvmStatic
        val days = run {
            val reflections = Reflections("me.reckter")
            reflections.getSubTypesOf(Day::class.java)
                .toSet()
                .sortedBy { it.simpleName.removePrefix("Day").toInt() }
        }
    }
}
