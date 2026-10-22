# Property Types Categorized Details & Multi-Step Listing Implementation Plan

A unified, state-driven property categorization system that structures rental attributes across **Boarding House**, **Apartment**, **Condominium**, **Studio**, and **Townhouse**. Delivers a multi-step listing form with role-based verification, seamless integration of category specifications on the property detail screen, and dynamic type-specific filters on the search filter sheet.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product decisions were confirmed during the planning interview:
> - **Core Property Types**: Boarding House, Apartment, Condominium, Studio, and Townhouse.
> - **Property Detail Screen**: Integrated seamlessly into the existing specifications list, ensuring uniform typography, Filipino rental clarity, and no redundant boxes.
> - **Filter Page**: Dynamic category filters that dynamically present type-specific options (e.g., room capacity for boarding houses, floor level/dues for condos, stories/garage for townhouses) whenever that specific property type is selected.
> - **Posting Workflow**: Multi-step flow featuring:
>   * Step 1: Role-Based Verification (Owner vs. Authorized Representative vs. Sublessor).
>   * Step 2: Base Property Information (Location cascading, Property Type, Photos [min 3, max 10], Description, Title).
>   * Step 3: Conditional Property Specifications (Dynamic form fields driven strictly by selected Property Type).
>   * Step 4: Review & Publish (Validation of all conditional fields, preview summary, and Room database persistence).

---

## 1. Overview & Core Concept

Balay-Inn caters to diverse Philippine rental setups—from student bedspaces in university belts and urban condo rentals in BPO hubs to multi-door apartments and suburban family townhouses. Because each property type has distinct practical concerns (such as bed capacities and communal bath rules in boarding houses versus association dues and floor levels in condominiums), this feature provides:
1. **Targeted Onboarding**: Role-based verification ensuring transparency between direct title holders, authorized caretakers/managers, and master tenants.
2. **Context-Sensitive Data Entry**: Landlords only fill out relevant fields (no irrelevant "garage capacity" for a 1-bed boarding house, and no "bunk bed count" for a luxury townhouse).
3. **Structured Detail Presentation**: Renters view clearly organized specifications directly alongside standard rental information.
4. **Precision Search & Filtering**: Renters can filter specifically by the attributes that matter to their property type (e.g., filter apartments by lease term and kitchen type, or condominiums by parking and dues inclusion).

---

## 2. User Experience & Visual Design

### Key User Flows

#### A. Multi-Step Property Posting Flow (`PostPropertyScreen`)
1. **Step Indicator & Header**: Stepper displaying `1. Your Role` → `2. Base Info` → `3. Specifications` → `4. Review & Publish` with progress bar and step transition animation.
2. **Step 1 — Role-Based Verification**:
   - Selector chips: `Property Owner`, `Authorized Representative / Caretaker`, `Sublessor / Master Tenant`.
   - *If Owner*: Ownership title confirmation toggle, PhilSys/Passport/UMID ID verification type, ID number input, and contact preference.
   - *If Authorized Representative*: Landlord full name, relationship to owner, authorization status confirmation (Yes/No), authorization document/SPA declaration, representative phone.
   - *If Sublessor*: Master landlord name, written sublease permission toggle, remaining master lease term.
3. **Step 2 — Base Property Information**:
   - Geographic cascading location (Province → City/Municipality → Barangay) using Philippine PSA data.
   - Street address & building name input.
   - Property Type Selector: Boarding House, Apartment, Condominium, Studio, Townhouse.
   - Media Upload: Visual photo picker with minimum 3 and maximum 10 enforcement, thumbnail preview, delete buttons, and photo counter badge.
   - Listing Title & long-form Description with quick Filipino locality helper chips.
