package com.example.snaphunt.data.repositories.points_multiplier

import com.example.snaphunt.data.user.UserChallengeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class PointsMultiplierRepository {

    private val _rawChallenges = MutableStateFlow<List<UserChallengeItem>>(emptyList())
    val challenges: Flow<List<UserChallengeItem>> = _rawChallenges

    fun emitChallenges(list: List<UserChallengeItem>) {
        _rawChallenges.value = list
    }

    val weeklyMultiplier: Flow<Int> = _rawChallenges
        .map { list ->
            val today = LocalDate.now()
            val currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val currentSunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            val activeDates = list.mapNotNull { challenge ->
                try {
                    LocalDate.parse(challenge.createdAt.substring(0, 10))
                } catch (e: Exception) {
                    null
                }
            }.filter { data ->
                !data.isBefore(currentMonday) && !data.isAfter(currentSunday)
            }.toSet()

            var streak = 0

            if (activeDates.contains(today)) {
                var verifyDate = today
                while (activeDates.contains(verifyDate) && !verifyDate.isBefore(currentMonday)) {
                    streak++
                    verifyDate = verifyDate.minusDays(1)
                }
            }

            else {
                val yesterday = today.minusDays(1)
                var verifyDate = yesterday
                while (activeDates.contains(verifyDate) && !verifyDate.isBefore(currentMonday)) {
                    streak++
                    verifyDate = verifyDate.minusDays(1)
                }
                if (streak > 0) {
                    streak += 1
                }
            }

            val finalMultiplier = if (streak == 0) 1 else streak
            finalMultiplier.coerceIn(1, 7)
        }
}