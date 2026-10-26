# محاسبه‌گر تخصصی چرخ‌دنده (W & M) — کارگاه ساخت فولاد مبارکه
# Gear Metrology Calculator (Span W & Over Pins/Balls M) — Native Android App

A 100% offline native Android engineering calculator (**Kotlin**, **Jetpack Compose**, **Material 3**, **minSdk 26**) developed for **کارگاه ساخت فولاد مبارکه** (Mobarakeh Steel Company — Manufacturing Workshop). It computes the **Span Measurement / Base Tangent Length ($W_k$)** and **Measurement Over Pins / Balls ($M$)** for external **spur** and **helical** involute gears in the normal module system according to **DIN 3960** and **ISO 21771**.

---

## 1. Project Architecture & Structure

```text
GearMetrologyApp/
├── .github/workflows/
│   └── android-release.yml                 # Automated CI workflow to build unit tests & signed release APK
├── app/
│   ├── build.gradle.kts                    # Android app module config (minSdk 26, targetSdk 35, Compose, release signing)
│   ├── proguard-rules.pro                  # R8 / ProGuard optimization rules
│   ├── keystore/
│   │   └── release-keystore.jks            # Pre-generated 2048-bit RSA release keystore
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml         # Strictly offline (0 network permissions), full RTL support
│       │   ├── ic_launcher-playstore.png   # 512x512 high-contrast custom launcher icon
│       │   ├── java/ir/msc/gearmetrology/
│       │   │   ├── MainActivity.kt         # App root, dynamic FA (RTL) / EN (LTR) layout switching, navigation
│       │   │   ├── domain/
│       │   │   │   ├── GearModels.kt       # Data models, validation codes, calculation result structures
│       │   │   │   ├── GearCalculator.kt   # DIN 3960 / ISO 21771 calculation engine & Newton-Raphson inv^-1 solver
│       │   │   │   └── TestCases.kt        # 5 verified engineering reference cases (Spur/Helical, Even/Odd z, Warnings)
│       │   │   └── ui/
│       │   │       ├── Strings.kt          # Bilingual FA/EN dictionary, Persian numeral normalizer, report formatter
│       │   │       ├── theme/Theme.kt      # Material 3 Light/Dark industrial steel & amber theme
│       │   │       └── screens/
│       │   │           ├── SplashScreen.kt           # Splash screen with Mobarakeh Steel logo & RTL workshop credit
│       │   │           ├── CalculatorScreen.kt       # Single-screen calculator, warning banners, result card, copy/share
│       │   │           ├── FormulaReferenceScreen.kt # Complete DIN 3960 / ISO 21771 mathematical reference
│       │   │           ├── TestCasesScreen.kt        # Live verification table & one-tap preset loader
│       │   │           └── AboutScreen.kt            # Mobarakeh Steel logo, app specs, and RTL developer credit
│       │   └── res/
│       │       ├── drawable/
│       │       │   ├── ic_launcher_background.xml    # Custom adaptive icon navy blueprint grid background
│       │       │   └── ic_launcher_foreground.xml    # Custom adaptive icon gear + measuring pins + caliper jaws
│       │       ├── drawable-nodpi/
│       │       │   └── logo_mobarakeh_steel.png      # Attached Mobarakeh Steel Company official emblem
│       │       ├── mipmap-anydpi-v26/
│       │       │   ├── ic_launcher.xml               # Adaptive icon definition (API 26+)
│       │       │   └── ic_launcher_round.xml         # Round adaptive icon definition (API 26+)
│       │       ├── mipmap-mdpi/ ... mipmap-xxxhdpi/  # Raster launcher icons across all screen densities
│       │       ├── values/strings.xml & themes.xml
│       │       └── values-fa/strings.xml
│       └── test/java/ir/msc/gearmetrology/domain/
│           └── GearCalculatorTest.kt       # JUnit 4 unit test suite verifying all formulas & test cases
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/gradle-wrapper.properties
└── generate-keystore.sh
```

---

## 2. Mathematical Model & Standard Involute Gear Formulas (DIN 3960 / ISO 21771)

