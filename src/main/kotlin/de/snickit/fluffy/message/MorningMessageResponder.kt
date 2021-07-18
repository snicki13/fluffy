package de.snickit.fluffy.message

import io.github.serpro69.kfaker.Faker
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class MorningMessageResponder: KoinComponent, MessageResponder {

    private val userProps = HashSet<User>()

    private val faker by inject<Faker>()

    override fun respondToMessage(channel: MessageChannel, message: Message) {
        val date = LocalDate.now()
        if (date.isBefore(LocalDate.now())) {
            userProps.clear()
        }
        val user = message.author
        if (userProps.contains(user)) {
            return
        }
        channel.sendMessage("Guten Morgen ${message.author.name} von Fluffy :dog:! " +
                "Du bist ja früh wach. Heute schon einen ${faker.coffee.variety()} getrunken? :coffee:").queue()
        userProps.add(user)
    }
}