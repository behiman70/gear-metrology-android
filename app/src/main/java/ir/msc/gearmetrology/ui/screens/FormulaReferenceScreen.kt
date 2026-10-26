package ir.msc.gearmetrology.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.msc.gearmetrology.domain.AppLanguage

private data class FormulaSection(
    val titleFa: String,
    val titleEn: String,
    val descriptionFa: String,
    val descriptionEn: String,
    val equations: List<String>
)

private val FORMULA_SECTIONS = listOf(
    FormulaSection(
        titleFa = "۱. تبدیل پارامترهای نرمال به صفحه مقطع (چرخ‌دنده ساده و مارپیچ)",
        titleEn = "1. Normal-to-Transverse Plane Conversions (DIN 3960 / ISO 21771)",
        descriptionFa = "در چرخ‌دنده‌های ساده زاویه مارپیچ β = 0° بوده و مقادیر مقطع با نرمال برابرند. در چرخ‌دنده‌های مارپیچ (سیستم نرمال):",
        descriptionEn = "For spur gears β = 0° so transverse parameters equal normal parameters. For normal-system helical gears:",
        equations = listOf(
            "m_t = m_n / cos(β)",
            "tan(α_t) = tan(α_n) / cos(β)",
            "sin(β_b) = sin(β) · cos(α_n)",
            "d = z · m_t = (z · m_n) / cos(β)",
            "d_b = d · cos(α_t) = m_t · z · cos(α_t)",
            "d_a = d + 2 · m_n · (1 + x)",
            "d_f = d - 2 · m_n · (1.25 - x)",
            "s_n = m_n · (π / 2 + 2 · x · tan(α_n))"
        )
    ),
    FormulaSection(
        titleFa = "۲. تابع اینولوت و حل عددی معکوس اینولوت (Newton-Raphson)",
        titleEn = "2. Involute Function & Inverse Involute Solver",
        descriptionFa = "تابع اینولوت بر حسب رادیان تعریف می‌شود و معکوس آن با الگوریتم نیوتن-رافسون تا دقت 10⁻¹³ رادیان محاسبه می‌گردد:",
        descriptionEn = "The involute function takes α in radians; its inverse is solved via Newton-Raphson iteration to 1e-13 rad accuracy:",
        equations = listOf(
            "inv(α) = tan(α) - α   [α in rad]",
            "α_0 = (3 · y)^(1/3) - 0.4 · y",
            "α_{i+1} = α_i - (tan(α_i) - α_i - y) / tan²(α_i)"
        )
    ),
    FormulaSection(
        titleFa = "۳. اندازه دهانه (طول مماس مبنا W_k) و تعداد دندانه بهینه (k)",
        titleEn = "3. Span Measurement (Base Tangent Length W_k) & Optimal Teeth (k)",
        descriptionFa = "تعداد دندانه بهینه دهانه (k) به‌گونه‌ای تعیین می‌شود که تماس فک‌های کولیس نزدیک دایره میانی پروفیل اصلاح‌شده (d_m = d + 2·x·m_n) رخ دهد:",
        descriptionEn = "Optimal tooth span k places caliper jaw contact near the shifted mid-flank cylinder (d_m = d + 2·x·m_n):",
        equations = listOf(
            "cos(α_M) = (z · cos(α_t)) / (z + 2 · x · cos(β))",
            "k_exact = (z / π) · [ tan(α_M) / cos²(β_b) - (2·x·tan(α_n))/z - inv(α_t) ] + 0.5",
            "k = round(k_exact)",
            "W_k = m_n · cos(α_n) · [ π · (k - 0.5) + z · inv(α_t) ] + 2 · x · m_n · sin(α_n)",
            "b_min = W_k · sin(β_b) + 2.0 mm   (حداقل پهنای دندانه مارپیچ)"
        )
    ),
    FormulaSection(
        titleFa = "۴. اندازه‌گیری روی پین یا ساچمه (M) برای تعداد دندانه زوج و فرد",
        titleEn = "4. Measurement Over Pins / Balls (M) — Even & Odd Tooth Counts",
        descriptionFa = "ابتدا زاویه فشار مرکز پین/ساچمه (α_D) از رابطه اینولوت فضای دندانه به دست می‌آید و سپس قطر دایره مراکز پین (d_M) و اندازه M محاسبه می‌شود:",
        descriptionEn = "First the pin center pressure angle α_D is found via the space-width involute relation, giving pin center circle d_M and M:",
        equations = listOf(
            "inv(α_D) = d_D / (m_n · z · cos(α_n)) - π / (2·z) + (2 · x · tan(α_n)) / z + inv(α_t)",
            "α_D = inv⁻¹( inv(α_D) )",
            "d_M = d_b / cos(α_D)",
            "M (even z / زوج) = d_M + d_D = d_b / cos(α_D) + d_D",
            "M (odd z / فرد)  = d_M · cos(90° / z) + d_D = (d_b / cos(α_D)) · cos(π / (2·z)) + d_D"
        )
    ),
    FormulaSection(
        titleFa = "۵. قطر پین/ساچمه ایده‌آل (d_D) و شرط عدم وقوع آندرکات (x_min)",
        titleEn = "5. Ideal Pin Diameter (d_D) & Undercut Limit (x_min)",
        descriptionFa = "قطر پین ایده‌آل به روش DIN 3960 (تماس در دایره میانی پروفیل) یا ضریب استاندارد کارگاهی 1.728·m_n و شرط عدم آندرکات با ابزار استاندارد (h_a0* = 1.0):",
        descriptionEn = "Ideal pin diameter per DIN 3960 mid-flank contact (or standard 1.728·m_n series) and non-undercut boundary for h_a0* = 1.0:",
        equations = listOf(
            "α_D,ideal = tan(α_M) + π / (2·z) - (2 · x · tan(α_n)) / z - inv(α_t)",
            "d_D,ideal = m_n · z · cos(α_n) · ( tan(α_D,ideal) - tan(α_M) )",
            "d_D,std   = 1.728 · m_n",
            "x_min (no undercut) = 1.0 - (z · sin²(α_t)) / (2 · cos(β))"
        )
    )
)

@Composable
fun FormulaReferenceScreen(
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isFa = (lang == AppLanguage.FA)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (isFa) "مرجع فرمول‌های مهندسی (DIN 3960 / ISO 21771)" else "Engineering Formula Reference (DIN 3960 / ISO 21771)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = if (isFa) {
                "تمامی محاسبات این نرم‌افزار بر پایه روابط دقیق هندسه اینولوت در سیستم مدول نرمال (Normal System) برای چرخ‌دنده‌های خارجی ساده و مارپیچ انجام می‌شود."
            } else {
                "All calculations use exact involute gear geometry in the normal module system for external spur and helical gears."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FORMULA_SECTIONS.forEach { sec ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isFa) sec.titleFa else sec.titleEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isFa) sec.descriptionFa else sec.descriptionEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Always render mathematical formulas in LTR monospace block
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                sec.equations.forEach { eq ->
                                    Text(
                                        text = eq,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