4. **Step 3 — Conditional Property Specifications**:
   - **Boarding House**: Pricing model toggle (`Per Person/Bed` vs `Per Room`), monthly rent currency input, Room Capacity dropdown (`Single`, `Double bed`, `4-bed bunk`, `6-bed bunk`), Tenant Preference dropdown (`Male Only`, `Female Only`, `Mixed`), Bathroom Type toggle (`Private/En-suite` vs `Shared/Communal`), Utilities checkboxes (`Sub-metered Electricity`, `Sub-metered Water`, `Free Wi-Fi included`).
   - **Apartment**: Monthly rent, Total floor area (sqm), Bedroom stepper, Bathroom stepper, Lease term minimum dropdown (`6 months`, `1 year`), Furnishing dropdown (`Bare`, `Semi-Furnished`, `Fully Furnished`), Kitchen type toggle (`Indoor/Integral` vs `Outdoor/Dirty Kitchen`), Utility billing toggle (`Own Meter` vs `Sub-metered by Landlord`).
   - **Condominium / Studio**: Monthly rent, Association dues toggle (`Inclusive in rent` vs `Exclusive / Tenant pays separately`), Unit layout dropdown (`Studio`, `1BR`, `2BR`, `Loft`, `Bi-level`), Total floor area (sqm), Floor level input (e.g., `14th Floor`), Parking dropdown (`Included in rent`, `Available for extra fee`, `None`), Amenities checkboxes (`Pool`, `Gym`, `24/7 Security`, `Balcony`), Pet policy toggle (`Strictly No Pets` vs `Pet-Friendly`).
   - **Townhouse**: Monthly rent, Lot area (sqm), Floor area (sqm), Number of stories/floors stepper, Bedroom & Bathroom steppers, Carport/garage capacity stepper, Maid's room toggle (`Yes` / `No`), Gated subdivision toggle (`Yes` / `No`).
5. **Step 4 — Review & Publish**:
   - Comprehensive card summary reflecting the user's role verification, base info, and property-specific specifications.
   - "Publish Listing" action validating that all active conditional fields are satisfied before inserting into Room database and redirecting back.

#### B. Property Detail Screen (`PropertyDetailScreen`)
- Seamlessly incorporates the category specifications into the **Detailed Living Specifications** list:
  * Category badge indicating property type (e.g. `Condominium`, `Boarding House`).
  * Structured icon-value rows matching the active specifications (e.g., `Room Capacity: 4-bed bunk`, `Bathroom: Shared/Communal`, `Kitchen: Dirty kitchen outdoor`, `HOA Dues: Inclusive in rent`, `Subdivision: Gated with security guards`).
  * Eliminates clutter and maintains consistent Philippine typography and layout spacing.

#### C. Dynamic Filter Sheet (`ExploreScreen`)
- In the filter bottom sheet, choosing a specific Property Type reveals a dynamic **Type-Specific Specifications** section:
  * Selecting **Boarding House** reveals filters for: Pricing Model, Room Capacity, Tenant Preference (Male/Female/Mixed), Bathroom Type (Shared/Private).
  * Selecting **Apartment** reveals: Furnishing, Lease Term (6 mos / 1 yr), Kitchen Type.
  * Selecting **Condominium / Studio** reveals: Unit Layout, Assoc Dues (Inclusive vs Exclusive), Parking, Balcony.
  * Selecting **Townhouse** reveals: Number of Stories, Gated Subdivision filter, Maid's Room.
- Selecting **All** hides type-specific filters and presents general rental filters.

---

## 3. Key Product Decisions & Trade-Offs

- **State-Driven Multi-Step Form vs. Single Long Form**:
  - *Chosen*: Multi-step form with clear progress indication.
  - *Why*: Prevents user fatigue, guarantees that Step 1 role verification and Step 2 property type are settled before asking for specific room capacities or association dues.
  - *Alternative Considered*: Showing all conditional sections dynamically inside one long scrollable page. This caused layout jumps and confusion when users changed property types mid-scroll.

- **Unified Category Specs Serialization**:
  - *Chosen*: Store the type-specific specifications as a lightweight JSON string (`categorySpecsJson`) in `PropertyEntity` alongside normalized core columns (`bedrooms`, `bathrooms`, `floorAreaSqm`, `petPolicy`, `associationDues`).
  - *Why*: Allows backward compatibility with existing Room database schema without requiring destructive database migrations, while enabling rich type-safe serialization.

