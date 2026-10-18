# Implementation Plan - Login Screen Background Color & Style Enhancements

Enhance the **Login screen background visual presentation** of the Balay-Inn app using a **Steel Blue & Warm Amber gradient theme** with a soft gradient header and rounded card container.

## Proposed Changes

### `LoginScreen.kt`
- **Gradient Background**:
  - Apply a custom background gradient using `Brush.verticalGradient()` combining **Steel Blue** (`#1E3A8A` / `#2563EB`) and **Warm Amber** (`#F59E0B` / `#D97706`) accents.
  - Add soft radial or linear ambient gradient highlights in the top branding section to create depth.
- **Card Container Styling**:
  - Place the authentication tabs, input fields, password toggle, and SSO quick-action buttons inside a clean, rounded `Card` container (`RoundedCornerShape(24.dp)`).
  - Ensure high-contrast input text fields (`OutlinedTextField`) with clear focus borders and proper elevation so all elements are easily readable against the gradient background.
  - Provide full dark mode and edge-to-edge support.

## User Review Required

- **Gradient Tone**: Confirm the Steel Blue to Warm Amber gradient contrast meets your aesthetic preference.

## Verification Plan

### Automated Verification
- Run `compile_applet` to verify Kotlin compilation and Compose layout build.

### Manual / Visual Verification
- Review the Login screen in the browser preview to inspect the new gradient background, header branding, and rounded card container.
