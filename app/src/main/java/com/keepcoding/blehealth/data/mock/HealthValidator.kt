package com.keepcoding.blehealth.data.mock

object HealthValidator {
    fun heartRate(value: Int?): Int? = value?.takeIf { it in 30..220 }
    fun bloodOxygen(value: Int?): Int? = value?.takeIf { it in 70..100 }
    fun steps(value: Int?): Int? = value?.takeIf { it in 0..100_000 }
    fun sleepMinutes(value: Int?): Int? = value?.takeIf { it in 0..1_440 }
}
