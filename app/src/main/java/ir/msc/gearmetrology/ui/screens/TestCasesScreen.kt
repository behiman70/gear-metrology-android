package ir.msc.gearmetrology.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.msc.gearmetrology.domain.AppLanguage
import ir.msc.gearmetrology.domain.BuiltInTestCase
import ir.msc.gearmetrology.domain.GearCalculator
import ir.msc.gearmetrology.domain.GearInput
import ir.msc.gearmetrology.domain.TestCases
import ir.msc.gearmetrology.ui.Localization
import ir.msc.gearmetrology.ui.NumberFormatter
import kotlin.math.abs

@Composable
fun TestCasesScreen(
    lang: AppLanguage,
    onLoadTestCase: (GearInput) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFa = (lang == AppLanguage.FA)
    val strings = Localization.get(lang)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (isFa) "مثال‌های مرجع و صحت‌سنجی محاسبات (Test Cases)" else "Built-in Verification Test Cases",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = if (isFa) {
                "موارد زیر با مقادیر مرجع استاندارد DIN 3960 مقایسه و راستی‌آزمایی شده‌اند. با لمس دکمه هر کارت، پارامترهای آن مستقیماً در صفحه ماشین‌حساب بارگذاری می‌شوند."
            } else {
                "Each case below is verified live against DIN 3960 reference outputs. Tap 'Load into Calculator' on any card to inspect all intermediate values."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TestCases.ALL.forEach { tc ->
            TestCaseCard(
                tc = tc,
                isFa = isFa,
                loadBtnText = strings.loadTestCaseBtn,
                onLoad = { onLoadTestCase(tc.input) }
            )
        }
    }
}

@Composable
private fun TestCaseCard(
    tc: BuiltInTestCase,
    isFa: Boolean,
    loadBtnText: String,
    onLoad: () -> Unit
) {
    val live = GearCalculator.calculate(tc.input)
    val verified = live.isValid &&
        live.optimalSpanTeethK == tc.expectedK &&
        abs(live.spanMeasurementWk - tc.expectedWkMm) < 1e-3 &&
        abs(live.measurementOverPinsM - tc.expectedMMm) < 1e-3

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFa) tc.titleFa else tc.titleEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (verified) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (verified) Color(0xFF15803D) else Color(0xFFB91C1C)
                        )
                        Text(
                            text = if (isFa) "تأیید شده" else "VERIFIED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (verified) Color(0xFF15803D) else Color(0xFFB91C1C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = if (isFa) tc.subtitleFa else tc.subtitleEn,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isFa) tc.notesFa else tc.notesEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "k   = ${live.optimalSpanTeethK} (expected: ${tc.expectedK})",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "W_k = ${NumberFormatter.format(live.spanMeasurementWk, 4)} mm (expected: ${NumberFormatter.format(tc.expectedWkMm, 4)} mm)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "M   = ${NumberFormatter.format(live.measurementOverPinsM, 4)} mm (expected: ${NumberFormatter.format(tc.expectedMMm, 4)} mm)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "d_b = ${NumberFormatter.format(live.baseCircleDiameterDb, 4)} mm (expected: ${NumberFormatter.format(tc.expectedDbMm, 4)} mm)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLoad,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Text(
                    text = loadBtnText,
                    modifier = Modifier.padding(start = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
