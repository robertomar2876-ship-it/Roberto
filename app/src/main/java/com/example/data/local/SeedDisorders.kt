package com.example.data.local

import com.example.data.model.MentalDisorderEntity

object SeedDisorders {
    const val EXPECTED_COUNT = 24

    fun all(): List<MentalDisorderEntity> =
        SeedDisordersPart1.entries() + SeedDisordersPart2.entries()
}
