# Balay-Inn Rental Marketplace
## Official Application User Manual & Complete Support Documentation

Platform: Android (Jetpack Compose & Kotlin) | Database: Room SQLite & Supabase PostgreSQL | Version 2.5

## 1. Overview & System Capabilities
Balay-Inn is an Android application engineered for rental housing across the Philippines. It connects verified tenants, direct property owners, licensed real estate brokers, property managers, caretakers, and authorized representatives.
• Philippine Geographic Cascading Hierarchy: Filter rentals strictly across Country -> Region -> Province -> City/Municipality -> Barangay using standard PSGC data.
• Interactive Map: Visual pins, pinch-to-zoom clusters, and Search this area map bounds detection.
• Role-Based Authority: Distinguish direct owners from licensed brokers and authorized representatives.
• End-to-End Rental Journey: Schedule viewings, chat in real-time, push through notification alerts, and manage contracts.

## 2. Searching & Exploring Properties

### 2.1 Compact Search Bar & View Toggle
The Explore screen features a compact 40dp search bar for rapid keyword queries, alongside the 40dp Map/List View Mode Toggle button and Filter button.

### 2.2 Advanced Filter Suite & Listed By Filter
Tap the Filter button to open the filter modal bottom sheet:
• Geographic Location: Select Province, City/Municipality, and Barangay for localized results.
• Listed By Filter: Filter by Direct Owner, Licensed Broker, Property Manager, Caretaker, Family Member, or Authorized Representative.
• Authorized Posters Only: Toggle switch to show only verified direct owners or authorized agents.
• Monthly Rent Budget Slider: Dual-thumb price slider ranging from PHP 0 to PHP 100,000+.
• Amenities & Policies: Filter by bedrooms, furnished status, parking, and pet policies.

## 3. Posting Rental Properties (Host Flow)
Navigate to the Post tab to launch the 5-step posting wizard:
1. Listing Basics: Title, rental category, monthly rate, security deposit, and poster role.
2. Location: Select Country, Province, City, Barangay, and address coordinates.
3. Specifications: Floor area, bedrooms, bathrooms, parking, WiFi, utilities, and pet policy.
4. Photo Gallery: Upload photos using the system photo picker.
5. Verification & Authority: Upload Land Title (TCT), Tax Declaration, or notarized Special Power of Attorney (SPA).

## 4. Scheduling Viewings & Notifications
• Booking a Viewing: On any property detail page, tap Schedule Viewing, select date and time slot, and submit.
• Host Alerts: Hosts receive instant alerts and push notifications for incoming requests.
• Viewing Lifecycle: Requests transition through Requested -> Confirmed -> Completed / Cancelled.

## 5. In-App Messaging & Real-Time Chat
• Chat with Host: Start instant messaging directly from any property page.
• Contextual Header: Pinned property card keeps rental details visible during communication.

## 6. Profile, Appearance & Logging Out

### 6.1 Profile Screen & Edit Profile Modal
Tap the Profile tab on the bottom navigation bar. Tap Edit to open the Edit Profile modal bottom sheet where you can update your Full Name, Phone Number, Email, and Role.

### 6.2 Appearance Mode (Moon/Sun Icon)
Tap the little moon/sun icon on the top right of the top bar to toggle between Dark Mode and Light Mode.

### 6.3 Push Notifications Setting
Easily enable or disable push-through notifications using the Push Notifications toggle on the Profile screen.

### 6.4 Lower Profile Log Out Button
Scroll to the bottom of the Profile page to access the Account Session card. Displays your signed-in identity, along with the prominent red Log Out button. Tapping Log Out displays a confirmation dialog before safely terminating your session and returning to the Login screen.

## 7. Frequently Asked Questions (FAQ)
• How to filter by Direct Owners only? Open Explore -> tap Filter -> tap Direct Owner under Listed By or toggle Authorized Posters Only.
• Offline Support: All properties, favorites, and viewings are stored locally in Room SQLite and sync automatically when online.
• Where is the Log Out button? Tap the Profile tab on the bottom navigation bar, scroll to the lower section, and tap Log Out.