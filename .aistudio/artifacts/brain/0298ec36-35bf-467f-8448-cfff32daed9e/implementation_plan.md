# Balay-Inn Production Compliance & Regulatory Integration

Implementation plan for integrating Google Play Store policy requirements and Philippine Data Privacy Act (RA 10173 / NPC) compliance into the Balay-Inn rental platform.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following compliance priorities were confirmed in Phase 1:
> - **Google Play Store Compliance**: Priority on Privacy Policy viewer, Data Safety disclosures, Terms of Service agreement, and an explicit Account Deletion Request flow.
> - **Philippine Regulatory Compliance**: Priority on National Privacy Commission (NPC) Data Privacy Act of 2012 (RA 10173) consent banner and localized rental terms notice.

---

## 1. Overview & Core Concept

Balay-Inn requires full compliance with both Google Play Developer Program policies and Philippine government regulations before publishing or production deployment:

1. **Google Play Store Policies**:
   - **Privacy Policy & Data Safety**: Transparent in-app disclosure of personal data collection (name, phone number, location, verification IDs).
   - **Account Deletion Requirement**: Mandatory in-app option for users (tenants and hosts) to request deletion of their account and associated stored data.
   - **Terms of Service**: Clear user agreement covering rental inquiry conduct, host listing standards, and verification guidelines.

2. **Philippine Data Privacy Act of 2012 (RA 10173 / NPC)**:
   - **Explicit Consent**: First-launch consent banner informing users of NPC compliance, user rights (Right to Be Informed, Right to Access, Right to Erasure/Blocking), and contact details for the Data Protection Officer (DPO).

---

## 2. User Experience & Visual Design

### Key User Flows & Components

```
┌────────────────────────────────────────────────────────────────────────┐
│                        App Launch / Dashboard                          │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 🇵🇭 Data Privacy Act (RA 10173) Notice                              │ │
│ │ Balay-Inn processes your data in compliance with Philippine NPC    │ │
│ │ guidelines. [Read Privacy Policy] [Read Terms]      [ Accept & Close ] │ │
│ └────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      Dashboard / User Profile                          │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 🛡️ Security, Privacy & Legal Suite                                 │ │
│ │ • Privacy Policy & NPC Disclosures                                 │ │
│ │ • Data Safety & Contact Masking Status                             │ │
│ │ • Terms of Service & Host Guidelines                               │ │
│ │ • ⚠️ Request Account & Data Deletion                              │ │
│ └────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                  Account Deletion Request Dialog                       │
│                                                                        │
│ "Are you sure you want to request account deletion? All personal data,  │
│ saved properties, and viewing history will be permanently purged."    │
│                                                                        │
│                 [ Cancel ]    [ Confirm Account Deletion ]             │
└────────────────────────────────────────────────────────────────────────┘
```

1. **Philippine Data Privacy Act (NPC) Consent Banner**:
   - Non-intrusive floating card anchored at the bottom of `DashboardScreen` or `TenantHomeScreen`.
   - Displays clear text regarding RA 10173 compliance with action buttons to view full **Privacy Policy**, **Terms of Service**, and **Accept**.

