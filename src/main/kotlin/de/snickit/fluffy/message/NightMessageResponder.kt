package de.snickit.fluffy.message

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import org.koin.core.component.KoinComponent
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class NightMessageResponder: KoinComponent, MessageResponder {

    private val channelProps = HashMap<MessageChannel, Pair<Int, ZonedDateTime>>()

    private val nightlyQuotes = listOf("Hey, es ist {} Uhr. Geh doch mal ins Bett. :dog:",
        "https://tenor.com/view/monsters-inc-james-go-to-sleep-gif-6146952",
        "knurrr, letzte Warnung!",
        "https://tenor.com/view/happy-gilmore-ben-stiller-will-you-go-to-sleep-i-will-put-you-to-sleep-choice-gif-5314293",
        "https://tenor.com/view/harry-potter-fluffy-dog-touffu-fantastic-beasts-gif-12912138")

    override fun respondToMessage(channel: MessageChannel, message: Message) {
        val messageTimestamp = message.timeCreated.atZoneSameInstant(ZoneId.of("Europe/Berlin"))
        var (counter, lastMessage) = channelProps.computeIfAbsent(channel) {
            Pair(0, ZonedDateTime.now().minusHours(1))
        }
        if (!lastMessage.toLocalDate().equals(messageTimestamp.toLocalDate())) {
            counter = 0
        }
        if (lastMessage.isAfter(messageTimestamp.minusMinutes(20))) {
            return
        }
        channel.sendMessage(nightlyQuotes[counter].replace("{}", messageTimestamp.format(DateTimeFormatter.ofPattern("HH:mm")))).queue()
        counter = (counter + 1) % nightlyQuotes.size
        lastMessage = messageTimestamp
        channelProps[channel] = Pair(counter, lastMessage)
    }


}