package com.example.snaphunt.ui.screens.profile.badge

import com.example.snaphunt.data.user.UserChallengeItem
import com.example.snaphunt.image_recognition.ObjectCategory

class BadgeEvaluator {

    fun calculateUnlockedBadges(photos: List<UserChallengeItem>): Map<BadgeType, Boolean> {
        val validLabels = photos.filter { it.success }.mapNotNull { it.aiLabel?.lowercase()?.trim() }.toSet()

        val hasAtLeastOnePhoto = photos.isNotEmpty()

        val techKeywords = listOf(
            ObjectCategory.TV, ObjectCategory.LAPTOP, ObjectCategory.MOUSE, ObjectCategory.REMOTE,
            ObjectCategory.KEYBOARD, ObjectCategory.CELL_PHONE, ObjectCategory.MICROWAVE,
            ObjectCategory.OVEN, ObjectCategory.TOASTER, ObjectCategory.REFRIGERATOR, ObjectCategory.HAIR_DRIER
        ).map { it.keyword }
        val hasTech = validLabels.any { it in techKeywords }

        val animalKeywords = listOf(
            ObjectCategory.BIRD, ObjectCategory.CAT, ObjectCategory.DOG, ObjectCategory.HORSE,
            ObjectCategory.SHEEP, ObjectCategory.COW, ObjectCategory.ELEPHANT, ObjectCategory.BEAR,
            ObjectCategory.ZEBRA, ObjectCategory.GIRAFFE
        ).map { it.keyword }
        val hasAnimal = validLabels.any { it in animalKeywords }

        val hasHighConfidence = photos.any { it.success && (it.aiConfidence?.toDouble() ?: 0.0) > 0.80 }

        val urbanKeywords = listOf(
            ObjectCategory.BICYCLE, ObjectCategory.CAR, ObjectCategory.MOTORCYCLE, ObjectCategory.BUS,
            ObjectCategory.TRAIN, ObjectCategory.TRUCK, ObjectCategory.TRAFFIC_LIGHT,
            ObjectCategory.FIRE_HYDRANT, ObjectCategory.STOP_SIGN, ObjectCategory.PARKING_METER, ObjectCategory.BENCH
        ).map { it.keyword }
        val hasUrban = validLabels.any { it in urbanKeywords }

        val completedChallengesCount = photos.count { it.success }
        val hasFifteenChallenges = completedChallengesCount >= 15

        return mapOf(
            BadgeType.FIRST_PHOTO to hasAtLeastOnePhoto,
            BadgeType.TECH_EXPERT to hasTech,
            BadgeType.ANIMAL_LOVER to hasAnimal,
            BadgeType.HIGH_CONFIDENCE to hasHighConfidence,
            BadgeType.URBAN_EXPLORER to hasUrban,
            BadgeType.CHALLENGE_MASTER to hasFifteenChallenges
        )
    }
}