# Switch App Typography to Bold Outfit Font Family

Replace the current thin font with **Outfit**, an energetic, punchy geometric typeface with naturally thick stroke weights, prominent letterforms, and bold headings across all Balay-Inn screens.

---

### User Review & Critical Decisions

- **Confirmed Typeface**: **Outfit** (Geometric sans-serif with strong stroke presence).
- **Weight Calibration**: Downloaded authentic individual weight TTF files (`outfit_regular.ttf`, `outfit_medium.ttf`, `outfit_semibold.ttf`, `outfit_bold.ttf`, `outfit_extrabold.ttf`, `outfit_black.ttf`) into `app/src/main/res/font/`.
- **Targeted Application**: Headings, display banners, and section titles are set to **Black (900)** and **ExtraBold (800)** weights, while body text and labels remain clean and regular.

---

## 1. Overview & Core Concept

- **Problem Identified**: The previous single TTF asset only provided regular 400 glyphs, causing requested bold/extrabold weights to fall back to regular thickness.
- **Solution**: Bundled authentic TrueType files for each weight (Regular 400, Medium 500, SemiBold 600, Bold 700, ExtraBold 800, Black 900) so Android renders true bold letterforms.
- **Key Value**: Headings and titles (*Description*, *Unit Specifications*, *Amenities & Rules*, *Explore Neighborhood*, *Host*, property titles, and rental prices) are unmistakably thick, bold, and punchy, while body paragraphs and labels stay uncluttered.

---

## 2. Typographic Hierarchy with Outfit Multi-Weight

```
┌─────────────────────────────────────────────────────────────┐
│ DISPLAY & SCREEN HEADINGS                                   │
│ Outfit Black (900) • 20sp - 32sp                            │
│ Real TrueType Black glyphs, ultra-thick, heavy presence     │
├─────────────────────────────────────────────────────────────┤
│ PROPERTY TITLES & SECTION HEADERS                           │
│ Outfit Black (900) / ExtraBold (800) • 15.5sp - 18sp        │
│ Solid, heavy, and distinct from content underneath          │
├─────────────────────────────────────────────────────────────┤
│ PRICING & RENT CALLOUTS                                     │
│ Outfit Black (900) • 18.5sp - 26sp                          │
│ Maximum punch with high contrast against body text          │
├─────────────────────────────────────────────────────────────┤
│ BODY TEXT & DESCRIPTIONS (Clean & Balanced)                 │
│ Outfit Normal (400) • 12sp - 15sp                           │
│ Natural readability without visual heaviness                │
├─────────────────────────────────────────────────────────────┤
│ BADGES, LABELS & PILLS                                      │
│ Outfit Medium (500) • 10.5sp - 13sp                         │
│ Crisp, neat, and readable at compact touch targets          │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Execution Summary

- **Font Assets**: Bundled authentic weight files directly in `app/src/main/res/font/`:
  - `outfit_regular.ttf` (Weight 400)
  - `outfit_medium.ttf` (Weight 500)
  - `outfit_semibold.ttf` (Weight 600)
  - `outfit_bold.ttf` (Weight 700)
  - `outfit_extrabold.ttf` (Weight 800)
  - `outfit_black.ttf` (Weight 900)
- **Typography Tokens (`Type.kt`)**: Mapped `OutfitFontFamily` across the authentic weight resources. Configured `display*`, `headline*`, and `title*` with `FontWeight.Black` and `FontWeight.ExtraBold`, while keeping `body*` and `label*` at `Normal` and `Medium`.
- **Screen Verification**: Screen headings across Property Details, Cards, and Home categories now display with authentic heavy bold styling.
- **Verification**: Verified clean Android compilation with `compile_applet` and confirmed all unit tests pass with `testDebugUnitTest` (`BUILD SUCCESSFUL in 16s`).
