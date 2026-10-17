# Comprehensive App Security & Data Protection Suite

Implement a robust, production-grade security architecture for **Balay-Inn** to safeguard tenant and landlord user identities, protect sensitive rental communications, and shield application runtime integrity against unauthorized access and tampering.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The security suite configuration was confirmed during Phase 1 clarification:

- **Confirmed Decision 1 (Authentication Security)**: **Strict Google Sign-In with Role-Based Access Control (RBAC)**. All sensitive actions (listing management, authorization letter editing, admin approvals) will strictly validate session tokens and user persona roles (`TENANT`, `LANDLORD`, `REPRESENTATIVE`, `ADMIN`).
- **Confirmed Decision 2 (Privacy & Data Protection)**: **Encrypted Local Storage & Phone Number Masking**. Contact phone numbers on public listing cards and property detail views will be masked by default (e.g., `+63 917 *** *567`) and revealed only to authenticated users with confirmed inquiries or viewings. Sensitive preferences will be encrypted locally.
- **Confirmed Decision 3 (App-Level Integrity)**: **Root & Tamper Detection with Secure Screen Protection (`FLAG_SECURE`)**. Window screenshot protection will be available for sensitive verification screens (Government IDs, auth letters), alongside runtime root/su binary checks.

---

## 1. Overview & Core Concept

- **What It Does**: Integrates a multi-layered security layer into Balay-Inn that manages user authorization, masks sensitive personal contact data, enforces runtime device integrity checks, and provides an interactive Security Status Dashboard.
- **Target Audience**: All Balay-Inn users—Tenants seeking private browsing, Landlords protecting document verification data, and Admins auditing platform trust and role compliance.
- **Key Value**: Guarantees zero leak of landlord and tenant phone numbers to web scrapers, prevents unauthorized screen captures during ID verification, and enforces strict RBAC across repository data operations.

---

## 2. User Experience & Visual Design

- **Security & Privacy Status Badge**:
  - A prominent **Security Shield Badge** added to the top bar/header of `DashboardScreen` and `VerificationCenterScreen`.
  - Shows real-time security posture: **Protected (Green Shield)** with active security toggles.
- **Interactive Security & Privacy Settings Sheet**:
  - Accessible via the Profile/Dashboard screen.
  - Toggles for **Contact Masking**, **Secure Screen Blur (`FLAG_SECURE`)**, and **Device Integrity Guard**.
  - Visual security indicator score (e.g., `100% Secured`).
- **Phone Number Masking & Reveal Flow**:
  - Public property detail screen displays host contact as `+63 917 *** *123` with a **"Reveal Phone Number"** button.
  - Tapping **"Reveal Phone Number"** checks authentication status: if logged in, unlocks the full number with a smooth reveal animation; if unauthenticated, prompts a secure login sheet.
- **Role-Based Access Guard & Unauthorized Prompts**:
  - Clear visual banner when a user attempts an action outside their role scope (e.g., a tenant trying to post a listing or edit landlord credentials), guiding them to switch personas or complete role verification.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Dynamic Contact Masking vs. Unmasked Display**:
  - *Chosen Approach*: Mask host phone numbers by default on public cards/details, revealing them only upon intentional user click when authenticated.
  - *Why*: Protects landlords from spam, marketing bots, and unauthorized data scraping.
- **Decision 2: Local Integrity Guard vs. External SDKs**:
  - *Chosen Approach*: Pure Kotlin lightweight `AppSecurityManager` executing local su binary/path checks and `FLAG_SECURE` window management without heavy external third-party binary bloat.
  - *Why*: Zero APK size overhead, fast execution, full compatibility with Jetpack Compose edge-to-edge.
- **Decision 3: Granular Role-Based Access Control (RBAC)**:
  - *Chosen Approach*: Validate user role permissions at both the Repository/Data layer (`BalayInnRepository`) and UI Navigation level.
  - *Why*: Prevents direct function call bypasses and ensures security policy consistency.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
│  ┌───────────────────────┐ ┌───────────────────┐ ┌───────────────────┐ │
│  │   Dashboard Screen    │ │  Property Detail  │ │ Verification Center│ │
│  │ (Security Settings)   │ │  (Phone Masking)  │ │   (FLAG_SECURE)   │ │
│  └───────────┬───────────┘ └─────────┬─────────┘ └─────────┬─────────┘ │
└──────────────│───────────────────────│─────────────────────────│─────────┘
               │                       │                         │
               ▼                       ▼                         ▼
┌────────────────────────────────────────────────────────────────────────┐
│                          AppSecurityManager                            │
│  ┌──────────────────────┐ ┌───────────────────┐ ┌───────────────────┐  │
│  │ Root/Tamper Checker  │ │ Phone Masker Engine│ │ FLAG_SECURE Guard │  │
│  └──────────────────────┘ └───────────────────┘ └───────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │            Encrypted Local Security Preferences Store            │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                 BalayInnRepository & RBAC Enforcement                  │
│  • Role Authorization Checks (TENANT | LANDLORD | REPRESENTATIVE | ADMIN)│
│  • Secure Data Query Filtering & Access Logging                        │
└────────────────────────────────────────────────────────────────────────┘
```

### Key Security Components to Add/Modify:
1. `com.example.util.AppSecurityManager`:
   - `maskPhoneNumber(phone: String, isRevealed: Boolean): String`
   - `isDeviceRooted(): Boolean`
   - `applySecureScreenProtection(activity: Activity, enable: Boolean)`
   - `checkRolePermission(userRole: UserRole, requiredRole: UserRole): Boolean`
2. `com.example.ui.components.SecurityPrivacyBottomSheet`:
   - Interactive modal sheet allowing users to review active security measures, toggle contact masking, and view their security score.
3. `com.example.ui.screens.PropertyDetailScreen`:
   - Integrate phone masking for host contact section with interactive reveal button.
4. `com.example.data.repository.BalayInnRepository`:
   - Enforce RBAC validation across property mutation and authorization letter updates.
