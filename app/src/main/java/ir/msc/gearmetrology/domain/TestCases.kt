package ir.msc.gearmetrology.domain

data class BuiltInTestCase(
    val id: Int,
    val titleFa: String,
    val titleEn: String,
    val subtitleFa: String,
    val subtitleEn: String,
    val input: GearInput,
    val expectedK: Int,
    val expectedWkMm: Double,
    val expectedMMm: Double,
    val expectedDbMm: Double,
    val notesFa: String,
    val notesEn: String
)

object TestCases {
    val ALL: List<BuiltInTestCase> = listOf(
        BuiltInTestCase(
            id = 1,
            titleFa = "۱. چرخ‌دنده ساده استاندارد (تعداد دندانه زوج)",
            titleEn = "1. Standard Spur Gear (Even Tooth Count)",
            subtitleFa = "mn = 3.0 mm, z = 24, αn = 20°, x = 0, dD = 5.184 mm",
            subtitleEn = "mn = 3.0 mm, z = 24, αn = 20°, x = 0, dD = 5.184 mm",
            input = GearInput(
                gearType = GearType.SPUR,
                normalModuleMn = 3.0,
                teethCountZ = 24,
                normalPressureAngleDeg = 20.0,
                profileShiftX = 0.0,
                helixAngleBetaDeg = 0.0,
                helixHand = HelixHand.RIGHT,
                faceWidthB = 30.0,
                manualPinDiameterDd = 5.184
            ),
            expectedK = 3,
            expectedWkMm = 23.1494,
            expectedMMm = 79.1991,
            expectedDbMm = 67.6579,
            notesFa = "حالت مرجع استاندارد DIN 3960 برای چرخ‌دنده ساده بدون تصحیح پروفیل با پین استاندارد 1.728×mn.",
            notesEn = "Standard DIN 3960 reference case for an unshifted spur gear using standard 1.728×mn pin diameter."
        ),
        BuiltInTestCase(
            id = 2,
            titleFa = "۲. چرخ‌دنده ساده اصلاح‌شده (تعداد دندانه فرد)",
            titleEn = "2. Profile-Shifted Spur Gear (Odd Tooth Count)",
            subtitleFa = "mn = 4.0 mm, z = 19, αn = 20°, x = +0.25, dD = 7.000 mm",
            subtitleEn = "mn = 4.0 mm, z = 19, αn = 20°, x = +0.25, dD = 7.000 mm",
            input = GearInput(
                gearType = GearType.SPUR,
                normalModuleMn = 4.0,
                teethCountZ = 19,
                normalPressureAngleDeg = 20.0,
                profileShiftX = 0.25,
                helixAngleBetaDeg = 0.0,
                helixHand = HelixHand.RIGHT,
                faceWidthB = 40.0,
                manualPinDiameterDd = 7.000
            ),
            expectedK = 3,
            expectedWkMm = 31.2698,
            expectedMMm = 87.1189,
            expectedDbMm = 71.4166,
            notesFa = "آزمون ضریب تصحیح مثبت (x = +0.25) و فرمول تعداد دندانه فرد با ضریب کسینوسی cos(90°/z).",
            notesEn = "Verifies positive profile shift (x = +0.25) and odd-tooth chordal factor cos(90°/z) for M."
        ),
        BuiltInTestCase(
            id = 3,
            titleFa = "۳. چرخ‌دنده مارپیچ راست‌گرد (تعداد دندانه زوج)",
            titleEn = "3. Helical Gear Right-Hand (Even Tooth Count)",
            subtitleFa = "mn = 2.5 mm, z = 32, αn = 20°, x = +0.10, β = 15° RH, b = 28 mm, dD = 4.500 mm",
            subtitleEn = "mn = 2.5 mm, z = 32, αn = 20°, x = +0.10, β = 15° RH, b = 28 mm, dD = 4.500 mm",
            input = GearInput(
                gearType = GearType.HELICAL,
                normalModuleMn = 2.5,
                teethCountZ = 32,
                normalPressureAngleDeg = 20.0,
                profileShiftX = 0.10,
                helixAngleBetaDeg = 15.0,
                helixHand = HelixHand.RIGHT,
                faceWidthB = 28.0,
                manualPinDiameterDd = 4.500
            ),
            expectedK = 5,
            expectedWkMm = 34.6194,
            expectedMMm = 89.9274,
            expectedDbMm = 77.5025,
            notesFa = "بررسی تبدیل مدول و زاویه فشار به صفحه مقطع (mt, αt) و محاسبه زاویه مارپیچ مبنا (βb = 14.076°).",
            notesEn = "Verifies normal-to-transverse conversion (mt, αt), base helix angle (βb = 14.076°), and helical Wk."
        ),
        BuiltInTestCase(
            id = 4,
            titleFa = "۴. چرخ‌دنده مارپیچ چپ‌گرد (تعداد دندانه فرد)",
            titleEn = "4. Helical Gear Left-Hand (Odd Tooth Count)",
            subtitleFa = "mn = 5.0 mm, z = 21, αn = 20°, x = 0, β = 12° LH, b = 50 mm, dD = 8.640 mm",
            subtitleEn = "mn = 5.0 mm, z = 21, αn = 20°, x = 0, β = 12° LH, b = 50 mm, dD = 8.640 mm",
            input = GearInput(
                gearType = GearType.HELICAL,
                normalModuleMn = 5.0,
                teethCountZ = 21,
                normalPressureAngleDeg = 20.0,
                profileShiftX = 0.0,
                helixAngleBetaDeg = 12.0,
                helixHand = HelixHand.LEFT,
                faceWidthB = 50.0,
                manualPinDiameterDd = 8.640
            ),
            expectedK = 3,
            expectedWkMm = 38.4679,
            expectedMMm = 119.0178,
            expectedDbMm = 100.6065,
            notesFa = "بررسی اندازه‌گیری روی دو ساچمه (M) در چرخ‌دنده مارپیچ با تعداد دندانه فرد.",
            notesEn = "Verifies two-ball measurement (M) on an odd-tooth left-hand helical gear."
        ),
        BuiltInTestCase(
            id = 5,
            titleFa = "۵. سناریوی کنترل هشدارها (آندرکات + پهنای دندانه کم)",
            titleEn = "5. Validation Warning Test (Undercut + Narrow Face Width)",
            subtitleFa = "mn = 3.0 mm, z = 12, αn = 20°, x = 0, β = 25° RH, b = 8 mm, dD = 5.184 mm",
            subtitleEn = "mn = 3.0 mm, z = 12, αn = 20°, x = 0, β = 25° RH, b = 8 mm, dD = 5.184 mm",
            input = GearInput(
                gearType = GearType.HELICAL,
                normalModuleMn = 3.0,
                teethCountZ = 12,
                normalPressureAngleDeg = 20.0,
                profileShiftX = 0.0,
                helixAngleBetaDeg = 25.0,
                helixHand = HelixHand.RIGHT,
                faceWidthB = 8.0,
                manualPinDiameterDd = 5.184
            ),
            expectedK = 2,
            expectedWkMm = 14.8058,
            expectedMMm = 46.5177,
            expectedDbMm = 36.8266,
            notesFa = "تأیید عملکرد هم‌زمان هشدار آندرکات (z = 12 بدون تصحیح پروفیل) و هشدار ناکافی بودن پهنای دندانه (b = 8 mm < b_min = 7.89 mm).",
            notesEn = "Triggers both the root undercut warning (z = 12 at x = 0) and insufficient helical face width warning (b = 8.0 mm < b_min = 7.89 mm)."
        )
    )
}