### 2.1 Transverse & Base Geometry (Normal Module System)
For a spur gear ($\beta = 0^\circ$) or helical gear ($\beta > 0^\circ$) with normal module $m_n$, tooth count $z$, normal pressure angle $\alpha_n$, and profile shift coefficient $x$:

| Quantity | Symbol | Formula |
| :--- | :--- | :--- |
| Transverse Module | $m_t$ | $m_t = \frac{m_n}{\cos\beta}$ |
| Transverse Pressure Angle | $\alpha_t$ | $\tan\alpha_t = \frac{\tan\alpha_n}{\cos\beta}$ |
| Base Helix Angle | $\beta_b$ | $\sin\beta_b = \sin\beta \cdot \cos\alpha_n$ |
| Reference Pitch Diameter | $d$ | $d = z \cdot m_t = \frac{z \cdot m_n}{\cos\beta}$ |
| Base Circle Diameter | $d_b$ | $d_b = d \cdot \cos\alpha_t$ |
| Tip (Outside) Diameter | $d_a$ | $d_a = d + 2 m_n (1 + x)$ |
| Root Diameter | $d_f$ | $d_f = d - 2 m_n (1.25 - x)$ |
| Normal Reference Tooth Thickness | $s_n$ | $s_n = m_n \left(\frac{\pi}{2} + 2 x \tan\alpha_n\right)$ |
| Involute Function | $\text{inv}\,\alpha$ | $\text{inv}\,\alpha = \tan\alpha - \alpha \quad (\alpha\text{ in radians})$ |

### 2.2 Optimal Span Tooth Count ($k$) & Span Measurement ($W_k$)
To ensure caliper jaws contact the active involute flanks near the mid-flank reference cylinder $d_m = d + 2 x m_n$:

$$\cos\alpha_M = \frac{z \cos\alpha_t}{z + 2 x \cos\beta}$$

$$k_{\text{exact}} = \frac{z}{\pi} \left( \frac{\tan\alpha_M}{\cos^2\beta_b} - \frac{2 x \tan\alpha_n}{z} - \text{inv}\,\alpha_t \right) + 0.5, \qquad k = \text{round}(k_{\text{exact}})$$

$$W_k = m_n \cos\alpha_n \left[ \pi (k - 0.5) + z \,\text{inv}\,\alpha_t \right] + 2 x m_n \sin\alpha_n$$

For helical gears, the minimum required face width to seat caliper jaws across the base tangent length is:

$$b_{\min} = W_k \sin\beta_b + 2.0\text{ mm}$$

### 2.3 Measurement Over Pins / Balls ($M$) for Even and Odd Tooth Counts
Given a pin or ball diameter $d_D$ (auto-calculated via standard $1.728 \cdot m_n$ or exact DIN 3960 mid-flank contact, or manually overridden), the involute of the pressure angle $\alpha_D$ at the pin center circle is:

$$\text{inv}\,\alpha_D = \frac{d_D}{m_n z \cos\alpha_n} - \frac{\pi}{2z} + \frac{2 x \tan\alpha_n}{z} + \text{inv}\,\alpha_t$$

Solving $\alpha_D = \text{inv}^{-1}(\text{inv}\,\alpha_D)$ via Newton-Raphson iteration (with cubic initial guess $\alpha_0 = (3y)^{1/3} - 0.4y$ to $10^{-13}\text{ rad}$ tolerance) yields the pin center circle diameter $d_M = \frac{d_b}{\cos\alpha_D}$, from which:

- **Even number of teeth ($z$ even)**:
  $$M = d_M + d_D = \frac{d_b}{\cos\alpha_D} + d_D$$
- **Odd number of teeth ($z$ odd)**:
  $$M = d_M \cos\left(\frac{90^\circ}{z}\right) + d_D = \frac{d_b}{\cos\alpha_D} \cos\left(\frac{\pi}{2z}\right) + d_D$$

---

## 3. Built-in Verification Test Cases Summary

