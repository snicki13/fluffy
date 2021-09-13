package de.snickit.fluffy

import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import java.time.LocalDateTime

object Utils {

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


    fun assignCategory(categoryName: String, guildChannel: GuildChannel) {
        val guild = guildChannel.guild
        val cats = guild.getCategoriesByName(categoryName, true)
        if (cats.size != 1) {
            throw Exception("Category archiv does not exist or is ambivalent!")
        }
        guildChannel.manager.setParent(cats.first()).queue()
    }

    fun addMembersToChannel(guildChannel: GuildChannel, channelMembers: List<Member>) {
        /*for(member: Member in channelMembers){
            guildChannel.putPermissionOverride(member)
        }*/
        //TODO complete
    }
}