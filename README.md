# Balay-Inn — Complete Philippine Rental Property Marketplace

> **Product Tagline:** *Find a place that fits your life.*

Balay-Inn is a modern, responsive, full-stack rental property marketplace architected specifically for the Philippine real estate landscape. It connects tenants, property owners, authorized representatives (family members, caretakers, property managers, licensed real estate brokers, and salespersons), and compliance staff.

---

## 1. Architectural Highlights

### A. Authoritative Hierarchical Geographic System (Philippines-Wide)
- **Hierarchy:** `Country (PH) -> Region (Region VII) -> Province (Bohol, Cebu) -> City/Municipality -> Barangay -> Property`
- **Complete Bohol Administrative Coverage:** 1 Component City (Tagbilaran City) + all 47 municipalities + authentic barangays (no abbreviated subset).
- **Cebu Province Coverage:** Cebu City (Lahug, Banilad, Mabolo, Guadalupe, Apas / IT Park, etc.), Mandaue City, Lapu-Lapu City, Talisay City, etc.
- **Extensibility:** The database-driven normalization allows administrators to add any Philippine province or LGU without database schema changes or frontend modifications.
- **Dynamic Cascading Location Selector:** Country -> Region -> Province -> City/Municipality -> Barangay with search dialog and clearing child selections when parent changes.

### B. Layered Verification & Document Privacy
- **Levels 0 to 8 Verification:**
  - Level 0: Account Created
  - Level 1: Phone Verified (OTP)
  - Level 2: Email Verified
  - Level 3: Identity Document Submitted
  - Level 4: Professional Information Submitted
  - Level 5: PRC License Verified
  - Level 6: Owner Authorization Verified
  - Level 7: Physical Property Visited
  - Level 8: Property Documents (Title/Tax Declaration) Reviewed
- **Document Privacy Guarantee:** Sensitive documents (Government IDs, PRC licenses, SPA letters) are kept encrypted and confidential for compliance staff review only. Public listings display verified status badges only—never the raw documents.
- **Transparent Disclaimers:** No misleading "100% safe" claims; clear disclosures explaining what each badge verifies.

### C. Poster vs. Owner Relationship
- Balay-Inn natively supports authorized non-owner posters:
  - Property Owner
  - Family Member (Authorized)
  - Friend / Caretaker
  - Property Manager
  - Licensed Broker / Real Estate Salesperson
- Listings clearly indicate who posted the property and their owner authorization status.

### D. Cost Transparency
- Displays total estimated monthly living cost:
  `Monthly Base Rent + Association Dues + Parking Fees + Maintenance/Other Fees = Total Monthly Cost`
- Displays estimated move-in cash required:
  `Advance Rent + Security Deposit = Total Move-in Cash Required`

### E. Location Privacy & Interactive Map
- Exact residential street numbers remain confidential until viewing approval.
- Canvas-based interactive map with approximate radius circle boundaries, pan/zoom, and preview cards.

### F. Tenant Requirement Profiles & Transparent Matching
- Tenants can create profiles ("What are you looking for?") specifying budget, preferred location, furnishing, pets, and parking.
- Transparent match scoring (e.g., 92% Match) with itemized positive and warning reasons (no unexplained black-box AI scores).

### G. In-App Messaging & Viewing Requests
- Secure in-app messaging without exposing personal phone numbers.
- Viewing request scheduler with preferred date, time window, visitors count, and landlord response workflow.

### H. Admin Panel & Moderation
- Location Administration: Add/edit provinces, cities, barangays; toggle active/inactive.
- Report moderation queue (Fake property, fake owner, misleading photos, suspicious payments).
- Verification document queue.
- Location-aware market analytics labeled explicitly as *Balay-Inn platform listing data*.

---

## 2. Database Schema (PostgreSQL / Room Relational Mapping)

- `countries` (`id`, `name`, `code`, `is_active`)
- `regions` (`id`, `country_id`, `name`, `code`, `is_active`)
- `provinces` (`id`, `region_id`, `name`, `code`, `is_active`)
- `cities_municipalities` (`id`, `province_id`, `name`, `type`, `code`, `is_active`)
- `barangays` (`id`, `city_municipality_id`, `name`, `code`, `is_active`)
- `users` (`id`, `full_name`, `email`, `phone`, `role`, `relationship_to_property`, `is_authorized_by_owner`, `verification_level`, `phone_verified`, `email_verified`, `identity_submitted`, `professional_verified`, `prc_number`)
- `properties` (`id`, `title`, `description`, `property_type`, `monthly_rent`, `country_id`, `region_id`, `province_id`, `city_municipality_id`, `barangay_id`, `neighborhood`, `nearby_landmark`, `street_address_private`, `location_privacy`, `latitude`, `longitude`, `bedrooms`, `bathrooms`, `floor_area_sqm`, `furnished_status`, `parking_available`, `pet_policy`, `association_dues`, `parking_fees`, `maintenance_fees`, `amenities_json`, `status`, `owner_id`, `poster_id`, `poster_name`, `poster_role`, `is_poster_authorized`, `verification_level`)
- `property_images` (`id`, `property_id`, `image_url`, `is_primary`, `caption`, `order_index`)
- `viewing_requests` (`id`, `property_id`, `tenant_id`, `poster_id`, `preferred_date`, `preferred_time`, `number_of_visitors`, `tenant_message`, `poster_response`, `status`)
- `conversations` (`id`, `property_id`, `property_title`, `tenant_id`, `tenant_name`, `poster_id`, `poster_name`, `last_message_text`, `last_message_time`)
- `messages` (`id`, `conversation_id`, `sender_id`, `sender_name`, `message_text`, `timestamp`, `is_read`)
- `tenant_requirements` (`id`, `user_id`, `profile_name`, `preferred_province_id`, `preferred_city_id`, `min_budget`, `max_budget`, `furnished_required`, `parking_required`, `pet_friendly_required`)
- `saved_properties` (`id`, `user_id`, `property_id`, `saved_at`)
- `saved_searches` (`id`, `user_id`, `search_name`, `query_json`, `frequency`)
- `notifications` (`id`, `user_id`, `title`, `message`, `category`, `related_property_id`, `is_read`)
- `reports` (`id`, `reporter_id`, `target_type`, `target_id`, `reason`, `details`, `status`)
- `verification_documents` (`id`, `user_id`, `property_id`, `document_type`, `status`, `file_name`, `reviewer_notes`)
- `audit_logs` (`id`, `admin_user_id`, `action_type`, `entity_type`, `entity_id`, `notes`, `timestamp`)

---

## 3. Demo Persona Testing Switcher

To test all user journeys without dead ends, tap the **Persona Pill** in the top-right corner of the Home screen:
1. **Joshua Alcantara** (Tenant) — Test search, favorites, requirement profile matching, viewing requests, direct chat.
2. **Donato Yap** (Property Owner) — Test listing management, rent status toggle, incoming inquiries.
3. **Maria Santos** (Authorized Representative / Family Member) — Test representative listing posting, viewing approvals.
4. **Marco Villanueva, REB** (Licensed Real Estate Broker, PRC #0028192) — Test PRC verified professional badge and high-value listing management.
5. **Balay-Inn Compliance Admin** (Admin) — Access the Admin Panel: location management, reports moderation, document approvals, and platform analytics.
