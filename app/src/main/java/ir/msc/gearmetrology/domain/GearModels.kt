package ir.msc.gearmetrology.domain

enum class GearType {
    SPUR,
    HELICAL
}

enum class HelixHand {
    RIGHT,
    LEFT
}

enum class AppLanguage(val code: String, val isRtl: Boolean) {
    FA("fa", true),
    EN("en", false)
}

enum class ValidationSeverity {
    ERROR,
    WARNING
}

enum class ValidationCode {
    INVALID_MODULE,
    INVALID_TEETH,
    INVALID_PRESSURE_ANGLE,
    INVALID_PROFILE_SHIFT,
    INVALID_HELIX_ANGLE,
    INVALID_FACE_WIDTH,
    INVALID_PIN_DIAMETER,
    UNDERCUT_DETECTED,
    FACE_WIDTH_TOO_SMALL,
    PIN_BELOW_TIP_DIAMETER,
    PIN_CONTACTS_BELOW_BASE
}

data class ValidationIssue(
    val code: ValidationCode,
    val severity: ValidationSeverity,
    val titleFa: String,
    val titleEn: String,
    val detailFa: String,
    val detailEn: String
)

data class GearInput(
    val gearType: GearType = GearType.SPUR,
    val normalModuleMn: Double = 3.0,
    val teethCountZ: Int = 24,
    val normalPressureAngleDeg: Double = 20.0,
    val profileShiftX: Double = 0.0,
    val helixAngleBetaDeg: Double = 15.0,
    val helixHand: HelixHand = HelixHand.RIGHT,
    val faceWidthB: Double = 30.0,
    val manualPinDiameterDd: Double? = null,
    val manualSpanTeethK: Int? = null
)

data class SpanNeighborRow(
    val k: Int,
    val wkMm: Double,
    val bMinMm: Double,
    val isOptimal: Boolean
)

data class GearCalculationResult(
    val input: GearInput,
    val isValid: Boolean,
    val issues: List<ValidationIssue>,
    // Primary Outputs
    val optimalSpanTeethK: Int = 0,
    val appliedSpanTeethK: Int = 0,
    val exactSpanTeethK: Double = 0.0,
    val spanMeasurementWk: Double = 0.0,
    val spanNeighbors: List<SpanNeighborRow> = emptyList(),
    val measurementOverPinsM: Double = 0.0,
    val isEvenTeeth: Boolean = true,
    // Pin Diameter Outputs
    val pinDiameterUsed: Double = 0.0,
    val idealPinDiameterDin: Double = 0.0,
    val standardPinDiameter1728: Double = 0.0,
    // Key Intermediate Values
    val transverseModuleMt: Double = 0.0,
    val transversePressureAngleDeg: Double = 0.0,
    val pitchDiameterD: Double = 0.0,
    val baseCircleDiameterDb: Double = 0.0,
    val tipDiameterDa: Double = 0.0,
    val rootDiameterDf: Double = 0.0,
    val baseHelixAngleDeg: Double = 0.0,
    val normalToothThicknessSn: Double = 0.0,
    val invAlphaN: Double = 0.0,
    val invAlphaT: Double = 0.0,
    val invAlphaD: Double = 0.0,
    val pinPressureAngleDeg: Double = 0.0,
    val pinCenterCircleDiameterDm: Double = 0.0,
    // Diagnostic Thresholds
    val minProfileShiftNoUndercut: Double = 0.0,
    val minTeethNoUndercutAtZeroShift: Double = 0.0,
    val minFaceWidthTheoreticalMm: Double = 0.0,
    val minFaceWidthPracticalMm: Double = 0.0
)
