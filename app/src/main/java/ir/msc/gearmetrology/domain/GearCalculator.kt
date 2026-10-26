package ir.msc.gearmetrology.domain

import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tan

/**
 * Standard DIN 3960 / ISO 21771 Involute Gear Metrology Calculation Engine.
 * Supports external spur and helical gears in the normal module system.
 * Works 100% offline with double-precision numerical methods.
 */
object GearCalculator {

    private const val PI = Math.PI
    private const val PRACTICAL_CALIPER_MARGIN_MM = 2.0

    /**
     * Involute function: inv(alpha) = tan(alpha) - alpha (where alpha is in radians).
     */
    fun inv(alphaRad: Double): Double {
        return tan(alphaRad) - alphaRad
    }

    /**
     * Inverse involute function: solves tan(alpha) - alpha = y for alpha in radians (0 <= alpha < pi/2).
     * Uses cubic power initial approximation followed by Newton-Raphson iterations to 1e-13 accuracy.
     */
    fun invInverse(y: Double, tolerance: Double = 1e-13, maxIterations: Int = 100): Double {
        if (y <= 0.0) return 0.0
        var alpha = (3.0 * y).pow(1.0 / 3.0) - 0.4 * y
        alpha = alpha.coerceIn(1e-9, PI / 2.0 - 1e-6)

        for (i in 0 until maxIterations) {
            val t = tan(alpha)
            val f = t - alpha - y
            if (abs(f) < tolerance) {
                return alpha
            }
            val df = t * t
            if (df < 1e-15) break
            alpha = (alpha - f / df).coerceIn(1e-9, PI / 2.0 - 1e-6)
        }
        return alpha
    }

    /**
     * Computes ideal pin/ball diameter according to DIN 3960 such that the pin contacts
     * the involute tooth flanks at the mid-flank reference circle d_m = d + 2 * x * m_n.
     */
    fun calculateIdealPinDiameterDin(
        mn: Double,
        z: Int,
        alphaNDeg: Double,
        x: Double,
        betaDeg: Double
    ): Double {
        val alphaN = Math.toRadians(alphaNDeg)
        val beta = Math.toRadians(betaDeg)
        val cosB = cos(beta)
        val alphaT = atan(tan(alphaN) / cosB)
        val invAlphaT = inv(alphaT)

        val cosAlphaM = ((z * cos(alphaT)) / (z + 2.0 * x * cosB)).coerceIn(-1.0, 1.0)
        val alphaM = acos(cosAlphaM)

        val alphaD = (tan(alphaM) + PI / (2.0 * z) - (2.0 * x * tan(alphaN)) / z - invAlphaT)
            .coerceIn(alphaT * 0.5, PI / 2.0 - 0.05)

        val ddExact = mn * z * cos(alphaN) * (
            inv(alphaD) + PI / (2.0 * z) - (2.0 * x * tan(alphaN)) / z - invAlphaT
        )
        return if (ddExact > 0.1 * mn) ddExact else 1.728 * mn
    }

