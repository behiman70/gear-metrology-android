package ir.msc.gearmetrology

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.msc.gearmetrology.domain.AppLanguage
import ir.msc.gearmetrology.domain.GearInput
import ir.msc.gearmetrology.ui.Localization
import ir.msc.gearmetrology.ui.screens.AboutScreen
import ir.msc.gearmetrology.ui.screens.CalculatorScreen
import ir.msc.gearmetrology.ui.screens.FormulaReferenceScreen
import ir.msc.gearmetrology.ui.screens.SplashScreen
import ir.msc.gearmetrology.ui.screens.TestCasesScreen
import ir.msc.gearmetrology.ui.theme.GearMetrologyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GearMetrologyTheme {
                GearMetrologyAppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearMetrologyAppRoot() {
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var language by rememberSaveable { mutableStateOf(AppLanguage.FA) }
    var currentTab by rememberSaveable { mutableIntStateOf(0) }
    var loadedPreset by remember { mutableStateOf<GearInput?>(null) }

    if (showSplash) {
        SplashScreen(onFinished = { showSplash = false })
        return
    }

    val strings = Localization.get(language)
    val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = strings.appTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = "کارگاه ساخت فولاد مبارکه",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    actions = {
                        OutlinedButton(
                            onClick = {
                                language = if (language == AppLanguage.FA) AppLanguage.EN else AppLanguage.FA
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language"
                            )
                            Text(
                                text = if (language == AppLanguage.FA) " EN" else " فارسی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors()
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = (currentTab == 0),
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                        label = { Text(strings.tabCalculator) }
                    )
                    NavigationBarItem(
                        selected = (currentTab == 1),
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.Functions, contentDescription = null) },
                        label = { Text(strings.tabFormulas) }
                    )
                    NavigationBarItem(
                        selected = (currentTab == 2),
                        onClick = { currentTab = 2 },
                        icon = { Icon(Icons.Default.FactCheck, contentDescription = null) },
                        label = { Text(strings.tabTestCases) }
                    )
                    NavigationBarItem(
                        selected = (currentTab == 3),
                        onClick = { currentTab = 3 },
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        label = { Text(strings.tabAbout) }
                    )
                }
            }
        ) { innerPadding ->
            when (currentTab) {
                0 -> CalculatorScreen(
                    lang = language,
                    externalPreset = loadedPreset,
                    onPresetConsumed = { loadedPreset = null },
                    modifier = Modifier.padding(innerPadding)
                )
                1 -> FormulaReferenceScreen(
                    lang = language,
                    modifier = Modifier.padding(innerPadding)
                )
                2 -> TestCasesScreen(
                    lang = language,
                    onLoadTestCase = { preset ->
                        loadedPreset = preset
                        currentTab = 0
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                3 -> AboutScreen(
                    lang = language,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
