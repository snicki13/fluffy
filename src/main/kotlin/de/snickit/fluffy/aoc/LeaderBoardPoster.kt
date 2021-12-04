package de.snickit.fluffy.aoc

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.koin.core.component.KoinComponent
import java.time.LocalDateTime

class LeaderBoardPoster: KoinComponent {

    private val insults = listOf(
        "Immerhin bist du hübsch.",
        "Mein Code sah früher auch so aus.",
        "Wow! Das hab ich gar nicht von dir erwartet!",
        "So wenig Code! Herzberg wäre stolz auf dich!",
    )

    private val session = System.getenv("AOC_SESSION")
    private val ownerId = System.getenv("AOC_OWNER")
    private fun currentEvent() = LocalDateTime.now().let { if (it.monthValue < 12) it.year - 1 else it.year }.toString()

    fun postLeaderboard(event: MessageReceivedEvent, commandTokens: List<String>, channelMembers: List<Member>) {

        val leaderboard = AocLeaderboardBuilder.getLeaderboard(
            event = commandTokens.getOrElse(0) { currentEvent() },
            ownerId = commandTokens.getOrElse(1) { ownerId },
            session = commandTokens.getOrElse(2) { session }
        )
        event.channel.sendMessage(leaderboard.toString()).queue()
        event.channel.sendMessage("${channelMembers.random().asMention} ${insults.random()}").queue()
    }

}