    /**
     * Performs full validation and calculation for a spur or helical gear.
     */
    fun calculate(input: GearInput): GearCalculationResult {
        val issues = mutableListOf<ValidationIssue>()

        // 1. Validate primary inputs
        if (input.normalModuleMn <= 0.0 || input.normalModuleMn > 100.0) {
            issues.add(
                ValidationIssue(
                    code = ValidationCode.INVALID_MODULE,
                    severity = ValidationSeverity.ERROR,
                    titleFa = "مدول نرمال نامعتبر است",
                    titleEn = "Invalid Normal Module (mn)",
                    detailFa = "مدول نرمال (mn) باید عددی مثبت بین 0.1 تا 100 میلی‌متر باشد.",
                    detailEn = "Normal module (mn) must be a positive value between 0.1 and 100 mm."
                )
            )
        }

        if (input.teethCountZ < 5 || input.teethCountZ > 1000) {
            issues.add(
                ValidationIssue(
                    code = ValidationCode.INVALID_TEETH,
                    severity = ValidationSeverity.ERROR,
                    titleFa = "تعداد دندانه نامعتبر است",
                    titleEn = "Invalid Number of Teeth (z)",
                    detailFa = "تعداد دندانه (z) باید عددی صحیح بین 5 تا 1000 باشد.",
                    detailEn = "Number of teeth (z) must be an integer between 5 and 1000."
                )
            )
        }

        if (input.normalPressureAngleDeg < 10.0 || input.normalPressureAngleDeg > 35.0) {
            issues.add(
                ValidationIssue(
                    code = ValidationCode.INVALID_PRESSURE_ANGLE,
                    severity = ValidationSeverity.ERROR,
                    titleFa = "زاویه فشار نرمال خارج از محدوده است",
                    titleEn = "Invalid Normal Pressure Angle (αn)",
                    detailFa = "زاویه فشار نرمال (αn) باید بین 10° تا 35° باشد (مقدار استاندارد: 20°).",
                    detailEn = "Normal pressure angle (αn) must be between 10° and 35° (standard: 20°)."
                )
            )
        }

        if (input.profileShiftX < -1.5 || input.profileShiftX > 2.5) {
            issues.add(
                ValidationIssue(
                    code = ValidationCode.INVALID_PROFILE_SHIFT,
                    severity = ValidationSeverity.ERROR,
                    titleFa = "ضریب تصحیح پروفیل خارج از محدوده است",
                    titleEn = "Invalid Profile Shift Coefficient (x)",
                    detailFa = "ضریب تصحیح پروفیل (x) باید بین -1.5 تا +2.5 باشد.",
                    detailEn = "Profile shift coefficient (x) must be between -1.5 and +2.5."
                )
            )
        }

        val effectiveBetaDeg = if (input.gearType == GearType.HELICAL) input.helixAngleBetaDeg else 0.0
        if (input.gearType == GearType.HELICAL && (effectiveBetaDeg <= 0.0 || effectiveBetaDeg > 45.0)) {
            issues.add(
                ValidationIssue(
                    code = ValidationCode.INVALID_HELIX_ANGLE,
                    severity = ValidationSeverity.ERROR,
                    titleFa = "زاویه مارپیچ نامعتبر است",
                    titleEn = "Invalid Helix Angle (β)",
                    detailFa = "برای چرخ‌دنده مارپیچ، زاویه مارپیچ (β) باید بین 0.1° تا 45° باشد.",
                    detailEn = "For helical gears, helix angle (β) must be between 0.1° and 45°."
                )
            )
        }

        if (input.gearType == GearType.HELICAL && input.faceWidthB <= 0.0) {
            issues.add(
                ValidationIssue(
                    code = ValidationCode.INVALID_FACE_WIDTH,
                    severity = ValidationSeverity.ERROR,
                    titleFa = "پهنای دندانه نامعتبر است",
                    titleEn = "Invalid Face Width (b)",
                    detailFa = "پهنای دندانه (b) باید بزرگ‌تر از صفر باشد.",
                    detailEn = "Face width (b) must be greater than zero."
                )
            )
        }

        if (input.manualPinDiameterDd != null && input.manualPinDiameterDd <= 0.0) {
            issues.add(
                ValidationIssue(
                    code = ValidationCode.INVALID_PIN_DIAMETER,
                    severity = ValidationSeverity.ERROR,
                    titleFa = "قطر پین/ساچمه نامعتبر است",
                    titleEn = "Invalid Pin/Ball Diameter (dD)",
                    detailFa = "قطر پین یا ساچمه باید بزرگ‌تر از صفر باشد.",
                    detailEn = "Pin/ball diameter (dD) must be greater than zero."
                )
            )
        }

        if (issues.any { it.severity == ValidationSeverity.ERROR }) {
            return GearCalculationResult(
                input = input,
                isValid = false,
                issues = issues
            )
        }

        // 2. Fundamental Transverse & Base Geometry (DIN 3960 / ISO 21771)
        val mn = input.normalModuleMn
        val z = input.teethCountZ
        val x = input.profileShiftX
        val alphaN = Math.toRadians(input.normalPressureAngleDeg)
        val beta = Math.toRadians(effectiveBetaDeg)
        val cosB = cos(beta)
        val sinB = sin(beta)

        val mt = mn / cosB
        val alphaT = atan(tan(alphaN) / cosB)
        val invAlphaN = inv(alphaN)
        val invAlphaT = inv(alphaT)

        val d = z * mt
        val db = d * cos(alphaT)
        val da = d + 2.0 * mn * (1.0 + x)
        val df = d - 2.0 * mn * (1.25 - x)
        val sn = mn * (PI / 2.0 + 2.0 * x * tan(alphaN))

        val sinBetaB = (sinB * cos(alphaN)).coerceIn(-1.0, 1.0)
        val betaB = asin(sinBetaB)
        val cosBetaB = cos(betaB)

        // 3. Undercut Verification (Standard Rack Addendum Factor ha0* = 1.0)
        val sinAlphaT = sin(alphaT)
        val minTeethZeroShift = (2.0 * cosB) / (sinAlphaT * sinAlphaT)
        val minProfileShiftNoUndercut = 1.0 - (z * sinAlphaT * sinAlphaT) / (2.0 * cosB)
        if (x < minProfileShiftNoUndercut - 1e-6) {
            val xMinStr = String.format(Locale.US, "%+.4f", minProfileShiftNoUndercut)
            val zMinStr = String.format(Locale.US, "%.1f", minTeethZeroShift)
            issues.add(
                ValidationIssue(
                    code = ValidationCode.UNDERCUT_DETECTED,
                    severity = ValidationSeverity.WARNING,
                    titleFa = "هشدار آندرکات (زیربرش ریشه دندانه)",
                    titleEn = "Undercut Warning (Root Interference)",
                    detailFa = "ضریب تصحیح فعلی (x = $x) کمتر از حداقل مجاز بدون آندرکات (x_min = $xMinStr برای z_min = $zMinStr) است. آندرکات می‌تواند باعث کاهش طول پروفیل اینولوت و خطا در اندازه‌گیری دهانه (W) شود.",
                    detailEn = "Current profile shift (x = $x) is below the undercut threshold (x_min = $xMinStr, z_min = $zMinStr). Undercutting shortens the active involute flank and may compromise span measurement."
                )
            )
        }

        // 4. Span Measurement (Base Tangent Length W_k)
        // Reference pressure angle at shifted mid-flank circle d_m = d + 2*x*mn:
        val ratioM = (z * cos(alphaT)) / (z + 2.0 * x * cosB)
        val alphaM = if (ratioM in 0.0..1.0) acos(ratioM) else alphaT

        val kExact = (z / PI) * (
            tan(alphaM) / (cosBetaB * cosBetaB) - (2.0 * x * tan(alphaN)) / z - invAlphaT
        ) + 0.5
        val kOptimal = max(1, kExact.roundToInt())
        val kApplied = (input.manualSpanTeethK ?: kOptimal).coerceIn(1, max(1, z / 2))

        fun computeWk(kVal: Int): Double {
            return mn * cos(alphaN) * (PI * (kVal - 0.5) + z * invAlphaT) + 2.0 * x * mn * sin(alphaN)
        }

        val wk = computeWk(kApplied)
        val bMinTheoretical = wk * sin(betaB)
        val bMinPractical = if (input.gearType == GearType.HELICAL && effectiveBetaDeg > 0.0) {
            bMinTheoretical + PRACTICAL_CALIPER_MARGIN_MM
        } else {
            0.0
        }

        val neighborKs = listOf(kOptimal - 1, kOptimal, kOptimal + 1)
            .filter { it >= 1 && it <= max(1, z / 2) }
            .distinct()
        val spanNeighbors = neighborKs.map { kCandidate ->
            val wCandidate = computeWk(kCandidate)
            val bMinCand = if (input.gearType == GearType.HELICAL && effectiveBetaDeg > 0.0) {
                wCandidate * sin(betaB) + PRACTICAL_CALIPER_MARGIN_MM
            } else {
                0.0
            }
            SpanNeighborRow(
                k = kCandidate,
                wkMm = wCandidate,
                bMinMm = bMinCand,
                isOptimal = (kCandidate == kOptimal)
            )
        }

        // Helical Face Width Check for Span Measurement
        if (input.gearType == GearType.HELICAL && input.faceWidthB < bMinPractical) {
            val bMinStr = String.format(Locale.US, "%.2f", bMinPractical)
            val bTheoStr = String.format(Locale.US, "%.2f", bMinTheoretical)
            issues.add(
                ValidationIssue(
                    code = ValidationCode.FACE_WIDTH_TOO_SMALL,
                    severity = ValidationSeverity.WARNING,
                    titleFa = "پهنای دندانه (b) برای اندازه‌گیری دهانه (Wk) کافی نیست",
                    titleEn = "Face Width (b) Too Small for Span Measurement (Wk)",
                    detailFa = "پهنای دندانه (${input.faceWidthB} mm) کمتر از حداقل پهنای لازم ($bMinStr mm شامل ۲ میلی‌متر حاشیه فک کولیس؛ مقدار تئوری: $bTheoStr mm) برای k = $kApplied است. از اندازه‌گیری روی ساچمه/پین (M) استفاده کنید.",
                    detailEn = "Face width (${input.faceWidthB} mm) is smaller than the minimum required width ($bMinStr mm including 2 mm jaw clearance; theoretical: $bTheoStr mm) for k = $kApplied. Use Measurement over Balls/Pins (M) instead."
                )
            )
        }

        // 5. Measurement Over Pins / Balls (M)
        val idealPinDin = calculateIdealPinDiameterDin(
            mn = mn,
            z = z,
            alphaNDeg = input.normalPressureAngleDeg,
            x = x,
            betaDeg = effectiveBetaDeg
        )
        val standardPin1728 = 1.728 * mn
        // Default auto-calculated pin size uses standard workshop 1.728 * mn when x == 0 and alpha_n == 20,
        // or the exact DIN 3960 pitch-contact pin diameter rounded to 3 decimal places when shifted:
        val autoPinDiameter = if (abs(x) < 1e-6 && abs(input.normalPressureAngleDeg - 20.0) < 1e-6) {
            standardPin1728
        } else {
            idealPinDin
        }
        val dd = input.manualPinDiameterDd ?: autoPinDiameter

        val invAlphaD = (dd / (mn * z * cos(alphaN))) - (PI / (2.0 * z)) +
            ((2.0 * x * tan(alphaN)) / z) + invAlphaT

        val alphaD: Double
        val dm: Double
        val mOverPins: Double
        val isEven = (z % 2 == 0)

        if (invAlphaD <= 0.0) {
            alphaD = 0.0
            dm = db
            mOverPins = if (isEven) dm + dd else dm * cos(PI / (2.0 * z)) + dd
            issues.add(
                ValidationIssue(
                    code = ValidationCode.PIN_CONTACTS_BELOW_BASE,
                    severity = ValidationSeverity.WARNING,
                    titleFa = "قطر پین/ساچمه بسیار کوچک است (inv αD ≤ 0)",
                    titleEn = "Pin/Ball Diameter Too Small (inv αD ≤ 0)",
                    detailFa = "پین انتخاب‌شده (${String.format(Locale.US, "%.3f", dd)} mm) به زیر دایره مبنا برخورد می‌کند. قطر پین بزرگ‌تری انتخاب کنید (پیشنهادی: ${String.format(Locale.US, "%.3f", idealPinDin)} mm).",
                    detailEn = "Selected pin (${String.format(Locale.US, "%.3f", dd)} mm) contacts below the base circle. Choose a larger pin diameter (recommended: ${String.format(Locale.US, "%.3f", idealPinDin)} mm)."
                )
            )
        } else {
            alphaD = invInverse(invAlphaD)
            dm = db / cos(alphaD)
            mOverPins = if (isEven) {
                dm + dd
            } else {
                dm * cos(PI / (2.0 * z)) + dd
            }

            if (dm + dd <= da) {
                val minClearanceStr = String.format(Locale.US, "%.3f", da)
                val currentTipSpanStr = String.format(Locale.US, "%.3f", dm + dd)
                issues.add(
                    ValidationIssue(
                        code = ValidationCode.PIN_BELOW_TIP_DIAMETER,
                        severity = ValidationSeverity.WARNING,
                        titleFa = "پین/ساچمه بالاتر از قطر سر دندانه (da) قرار نمی‌گیرد",
                        titleEn = "Pin/Ball Does Not Project Above Tip Circle (da)",
                        detailFa = "قطر روی پین‌ها ($currentTipSpanStr mm) کوچک‌تر یا مساوی قطر سر دندانه ($minClearanceStr mm) است و فک میکرومتر/کولیس با سر دندانه تداخل خواهد داشت. قطر پین را افزایش دهید.",
                        detailEn = "Diameter over pin crown ($currentTipSpanStr mm) does not exceed tip diameter da ($minClearanceStr mm), so micrometer anvils cannot seat on the pins. Increase pin diameter."
                    )
                )
            }
        }

        return GearCalculationResult(
            input = input,
            isValid = true,
            issues = issues,
            optimalSpanTeethK = kOptimal,
            appliedSpanTeethK = kApplied,
            exactSpanTeethK = kExact,
            spanMeasurementWk = wk,
            spanNeighbors = spanNeighbors,
            measurementOverPinsM = mOverPins,
            isEvenTeeth = isEven,
            pinDiameterUsed = dd,
            idealPinDiameterDin = idealPinDin,
            standardPinDiameter1728 = standardPin1728,
            transverseModuleMt = mt,
            transversePressureAngleDeg = Math.toDegrees(alphaT),
            pitchDiameterD = d,
            baseCircleDiameterDb = db,
            tipDiameterDa = da,
            rootDiameterDf = df,
            baseHelixAngleDeg = Math.toDegrees(betaB),
            normalToothThicknessSn = sn,
            invAlphaN = invAlphaN,
            invAlphaT = invAlphaT,
            invAlphaD = invAlphaD,
            pinPressureAngleDeg = Math.toDegrees(alphaD),
            pinCenterCircleDiameterDm = dm,
            minProfileShiftNoUndercut = minProfileShiftNoUndercut,
            minTeethNoUndercutAtZeroShift = minTeethZeroShift,
            minFaceWidthTheoreticalMm = bMinTheoretical,
            minFaceWidthPracticalMm = bMinPractical
        )
    }
}
