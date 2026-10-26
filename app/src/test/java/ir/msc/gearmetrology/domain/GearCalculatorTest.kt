package ir.msc.gearmetrology.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class GearCalculatorTest {

    @Test
    fun `inv and invInverse are accurate inverses across operating angles`() {
        val testAnglesDeg = listOf(14.5, 17.5, 20.0, 22.5, 25.0, 30.0)
        for (deg in testAnglesDeg) {
            val rad = Math.toRadians(deg)
            val y = GearCalculator.inv(rad)
            val recoveredRad = GearCalculator.invInverse(y)
            assertEquals("Failed inverse involute at $deg deg", rad, recoveredRad, 1e-10)
        }
    }

    @Test
    fun `all built-in test cases match DIN 3960 reference outputs`() {
        for (tc in TestCases.ALL) {
            val res = GearCalculator.calculate(tc.input)
            assertTrue("Test case ${tc.id} should be valid", res.isValid)
            assertEquals("Test case ${tc.id} optimal k mismatch", tc.expectedK, res.optimalSpanTeethK)
            assertEquals("Test case ${tc.id} Wk mismatch", tc.expectedWkMm, res.spanMeasurementWk, 1e-3)
            assertEquals("Test case ${tc.id} M mismatch", tc.expectedMMm, res.measurementOverPinsM, 1e-3)
            assertEquals("Test case ${tc.id} db mismatch", tc.expectedDbMm, res.baseCircleDiameterDb, 1e-3)
        }
    }

    @Test
    fun `test case 5 triggers undercut and narrow face width warnings`() {
        val tc5 = TestCases.ALL.first { it.id == 5 }
        val res = GearCalculator.calculate(tc5.input)
        val codes = res.issues.map { it.code }.toSet()
        assertTrue("Expected UNDERCUT_DETECTED warning", codes.contains(ValidationCode.UNDERCUT_DETECTED))
        assertTrue("Expected FACE_WIDTH_TOO_SMALL warning", codes.contains(ValidationCode.FACE_WIDTH_TOO_SMALL))
    }
}
