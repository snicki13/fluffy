package de.snickit.fluffy.createModule

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.awt.Color
import java.util.function.Consumer


class CreateModuleHandler: KoinComponent {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createModule(event: MessageReceivedEvent, commandTokens: List<String>) {

        /*
        Syntax:
        /create module-name [Full Name] [Color] [emoji]
         */


        val channelName = commandTokens.first()
        var color: Color? = null
        var emoji: String? = null
        var description: String? = null

        for (item in commandTokens.drop(1)){

            if(item.startsWith(':') && item.endsWith(':')){
                emoji = item
                continue
            }

            val tmpColor = Color.getColor(item)
            if(tmpColor != null) {
                color = tmpColor
                continue
            }

            description = item
        }

        logger.info("Detected channel with name $channelName, color $color, emoji $emoji and description '$description' ")

        val moduleCategory = event.guild.getCategoriesByName("module", true).first()


        val callback =
            Consumer { role: Role ->
                val channelAction = moduleCategory.createTextChannel(channelName)
                channelAction.syncPermissionOverrides()
                channelAction.addRolePermissionOverride(role.idLong, listOf(Permission.VIEW_CHANNEL), listOf())
                if(description != null) channelAction.setTopic(description)
                channelAction.queue()
                logger.info("created channel")
                //TODO callback for emoji posting
            }

        if(event.guild.getRolesByName(channelName, true).isNotEmpty()){
            logger.info("The role does already exist")
            event.channel.sendMessage("A role of that channel name does already exist.").queue()
            return
        }
        val roleAction = event.guild.createRole()
        if(color != null)
            roleAction.setColor(color)
        roleAction.setMentionable(true)
            .setName(channelName)
            .setHoisted(false)
            .queue(callback)
        logger.info("Created role")




    }

}