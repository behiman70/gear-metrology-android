package ir.msc.gearmetrology.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.msc.gearmetrology.R
import ir.msc.gearmetrology.domain.AppLanguage
import ir.msc.gearmetrology.domain.GearCalculationResult
import ir.msc.gearmetrology.domain.GearCalculator
import ir.msc.gearmetrology.domain.GearInput
import ir.msc.gearmetrology.domain.GearType
import ir.msc.gearmetrology.domain.HelixHand
import ir.msc.gearmetrology.domain.ValidationIssue
import ir.msc.gearmetrology.domain.ValidationSeverity
import ir.msc.gearmetrology.ui.Localization
import ir.msc.gearmetrology.ui.NumberFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalculatorScreen(
    lang: AppLanguage,
    externalPreset: GearInput?,
    onPresetConsumed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = Localization.get(lang)
    val isFa = (lang == AppLanguage.FA)

    // Form state
    var gearType by rememberSaveable { mutableStateOf(GearType.SPUR) }
    var mnText by rememberSaveable { mutableStateOf("3.0") }
    var zText by rememberSaveable { mutableStateOf("24") }
    var alphaNText by rememberSaveable { mutableStateOf("20.0") }
    var xText by rememberSaveable { mutableStateOf("0.0") }
    var betaText by rememberSaveable { mutableStateOf("15.0") }
    var helixHand by rememberSaveable { mutableStateOf(HelixHand.RIGHT) }
    var faceWidthText by rememberSaveable { mutableStateOf("30.0") }
    var isManualPin by rememberSaveable { mutableStateOf(false) }
    var manualPinText by rememberSaveable { mutableStateOf("5.184") }
    var manualSpanK by rememberSaveable { mutableStateOf<Int?>(null) }
    var decimals by rememberSaveable { mutableIntStateOf(3) }

    // Apply loaded test case preset when triggered from TestCasesScreen
    LaunchedEffect(externalPreset) {
        if (externalPreset != null) {
            gearType = externalPreset.gearType
            mnText = externalPreset.normalModuleMn.toString()
            zText = externalPreset.teethCountZ.toString()
            alphaNText = externalPreset.normalPressureAngleDeg.toString()
            xText = externalPreset.profileShiftX.toString()
            betaText = externalPreset.helixAngleBetaDeg.toString()
            helixHand = externalPreset.helixHand
            faceWidthText = externalPreset.faceWidthB.toString()
            if (externalPreset.manualPinDiameterDd != null) {
                isManualPin = true
                manualPinText = externalPreset.manualPinDiameterDd.toString()
            } else {
                isManualPin = false
            }
            manualSpanK = externalPreset.manualSpanTeethK
            onPresetConsumed()
        }
    }

    // Parse inputs safely
    val mnVal = NumberFormatter.normalizeInput(mnText).toDoubleOrNull() ?: -1.0
    val zVal = NumberFormatter.normalizeInput(zText).toIntOrNull() ?: -1
    val alphaNVal = NumberFormatter.normalizeInput(alphaNText).toDoubleOrNull() ?: -1.0
    val xVal = NumberFormatter.normalizeInput(xText).toDoubleOrNull() ?: 0.0
    val betaVal = NumberFormatter.normalizeInput(betaText).toDoubleOrNull() ?: -1.0
    val bVal = NumberFormatter.normalizeInput(faceWidthText).toDoubleOrNull() ?: -1.0
    val manualPinVal = if (isManualPin) {
        NumberFormatter.normalizeInput(manualPinText).toDoubleOrNull() ?: -1.0
    } else {
        null
    }

    val currentInput = GearInput(
        gearType = gearType,
        normalModuleMn = mnVal,
        teethCountZ = zVal,
        normalPressureAngleDeg = alphaNVal,
        profileShiftX = xVal,
        helixAngleBetaDeg = betaVal,
        helixHand = helixHand,
        faceWidthB = bVal,
        manualPinDiameterDd = manualPinVal,
        manualSpanTeethK = manualSpanK
    )

    val result = GearCalculator.calculate(currentInput)
    val fmt = { v: Double -> NumberFormatter.format(v, decimals) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Gear Type Selector + Decimal Precision Row
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.gearTypeLabel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    AssistChip(
                        onClick = {
                            gearType = GearType.SPUR
                            mnText = "3.0"
                            zText = "24"
                            alphaNText = "20.0"
                            xText = "0.0"
                            betaText = "15.0"
                            helixHand = HelixHand.RIGHT
                            faceWidthText = "30.0"
                            isManualPin = false
                            manualSpanK = null
                        },
                        label = { Text(strings.resetDefaultsBtn, fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    )
                }

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = (gearType == GearType.SPUR),
                        onClick = {
                            gearType = GearType.SPUR
                            manualSpanK = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(strings.spurGear, fontWeight = FontWeight.SemiBold)
                    }
                    SegmentedButton(
                        selected = (gearType == GearType.HELICAL),
                        onClick = {
                            gearType = GearType.HELICAL
                            manualSpanK = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(strings.helicalGear, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Decimal Precision Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = strings.decimalPrecisionLabel,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(2, 3, 4, 5).forEach { dec ->
                            FilterChip(
                                selected = (decimals == dec),
                                onClick = { decimals = dec },
                                label = { Text("${dec}d", fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // 2. Inputs Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = strings.inputsSectionTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Row 1: Normal Module (mn) & Number of Teeth (z)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EngineeringNumericField(
                        value = mnText,
                        onValueChange = {
                            mnText = it
                            manualSpanK = null
                        },
                        label = strings.normalModuleLabel,
                        modifier = Modifier.weight(1f)
                    )
                    EngineeringNumericField(
                        value = zText,
                        onValueChange = {
                            zText = it
                            manualSpanK = null
                        },
                        label = strings.teethCountLabel,
                        isInteger = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Quick standard module chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("1.5", "2.0", "2.5", "3.0", "4.0", "5.0", "6.0", "8.0").forEach { mPreset ->
                        FilterChip(
                            selected = (mnText == mPreset),
                            onClick = {
                                mnText = mPreset
                                manualSpanK = null
                            },
                            label = { Text("m=$mPreset", fontSize = 11.sp) }
                        )
                    }
                }

                // Row 2: Normal Pressure Angle (alpha_n) & Profile Shift (x)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EngineeringNumericField(
                        value = alphaNText,
                        onValueChange = {
                            alphaNText = it
                            manualSpanK = null
                        },
                        label = strings.normalPressureAngleLabel,
                        modifier = Modifier.weight(1f)
                    )
                    EngineeringNumericField(
                        value = xText,
                        onValueChange = {
                            xText = it
                            manualSpanK = null
                        },
                        label = strings.profileShiftLabel,
                        allowNegative = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Helical-only inputs (Helix Angle, Hand, Face Width)
                AnimatedVisibility(visible = (gearType == GearType.HELICAL)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            EngineeringNumericField(
                                value = betaText,
                                onValueChange = {
                                    betaText = it
                                    manualSpanK = null
                                },
                                label = strings.helixAngleLabel,
                                modifier = Modifier.weight(1f)
                            )
                            EngineeringNumericField(
                                value = faceWidthText,
                                onValueChange = { faceWidthText = it },
                                label = strings.faceWidthLabel,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = strings.helixHandLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            SingleChoiceSegmentedButtonRow {
                                SegmentedButton(
                                    selected = (helixHand == HelixHand.RIGHT),
                                    onClick = { helixHand = HelixHand.RIGHT },
                                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                                ) {
                                    Text(strings.handRight, fontSize = 12.sp)
                                }
                                SegmentedButton(
                                    selected = (helixHand == HelixHand.LEFT),
                                    onClick = { helixHand = HelixHand.LEFT },
                                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                                ) {
                                    Text(strings.handLeft, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Pin / Ball Diameter Auto vs Manual Override
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.pinDiameterModeLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isManualPin) strings.pinManualLabel else strings.pinAutoLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isManualPin,
                        onCheckedChange = { checked ->
                            isManualPin = checked
                            if (checked && result.isValid) {
                                manualPinText = NumberFormatter.format(result.pinDiameterUsed, 3)
                            }
                        }
                    )
                }

                if (isManualPin) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EngineeringNumericField(
                            value = manualPinText,
                            onValueChange = { manualPinText = it },
                            label = strings.pinDiameterInputLabel,
                            modifier = Modifier.weight(1f)
                        )
                        if (result.isValid) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        manualPinText = NumberFormatter.format(result.idealPinDiameterDin, 3)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "${strings.useDinIdealBtn} (${NumberFormatter.format(result.idealPinDiameterDin, 3)})",
                                        fontSize = 11.sp
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        manualPinText = NumberFormatter.format(result.standardPinDiameter1728, 3)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "${strings.use1728Btn} (${NumberFormatter.format(result.standardPinDiameter1728, 3)})",
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                } else if (result.isValid) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.pinDiameterUsedLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = NumberFormatter.ltr("${fmt(result.pinDiameterUsed)} mm (DIN: ${fmt(result.idealPinDiameterDin)} mm)"),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // 3. Validation Warnings & Errors Section
        if (result.issues.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                result.issues.forEach { issue ->
                    ValidationBanner(issue = issue, isFa = isFa)
                }
            }
        }

        // 4. Primary Result Card (Wk and M + Copy/Share buttons)
        if (result.isValid) {
            PrimaryResultCard(
                result = result,
                decimals = decimals,
                lang = lang,
                onAdjustSpanK = { newK -> manualSpanK = newK },
                onCopyReport = {
                    val report = Localization.buildShareableReport(result, decimals, lang)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("GearMetrologyReport", report))
                    Toast.makeText(context, strings.copiedToast, Toast.LENGTH_SHORT).show()
                },
                onShareReport = {
                    val report = Localization.buildShareableReport(result, decimals, lang)
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, strings.appTitle)
                        putExtra(Intent.EXTRA_TEXT, report)
                    }
                    context.startActivity(Intent.createChooser(sendIntent, strings.shareReportBtn))
                }
            )

            // 5. Key Intermediate Values & Span Neighbors Card
            IntermediateValuesCard(
                result = result,
                decimals = decimals,
                lang = lang,
                onSelectNeighborK = { selectedK -> manualSpanK = selectedK }
            )
        }

        // 6. Persistent Footer with Mobarakeh Steel Logo & RTL Credit
        BrandFooterCard(lang = lang)
    }
}

@Composable
private fun EngineeringNumericField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isInteger: Boolean = false,
    allowNegative: Boolean = false
) {
    val kbType = when {
        isInteger -> KeyboardType.Number
        allowNegative -> KeyboardType.Decimal
        else -> KeyboardType.Decimal
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textDirection = TextDirection.Ltr
        ),
        keyboardOptions = KeyboardOptions(keyboardType = kbType),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}

@Composable
private fun ValidationBanner(
    issue: ValidationIssue,
    isFa: Boolean
) {
    val isError = (issue.severity == ValidationSeverity.ERROR)
    val bgColor = if (isError) Color(0xFFFEF2F2) else Color(0xFFFFFBEB)
    val borderColor = if (isError) Color(0xFFEF4444) else Color(0xFFF59E0B)
    val titleColor = if (isError) Color(0xFF991B1B) else Color(0xFF92400E)
    val bodyColor = if (isError) Color(0xFF7F1D1D) else Color(0xFF78350F)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.ErrorOutline else Icons.Default.WarningAmber,
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(24.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isFa) issue.titleFa else issue.titleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = titleColor
                )
                Text(
                    text = if (isFa) issue.detailFa else issue.detailEn,
                    fontSize = 12.sp,
                    color = bodyColor,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun PrimaryResultCard(
    result: GearCalculationResult,
    decimals: Int,
    lang: AppLanguage,
    onAdjustSpanK: (Int) -> Unit,
    onCopyReport: () -> Unit,
    onShareReport: () -> Unit
) {
    val strings = Localization.get(lang)
    val fmt = { v: Double -> NumberFormatter.format(v, decimals) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = strings.primaryResultsTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Span Measurement W_k Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = strings.spanMeasurementTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.optimalSpanTeethLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${strings.exactSpanTeethLabel}: ${NumberFormatter.ltr(NumberFormatter.format(result.exactSpanTeethK, 3))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Stepper to inspect k-1, k, k+1 directly
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (result.appliedSpanTeethK > 1) {
                                        onAdjustSpanK(result.appliedSpanTeethK - 1)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "k-1")
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "k = ${result.appliedSpanTeethK}",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    onAdjustSpanK(result.appliedSpanTeethK + 1)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "k+1")
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "W_${result.appliedSpanTeethK}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = NumberFormatter.ltr("${fmt(result.spanMeasurementWk)} mm"),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Measurement Over Pins / Balls M Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.measurementOverPinsTitle,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = if (result.isEvenTeeth) strings.evenTeethBadge else strings.oddTeethBadge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "${strings.pinDiameterUsedLabel}: ${NumberFormatter.ltr("${fmt(result.pinDiameterUsed)} mm")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "M",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = NumberFormatter.ltr("${fmt(result.measurementOverPinsM)} mm"),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Copy & Share Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCopyReport,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.copyReportBtn, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onShareReport,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.shareReportBtn, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun IntermediateValuesCard(
    result: GearCalculationResult,
    decimals: Int,
    lang: AppLanguage,
    onSelectNeighborK: (Int) -> Unit
) {
    val strings = Localization.get(lang)
    val fmt = { v: Double -> NumberFormatter.format(v, decimals) }
    val fmt6 = { v: Double -> NumberFormatter.format(v, 6) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = strings.intermediateValuesTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            IntermediateRow(strings.baseCircleDiameterLabel, "${fmt(result.baseCircleDiameterDb)} mm", highlight = true)
            IntermediateRow(strings.pitchDiameterLabel, "${fmt(result.pitchDiameterD)} mm")
            IntermediateRow(strings.tipDiameterLabel, "${fmt(result.tipDiameterDa)} mm")
            IntermediateRow(strings.rootDiameterLabel, "${fmt(result.rootDiameterDf)} mm")
            IntermediateRow(strings.transverseModuleLabel, "${fmt(result.transverseModuleMt)} mm", highlight = true)
            IntermediateRow(strings.transversePressureAngleLabel, "${fmt(result.transversePressureAngleDeg)}°", highlight = true)
            if (result.input.gearType == GearType.HELICAL) {
                IntermediateRow(strings.baseHelixAngleLabel, "${fmt(result.baseHelixAngleDeg)}°")
                IntermediateRow(strings.minFaceWidthLabel, "${fmt(result.minFaceWidthPracticalMm)} mm")
            }
            IntermediateRow(strings.normalToothThicknessLabel, "${fmt(result.normalToothThicknessSn)} mm")

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            IntermediateRow(strings.invAlphaNLabel, fmt6(result.invAlphaN), highlight = true)
            IntermediateRow(strings.invAlphaTLabel, fmt6(result.invAlphaT), highlight = true)
            IntermediateRow(strings.invAlphaDLabel, fmt6(result.invAlphaD), highlight = true)
            IntermediateRow(strings.pinPressureAngleLabel, "${fmt(result.pinPressureAngleDeg)}°")
            IntermediateRow(strings.pinCenterCircleLabel, "${fmt(result.pinCenterCircleDiameterDm)} mm")
            IntermediateRow(strings.minProfileShiftLabel, NumberFormatter.format(result.minProfileShiftNoUndercut, 4))

            if (result.spanNeighbors.size > 1) {
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = strings.spanNeighborTableTitle,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    result.spanNeighbors.forEach { row ->
                        val isSelected = (row.k == result.appliedSpanTeethK)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectNeighborK(row.k) }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (row.isOptimal) "k = ${row.k} ★" else "k = ${row.k}",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = NumberFormatter.ltr("${fmt(row.wkMm)} mm"),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IntermediateRow(
    label: String,
    valueAscii: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (highlight) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = NumberFormatter.ltr(valueAscii),
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BrandFooterCard(lang: AppLanguage) {
    val strings = Localization.get(lang)
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                modifier = Modifier.size(46.dp)
            ) {
                Box(modifier = Modifier.padding(4.dp), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_mobarakeh_steel),
                        contentDescription = "کارگاه ساخت فولاد مبارکه",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = "کارگاه ساخت فولاد مبارکه",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Right
                    )
                }
                Text(
                    text = strings.creatorCreditSub,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
