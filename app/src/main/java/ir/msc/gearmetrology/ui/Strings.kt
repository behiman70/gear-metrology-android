package ir.msc.gearmetrology.ui

import ir.msc.gearmetrology.domain.AppLanguage
import ir.msc.gearmetrology.domain.GearCalculationResult
import ir.msc.gearmetrology.domain.GearType
import ir.msc.gearmetrology.domain.HelixHand
import java.util.Locale

object NumberFormatter {
    /**
     * Converts Persian/Arabic-Indic numerals and decimal separators to standard ASCII
     * so users typing with a Persian keyboard can seamlessly input numbers.
     */
    fun normalizeInput(raw: String): String {
        val sb = StringBuilder(raw.length)
        for (ch in raw) {
            when (ch) {
                in '۰'..'۹' -> sb.append((ch - '۰' + '0'.code).toChar())
                in '٠'..'٩' -> sb.append((ch - '٠' + '0'.code).toChar())
                '٫', '،', ',' -> sb.append('.')
                '−', '–' -> sb.append('-')
                else -> sb.append(ch)
            }
        }
        return sb.toString().trim()
    }

    fun format(value: Double, decimals: Int): String {
        return String.format(Locale.US, "%.${decimals}f", value)
    }

    /**
     * Wraps an ASCII numeric/formula string in Left-To-Right Embedding (U+202A ... U+202C)
     * so negative signs, decimal points, and units render cleanly inside RTL Persian layouts.
     */
    fun ltr(text: String): String = "\u202A$text\u202C"
}

data class UiStrings(
    val appTitle: String,
    val appSubtitle: String,
    val creatorCreditRtl: String,
    val creatorCreditSub: String,
    val tabCalculator: String,
    val tabFormulas: String,
    val tabTestCases: String,
    val tabAbout: String,
    val gearTypeLabel: String,
    val spurGear: String,
    val helicalGear: String,
    val inputsSectionTitle: String,
    val normalModuleLabel: String,
    val teethCountLabel: String,
    val normalPressureAngleLabel: String,
    val profileShiftLabel: String,
    val helixAngleLabel: String,
    val helixHandLabel: String,
    val handRight: String,
    val handLeft: String,
    val faceWidthLabel: String,
    val pinDiameterModeLabel: String,
    val pinAutoLabel: String,
    val pinManualLabel: String,
    val pinDiameterInputLabel: String,
    val useDinIdealBtn: String,
    val use1728Btn: String,
    val decimalPrecisionLabel: String,
    val primaryResultsTitle: String,
    val spanMeasurementTitle: String,
    val optimalSpanTeethLabel: String,
    val exactSpanTeethLabel: String,
    val measurementOverPinsTitle: String,
    val evenTeethBadge: String,
    val oddTeethBadge: String,
    val pinDiameterUsedLabel: String,
    val copyReportBtn: String,
    val shareReportBtn: String,
    val copiedToast: String,
    val intermediateValuesTitle: String,
    val baseCircleDiameterLabel: String,
    val pitchDiameterLabel: String,
    val tipDiameterLabel: String,
    val rootDiameterLabel: String,
    val transverseModuleLabel: String,
    val transversePressureAngleLabel: String,
    val baseHelixAngleLabel: String,
    val normalToothThicknessLabel: String,
    val invAlphaNLabel: String,
    val invAlphaTLabel: String,
    val invAlphaDLabel: String,
    val pinPressureAngleLabel: String,
    val pinCenterCircleLabel: String,
    val minProfileShiftLabel: String,
    val minFaceWidthLabel: String,
    val spanNeighborTableTitle: String,
    val loadTestCaseBtn: String,
    val testCaseLoadedToast: String,
    val resetDefaultsBtn: String
)

