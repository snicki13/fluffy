package de.snickit.fluffy.aoc

import org.json.JSONObject
import java.time.LocalDateTime
import khttp.get

object AocLeaderboardBuilder {

    private var lastCall: LocalDateTime = LocalDateTime.MIN
    private var cache: JSONObject? = null

    fun getLeaderboard(event: String, ownerId: String, session: String): Leaderboard {
        val json = if (cache == null || lastCall.isBefore(LocalDateTime.now().minusMinutes(15L))) {
            get(
                url = "https://adventofcode.com/$event/leaderboard/private/view/$ownerId.json",
                cookies = mapOf(Pair("session", session))
            ).jsonObject.also { cache = it }
        } else cache

        val users = parseMembers(json!!.getJSONObject("members"))

        return Leaderboard(event, ownerId, users)
    }

    private fun parseMembers(members: JSONObject): List<AdventUser> =
        members.keySet().map {
            parseAdventUser(members.getJSONObject(it))
        }

    private fun parseAdventUser(member: JSONObject): AdventUser =
        AdventUser(
            name = member.getString("name"),
            localScore = member.getInt("local_score"),
            globalScore = member.getInt("global_score"),
            stars = member.getInt("stars"),
            lastStarTs = member.getInt("last_star_ts").toLong(),
            id = member.getString("id"),
            completionDayLevel = parseCompletionDayLevels(member.getJSONObject("completion_day_level"))
        )

    private fun parseCompletionDayLevels(completionDayLevel: JSONObject): Map<Int, CompletionDayLevel> =
        completionDayLevel.keySet().map { day ->
            val timestamps = completionDayLevel.getJSONObject(day)
            val tsGetStar1 = try { timestamps.getJSONObject("1").getInt("get_star_ts").toLong() } catch (e: Exception) { 0L }
            val tsGetStar2 = try { timestamps.getJSONObject("2").getInt("get_star_ts").toLong() } catch (e: Exception) { 0L }
            CompletionDayLevel(day.toInt(), tsGetStar1, tsGetStar2)
        }.sortedBy(CompletionDayLevel::challenge).associateBy { it.challenge }
}
