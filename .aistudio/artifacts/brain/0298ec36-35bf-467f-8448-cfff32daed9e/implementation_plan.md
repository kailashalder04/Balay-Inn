# Switch App Typography to Inter Font Family

Establish a crisp, highly legible, and neutral interface aesthetic across the entire Balay-Inn application by integrating the **Inter** font family into the Material 3 typography system.

---

### User Review & Critical Decisions

- **Confirmed Decision**: Switched primary app typeface to **Inter** for a clean, neutral, and high-clarity UI appearance.
- **Installation Standard**: Downloaded static `inter.ttf` directly to `app/src/main/res/font/` using the local `font-util` tool.
- **Material 3 Scope**: Configured all 15 M3 typography tokens in `Type.kt` using `InterFontFamily`.

---

## 1. Overview & Core Concept

- **What It Delivers**: A unified, professional, and utilitarian typography system tailored for high-density listing apps.
- **Target Audience**: Tenants, property owners, brokers, and administrators navigating complex rental data, multi-tiered price breakdowns, location details, and unit specifications.
- **Key Value**: Inter features a tall x-height, distinct letterforms, and optimized kerning for small text and numerical figures, making price schedules, map callouts, specification tags, and filter chips significantly easier to scan quickly.

---

## 2. User Experience & Visual Design

### Typographic Hierarchy with Inter

Inter delivers clear visual hierarchy between headers, tabular property data, and metadata:

```
┌─────────────────────────────────────────────────────────────┐
│ DISPLAY / HERO HEADINGS                                     │
│ Inter Bold (24sp - 32sp) • Negative letter spacing (-0.2sp) │
├─────────────────────────────────────────────────────────────┤
│ PROPERTY TITLES & SECTION HEADERS                           │
│ Inter SemiBold (16sp - 20sp) • Snappy, compact line heights │
├─────────────────────────────────────────────────────────────┤
│ NUMERICAL VALUES & PRICING                                  │
│ Inter ExtraBold (18sp - 26sp) • High contrast against "/mo" │
├─────────────────────────────────────────────────────────────┤
│ BODY & DESCRIPTIONS                                         │
│ Inter Regular (13.5sp - 15sp) • Balanced 1.4x line-height   │
├─────────────────────────────────────────────────────────────┤
│ SPEC TAGS, BADGES & SYSTEM LABELS                           │
│ Inter Medium / SemiBold (10.5sp - 12sp) • Crisp rendering   │
└─────────────────────────────────────────────────────────────┘
```

- **Visual Tone**: Modern, objective, trustworthy, and sleek.
- **Dark & Light Mode Harmony**: Consistent font weight rendering across slate dark backgrounds and crisp light containers.

---

## 3. Key Product Decisions & Trade-Offs

- **Inter vs. Manrope**:
  - *Chosen*: Inter.
  - *Why*: Balay-Inn features dense specifications (floor area, room counts, bathroom counts, barangay/province selectors, electric provider schedules, move-in cash breakdown tables). Inter's neutral tone and tall x-height maximize data readability and prevent text clutter in compact card layouts.
- **Local TTF Resource vs. Downloadable Fonts**:
  - *Chosen*: Static bundled font via `font-util install "Inter" --target app/src/main/res/font`.
  - *Why*: Eliminates Google Play Services network latency, avoids runtime downloadable font initialization failures in emulator containers, and ensures offline capability.

---

## 4. Execution Summary

- **Font Asset**: Downloaded `inter.ttf` to `app/src/main/res/font/`.
- **Typography Scale**: Configured `InterFontFamily` in `Type.kt` mapped to `Light`, `Normal`, `Medium`, `SemiBold`, `Bold`, and `ExtraBold`.
- **Theme Integration**: All 15 M3 typography tokens updated and active through `MaterialTheme` in `Theme.kt`.
- **Verification**: Verified clean Android compilation with `compile_applet` and executed Robolectric/unit tests via `testDebugUnitTest` with 100% passing results.
