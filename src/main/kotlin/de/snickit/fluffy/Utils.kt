package de.snickit.fluffy

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Utils {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     *01.04.XX, start des Sommersemesters
     */
    private val summerStartDay: LocalDateTime = LocalDateTime.of(0, 4, 1, 0, 0)

    /**
     *1.10, start des Wintersemesters
     */
    private val  winterStartDay: LocalDateTime = LocalDateTime.of(0, 10, 1, 0, 0)

    /**
     * Gibt an, in welcher Zeitspanne nach Semesterstart Module abgeschlossen werden können, damit Sie noch zum vorherigen Semester zählen.
     * Nötig falls z.B Blockkurse über den Semesterstart hinausgehen.
     */
    private const val gracePeriodInDays: Long = 60

    /**
     * Gibt das Semester für ein bestimmtes Datum an.
     * Notation:
     * - 20-21ws → Wintersemester 2020/2021
     * - 21ss → Sommersemester 2021
     * @param date Datum zu überprüfen
     * @return YY-YYws oder YYss, z.B 21-22ws
     */
    private fun getSemesterForDate(date: LocalDateTime): String {
        val summerSameYear = summerStartDay.withYear(date.year).plusDays(gracePeriodInDays)
        val dateFormat = DateTimeFormatter.ofPattern("yy")
        val thisYear = dateFormat.format(date)
        //vor dem z.B 01.04 des Jahres -> WS letztes/dieses Jahr
        if (date.isBefore(summerSameYear)) {
            //21-22ws
            return "${dateFormat.format(date.minusYears(1))}-${thisYear}ws"
        }

        val winterSameYear = winterStartDay.withYear(date.year).plusDays(gracePeriodInDays)

        // Alles vor winterSameYear wird zum vorherigen Sommersemester gezählt
        if (date.isBefore(winterSameYear)) {
            return "${thisYear}ss"
        }

        // Nach dem SS zählt alles ins nächste WS dieses/nächstes Jahr
        //22-23ws
        return "${thisYear}-${dateFormat.format(date.plusYears(1))}ws"
    }

    fun getCurrentSemester(): String =
        getSemesterForDate(LocalDateTime.now())


    fun assignCategory(categoryName: String, guildChannel: GuildChannel, sync: Boolean) {
        val cats = guildChannel.guild.getCategoriesByName(categoryName, true)
        if (cats.size != 1) {
            throw Exception("Category $categoryName does not exist or is ambivalent!")
        }
        val parentCat = cats.first()
        logger.info("assign category ${parentCat.name}")
        guildChannel.manager.setParent(parentCat)
        if(sync) {
            logger.info("syncing permissions with ${parentCat.name}")
            guildChannel.manager.sync(parentCat)
        }
    }

    fun addMembersToChannel(guildChannel: GuildChannel, channelMembers: List<Member>) {
        for(member: Member in channelMembers){
            logger.info("assign permissions for  ${member.effectiveName}")
            guildChannel.manager.putPermissionOverride(
                member,
                listOf(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY),
                listOf()
            )
        }
    }
}