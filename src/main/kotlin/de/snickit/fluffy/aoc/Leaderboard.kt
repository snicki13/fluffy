package de.snickit.fluffy.aoc

import java.time.Instant
import java.time.ZoneId

class Leaderboard(
    val event: String,
    val ownerId: String,
    val users: List<AdventUser>
) {

    private val firstLine: String
    private val secondLine: String

    init {
        var first = "".padStart(8, ' ')
        var second = "".padStart(8, ' ')
        (1 .. 25).forEach { day ->
            first += if (day >= 10) "${day.toString()[0]} " else "  "
            second += "${day % 10} "
        }
        firstLine = first
        secondLine = second
    }

    fun users(): String = users.sortedByDescending { it.localScore }.mapIndexed { index, adventUser ->
        "%3d) $adventUser".format(index + 1)
    }.joinToString("\n")

    override fun toString(): String = """
```
$firstLine
$secondLine
${users()}
```
"""
}

class AdventUser(
    var stars: Int = 0,
    var localScore: Int = 0,
    var id: String = "",
    val completionDayLevel: Map<Int, CompletionDayLevel> = mutableMapOf(),
    lastStarTs: Long = 0L,
    var name: String = "",
    var globalScore: Int = 0
) {
    val lastStarTs = Instant.ofEpochSecond(lastStarTs).atZone(ZoneId.systemDefault())

    private fun printStars(): String =
        (1..25).map {
            completionDayLevel.getOrDefault(it, CompletionDayLevel())
        }.joinToString(" ")

    override fun toString(): String = "%2d ${printStars()} $name".format(localScore)
}

class CompletionDayLevel(
    val challenge: Int = 0,
    val tsLevel1: Long = 0L,
    val tsLevel2: Long = 0L
) {
    //val tsLevel1 = Instant.ofEpochSecond(tsLevel1).atZone(ZoneId.systemDefault())
    //val tsLevel2 = Instant.ofEpochSecond(tsLevel2).atZone(ZoneId.systemDefault())

    private fun level(): String =
        if (tsLevel2 != 0L) "★" //"🌟"
        else if (tsLevel1 != 0L) "☆" //"⭐️"
        else "☓︎"

    override fun toString(): String = level()
}
