package com.keepcoding.blehealth.data.mock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HealthValidatorTest {
    @Test
    fun validValuesAreKept() {
        assertEquals(72, HealthValidator.heartRate(72))
        assertEquals(98, HealthValidator.bloodOxygen(98))
        assertEquals(6_420, HealthValidator.steps(6_420))
        assertEquals(438, HealthValidator.sleepMinutes(438))
    }

    @Test
    fun invalidValuesAreRejected() {
        assertNull(HealthValidator.heartRate(221))
        assertNull(HealthValidator.bloodOxygen(69))
        assertNull(HealthValidator.steps(-1))
        assertNull(HealthValidator.sleepMinutes(1_441))
    }
}
