# Property Detail Boxes Background Color Update to #3783BD

Update the background color of all property detail screen boxes, cards, and bars in Light mode to the specified vibrant Steel Blue shade `#3783BD` (`Color(0xFF3783BD)` / `PropertyBoxBg`), while optimizing text and icon contrast for effortless legibility, and preserving `#1E2E40` in Dark mode.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following parameters were confirmed during interactive clarification:

- **Confirmed Decision 1 (Scope of Theme)**: Light mode only.
- **Confirmed Decision 2 (Color Value)**: `#3783BD` (`Color(0xFF3783BD)`), assigned to `PropertyBoxBg` in `Color.kt`.
- **Confirmed Decision 3 (Readability & Contrast Optimization)**: Because `#3783BD` is a rich medium blue, container text and icons will use crisp white (`Color.White` / `#F8FAFC`) and soft tinted accents in Light mode to ensure WCAG 2.1 AA accessibility (4.5:1+ contrast ratio), preventing dark text from blending into the blue background.
- **Confirmed Decision 4 (Dark Mode Unchanged)**: Dark mode maintains solid dark slate (`#1E2E40`) for ocular comfort.

---

### 1. Overview & Core Concept

- **What It Does**: Transforms the container background of all property detail boxes, cards, specifications, and amenity chips into a vibrant, branded Steel Blue tone (`#3783BD`), giving the property page a cohesive, modern visual identity.
- **Target Audience / Persona**: Renters viewing property details who appreciate bold visual sectioning and clear content grouping.
- **Key Value**: Delivers the user's exact requested color tone (`#3783BD`) across all property detail components while maintaining sharp readability.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Viewing Property Details in Light Mode**:
     - **Bed Availability & Gender Rule Boxes**: `#3783BD` surface fill with high-contrast text and green availability indicator.
     - **Location & Landmark Box**: `#3783BD` container with white text and map pin icon.
     - **Description Card**: `#3783BD` container with crisp white description text.
     - **Overview Specs Bar**: `#3783BD` bar with clean white metrics (Rooms, Bath/CR, Furnished).
     - **Room & Bedspace Configuration Cards**: `#3783BD` outer card fills with white headers and distinct nested room chips.
     - **Detailed Specifications Card**: `#3783BD` container with white spec row text and icons.
     - **Amenities & Compound Features Chips**: `#3783BD` pill badges with white checkmarks.
     - **Host Information Card & Contact Channel**: `#3783BD` container with white host title and contact details.
     - **Neighbourhood Exploration Card**: `#3783BD` outer card with light tabs and transit details.
  2. **Viewing in Dark Mode**:
     - Continues to display `#1E2E40` dark slate containers.

- **Color Tokens & Contrast**:
  - `PropertyBoxBg`: `Color(0xFF3783BD)`.
  - Box Text on `#3783BD`: `Color.White` / `Color(0xFFF8FAFC)` (high contrast).
  - Box Border: `BorderStroke(1.dp, Color(0xFF2E6896))` or subtle white/light border overlay for crisp edge definition.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Centralized Palette Token (`PropertyBoxBg`)**:
  - *Chosen Approach*: Update `val PropertyBoxBg = Color(0xFF3783BD)` in `Color.kt`.
  - *Why*: All 9 property detail components already reference `PropertyBoxBg`. Updating this token ensures instantaneous consistency across all components.

- **Decision 2: Text Contrast Inversion for #3783BD**:
  - *Chosen Approach*: Ensure all body, spec, and description text inside `#3783BD` containers render with white/light text in light mode (`if (isDark) Color(0xFFF8FAFC) else Color.White` or `Color(0xFFF0F9FF)`), rather than dark navy text (`#0F263B`).
  - *Why*: `#3783BD` has medium-deep luminance (~53%). Dark text would be illegible. White text achieves high contrast and great readability.

---

### 4. Technical Architecture & Component Mapping

```
┌────────────────────────────────────────────────────────────────────────┐
│                        com.example.ui.theme.Color                      │
│            val PropertyBoxBg = Color(0xFF3783BD)                       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│               Property Detail Screen (Light Mode: #3783BD)             │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 1. Bed Availability & Gender Rule Boxes  [bg = #3783BD, text=white]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 2. Combined Location & Landmark Box      [bg = #3783BD, text=white]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 3. Main Description Card                 [bg = #3783BD, text=white]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 4. Overview Specifications Bar           [bg = #3783BD, text=white]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 5. Room Configuration Outer Cards        [bg = #3783BD, text=white]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 6. Detailed Specifications Card          [bg = #3783BD, text=white]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 7. Amenities & Compound Features Chips   [bg = #3783BD, text=white]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 8. Host Information Card & Contact Box   [bg = #3783BD, text=white]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 9. Neighbourhood Exploration Card        [bg = #3783BD, text=white]│ │
│ └────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

- **Target Files**:
  1. `app/src/main/java/com/example/ui/theme/Color.kt`:
     - Update `PropertyBoxBg = Color(0xFF3783BD)`.
  2. `app/src/main/java/com/example/ui/screens/PropertyDetailScreen.kt`:
     - Audit text and icon colors within the `#3783BD` containers to use white/high-contrast tones in Light mode.
  3. `app/src/main/java/com/example/ui/components/CommuteRouteGuideSection.kt`:
     - Audit header and tab text against `#3783BD`.
