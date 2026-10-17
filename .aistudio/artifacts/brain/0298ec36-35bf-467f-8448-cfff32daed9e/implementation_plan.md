# Implementation Plan - Balay-Inn Modern Bahay Kubo & Shield Logo Design

Redesign the primary application logo and launcher branding for **Balay-Inn** to feature a modern **Bahay Kubo (Nipa Hut) icon integrated into a sleek security shield badge** with a **Steel Blue (`#4579AC`) and Warm Amber (`#F59E0B`) gradient backdrop**.

---

## 🎨 Proposed Logo Architecture & Styling

1. **Bahay Kubo + Security Shield Emblem**:
   - **Roofline & House Stilts**: Modern geometric lines representing the iconic Filipino Bahay Kubo (Nipa Hut) roof and elevated posts, signifying authentic local Philippine housing.
   - **Shield Frame**: A clean, protective shield outline surrounding the house to symbolize trust, safety, and verified rental listings.
   - **Color Gradient**: Steel Blue (`#4579AC` / `#2E618B`) representing stability and professional trust, blended with Warm Amber (`#F59E0B`) representing welcoming warmth and hospitality.

2. **Integration Locations across Balay-Inn**:
   - **Login & Registration Screen**: Large 100dp hero logo badge with gradient backdrop.
   - **App Header (`BalayInnLogoHeader.kt`)**: Top toolbar logo badge on Home, Explore, and Dashboard screens.
   - **Adaptive Android Launcher Icon**: Custom vector icon configuration for phone home screen.

---

## 🛠️ Step-by-Step Execution Plan

### Step 1: Create Custom Compose Logo Composable (`BahayKuboShieldLogo.kt`)
- Build a dedicated Jetpack Compose vector composable rendering the modern Bahay Kubo roof structure, central door/window, stilted foundation, and encircling security shield badge.
- Apply dynamic Brush gradients matching Steel Blue (`#4579AC`) and Warm Amber (`#F59E0B`).

### Step 2: Update Login Screen & Brand Headers
- Update `LoginScreen.kt` to replace the standard home icon with the new `BahayKuboShieldLogo`.
- Update `BalayInnLogoHeader.kt` so top bars across Tenant, Landlord, Broker, and Admin dashboards feature the new Bahay Kubo logo emblem.

### Step 3: Configure Adaptive App Launcher Icon
- Create adaptive icon XML resources in `res/drawable/ic_launcher_foreground.xml` and `res/drawable/ic_launcher_background.xml` featuring the Bahay Kubo shield emblem.

---

## 🎯 Verification
- Run `compile_applet` to verify syntax and visual compilation across all screens.
- Inspect Light and Dark mode rendering to ensure contrast compliance.
