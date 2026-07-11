package com.example.snaphunt.graphs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaphunt.data.user.UserChallengeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.format.TextStyle
import java.util.Locale

class GraphsViewModel : ViewModel() {

    private val _rawChallenges = MutableStateFlow<List<UserChallengeItem>>(emptyList())

    fun updateChallenges(challenges: List<UserChallengeItem>) {
        _rawChallenges.value = challenges
    }

    val weeklyPoints: StateFlow<List<Pair<String, Float>>> = _rawChallenges
        .map { list ->
            val oggi = LocalDate.now()
            val lunediCorrente = oggi.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val domenicaCorrente = oggi.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            val giorniMappa = linkedMapOf(
                "Mon" to 0f, "Tue" to 0f, "Wed" to 0f, "Thu" to 0f, "Fri" to 0f, "Sat" to 0f, "Sun" to 0f
            )

            list.forEach { sfida ->
                try {
                    val dataSfida = LocalDate.parse(sfida.createdAt.substring(0, 10))
                    if (!dataSfida.isBefore(lunediCorrente) && !dataSfida.isAfter(domenicaCorrente)) {
                        val nomeGiorno = dataSfida.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .replaceFirstChar { it.uppercase() }.take(3)

                        if (giorniMappa.containsKey(nomeGiorno)) {
                            giorniMappa[nomeGiorno] = giorniMappa[nomeGiorno]!! + sfida.points
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            giorniMappa.toList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf("Mon" to 0f, "Tue" to 0f, "Wed" to 0f, "Thu" to 0f, "Fri" to 0f, "Sat" to 0f, "Sun" to 0f)
        )
}