- **Dynamic Filters inside Bottom Sheet**:
  - *Chosen*: Render type-specific filter chips conditionally inside `ExploreScreen`'s filter bottom sheet based on `filterState.propertyType`.
  - *Why*: Keeps the filter dialog compact and relevant; renters searching for a studio condo aren't overwhelmed with boarding house bunk-bed filters.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        BALAY-INN ARCHITECTURE                          │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  [Step 1: Role Verification]  ──▶  [Step 2: Base Info]                 │
│  - Owner (Title, ID verification)   - Location (Cascading PSA)         │
│  - Representative (Landlord name,   - Property Type Selection          │
│    authorization confirmation)      - Photos (3 - 10)                  │
│  - Sublessor (Master lease proof)   - Title & Description              │
│                 │                                │                     │
│                 ▼                                ▼                     │
│  [Step 3: Conditional Specs] ◀───────────────────┘                     │
│  ├── Boarding House (Beds, pricing model, tenant gender, shared bath)  │
│  ├── Apartment (Bed/bath steppers, lease term, furnishing, kitchen)   │
│  ├── Condominium / Studio (Dues inclusion, layout, floor, parking)     │
│  └── Townhouse (Lot/floor area, stories, garage, gated subdivision)    │
│                 │                                                      │
│                 ▼                                                      │
│  [Step 4: Review & Publish] ──▶ MarketplaceViewModel.postProperty()     │
│                                           │                            │
│                                           ▼                            │
│                              [PropertyDao / Room DB]                   │
│                              (categorySpecsJson stored)                │
│                                           │                            │
│              ┌────────────────────────────┴──────────────────────────┐ │
│              ▼                                                       ▼ │
│  [PropertyDetailScreen]                                 [ExploreScreen]│
│  Integrated Living Specs list                          Dynamic Filter  │
│  rendering type-specific badges                        Sheet adapting  │
│  and icon rows                                         to active type  │
└────────────────────────────────────────────────────────────────────────┘
```

### Component & State Mapping

1. **`PropertyCategoryDetails.kt` (`data/model/`)**:
   - Data classes for each conditional type:
     * `BoardingHouseDetails`: `pricingModel`, `roomCapacity`, `tenantPreference`, `bathroomType`, `utilitiesIncluded`.
     * `ApartmentDetails`: `floorAreaSqm`, `bedrooms`, `bathrooms`, `leaseTerm`, `furnishing`, `kitchenType`, `utilityBilling`.
     * `CondominiumDetails`: `associationDuesStatus`, `unitLayout`, `floorAreaSqm`, `floorLevel`, `parking`, `amenities`, `petPolicy`.
     * `TownhouseDetails`: `lotAreaSqm`, `floorAreaSqm`, `stories`, `bedrooms`, `bathrooms`, `garageCapacity`, `hasMaidsRoom`, `isGatedSubdivision`.
   - Unified `PropertyCategorySpecifications` container with JSON serialization / deserialization helpers and detail item generators.

2. **`PostPropertyScreen.kt` (`ui/screens/`)**:
   - Refactored into a 4-step state-driven form (`currentStep` state: 1 to 4).
   - Step 1: Branching role-based verification UI.
   - Step 2: Base info, location cascading, photos (min 3 validation).
   - Step 3: Conditional property specifications switching dynamically via `when (propertyType)`.
   - Step 4: Review card with Publish button validating conditional state.

3. **`PropertyDetailScreen.kt` (`ui/screens/`)**:
   - Seamlessly deserializes `categorySpecsJson` and injects specific living specification rows into the living details section with appropriate icons.

4. **`ExploreScreen.kt` & `MarketplaceViewModel.kt`**:
   - `MarketplaceFilterState`: Add conditional filter fields (`bunkCapacityFilter`, `furnishingFilter`, `leaseTermFilter`, `layoutFilter`, `gatedFilter`, etc.).
   - Filter sheet dynamically renders the matching filter block when a property type is selected, filtering the property list reactively.