| # | Case Description | Inputs ($m_n, z, \alpha_n, x, \beta, b, d_D$) | Optimal $k$ | Span $W_k$ (mm) | Over Pins $M$ (mm) | Base Circle $d_b$ (mm) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | Spur Gear (Even $z$) | $m_n=3.0, z=24, \alpha_n=20^\circ, x=0, d_D=5.184$ | `3` | `23.1494` | `79.1991` | `67.6579` |
| **2** | Spur Gear (Odd $z$, Shifted) | $m_n=4.0, z=19, \alpha_n=20^\circ, x=+0.25, d_D=7.000$ | `3` | `31.2698` | `87.1189` | `71.4166` |
| **3** | Helical Gear RH (Even $z$) | $m_n=2.5, z=32, \alpha_n=20^\circ, x=+0.10, \beta=15^\circ, b=28, d_D=4.500$ | `5` | `34.6194` | `89.9274` | `77.5025` |
| **4** | Helical Gear LH (Odd $z$) | $m_n=5.0, z=21, \alpha_n=20^\circ, x=0, \beta=12^\circ, b=50, d_D=8.640$ | `3` | `38.4679` | `119.0178` | `100.6065` |
| **5** | Warning Verification Case | $m_n=3.0, z=12, \alpha_n=20^\circ, x=0, \beta=25^\circ, b=8, d_D=5.184$ | `2` | `14.8058` | `46.5177` | `36.8266` |

---

## 4. Step-by-Step Build & Signed Release APK Instructions

### Prerequisites
- **JDK 17** (e.g., Eclipse Temurin 17 or Android Studio bundled JBR 17)
- **Android SDK Platform 35** (`platforms;android-35` and `build-tools;35.0.0`)

### Option A: Build via Android Studio (Recommended for Interactive Inspection)
1. Extract `GearMetrology_Android_Project.zip` to a local folder.
2. Open **Android Studio Ladybug (2024.2.1) or newer** and select **Open** -> choose the `GearMetrologyApp` directory.
3. Wait for Gradle Sync to finish (it will automatically configure the Gradle 8.9 wrapper and download dependencies).
4. To run directly on an emulator or physical device, click **Run ('app')**.
5. To build the **Signed Release APK**:
   - From the top menu, select **Build > Assemble Project** or run the `assembleRelease` Gradle task.
   - Because `app/build.gradle.kts` is already pre-configured with the bundled production keystore at `app/keystore/release-keystore.jks`, Gradle automatically signs the release APK using V1 + V2 + V3 signing!
   - Retrieve your signed APK at:
     `app/build/outputs/apk/release/app-release.apk`

### Option B: Build from the Command Line (CLI)
1. Extract `GearMetrology_Android_Project.zip` and `cd GearMetrologyApp`.
2. Ensure `ANDROID_HOME` points to your Android SDK installation (or add `sdk.dir=/path/to/Android/Sdk` to `local.properties`).
3. Run the JUnit 4 verification suite:
   ```bash
   gradle testDebugUnitTest
   ```
4. Assemble the signed release APK:
   ```bash
   gradle assembleRelease
   ```
5. Verify the APK signature using `apksigner`:
   ```bash
   $ANDROID_HOME/build-tools/35.0.0/apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
   ```

### Option C: One-Click Cloud Build via GitHub Actions (No Local Android SDK Required)
1. Push the extracted `GearMetrologyApp` folder to a GitHub repository.
2. The included `.github/workflows/android-release.yml` workflow will automatically run on push (or via **Actions > Build Signed Release APK > Run workflow**).
3. Download the compiled, signed `app-release.apk` directly from the workflow's **Artifacts** section (`GearMetrology-Signed-Release-APK`).

### Keystore & Signing Credentials Reference
A pre-generated 2048-bit RSA keystore is included at `app/keystore/release-keystore.jks` with the following default credentials (which can also be overridden via environment variables `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` or regenerated via `./generate-keystore.sh`):
- **Keystore Path**: `app/keystore/release-keystore.jks`
- **Key Alias**: `gear_metrology_key`
- **Store Password / Key Password**: `mscGear2026!`
- **Certificate Subject (`dname`)**: `CN=Mobarakeh Steel Manufacturing Workshop, OU=Manufacturing Workshop, O=Mobarakeh Steel Company, L=Mobarakeh, ST=Isfahan, C=IR`