object Localization {
    private val FA_STRINGS = UiStrings(
        appTitle = "محاسبه‌گر تخصصی چرخ‌دنده (W & M)",
        appSubtitle = "اندازه‌گیری دهانه (W) و روی پین/ساچمه (M) — استاندارد DIN 3960",
        creatorCreditRtl = "کارگاه ساخت فولاد مبارکه",
        creatorCreditSub = "مجتمع فولاد مبارکه اصفهان — کاملاً آفلاین",
        tabCalculator = "ماشین‌حساب",
        tabFormulas = "فرمول‌ها",
        tabTestCases = "مثال‌های مرجع",
        tabAbout = "درباره",
        gearTypeLabel = "نوع چرخ‌دنده",
        spurGear = "ساده (Spur)",
        helicalGear = "مارپیچ (Helical)",
        inputsSectionTitle = "پارامترهای ورودی چرخ‌دنده (میلی‌متر / درجه)",
        normalModuleLabel = "مدول نرمال (mn) [mm]",
        teethCountLabel = "تعداد دندانه (z)",
        normalPressureAngleLabel = "زاویه فشار نرمال (αn) [°]",
        profileShiftLabel = "ضریب تصحیح پروفیل (x)",
        helixAngleLabel = "زاویه مارپیچ (β) [°]",
        helixHandLabel = "جهت مارپیچ",
        handRight = "راست‌گرد (RH)",
        handLeft = "چپ‌گرد (LH)",
        faceWidthLabel = "پهنای دندانه (b) [mm]",
        pinDiameterModeLabel = "قطر پین / ساچمه اندازه‌گیری (dD)",
        pinAutoLabel = "محاسبه خودکار قطر ایده‌آل",
        pinManualLabel = "ورود دستی قطر پین (Override)",
        pinDiameterInputLabel = "قطر پین / ساچمه (dD) [mm]",
        useDinIdealBtn = "ایده‌آل DIN",
        use1728Btn = "استاندارد 1.728·mn",
        decimalPrecisionLabel = "دقت اعشار:",
        primaryResultsTitle = "نتایج اصلی کنترل ابعادی",
        spanMeasurementTitle = "اندازه دهانه (طول مماس مبنا Wk)",
        optimalSpanTeethLabel = "تعداد دندانه بهینه دهانه (k)",
        exactSpanTeethLabel = "مقدار دقیق تئوری (k_exact)",
        measurementOverPinsTitle = "اندازه روی پین / ساچمه (M)",
        evenTeethBadge = "فرمول تعداد دندانه زوج (z زوج)",
        oddTeethBadge = "فرمول تعداد دندانه فرد (z فرد)",
        pinDiameterUsedLabel = "قطر پین/ساچمه مبنای محاسبه (dD)",
        copyReportBtn = "کپی گزارش",
        shareReportBtn = "اشتراک‌گذاری",
        copiedToast = "گزارش اندازه‌گیری در حافظه کپی شد",
        intermediateValuesTitle = "مقادیر میانی و هندسه دایره مبنا",
        baseCircleDiameterLabel = "قطر دایره مبنا (db)",
        pitchDiameterLabel = "قطر دایره گام مرجع (d)",
        tipDiameterLabel = "قطر سر دندانه (da)",
        rootDiameterLabel = "قطر پای دندانه (df)",
        transverseModuleLabel = "مدول ظاهری / مقطع (mt)",
        transversePressureAngleLabel = "زاویه فشار مقطع (αt)",
        baseHelixAngleLabel = "زاویه مارپیچ مبنا (βb)",
        normalToothThicknessLabel = "ضخامت دندانه نرمال در گام (sn)",
        invAlphaNLabel = "تابع اینولوت نرمال (inv αn)",
        invAlphaTLabel = "تابع اینولوت مقطع (inv αt)",
        invAlphaDLabel = "اینولوت مرکز پین (inv αD)",
        pinPressureAngleLabel = "زاویه فشار مرکز پین (αD)",
        pinCenterCircleLabel = "قطر دایره مراکز پین (dM)",
        minProfileShiftLabel = "حداقل ضریب تصحیح بدون آندرکات (x_min)",
        minFaceWidthLabel = "حداقل پهنای دندانه لازم برای دهانه (b_min)",
        spanNeighborTableTitle = "جدول مقایسه دهانه‌های مجاور (k-1, k, k+1)",
        loadTestCaseBtn = "بارگذاری در ماشین‌حساب",
        testCaseLoadedToast = "مثال مرجع در ماشین‌حساب بارگذاری شد",
        resetDefaultsBtn = "بازنشانی مقادیر"
    )

