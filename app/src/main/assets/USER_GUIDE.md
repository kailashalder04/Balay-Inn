# Balay-Inn Application User Guide & Operational Manual
**Comprehensive Guide for Tenants, Property Owners, Brokers, and Administrators**  
*Version 2.4 - October 2026* | *Balay-Inn Rental Housing Marketplace & Transparency Platform*

---

## Table of Contents
- [1. Introduction & Overview](#1-introduction--overview)
- [2. Getting Started & Account Authentication](#2-getting-started--account-authentication)
- [3. Tenant Experience & Rental Discovery](#3-tenant-experience--rental-discovery)
- [4. Interactive Map Discovery](#4-interactive-map-discovery)
- [5. Property Details & Inquiries](#5-property-details--inquiries)
- [6. Landlord & Host Features (Posting & Management)](#6-landlord--host-features-posting--management)
- [7. Verification Center & Trust Levels](#7-verification-center--trust-levels)
- [8. Dashboard & Account Management](#8-dashboard--account-management)
- [9. Technical Architecture & Support](#9-technical-architecture--support)

---

## 1. Introduction & Overview

### 1.1 About Balay-Inn

Balay-Inn is an authoritative rental housing marketplace and transparency platform tailored for the Philippine rental real estate market. The platform brings trust, cost transparency, and anti-fraud protections to renters, property owners, licensed real estate brokers, and authorized property caretakers.

Key differentiators of Balay-Inn include:

- Hierarchical Geographic Discovery: Complete geographic coverage down to Region, Province, City/Municipality (including all 48 LGUs of Bohol and Cebu City), and Barangay.
- Multi-Tier Verification Ladder: An 8-level trust verification framework evaluating phone, email, PhilSys government ID, liveness checks, title deeds, PRC broker accreditation, and on-site physical property visits.
- Total Cost Transparency: Mandatory breakdown of monthly rent, association dues, advance rent, security deposits, and estimated first-month total upfront costs.
- Anti-Fraud Poster Authority: Clear distinction and legal verification between direct property owners, licensed brokers, family members, and caretakers.
- Offline-First Sync with Supabase: Built with local Room SQLite storage that operates seamlessly offline and syncs in real-time with Supabase PostgreSQL cloud backend when connected.


## 2. Getting Started & Account Authentication

### 2.1 Supabase Cloud Authentication

Balay-Inn uses Supabase Auth for security, session persistence, and multi-device identity. When launching the app, users are greeted by the Login & Registration screen.

- Sign In: Enter your registered email address and secure password. Tap 'Sign In with Supabase' to access your account.
- Register New Account: Switch to the 'Register' tab, select your primary role (Tenant, Property Owner, Licensed Broker, or Caretaker), provide your full legal name, email, phone number, and password.
- Session Persistence: Your secure authentication tokens are safely stored in Android Encrypted Preferences and validated automatically on startup.
### 2.2 Quick Persona Switcher (Demo / Testing Mode)

For rapid testing and stakeholder review, Balay-Inn includes instant demo persona buttons:

- Maria Santos (Direct Owner): Manage listings, view booking requests, and test landlord features.
- Juan Dela Cruz (Tenant): Search properties, set rental requirements, save favorites, and schedule viewings.
- Atty. Roberto Gomez (Licensed Broker): PRC-accredited broker (#0028192) managing premium client listings.
- Elena Reyes (Authorized Caretaker): On-site caretaker coordinating physical viewings with owner authorization.


## 3. Tenant Experience & Rental Discovery

### 3.1 Personalized Home Feed

Upon signing in as a tenant, the Home screen displays personalized rental recommendations based on your preferences, recently viewed properties, active viewing appointments, and saved search alerts.

### 3.2 Search & Multi-Criteria Filtering (Explore Screen)

The Explore Screen features a streamlined search bar and comprehensive filter sheet:

- Compact Top Search Bar: Search by keyword, landmark (e.g. 'Ayala Center', 'Alona Beach'), neighborhood, or city name. Sized to 40dp for a clean, balanced layout.
- List View vs. Interactive Map View: Toggle instantly between scrolling property cards and full interactive map with the 40dp map toggle button.
- Geographic Cascading Selector: Filter precisely by Country (Philippines) -> Province (e.g. Bohol, Cebu) -> City/Municipality (e.g. Tagbilaran, Panglao, Cebu City) -> Barangay.
- Property Type Filter: Filter by Studio, Condominium, Apartment, House, Room, or Townhouse.
- Listed By Filter: Filter properties by who posted them: Direct Owner, Licensed Broker, Property Manager, Caretaker, Family Member, or Authorized Representative.
- Authorized Posters Only Toggle: Restrict search results strictly to listings with verified legal authority or direct ownership.
- Monthly Rent Budget Slider: Set your minimum and maximum monthly rent range (₱0 to ₱100,000+).
- Specifications & Amenities: Toggles for dedicated parking required, pet-friendly only, and verified listings only (Level 3+).
- Sort Order: Sort listings by Newest, Price (Low to High), or Price (High to Low).
- Active Filter Badges: Quick-removal chips at the top of the screen allow you to see and clear individual active filters with one tap, or tap 'Reset All'.


## 4. Interactive Map Discovery

### 4.1 Map Features & Gestures

The Interactive Map provides visual spatial discovery across the Philippine archipelago:

- Fluid Navigation: Pan across islands and cities with single-finger drag. Pinch with two fingers to zoom in and out smoothly.
- Location Presets (Fly-To): Quick buttons allow instant jump to Cebu City, Bohol, or Panglao Island.
- Search Visible Map Area: Tap 'Search this map area' to filter listings exclusively within the bounds of your currently visible screen.
- Radius Distance Search: Search properties within 1 km, 5 km, 10 km, or 25 km of any target location.
- Interactive Property Markers: Tap any price pin or cluster to view a pop-up preview card with rent, title, verification badge, and direct link to property details.


## 5. Property Details & Inquiries

### 5.1 Complete Cost Transparency

Every listing provides an itemized cost schedule to eliminate hidden move-in surprises:

- Monthly Rent: Base recurring monthly rental fee.
- Association & Condo Dues: Explicitly listed as included or separate monthly amount.
- Advance Rent: Number of months required upfront (typically 1 month).
- Security Deposit: Refundable deposit months (typically 2 months).
- Estimated First Month Out-of-Pocket Cost: Automatically calculated total to prepare tenant budget accurately.
### 5.2 Verification Badges & Poster Identity

Tenants can review the poster's relationship to the property (e.g. 'Direct Owner', 'Family Member (Authorized)', 'Licensed Broker (PRC #0028192)') along with their verification level badge.

### 5.3 Viewing Requests & In-App Messaging

- Schedule In-Person Viewing: Select desired viewing date, time slot, and add notes. Host receives instant in-app and email notifications.
- Direct In-App Chat: Send questions directly to the landlord or broker within the secure chat thread. Property details are attached to the conversation header.


## 6. Landlord & Host Features (Posting & Management)

### 6.1 6-Step Property Posting Wizard

Property owners and authorized representatives can publish listings via the Post Property flow:

- Step 1: Poster Relationship & Authority: Specify whether you are the direct owner, family member, caretaker, or broker. State if you possess legal authorization.
- Step 2: Basic Details: Title, detailed description, property type, monthly rent, and association dues.
- Step 3: Geographic Location: Select Province, City/Municipality, Barangay, street address, and privacy level (Exact, Approximate, or Hidden).
- Step 4: Amenities & Policies: Specify bedrooms, bathrooms, floor area (sqm), furnishing status, parking availability, and pet policy.
- Step 5: Photos: Add high-resolution property image URLs.
- Step 6: Review & Publish: Confirm listing details and publish immediately to Room local database and Supabase cloud.
### 6.2 Viewing Requests Management

Hosts can review pending tenant viewing requests, confirm appointment dates, suggest alternative times, or mark viewings as completed.



## 7. Verification Center & Trust Levels

### 7.1 The 8-Level Verification Framework

Balay-Inn implements a comprehensive multi-tier trust ladder:

- Level 1: Phone Verified (SMS OTP confirmation).
- Level 2: Email Verified (Cryptographic email link validation).
- Level 3: Government ID Verified (PhilSys National ID, Philippine Passport, Driver's License, or UMID).
- Level 4: Biometric Liveness Check (Facial biometric matching).
- Level 5: Proof of Ownership / Authority (Transfer Certificate of Title, Tax Declaration, or Special Power of Attorney).
- Level 6: Professional Broker Accreditation (PRC Real Estate Broker ID and license validation).
- Level 7: On-Site Physical Inspection (Physical inspection and GPS photo confirmation by Balay-Inn staff).
- Level 8: Certified Trusted Partner (Verified track record of 10+ completed leases with positive tenant reviews).


## 8. Dashboard & Account Management

### 8.1 Dashboard Capabilities

The Dashboard serves as the central operational hub:

- Profile Summary: Displays user role, current verification level, and account status.
- Quick Analytics: Real-time counts of active listings, saved favorites, viewing inquiries, and conversations.
- Appearance Mode: Toggle between dynamic Light Mode and high-contrast Dark Mode.
- Supabase Cloud Sync: Real-time cloud synchronization status indicator with manual 'Sync Now' action.
- Notification Center: Read, review, and clear system notifications and booking alerts.
- Account Session & Log Out: Located at the lower section of the dashboard page. Tapping 'Log Out' triggers a confirmation dialog that clears active sessions and tokens, safely returning to the Login screen.


## 9. Technical Architecture & Support

### 9.1 Tech Stack

- UI Framework: Jetpack Compose with Material Design 3 (M3).
- Language: 100% Kotlin with Coroutines and StateFlow.
- Local Persistence: Android Room SQLite database with destructive migration fallback and automated seeding.
- Cloud Backend: Supabase PostgreSQL (PostgREST API, Row Level Security, Supabase Auth).
- Networking: Retrofit 2 with Moshi JSON serializer and ISO-8601 timestamp adapters.
- Image Loading: Coil Compose for asynchronous image rendering and caching.
### 9.2 Frequently Asked Questions (FAQ)

Q: Can I use the app without internet connectivity?

A: Yes. Balay-Inn is built with an offline-first architecture using Room database. All cached listings, favorites, and geographic data remain fully accessible without internet. Once connectivity is restored, data synchronizes automatically with Supabase.

Q: How do I know if a listing is posted by the real owner?

A: Check the 'Listed By' label and verification badge on the property card or detail page. Listings tagged 'Direct Owner' with Level 5+ verification have uploaded official Title deeds or Tax declarations.

Q: Where is the log out button located?

A: Navigate to the Dashboard tab (bottom navigation bar) and scroll to the lower dashboard section to find the 'Account Session' card and 'Log Out' button.


