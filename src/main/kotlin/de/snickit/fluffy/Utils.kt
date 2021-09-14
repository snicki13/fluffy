package de.snickit.fluffy

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object Utils {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val summerStartDay: LocalDateTime = LocalDateTime.of(0, 4, 1, 0, 0)

    private val  winterStartDay: LocalDateTime = LocalDateTime.of(0, 10, 1, 0, 0)

    private const val gracePeriodInDays: Long = 30

    private fun getSemesterForDate(date: LocalDateTime): String {
        val summerSameYear = summerStartDay.withYear(date.year).plusDays(gracePeriodInDays)

        if (date.isBefore(summerSameYear)) {
            return "${date.year}WS"
        }

        val winterSameYear = winterStartDay.withYear(date.year).plusDays(gracePeriodInDays)
        if (date.isBefore(winterSameYear)) {
            return "${date.year}SS"
        }

        return "${date.year + 1}WS"
    }

    fun getCurrentSemester(): String =
        getSemesterForDate(LocalDateTime.now())


    fun assignCategory(categoryName: String, guildChannel: GuildChannel, sync: Boolean) {
        val guild = guildChannel.guild
        val cats = guild.getCategoriesByName(categoryName, true)
        if (cats.size != 1) {
            throw Exception("Category $categoryName does not exist or is ambivalent!")
        }
        logger.info("assign category $categoryName")
        guildChannel.manager.setParent(cats.first()).queue()
        if(sync) guildChannel.manager.sync(cats.first()).queue()
    }

    fun addMembersToChannel(guildChannel: GuildChannel, channelMembers: List<Member>) {
        for(member: Member in channelMembers){
            logger.info("assign permissions for  ${member.effectiveName}")
            guildChannel.manager.putPermissionOverride(
                member,
                listOf(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY),
                listOf()
            ).queue()
        }
    }
}