    private val EN_STRINGS = UiStrings(
        appTitle = "Gear Metrology Calculator (W & M)",
        appSubtitle = "Span (W) & Over Pins/Balls (M) — DIN 3960 / ISO 21771",
        creatorCreditRtl = "کارگاه ساخت فولاد مبارکه",
        creatorCreditSub = "Mobarakeh Steel Co. Manufacturing Workshop — 100% Offline",
        tabCalculator = "Calculator",
        tabFormulas = "Formulas",
        tabTestCases = "Test Cases",
        tabAbout = "About",
        gearTypeLabel = "Gear Type",
        spurGear = "Spur Gear",
        helicalGear = "Helical Gear",
        inputsSectionTitle = "Gear Geometry Inputs (mm / deg)",
        normalModuleLabel = "Normal Module (mn) [mm]",
        teethCountLabel = "Number of Teeth (z)",
        normalPressureAngleLabel = "Normal Pressure Angle (αn) [°]",
        profileShiftLabel = "Profile Shift Coefficient (x)",
        helixAngleLabel = "Helix Angle (β) [°]",
        helixHandLabel = "Helix Hand",
        handRight = "Right-Hand (RH)",
        handLeft = "Left-Hand (LH)",
        faceWidthLabel = "Face Width (b) [mm]",
        pinDiameterModeLabel = "Measuring Pin / Ball Diameter (dD)",
        pinAutoLabel = "Auto-Calculate Ideal Size",
        pinManualLabel = "Manual Override",
        pinDiameterInputLabel = "Pin / Ball Diameter (dD) [mm]",
        useDinIdealBtn = "DIN Ideal",
        use1728Btn = "1.728·mn",
        decimalPrecisionLabel = "Decimals:",
        primaryResultsTitle = "Primary Inspection Outputs",
        spanMeasurementTitle = "Span Measurement (Base Tangent Length Wk)",
        optimalSpanTeethLabel = "Optimal Teeth Spanned (k)",
        exactSpanTeethLabel = "Theoretical Exact Span (k_exact)",
        measurementOverPinsTitle = "Measurement Over Pins / Balls (M)",
        evenTeethBadge = "Even Tooth Count Formula (z even)",
        oddTeethBadge = "Odd Tooth Count Formula (z odd)",
        pinDiameterUsedLabel = "Pin / Ball Diameter Used (dD)",
        copyReportBtn = "Copy Report",
        shareReportBtn = "Share Report",
        copiedToast = "Inspection report copied to clipboard",
        intermediateValuesTitle = "Key Intermediate Geometry & Involutes",
        baseCircleDiameterLabel = "Base Circle Diameter (db)",
        pitchDiameterLabel = "Reference Pitch Diameter (d)",
        tipDiameterLabel = "Tip / Outside Diameter (da)",
        rootDiameterLabel = "Root Diameter (df)",
        transverseModuleLabel = "Transverse Module (mt)",
        transversePressureAngleLabel = "Transverse Pressure Angle (αt)",
        baseHelixAngleLabel = "Base Helix Angle (βb)",
        normalToothThicknessLabel = "Normal Reference Tooth Thickness (sn)",
        invAlphaNLabel = "Normal Involute (inv αn)",
        invAlphaTLabel = "Transverse Involute (inv αt)",
        invAlphaDLabel = "Pin Center Involute (inv αD)",
        pinPressureAngleLabel = "Pin Center Pressure Angle (αD)",
        pinCenterCircleLabel = "Pin Center Circle Diameter (dM)",
        minProfileShiftLabel = "Min Profile Shift Without Undercut (x_min)",
        minFaceWidthLabel = "Min Face Width for Span (b_min)",
        spanNeighborTableTitle = "Adjacent Span Teeth Comparison (k-1, k, k+1)",
        loadTestCaseBtn = "Load into Calculator",
        testCaseLoadedToast = "Test case loaded into calculator",
        resetDefaultsBtn = "Reset Defaults"
    )