2. **Privacy Policy & Legal Dialog Viewer (`PrivacyPolicyDialog.kt`)**:
   - Scrollable M3 Dialog displaying complete legal sections:
     - Section 1: Information Collected (Name, Email, Phone, Location, Gov't ID verification documents).
     - Section 2: Use of Data (Matchmaking tenants with boardings, location privacy masking).
     - Section 3: Philippine Data Privacy Act Rights (RA 10173 - Right to Access, Correct, Erase).
     - Section 4: Data Security & Contact Protection (+63 917 *** *567 masking).
     - Section 5: Data Retention & Purge Policy.

3. **Terms of Service Dialog Viewer (`TermsOfServiceDialog.kt`)**:
   - Outlines tenant conduct, landlord/host listing rules, deposit notices, and verification obligations.

4. **Account Deletion Flow (`DashboardScreen.kt` & `SecurityPrivacyBottomSheet.kt`)**:
   - Red prominent "Request Account Deletion" button inside settings/dashboard.
   - Triggers an M3 AlertDialog explaining the deletion scope (account profile, viewing history, saved items, and posted properties).
   - Upon confirmation, invokes `MarketplaceViewModel.requestAccountDeletion()`, clearing local session state and navigating back to `LoginScreen` with a success toast.

---

## 3. Key Product Decisions & Trade-Offs

- **In-App Interactive Legal Dialogs vs External Web Links**:
  - *Chosen Approach*: Build fully formatted, scrollable Jetpack Compose M3 dialogs in-app so users can review terms and privacy details directly without requiring an external browser tab, while keeping policy text structured for easy web URL linking during Google Play Console submission.
- **Immediate Account Purge vs Delayed Processing**:
  - *Chosen Approach*: Implement an immediate local user data purge in `MarketplaceViewModel` and `BalayInnRepository` when requested, satisfying Google Play's mandatory account deletion requirement without blocking the user.

---

## 4. Technical Architecture & Component Structure

```
┌───────────────────────────────────────────────────────────────────────┐
│                           UI Presentation                             │
│                                                                       │
│  ┌──────────────────────┐   ┌───────────────────────────────────────┐  │
│  │ DataPrivacyConsent   │   │     SecurityPrivacyBottomSheet        │  │
│  │       Banner         │   │   • Data Safety Disclosures           │  │
│  └──────────┬───────────┘   │   • Account Deletion Request Action   │  │
│             │               └───────────────────┬───────────────────┘  │
│             ▼                                   │                      │
│  ┌──────────────────────────────────────────────▼───────────────────┐  │
│  │               PrivacyPolicyDialog & TermsDialog                  │  │
│  └──────────────────────────────────────────────┬───────────────────┘  │
└─────────────────────────────────────────────────┼─────────────────────┘
                                                  │
                                                  ▼
┌───────────────────────────────────────────────────────────────────────┐
│                        MarketplaceViewModel                           │
│                                                                       │
│  • acceptDataPrivacyConsent()                                         │
│  • requestAccountDeletion(userId)                                     │
│  • dataPrivacyConsentAccepted: StateFlow<Boolean>                     │
└─────────────────────────────────────────────────┬─────────────────────┘
                                                  │
                                                  ▼
┌───────────────────────────────────────────────────────────────────────┐
│                       Data Layer & Repository                         │
│                                                                       │
│  • BalayInnRepository.deleteUserAccount(userId)                       │
│  • PreferenceDataStore / SharedPrefs (Consent State)                  │
└───────────────────────────────────────────────────────────────────────┘
```

### Proposed Changes

#### New Component & Screen Files
1. `app/src/main/java/com/example/ui/components/DataPrivacyConsentBanner.kt`:
   - Interactive consent banner for Philippine Data Privacy Act (RA 10173).
2. `app/src/main/java/com/example/ui/components/PrivacyPolicyDialog.kt`:
   - Comprehensive M3 dialog rendering formatted Privacy Policy & NPC compliance text.
3. `app/src/main/java/com/example/ui/components/TermsOfServiceDialog.kt`:
   - Formatted Terms of Service dialog.

#### Modified Files
1. `app/src/main/java/com/example/ui/components/SecurityPrivacyBottomSheet.kt`:
   - Add "Google Play Account Deletion Request" section and trigger button.
   - Add quick links to view Privacy Policy and Terms of Service.
2. `app/src/main/java/com/example/ui/screens/DashboardScreen.kt`:
   - Include `DataPrivacyConsentBanner` and Account Deletion trigger with confirmation dialog.
3. `app/src/main/java/com/example/ui/viewmodel/MarketplaceViewModel.kt`:
   - Add `dataPrivacyConsentAccepted` state flow.
   - Add `requestAccountDeletion()` method to handle clearing user state and resetting session.

---

## Verification Plan

### Automated Build Verification
- Execute `compile_applet` after implementing code updates to ensure zero compilation or syntax issues.

### CUJ Verification
1. **Data Privacy Consent Flow**: Launch app, verify the Philippine Data Privacy Act notice appears, open Privacy Policy dialog, accept terms, and verify banner dismisses permanently.
2. **Account Deletion Request Flow**: Open Dashboard/Settings -> Security & Privacy Suite -> Click "Request Account Deletion", confirm deletion dialog, and verify user session resets to Login screen cleanly.
