package com.azure.android.communication.ui.chat.implementation.ui.mock

import android.content.Context
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.implementation.redux.states.MockMessage
import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant
import java.time.LocalDateTime
import java.util.Random

class NameFaker(context: Context) {
    private val firstNames: List<String>
    private val lastNames: List<String>
    private val prefixes: List<String>
    private val suffixes: List<String>
    private val titleDescriptons: List<String>
    private val titleLevels: List<String>
    private val titleJobs: List<String>

    fun randomText(): String {
        val method = (Math.random() * 10).toInt()
        return when (method % 7) {
            0 -> firstName()
            1 -> lastName()
            2 -> fullName()
            3 -> completeName()
            4 -> prefix()
            5 -> suffix()
            6 -> title()
            else -> ""
        }
    }

    fun firstName(): String {
        return firstNames[Random().nextInt(firstNames.size)]
    }

    fun lastName(): String {
        return lastNames[Random().nextInt(lastNames.size)]
    }

    fun fullName(): String {
        return firstName() + " " + lastName()
    }

    fun completeName(): String {
        return prefix() + " " + firstName() + " " + lastName() + " " + suffix()
    }

    fun prefix(): String {
        return prefixes[Random().nextInt(prefixes.size)]
    }

    fun suffix(): String {
        return suffixes[Random().nextInt(suffixes.size)]
    }

    fun title(): String {
        return titleDescriptons[Random().nextInt(titleDescriptons.size)] +
            " " +
            titleLevels[Random().nextInt(titleLevels.size)] +
            " " +
            titleJobs[Random().nextInt(titleJobs.size)]
    }

    init {
        firstNames = context.resources.getStringArray(R.array.first_names).asList()
        lastNames = context.resources.getStringArray(R.array.last_names).asList()
        prefixes = context.resources.getStringArray(R.array.prefixes).asList()
        suffixes = context.resources.getStringArray(R.array.suffixes).asList()
        titleDescriptons = context.resources.getStringArray(R.array.title_descriptions).asList()
        titleLevels = context.resources.getStringArray(R.array.title_levels).asList()
        titleJobs = context.resources.getStringArray(R.array.title_jobs).asList()
    }
}

class YodaFaker(context: Context) {
    val messages: List<String>

    init {
        messages = context.resources.getStringArray(R.array.yoda).asList()
    }

    fun speak() = messages[Random().nextInt(messages.size)]
}

class MessageFaker(context: Context) {
    val nameFaker = NameFaker(context)
    val yoda = YodaFaker(context)
    fun generateMessages(count: Int = 30, minSelfMessages: Int = 5, minGroupedMessages: Int = 2, minMessagesPerGroup: Int = 3): List<MockMessage> {

        fun randomParticipant() = MockParticipant(
            nameFaker.fullName(),
            drawableAvatar = if ((Random().nextInt(100) < 50)) -1 else R.drawable.camera
        )

        val startTime = LocalDateTime.now().minusMinutes(count.toLong())
        val mockMessages = mutableListOf<MockMessage>()
        val selfProbab = minSelfMessages.toFloat() / count.toFloat()
        val currentUser = MockParticipant(nameFaker.fullName(), isCurrentUser = true)
        var groupedGeneratedCount = 0
        var currentGroupGenCount = 0
        for (i in 0..count) {
            val participant = if (currentGroupGenCount == 0 && Random().nextFloat() > selfProbab) randomParticipant() else currentUser
            if (participant == currentUser) {
                if (groupedGeneratedCount < minGroupedMessages && currentGroupGenCount < minMessagesPerGroup) {
                    currentGroupGenCount += 1
                } else {
                    currentGroupGenCount = 0
                    groupedGeneratedCount += 1
                }
            }
            val mockMessage = MockMessage(
                participant,
                message = yoda.speak(),
                receivedAt = startTime.plusMinutes(i.toLong())
            )
            mockMessages.add(mockMessage)
        }
        return mockMessages
    }
}