    fun get(lang: AppLanguage): UiStrings = when (lang) {
        AppLanguage.FA -> FA_STRINGS
        AppLanguage.EN -> EN_STRINGS
    }

    fun buildShareableReport(
        res: GearCalculationResult,
        decimals: Int,
        lang: AppLanguage
    ): String {
        val inp = res.input
        val f = { v: Double -> NumberFormatter.format(v, decimals) }
        val f6 = { v: Double -> NumberFormatter.format(v, 6) }
        return if (lang == AppLanguage.FA) {
            buildString {
                appendLine("========================================")
                appendLine("گزارش کنترل ابعادی چرخ‌دنده (W & M)")
                appendLine("کارگاه ساخت فولاد مبارکه")
                appendLine("========================================")
                appendLine("نوع چرخ‌دنده: ${if (inp.gearType == GearType.SPUR) "ساده (Spur)" else "مارپیچ (Helical)"}")
                appendLine("مدول نرمال (mn): ${f(inp.normalModuleMn)} mm")
                appendLine("تعداد دندانه (z): ${inp.teethCountZ}")
                appendLine("زاویه فشار نرمال (αn): ${f(inp.normalPressureAngleDeg)}°")
                appendLine("ضریب تصحیح پروفیل (x): ${f(inp.profileShiftX)}")
                if (inp.gearType == GearType.HELICAL) {
                    val handStr = if (inp.helixHand == HelixHand.RIGHT) "راست‌گرد (RH)" else "چپ‌گرد (LH)"
                    appendLine("زاویه مارپیچ (β): ${f(inp.helixAngleBetaDeg)}° ($handStr)")
                    appendLine("پهنای دندانه (b): ${f(inp.faceWidthB)} mm")
                }
                appendLine("----------------------------------------")
                appendLine("نتایج اصلی اندازه‌گیری:")
                appendLine("• تعداد دندانه دهانه (k): ${res.appliedSpanTeethK} (مقدار دقیق: ${NumberFormatter.format(res.exactSpanTeethK, 3)})")
                appendLine("• اندازه دهانه (Wk): ${f(res.spanMeasurementWk)} mm")
                appendLine("• قطر پین/ساچمه (dD): ${f(res.pinDiameterUsed)} mm")
                appendLine("• اندازه روی پین/ساچمه (M): ${f(res.measurementOverPinsM)} mm (${if (res.isEvenTeeth) "z زوج" else "z فرد"})")
                appendLine("----------------------------------------")
                appendLine("مقادیر میانی کلیدی:")
                appendLine("• قطر دایره مبنا (db): ${f(res.baseCircleDiameterDb)} mm")
                appendLine("• قطر دایره گام (d): ${f(res.pitchDiameterD)} mm")
                appendLine("• قطر سر دندانه (da): ${f(res.tipDiameterDa)} mm")
                appendLine("• قطر پای دندانه (df): ${f(res.rootDiameterDf)} mm")
                appendLine("• مدول مقطع (mt): ${f(res.transverseModuleMt)} mm")
                appendLine("• زاویه فشار مقطع (αt): ${f(res.transversePressureAngleDeg)}°")
                appendLine("• زاویه مارپیچ مبنا (βb): ${f(res.baseHelixAngleDeg)}°")
                appendLine("• inv(αn) = ${f6(res.invAlphaN)} | inv(αt) = ${f6(res.invAlphaT)} | inv(αD) = ${f6(res.invAlphaD)}")
                appendLine("• زاویه فشار مرکز پین (αD): ${f(res.pinPressureAngleDeg)}°")
                appendLine("• قطر دایره مراکز پین (dM): ${f(res.pinCenterCircleDiameterDm)} mm")
                if (res.issues.isNotEmpty()) {
                    appendLine("----------------------------------------")
                    appendLine("هشدارهای مهندسی:")
                    res.issues.forEach { issue ->
                        appendLine("! ${issue.titleFa}: ${issue.detailFa}")
                    }
                }
                appendLine("========================================")
            }
        } else {
            buildString {
                appendLine("========================================")
                appendLine("GEAR METROLOGY INSPECTION REPORT (W & M)")
                appendLine("کارگاه ساخت فولاد مبارکه | Mobarakeh Steel Workshop")
                appendLine("========================================")
                appendLine("Gear Type: ${if (inp.gearType == GearType.SPUR) "Spur" else "Helical"}")
                appendLine("Normal Module (mn): ${f(inp.normalModuleMn)} mm")
                appendLine("Number of Teeth (z): ${inp.teethCountZ}")
                appendLine("Normal Pressure Angle (αn): ${f(inp.normalPressureAngleDeg)}°")
                appendLine("Profile Shift Coefficient (x): ${f(inp.profileShiftX)}")
                if (inp.gearType == GearType.HELICAL) {
                    appendLine("Helix Angle (β): ${f(inp.helixAngleBetaDeg)}° (${inp.helixHand})")
                    appendLine("Face Width (b): ${f(inp.faceWidthB)} mm")
                }
                appendLine("----------------------------------------")
                appendLine("PRIMARY OUTPUTS:")
                appendLine("• Span Tooth Count (k): ${res.appliedSpanTeethK} (k_exact = ${NumberFormatter.format(res.exactSpanTeethK, 3)})")
                appendLine("• Span Measurement (Wk): ${f(res.spanMeasurementWk)} mm")
                appendLine("• Pin/Ball Diameter (dD): ${f(res.pinDiameterUsed)} mm")
                appendLine("• Measurement Over Pins/Balls (M): ${f(res.measurementOverPinsM)} mm (${if (res.isEvenTeeth) "Even z" else "Odd z"})")
                appendLine("----------------------------------------")
                appendLine("KEY INTERMEDIATE VALUES:")
                appendLine("• Base Circle Diameter (db): ${f(res.baseCircleDiameterDb)} mm")
                appendLine("• Reference Pitch Diameter (d): ${f(res.pitchDiameterD)} mm")
                appendLine("• Tip Diameter (da): ${f(res.tipDiameterDa)} mm")
                appendLine("• Root Diameter (df): ${f(res.rootDiameterDf)} mm")
                appendLine("• Transverse Module (mt): ${f(res.transverseModuleMt)} mm")
                appendLine("• Transverse Pressure Angle (αt): ${f(res.transversePressureAngleDeg)}°")
                appendLine("• Base Helix Angle (βb): ${f(res.baseHelixAngleDeg)}°")
                appendLine("• inv(αn) = ${f6(res.invAlphaN)} | inv(αt) = ${f6(res.invAlphaT)} | inv(αD) = ${f6(res.invAlphaD)}")
                appendLine("• Pin Center Pressure Angle (αD): ${f(res.pinPressureAngleDeg)}°")
                appendLine("• Pin Center Circle Diameter (dM): ${f(res.pinCenterCircleDiameterDm)} mm")
                if (res.issues.isNotEmpty()) {
                    appendLine("----------------------------------------")
                    appendLine("VALIDATION WARNINGS:")
                    res.issues.forEach { issue ->
                        appendLine("! ${issue.titleEn}: ${issue.detailEn}")
                    }
                }
                appendLine("========================================")
            }
        }
    }
}
