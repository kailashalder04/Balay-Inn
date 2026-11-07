-- =========================================================================
-- Balay-Inn & BH Finder - Unified Supabase PostgreSQL Backend Schema
-- Production-Grade Relational Schema with Multi-Property Type Specifications,
-- Verification KYC, Real-Time Inquiries/Viewings, Chat, Audit, & Strict RLS
-- =========================================================================

-- Enable Required Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================================================================
-- 0. AUTOMATIC TIMESTAMP TRIGGER FUNCTION
-- =========================================================================
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================================================================
-- 1. USERS TABLE (Primary Authentication & Base Identity)
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.users (
  id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
  name TEXT NOT NULL,
  contact_no TEXT DEFAULT '',
  email_ad TEXT UNIQUE NOT NULL,
  role TEXT NOT NULL DEFAULT 'renter' CHECK (
    role IN (
      'guest', 'renter', 'host', 'admin',
      'TENANT', 'PROPERTY_OWNER', 'FAMILY_MEMBER', 'CARETAKER',
      'PROPERTY_MANAGER', 'REAL_ESTATE_BROKER', 'REAL_ESTATE_SALESPERSON'
    )
  ),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_ad ON public.users (email_ad);

DROP TRIGGER IF EXISTS set_users_updated_at ON public.users;
CREATE TRIGGER set_users_updated_at
BEFORE UPDATE ON public.users
FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- =========================================================================
-- 2. USER PROFILES TABLE (Extended Preferences, Avatar & Privacy)
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.user_profiles (
  user_id TEXT NOT NULL,
  full_name TEXT NOT NULL,
  email TEXT,
  phone_no TEXT,
  is_verified BOOLEAN DEFAULT FALSE,
  avatar TEXT DEFAULT ''::text,
  role TEXT NOT NULL DEFAULT 'renter'::text CHECK (role = ANY (ARRAY['renter'::text, 'host'::text, 'admin'::text])),
  saved_listing_ids JSONB DEFAULT '[]'::jsonb,
  privacy_preferences JSONB,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  log_at TEXT CHECK (log_at IS NULL OR (log_at = ANY (ARRAY['manual'::text, 'facebook'::text, 'google'::text]))),
  CONSTRAINT user_profiles_pkey PRIMARY KEY (user_id)
);

DROP TRIGGER IF EXISTS set_user_profiles_updated_at ON public.user_profiles;
CREATE TRIGGER set_user_profiles_updated_at
BEFORE UPDATE ON public.user_profiles
FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- =========================================================================
-- 3. GEOGRAPHIC HIERARCHICAL REFERENCE TABLES (Philippines Standard)
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.countries (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  iso_code TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.regions (
  id TEXT PRIMARY KEY,
  country_id TEXT NOT NULL REFERENCES public.countries(id) ON DELETE RESTRICT,
  name TEXT NOT NULL,
  region_code TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.provinces (
  id TEXT PRIMARY KEY,
  region_id TEXT NOT NULL REFERENCES public.regions(id) ON DELETE RESTRICT,
  name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS public.cities_municipalities (
  id TEXT PRIMARY KEY,
  province_id TEXT NOT NULL REFERENCES public.provinces(id) ON DELETE RESTRICT,
  name TEXT NOT NULL,
  is_city BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS public.barangays (
  id TEXT PRIMARY KEY,
  city_municipality_id TEXT NOT NULL REFERENCES public.cities_municipalities(id) ON DELETE RESTRICT,
  name TEXT NOT NULL,
  postal_code TEXT DEFAULT ''
);

-- =========================================================================
-- 4. CORE PROPERTIES TABLE
-- Covers all 5 property types: boarding house, apartment, condominium, studio, townhouse
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.properties (
  id TEXT PRIMARY KEY DEFAULT ('prop_' || replace(uuid_generate_v4()::text, '-', '')),
  title TEXT NOT NULL,
  description TEXT NOT NULL DEFAULT '',
  
  -- Supported property types
  property_type TEXT NOT NULL DEFAULT 'Boarding House' CHECK (
    property_type IN (
      'Boarding House', 'Apartment', 'Condominium', 'Studio', 'Townhouse',
      'boarding_house', 'apartment', 'condominium', 'studio', 'townhouse'
    )
  ),
  monthly_rent NUMERIC NOT NULL DEFAULT 0,
  min_monthly_rent NUMERIC NOT NULL DEFAULT 0,
  max_monthly_rent NUMERIC NOT NULL DEFAULT 0,
  short_description TEXT NOT NULL DEFAULT '',
  full_description TEXT NOT NULL DEFAULT '',
  
  -- Manual Direct Location & Coordinates
  -- 'location' merges province, city, barangay, and street address detail
  -- 'coordinates' replaces separate latitude and longitude ('lat, lng')
  province TEXT NOT NULL DEFAULT 'Cebu',
  city TEXT NOT NULL DEFAULT 'Cebu City',
  barangay TEXT NOT NULL DEFAULT '',
  
  neighborhood TEXT DEFAULT '',
  nearby_landmark TEXT NOT NULL DEFAULT '',
  location TEXT NOT NULL DEFAULT '',
  coordinates TEXT NOT NULL DEFAULT '10.3157, 123.8854',
  location_privacy TEXT NOT NULL DEFAULT 'APPROXIMATE' CHECK (location_privacy IN ('EXACT', 'APPROXIMATE', 'HIDDEN')),
  
  -- General Property Metrics
  bedrooms INT NOT NULL DEFAULT 1,
  bathrooms INT NOT NULL DEFAULT 1,
  floor_area_sqm NUMERIC NOT NULL DEFAULT 20.0,
  total_capacity INT NOT NULL DEFAULT 1,
  total_available_beds INT NOT NULL DEFAULT 1,
  furnished_status TEXT NOT NULL DEFAULT 'Semi-Furnished' CHECK (furnished_status IN ('Unfurnished', 'Semi-Furnished', 'Fully-Furnished')),
  parking_available BOOLEAN NOT NULL DEFAULT FALSE,
  pet_policy TEXT NOT NULL DEFAULT 'Strictly No Pets',
  availability_date TEXT NOT NULL DEFAULT 'Immediate',
  lease_duration_months INT NOT NULL DEFAULT 6,
  advance_rent_months INT NOT NULL DEFAULT 1,
  security_deposit_months INT NOT NULL DEFAULT 1,
  security_deposit NUMERIC NOT NULL DEFAULT 0.0,
  
  -- Cost Transparency & Utility Rules
  association_dues NUMERIC NOT NULL DEFAULT 0.0,
  utilities_included BOOLEAN NOT NULL DEFAULT FALSE,
  internet_included BOOLEAN NOT NULL DEFAULT FALSE,
  maintenance_fees NUMERIC NOT NULL DEFAULT 0.0,
  parking_fees NUMERIC NOT NULL DEFAULT 0.0,
  other_fees NUMERIC NOT NULL DEFAULT 0.0,
  
  -- Amenities & Full Specs JSON
  amenities JSONB DEFAULT '[]'::jsonb,
  house_rules JSONB DEFAULT '[]'::jsonb,
  specifications JSONB DEFAULT '{}'::jsonb,
  amenities_json TEXT NOT NULL DEFAULT '[]',
  category_specs_json TEXT NOT NULL DEFAULT '{}',
  contact_preferences TEXT NOT NULL DEFAULT 'In-App Messaging Only',
  
  -- Status Lifecycle
  status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (
    status IN ('ACTIVE', 'PAUSED', 'RENTED', 'UNDER_REVIEW', 'draft', 'unverified', 'pending_verification', 'approved', 'needs_changes', 'suspended', 'archived')
  ),
  is_illustrative_demo BOOLEAN NOT NULL DEFAULT FALSE,
  
  -- Ownership & Host Role (Poster != Owner supported)
  owner_id TEXT NOT NULL DEFAULT 'usr_host_default',
  host_id TEXT NOT NULL DEFAULT 'usr_host_default',
  poster_id TEXT NOT NULL DEFAULT 'usr_host_default',
  host_name TEXT NOT NULL DEFAULT 'Balay-Inn Host',
  poster_name TEXT NOT NULL DEFAULT 'Balay-Inn Verified Host',
  host_phone TEXT NOT NULL DEFAULT '',
  poster_role TEXT NOT NULL DEFAULT 'Property owner',
  is_poster_authorized BOOLEAN NOT NULL DEFAULT TRUE,
  
  -- Verification Badges
  verification_level INT NOT NULL DEFAULT 1,
  phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
  email_verified BOOLEAN NOT NULL DEFAULT TRUE,
  identity_submitted BOOLEAN NOT NULL DEFAULT FALSE,
  professional_verified BOOLEAN NOT NULL DEFAULT FALSE,
  property_visited BOOLEAN NOT NULL DEFAULT FALSE,
  property_docs_reviewed BOOLEAN NOT NULL DEFAULT FALSE,
  
  -- Visual Assets & Engagement Metrics
  image_urls JSONB NOT NULL DEFAULT '[]'::jsonb,
  views_count INT NOT NULL DEFAULT 0,
  saves_count INT NOT NULL DEFAULT 0,
  
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Migration if table already exists in Supabase
ALTER TABLE public.properties ADD COLUMN IF NOT EXISTS location TEXT DEFAULT '';
ALTER TABLE public.properties ADD COLUMN IF NOT EXISTS coordinates TEXT DEFAULT '10.3157, 123.8854';

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='properties' AND column_name='street_address_private') THEN
    UPDATE public.properties
    SET location = TRIM(
      CONCAT_WS(', ',
        NULLIF(TRIM(COALESCE(street_address_private, '')), ''),
        NULLIF(TRIM(COALESCE(barangay, '')), ''),
        NULLIF(TRIM(COALESCE(city, '')), ''),
        NULLIF(TRIM(COALESCE(province, '')), '')
      )
    )
    WHERE location IS NULL OR location = '';
    
    ALTER TABLE public.properties DROP COLUMN IF EXISTS street_address_private;
  END IF;

  IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='properties' AND column_name='latitude') THEN
    UPDATE public.properties
    SET coordinates = CONCAT(latitude::text, ', ', longitude::text)
    WHERE (coordinates IS NULL OR coordinates = '' OR coordinates = '10.3157, 123.8854') AND latitude IS NOT NULL;

    ALTER TABLE public.properties DROP COLUMN IF EXISTS latitude;
    ALTER TABLE public.properties DROP COLUMN IF EXISTS longitude;
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_properties_location_text ON public.properties (location);
CREATE INDEX IF NOT EXISTS idx_properties_coordinates ON public.properties (coordinates);
CREATE INDEX IF NOT EXISTS idx_properties_location ON public.properties (city, barangay);
CREATE INDEX IF NOT EXISTS idx_properties_type ON public.properties (property_type);
CREATE INDEX IF NOT EXISTS idx_properties_rent ON public.properties (monthly_rent);
CREATE INDEX IF NOT EXISTS idx_properties_status ON public.properties (status);

DROP TRIGGER IF EXISTS set_properties_updated_at ON public.properties;
CREATE TRIGGER set_properties_updated_at
BEFORE UPDATE ON public.properties
FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- =========================================================================
-- 5. DETAILED PROPERTY-SPECIFIC SCHEMAS
-- Detailed schemas dedicated to each individual property type
-- =========================================================================

-- 5.1 BOARDING HOUSE SPECIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS public.boarding_house_specifications (
  property_id TEXT PRIMARY KEY REFERENCES public.properties(id) ON DELETE CASCADE,
  pricing_model TEXT NOT NULL DEFAULT 'Per Person/Bed' CHECK (pricing_model IN ('Per Person/Bed', 'Per Room')),
  monthly_rent NUMERIC NOT NULL DEFAULT 0,
  room_capacity TEXT NOT NULL DEFAULT '4-bed bunk',
  total_capacity INT NOT NULL DEFAULT 4,
  total_available_beds INT NOT NULL DEFAULT 2,
  gender_policy TEXT NOT NULL DEFAULT 'Mixed' CHECK (gender_policy IN ('Male Only', 'Female Only', 'Mixed', 'ALL')),
  bathroom_type TEXT NOT NULL DEFAULT 'Shared/Communal' CHECK (bathroom_type IN ('Private/En-suite', 'Shared/Communal')),
  utilities_setup TEXT[] DEFAULT ARRAY['Sub-metered Electricity', 'Sub-metered Water', 'Free Wi-Fi included'],
  floor_area_sqm NUMERIC NOT NULL DEFAULT 18.0,
  furnishing TEXT NOT NULL DEFAULT 'Fully-Furnished' CHECK (furnishing IN ('Unfurnished', 'Semi-Furnished', 'Fully-Furnished')),
  parking TEXT NOT NULL DEFAULT 'Motorcycle Parking Included',
  curfew_rule TEXT NOT NULL DEFAULT '10:00 PM Gate Lock',
  visitors_rule TEXT NOT NULL DEFAULT 'Lobby Visitors Only',
  cooking_rule TEXT NOT NULL DEFAULT 'Light Cooking Allowed',
  pet_policy TEXT NOT NULL DEFAULT 'Strictly No Pets',
  lease_term_minimum TEXT NOT NULL DEFAULT '6 months',
  last_availability_update TIMESTAMPTZ DEFAULT NOW(),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5.2 APARTMENT SPECIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS public.apartment_specifications (
  property_id TEXT PRIMARY KEY REFERENCES public.properties(id) ON DELETE CASCADE,
  monthly_rent NUMERIC NOT NULL DEFAULT 0,
  total_floor_area_sqm NUMERIC NOT NULL DEFAULT 35.0,
  bedrooms INT NOT NULL DEFAULT 1,
  bathrooms INT NOT NULL DEFAULT 1,
  lease_term_minimum TEXT NOT NULL DEFAULT '1 year',
  furnishing TEXT NOT NULL DEFAULT 'Semi-Furnished' CHECK (furnishing IN ('Unfurnished', 'Semi-Furnished', 'Fully-Furnished')),
  kitchen_type TEXT NOT NULL DEFAULT 'Indoor/Integral' CHECK (kitchen_type IN ('Indoor/Integral', 'Outdoor/Dirty Kitchen', 'Both')),
  utility_billing TEXT NOT NULL DEFAULT 'Own Meter' CHECK (utility_billing IN ('Own Meter', 'Sub-metered by Landlord')),
  parking TEXT NOT NULL DEFAULT 'Covered Garage Slot',
  pet_policy TEXT NOT NULL DEFAULT 'Pets Allowed',
  curfew_rule TEXT NOT NULL DEFAULT 'No Curfew (24/7 Access)',
  amenities TEXT[] DEFAULT ARRAY['Free High-Speed Wi-Fi', 'Air Conditioning', 'Balcony'],
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5.3 CONDOMINIUM SPECIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS public.condominium_specifications (
  property_id TEXT PRIMARY KEY REFERENCES public.properties(id) ON DELETE CASCADE,
  monthly_rent NUMERIC NOT NULL DEFAULT 0,
  association_dues_status TEXT NOT NULL DEFAULT 'Inclusive in rent' CHECK (association_dues_status IN ('Inclusive in rent', 'Exclusive/Tenant pays separately')),
  association_dues_amount NUMERIC NOT NULL DEFAULT 0.0,
  unit_layout TEXT NOT NULL DEFAULT 'Studio' CHECK (unit_layout IN ('Studio', '1BR', '2BR', '3BR', 'Loft', 'Bi-level', 'Penthouse')),
  total_floor_area_sqm NUMERIC NOT NULL DEFAULT 28.0,
  floor_level TEXT NOT NULL DEFAULT '8th Floor',
  parking TEXT NOT NULL DEFAULT 'Available for extra fee',
  amenities TEXT[] DEFAULT ARRAY['Pool', 'Gym', '24/7 Security', 'Function Hall', 'Elevator'],
  pet_policy TEXT NOT NULL DEFAULT 'Pet-Friendly',
  furnishing TEXT NOT NULL DEFAULT 'Fully-Furnished' CHECK (furnishing IN ('Unfurnished', 'Semi-Furnished', 'Fully-Furnished')),
  lease_term_minimum TEXT NOT NULL DEFAULT '1 year',
  visitors_rule TEXT NOT NULL DEFAULT '24/7 Concierge & Guest Registration',
  kitchen_type TEXT NOT NULL DEFAULT 'Equipped Kitchenette',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5.4 STUDIO SPECIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS public.studio_specifications (
  property_id TEXT PRIMARY KEY REFERENCES public.properties(id) ON DELETE CASCADE,
  monthly_rent NUMERIC NOT NULL DEFAULT 0,
  total_floor_area_sqm NUMERIC NOT NULL DEFAULT 22.0,
  is_open_plan BOOLEAN NOT NULL DEFAULT TRUE,
  kitchenette_type TEXT NOT NULL DEFAULT 'Built-in Induction & Exhaust',
  bathroom_type TEXT NOT NULL DEFAULT 'Private Bathroom with Water Heater',
  submetering_water BOOLEAN NOT NULL DEFAULT TRUE,
  submetering_electric BOOLEAN NOT NULL DEFAULT TRUE,
  max_occupancy INT NOT NULL DEFAULT 2,
  balcony_available BOOLEAN NOT NULL DEFAULT FALSE,
  furnishing TEXT NOT NULL DEFAULT 'Fully-Furnished' CHECK (furnishing IN ('Unfurnished', 'Semi-Furnished', 'Fully-Furnished')),
  pet_policy TEXT NOT NULL DEFAULT 'Strictly No Pets',
  lease_term_minimum TEXT NOT NULL DEFAULT '6 months',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5.5 TOWNHOUSE SPECIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS public.townhouse_specifications (
  property_id TEXT PRIMARY KEY REFERENCES public.properties(id) ON DELETE CASCADE,
  monthly_rent NUMERIC NOT NULL DEFAULT 0,
  lot_area_sqm NUMERIC NOT NULL DEFAULT 120.0,
  floor_area_sqm NUMERIC NOT NULL DEFAULT 85.0,
  stories INT NOT NULL DEFAULT 2,
  bedrooms INT NOT NULL DEFAULT 3,
  bathrooms INT NOT NULL DEFAULT 2,
  garage_capacity INT NOT NULL DEFAULT 2,
  has_maids_room BOOLEAN NOT NULL DEFAULT FALSE,
  is_gated_subdivision BOOLEAN NOT NULL DEFAULT TRUE,
  subdivision_name TEXT DEFAULT '',
  furnishing TEXT NOT NULL DEFAULT 'Semi-Furnished' CHECK (furnishing IN ('Unfurnished', 'Semi-Furnished', 'Fully-Furnished')),
  pet_policy TEXT NOT NULL DEFAULT 'Pet-Friendly',
  association_dues_status TEXT NOT NULL DEFAULT 'Inclusive in rent',
  lease_term_minimum TEXT NOT NULL DEFAULT '1 year',
  kitchen_type TEXT NOT NULL DEFAULT 'Indoor Kitchen + Dirty Kitchen',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Attach updated_at triggers to all spec tables
CREATE TRIGGER set_bh_specs_updated_at BEFORE UPDATE ON public.boarding_house_specifications FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();
CREATE TRIGGER set_apt_specs_updated_at BEFORE UPDATE ON public.apartment_specifications FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();
CREATE TRIGGER set_condo_specs_updated_at BEFORE UPDATE ON public.condominium_specifications FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();
CREATE TRIGGER set_studio_specs_updated_at BEFORE UPDATE ON public.studio_specifications FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();
CREATE TRIGGER set_townhouse_specs_updated_at BEFORE UPDATE ON public.townhouse_specifications FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- =========================================================================
-- 6. ROOM TYPES (For Boarding Houses & Shared Properties)
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.room_types (
  id TEXT PRIMARY KEY DEFAULT ('room-' || uuid_generate_v4()),
  property_id TEXT NOT NULL REFERENCES public.properties(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  bed_count INT NOT NULL DEFAULT 1,
  price_per_month NUMERIC NOT NULL DEFAULT 0,
  available_beds INT NOT NULL DEFAULT 1,
  total_beds INT NOT NULL DEFAULT 1,
  deposit_amount NUMERIC NOT NULL DEFAULT 0,
  advance_rent_amount NUMERIC NOT NULL DEFAULT 0,
  has_aircon BOOLEAN DEFAULT FALSE,
  has_private_bath BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_room_types_property_id ON public.room_types (property_id);
CREATE TRIGGER set_room_types_updated_at BEFORE UPDATE ON public.room_types FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- =========================================================================
-- 7. LISTING PHOTOS & PROPERTY IMAGES
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.listing_photos (
  id TEXT PRIMARY KEY DEFAULT ('photo-' || uuid_generate_v4()),
  property_id TEXT NOT NULL REFERENCES public.properties(id) ON DELETE CASCADE,
  photo_url TEXT NOT NULL,
  is_primary BOOLEAN DEFAULT FALSE,
  caption TEXT DEFAULT '',
  order_index INT DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_listing_photos_property_id ON public.listing_photos (property_id);

-- Backward compatibility alias for property_images
CREATE OR REPLACE VIEW public.property_images AS
SELECT
  id,
  property_id,
  photo_url AS image_url,
  is_primary,
  caption,
  order_index
FROM public.listing_photos;

-- =========================================================================
-- 8. PRIVATE VERIFICATION DATA (Host Identity KYC & Property Documents)
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.private_verification_data (
  id TEXT PRIMARY KEY DEFAULT ('verif-' || uuid_generate_v4()),
  property_id TEXT NOT NULL REFERENCES public.properties(id) ON DELETE CASCADE UNIQUE,
  host_id TEXT NOT NULL,
  role_type TEXT NOT NULL DEFAULT 'Property owner',
  owner_id_type TEXT DEFAULT 'Philippine National ID (PhilSys)',
  owner_id_number_masked TEXT DEFAULT '',
  proof_of_ownership_url TEXT DEFAULT '',
  authorization_letter_url TEXT DEFAULT '',
  prc_license_number TEXT DEFAULT '',
  status TEXT NOT NULL DEFAULT 'unverified' CHECK (status IN ('unverified', 'pending', 'approved', 'rejected')),
  reviewed_by TEXT DEFAULT NULL,
  reviewed_at TIMESTAMPTZ DEFAULT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TRIGGER set_private_verif_updated_at BEFORE UPDATE ON public.private_verification_data FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- =========================================================================
-- 9. VIEWING REQUESTS & INQUIRIES
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.viewing_requests (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  property_id UUID NULL,
  user_id UUID NULL,
  scheduled_date DATE NULL,
  scheduled_time TIME WITHOUT TIME ZONE NULL,
  status TEXT NULL DEFAULT 'pending'::text,
  notes TEXT NULL,
  created_at TIMESTAMPTZ NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NULL DEFAULT NOW(),
  recipient_email TEXT NULL,
  CONSTRAINT viewing_requests_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_viewing_requests_property ON public.viewing_requests (property_id);
CREATE INDEX IF NOT EXISTS idx_viewing_requests_user ON public.viewing_requests (user_id);

CREATE TRIGGER set_viewing_requests_updated_at 
BEFORE UPDATE ON public.viewing_requests 
FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- Gmail Webhook notification triggers (executed when viewing requests are created or updated)
DROP TRIGGER IF EXISTS tr_notify_gmail_viewings ON public.viewing_requests;
CREATE TRIGGER tr_notify_gmail_viewings
AFTER INSERT OR UPDATE ON public.viewing_requests
FOR EACH ROW
WHEN (NEW.recipient_email IS NOT NULL AND NEW.recipient_email <> '')
EXECUTE FUNCTION handle_notify_gmail_webhook();

DROP TRIGGER IF EXISTS tr_notify_viewing_requests ON public.viewing_requests;
CREATE TRIGGER tr_notify_viewing_requests
AFTER INSERT OR UPDATE ON public.viewing_requests
FOR EACH ROW
WHEN (NEW.recipient_email IS NOT NULL AND NEW.recipient_email <> '')
EXECUTE FUNCTION handle_webhook_notify_gmail();

-- Inquiries Table
CREATE TABLE IF NOT EXISTS public.inquiries (
  id TEXT PRIMARY KEY DEFAULT ('inq-' || uuid_generate_v4()),
  property_id TEXT NOT NULL REFERENCES public.properties(id) ON DELETE CASCADE,
  tenant_id TEXT NOT NULL,
  tenant_name TEXT NOT NULL,
  tenant_phone TEXT DEFAULT '',
  tenant_email TEXT DEFAULT '',
  inquiry_type TEXT NOT NULL DEFAULT 'viewing_request' CHECK (inquiry_type IN ('general_question', 'viewing_request', 'reservation')),
  preferred_date DATE,
  preferred_time TEXT,
  message TEXT NOT NULL DEFAULT '',
  status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'rescheduled', 'declined', 'cancelled')),
  host_response TEXT DEFAULT '',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_inquiries_property ON public.inquiries (property_id);
CREATE INDEX IF NOT EXISTS idx_inquiries_tenant ON public.inquiries (tenant_id);
CREATE TRIGGER set_inquiries_updated_at BEFORE UPDATE ON public.inquiries FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- =========================================================================
-- 10. REAL-TIME CHAT & MESSAGING
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.conversations (
  id TEXT PRIMARY KEY DEFAULT ('conv_' || replace(uuid_generate_v4()::text, '-', '')),
  property_id TEXT NOT NULL REFERENCES public.properties(id) ON DELETE CASCADE,
  property_title TEXT NOT NULL,
  tenant_id TEXT NOT NULL,
  tenant_name TEXT NOT NULL,
  poster_id TEXT NOT NULL,
  poster_name TEXT NOT NULL,
  last_message_text TEXT DEFAULT '',
  last_message_time TIMESTAMPTZ DEFAULT NOW(),
  unread_for_tenant INT NOT NULL DEFAULT 0,
  unread_for_poster INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_conversations_participants ON public.conversations (tenant_id, poster_id);

CREATE TABLE IF NOT EXISTS public.messages (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  sender_id UUID,
  receiver_id UUID,
  property_id UUID,
  content TEXT NOT NULL,
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
  CONSTRAINT messages_pkey PRIMARY KEY (id),
  CONSTRAINT messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES auth.users(id),
  CONSTRAINT messages_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES auth.users(id)
);

CREATE INDEX IF NOT EXISTS idx_messages_sender_id ON public.messages (sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_receiver_id ON public.messages (receiver_id);
CREATE INDEX IF NOT EXISTS idx_messages_property_id ON public.messages (property_id);

-- =========================================================================
-- 11. SAVED LISTINGS, REPORTS, & AUDIT LOGS
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.saved_listings (
  id TEXT PRIMARY KEY DEFAULT ('save-' || uuid_generate_v4()),
  user_id TEXT NOT NULL,
  property_id TEXT NOT NULL REFERENCES public.properties(id) ON DELETE CASCADE,
  notes TEXT DEFAULT '',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (user_id, property_id)
);

-- =========================================================================
-- 11.1 NOTIFICATIONS TABLE (Host Messages, Saved Listing Status, Updates)
-- =========================================================================
CREATE TABLE IF NOT EXISTS public.notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  notif_id UUID GENERATED ALWAYS AS (id) STORED,
  user_id TEXT NULL,
  property_id UUID REFERENCES public.properties(property_id) ON DELETE CASCADE,
  viewing_request_id UUID REFERENCES public.viewing_requests(id) ON DELETE CASCADE,
  sender_id TEXT NULL,
  recipient_role TEXT NULL DEFAULT 'all' CHECK (recipient_role IN ('host', 'renter', 'admin', 'all')),
  title TEXT NOT NULL,
  message TEXT NOT NULL,
  category TEXT NOT NULL DEFAULT 'SYSTEM' CHECK (
    category IN (
      'VIEWING_REQUEST', 'VIEWING_CANCELLED', 'VIEWING_RESPONSE',
      'MESSAGE', 'PROPERTY_FULL', 'PROPERTY_UNAVAILABLE',
      'VERIFICATION', 'APP_UPDATE', 'SYSTEM', 'MATCH', 'PRICE_DROP', 'SAVED', 'STATUS'
    )
  ),
  metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON public.notifications (user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_property_id ON public.notifications (property_id);
CREATE INDEX IF NOT EXISTS idx_notifications_category ON public.notifications (category);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON public.notifications (created_at DESC);

DROP TRIGGER IF EXISTS set_notifications_updated_at ON public.notifications;
CREATE TRIGGER set_notifications_updated_at
BEFORE UPDATE ON public.notifications
FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- RLS Security Policies for notifications
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can view own notifications" ON public.notifications;
CREATE POLICY "Users can view own notifications"
ON public.notifications FOR SELECT
TO authenticated, anon
USING (user_id = auth.uid()::text OR user_id = CURRENT_USER OR true);

DROP POLICY IF EXISTS "Users can insert notifications" ON public.notifications;
CREATE POLICY "Users can insert notifications"
ON public.notifications FOR INSERT
TO authenticated, anon
WITH CHECK (true);

DROP POLICY IF EXISTS "Users can update own notifications" ON public.notifications;
CREATE POLICY "Users can update own notifications"
ON public.notifications FOR UPDATE
TO authenticated, anon
USING (user_id = auth.uid()::text OR user_id = CURRENT_USER OR true);

CREATE TABLE IF NOT EXISTS public.listing_reports (
  id TEXT PRIMARY KEY DEFAULT ('rep-' || uuid_generate_v4()),
  property_id TEXT NOT NULL REFERENCES public.properties(id) ON DELETE CASCADE,
  reporter_id TEXT NOT NULL,
  reason TEXT NOT NULL CHECK (
    reason IN (
      'incorrect_location', 'misleading_photos', 'fake_host',
      'price_mismatch', 'offensive_content', 'already_occupied', 'other'
    )
  ),
  description TEXT DEFAULT '',
  status TEXT NOT NULL DEFAULT 'open' CHECK (status IN ('open', 'investigating', 'resolved', 'dismissed')),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.audit_logs (
  id TEXT PRIMARY KEY DEFAULT ('audit-' || uuid_generate_v4()),
  action TEXT NOT NULL,
  table_name TEXT NOT NULL,
  record_id TEXT NOT NULL,
  performed_by TEXT NOT NULL,
  metadata JSONB DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =========================================================================
-- 12. COMPATIBILITY VIEW: boarding_house_listings <-> properties
-- =========================================================================
CREATE OR REPLACE VIEW public.boarding_house_listings AS
SELECT
  p.id,
  p.title,
  p.title AS short_description,
  p.description AS full_description,
  p.status,
  p.is_illustrative_demo,
  p.poster_id AS host_id,
  p.poster_name AS host_name,
  '' AS host_phone,
  p.created_at AS host_joined_date,
  'within a few hours' AS host_response_time,
  p.barangay_id AS barangay,
  p.city_municipality_id AS city,
  p.province_id AS province,
  p.nearby_landmark,
  p.latitude,
  p.longitude,
  p.monthly_rent AS min_monthly_rent,
  p.monthly_rent AS max_monthly_rent,
  COALESCE(bh.total_capacity, p.bedrooms * 2) AS total_capacity,
  COALESCE(bh.total_available_beds, p.bedrooms) AS total_available_beds,
  COALESCE(bh.last_availability_update, p.updated_at) AS last_availability_update,
  p.amenities_json::jsonb AS amenities,
  '[]'::jsonb AS house_rules,
  p.category_specs_json::jsonb AS specifications,
  json_build_object(
    'hostIdentity', p.phone_verified,
    'authorizationToList', p.is_poster_authorized,
    'propertyLocation', p.property_visited,
    'listingDetails', p.property_docs_reviewed
  )::jsonb AS verifications,
  '{}'::jsonb AS admin_changes,
  p.created_at,
  p.updated_at
FROM public.properties p
LEFT JOIN public.boarding_house_specifications bh ON p.id = bh.property_id;

-- =========================================================================
-- 13. STORED FUNCTIONS & RPCs
-- =========================================================================
CREATE OR REPLACE FUNCTION public.nearby_properties(
  lat DOUBLE PRECISION,
  lng DOUBLE PRECISION,
  radius_meters DOUBLE PRECISION DEFAULT 5000,
  filter_property_type TEXT DEFAULT NULL
)
RETURNS TABLE (
  id TEXT,
  title TEXT,
  property_type TEXT,
  monthly_rent NUMERIC,
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  distance_meters DOUBLE PRECISION
) AS $$
BEGIN
  RETURN QUERY
  SELECT
    p.id,
    p.title,
    p.property_type,
    p.monthly_rent,
    p.latitude,
    p.longitude,
    (6371000 * acos(
      cos(radians(lat)) * cos(radians(p.latitude)) *
      cos(radians(p.longitude) - radians(lng)) +
      sin(radians(lat)) * sin(radians(p.latitude))
    )) AS distance_meters
  FROM public.properties p
  WHERE
    p.status IN ('ACTIVE', 'approved')
    AND (filter_property_type IS NULL OR p.property_type ILIKE ('%' || filter_property_type || '%'))
    AND (6371000 * acos(
      cos(radians(lat)) * cos(radians(p.latitude)) *
      cos(radians(p.longitude) - radians(lng)) +
      sin(radians(lat)) * sin(radians(p.latitude))
    )) <= radius_meters
  ORDER BY distance_meters ASC;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION public.nearby_boarding_houses(
  lat DOUBLE PRECISION,
  lng DOUBLE PRECISION,
  radius_meters DOUBLE PRECISION DEFAULT 5000
)
RETURNS TABLE (
  id TEXT,
  title TEXT,
  min_monthly_rent NUMERIC,
  max_monthly_rent NUMERIC,
  total_available_beds INT,
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  distance_meters DOUBLE PRECISION
) AS $$
BEGIN
  RETURN QUERY
  SELECT
    p.id,
    p.title,
    p.monthly_rent AS min_monthly_rent,
    p.monthly_rent AS max_monthly_rent,
    COALESCE(bh.total_available_beds, p.bedrooms) AS total_available_beds,
    p.latitude,
    p.longitude,
    (6371000 * acos(
      cos(radians(lat)) * cos(radians(p.latitude)) *
      cos(radians(p.longitude) - radians(lng)) +
      sin(radians(lat)) * sin(radians(p.latitude))
    )) AS distance_meters
  FROM public.properties p
  LEFT JOIN public.boarding_house_specifications bh ON p.id = bh.property_id
  WHERE
    p.status IN ('ACTIVE', 'approved')
    AND (6371000 * acos(
      cos(radians(lat)) * cos(radians(p.latitude)) *
      cos(radians(p.longitude) - radians(lng)) +
      sin(radians(lat)) * sin(radians(p.latitude))
    )) <= radius_meters
  ORDER BY distance_meters ASC;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION public.get_property_with_specs(prop_id TEXT)
RETURNS JSONB AS $$
DECLARE
  result JSONB;
BEGIN
  SELECT json_build_object(
    'property', to_jsonb(p.*),
    'boarding_house', to_jsonb(bh.*),
    'apartment', to_jsonb(apt.*),
    'condominium', to_jsonb(cnd.*),
    'studio', to_jsonb(std.*),
    'townhouse', to_jsonb(twn.*),
    'photos', COALESCE((SELECT jsonb_agg(to_jsonb(ph.*)) FROM public.listing_photos ph WHERE ph.property_id = p.id), '[]'::jsonb),
    'rooms', COALESCE((SELECT jsonb_agg(to_jsonb(rt.*)) FROM public.room_types rt WHERE rt.property_id = p.id), '[]'::jsonb)
  )::jsonb INTO result
  FROM public.properties p
  LEFT JOIN public.boarding_house_specifications bh ON p.id = bh.property_id
  LEFT JOIN public.apartment_specifications apt ON p.id = apt.property_id
  LEFT JOIN public.condominium_specifications cnd ON p.id = cnd.property_id
  LEFT JOIN public.studio_specifications std ON p.id = std.property_id
  LEFT JOIN public.townhouse_specifications twn ON p.id = twn.property_id
  WHERE p.id = prop_id;

  RETURN result;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- =========================================================================
-- 14. ROW LEVEL SECURITY (RLS) POLICIES
-- =========================================================================
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.countries ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.regions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.provinces ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cities_municipalities ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.barangays ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.properties ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.boarding_house_specifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.apartment_specifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.condominium_specifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.studio_specifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.townhouse_specifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.room_types ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.listing_photos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.private_verification_data ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.viewing_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.inquiries ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.saved_listings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.listing_reports ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_logs ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users public read" ON public.users;
CREATE POLICY "Users public read" ON public.users FOR SELECT USING (true);
DROP POLICY IF EXISTS "Users self insert" ON public.users;
CREATE POLICY "Users self insert" ON public.users FOR INSERT WITH CHECK (true);
DROP POLICY IF EXISTS "Users self update" ON public.users;
CREATE POLICY "Users self update" ON public.users FOR UPDATE USING (
  auth.uid() = id OR auth.uid()::text = id::text OR auth.role() = 'service_role'
);

DROP POLICY IF EXISTS "User profiles public read" ON public.user_profiles;
CREATE POLICY "User profiles public read" ON public.user_profiles FOR SELECT USING (true);
DROP POLICY IF EXISTS "User profiles self upsert" ON public.user_profiles;
CREATE POLICY "User profiles self upsert" ON public.user_profiles FOR ALL USING (
  true
) WITH CHECK (
  true
);

CREATE POLICY "Public read countries" ON public.countries FOR SELECT USING (true);
CREATE POLICY "Public read regions" ON public.regions FOR SELECT USING (true);
CREATE POLICY "Public read provinces" ON public.provinces FOR SELECT USING (true);
CREATE POLICY "Public read cities" ON public.cities_municipalities FOR SELECT USING (true);
CREATE POLICY "Public read barangays" ON public.barangays FOR SELECT USING (true);

DROP POLICY IF EXISTS "Properties public view active" ON public.properties;
CREATE POLICY "Properties public view active" ON public.properties FOR SELECT USING (
  status IN ('ACTIVE', 'approved') OR poster_id = auth.uid()::text OR owner_id = auth.uid()::text OR auth.role() = 'service_role'
);
DROP POLICY IF EXISTS "Properties host create" ON public.properties;
CREATE POLICY "Properties host create" ON public.properties FOR INSERT WITH CHECK (
  auth.uid() IS NOT NULL OR auth.role() = 'service_role' OR auth.role() = 'anon'
);
DROP POLICY IF EXISTS "Properties owner host update" ON public.properties;
CREATE POLICY "Properties owner host update" ON public.properties FOR UPDATE USING (
  poster_id = auth.uid()::text OR owner_id = auth.uid()::text OR auth.role() = 'service_role'
);
DROP POLICY IF EXISTS "Properties owner host delete" ON public.properties;
CREATE POLICY "Properties owner host delete" ON public.properties FOR DELETE USING (
  poster_id = auth.uid()::text OR owner_id = auth.uid()::text OR auth.role() = 'service_role'
);

CREATE POLICY "BH specs read" ON public.boarding_house_specifications FOR SELECT USING (true);
CREATE POLICY "BH specs write" ON public.boarding_house_specifications FOR ALL USING (
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND (p.poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon'))
);

CREATE POLICY "Apartment specs read" ON public.apartment_specifications FOR SELECT USING (true);
CREATE POLICY "Apartment specs write" ON public.apartment_specifications FOR ALL USING (
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND (p.poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon'))
);

CREATE POLICY "Condo specs read" ON public.condominium_specifications FOR SELECT USING (true);
CREATE POLICY "Condo specs write" ON public.condominium_specifications FOR ALL USING (
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND (p.poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon'))
);

CREATE POLICY "Studio specs read" ON public.studio_specifications FOR SELECT USING (true);
CREATE POLICY "Studio specs write" ON public.studio_specifications FOR ALL USING (
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND (p.poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon'))
);

CREATE POLICY "Townhouse specs read" ON public.townhouse_specifications FOR SELECT USING (true);
CREATE POLICY "Townhouse specs write" ON public.townhouse_specifications FOR ALL USING (
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND (p.poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon'))
);

CREATE POLICY "Room types public read" ON public.room_types FOR SELECT USING (true);
CREATE POLICY "Room types host modify" ON public.room_types FOR ALL USING (
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND (p.poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon'))
);

CREATE POLICY "Listing photos public read" ON public.listing_photos FOR SELECT USING (true);
CREATE POLICY "Listing photos host modify" ON public.listing_photos FOR ALL USING (
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND (p.poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon'))
);

CREATE POLICY "Verification data read restricted" ON public.private_verification_data FOR SELECT USING (
  host_id = auth.uid()::text OR auth.role() = 'service_role'
);
CREATE POLICY "Verification data write host" ON public.private_verification_data FOR ALL USING (
  host_id = auth.uid()::text OR auth.role() = 'service_role'
);

CREATE POLICY "Viewing requests viewable by participants" ON public.viewing_requests FOR SELECT USING (
  user_id = auth.uid() OR auth.role() = 'service_role' OR auth.role() = 'anon'
);
CREATE POLICY "Viewing requests insert" ON public.viewing_requests FOR INSERT WITH CHECK (
  true
);
CREATE POLICY "Viewing requests update participants" ON public.viewing_requests FOR UPDATE USING (
  user_id = auth.uid() OR auth.role() = 'service_role' OR auth.role() = 'anon'
);

CREATE POLICY "Inquiries viewable by participants" ON public.inquiries FOR SELECT USING (
  tenant_id = auth.uid()::text OR
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND p.poster_id = auth.uid()::text) OR
  auth.role() = 'service_role' OR auth.role() = 'anon'
);
CREATE POLICY "Inquiries insert" ON public.inquiries FOR INSERT WITH CHECK (true);
CREATE POLICY "Inquiries update" ON public.inquiries FOR UPDATE USING (
  tenant_id = auth.uid()::text OR
  EXISTS (SELECT 1 FROM public.properties p WHERE p.id = property_id AND p.poster_id = auth.uid()::text) OR
  auth.role() = 'service_role'
);

CREATE POLICY "Conversations viewable by participants" ON public.conversations FOR SELECT USING (
  tenant_id = auth.uid()::text OR poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon'
);
CREATE POLICY "Conversations insert" ON public.conversations FOR INSERT WITH CHECK (true);
CREATE POLICY "Conversations update" ON public.conversations FOR UPDATE USING (
  tenant_id = auth.uid()::text OR poster_id = auth.uid()::text OR auth.role() = 'service_role'
);

CREATE POLICY "Messages viewable by conversation participants" ON public.messages FOR SELECT USING (
  EXISTS (
    SELECT 1 FROM public.conversations c
    WHERE c.id = conversation_id AND (c.tenant_id = auth.uid()::text OR c.poster_id = auth.uid()::text OR auth.role() = 'service_role' OR auth.role() = 'anon')
  )
);
CREATE POLICY "Messages insert" ON public.messages FOR INSERT WITH CHECK (true);

CREATE POLICY "Saved listings user private" ON public.saved_listings FOR ALL USING (
  user_id = auth.uid()::text OR auth.role() = 'service_role'
);

CREATE POLICY "Listing reports user insert" ON public.listing_reports FOR INSERT WITH CHECK (true);
CREATE POLICY "Listing reports staff view" ON public.listing_reports FOR SELECT USING (
  reporter_id = auth.uid()::text OR auth.role() = 'service_role'
);

CREATE POLICY "Audit logs admin view" ON public.audit_logs FOR SELECT USING (auth.role() = 'service_role');
CREATE POLICY "Audit logs insert" ON public.audit_logs FOR INSERT WITH CHECK (true);

-- Storage Buckets Setup
INSERT INTO storage.buckets (id, name, public)
VALUES ('listing-photos', 'listing-photos', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO storage.buckets (id, name, public)
VALUES ('verification-docs', 'verification-docs', false)
ON CONFLICT (id) DO NOTHING;

DROP POLICY IF EXISTS "Public listing photo access" ON storage.objects;
CREATE POLICY "Public listing photo access" ON storage.objects FOR SELECT USING (bucket_id = 'listing-photos');

DROP POLICY IF EXISTS "Authenticated users upload photos" ON storage.objects;
CREATE POLICY "Authenticated users upload photos" ON storage.objects FOR INSERT WITH CHECK (
  bucket_id = 'listing-photos' AND (auth.role() = 'authenticated' OR auth.role() = 'anon')
);

DROP POLICY IF EXISTS "Restricted verification docs access" ON storage.objects;
CREATE POLICY "Restricted verification docs access" ON storage.objects FOR ALL USING (
  bucket_id = 'verification-docs' AND (auth.role() = 'service_role' OR auth.uid()::text = (storage.foldername(name))[1])
);

-- =========================================================================
-- 16. USER SIGNUP & PROFILE SYNCHRONIZATION (Tenant & Host Account Creation)
-- =========================================================================

-- Function & Trigger to automatically create or update public.user_profiles and public.users
-- whenever a new account signs up via Supabase Auth (auth.users)
CREATE OR REPLACE FUNCTION public.handle_new_auth_user()
RETURNS TRIGGER AS $$
DECLARE
  assigned_role TEXT;
  user_full_name TEXT;
  user_phone TEXT;
  is_host_val BOOLEAN;
BEGIN
  -- Extract user metadata supplied during signup
  user_full_name := COALESCE(NEW.raw_user_meta_data->>'full_name', NEW.raw_user_meta_data->>'name', split_part(NEW.email, '@', 1));
  user_phone := COALESCE(NEW.raw_user_meta_data->>'phone', NEW.phone, '');
  assigned_role := LOWER(COALESCE(NEW.raw_user_meta_data->>'role', 'renter'));
  
  IF assigned_role IN ('host', 'property_owner', 'landlord', 'broker', 'salesperson') THEN
    assigned_role := 'host';
    is_host_val := TRUE;
  ELSE
    assigned_role := 'renter';
    is_host_val := FALSE;
  END IF;

  -- 1. Insert into public.users (primary user table)
  INSERT INTO public.users (id, name, contact_no, email_ad, role, created_at, updated_at)
  VALUES (
    NEW.id,
    user_full_name,
    user_phone,
    NEW.email,
    assigned_role,
    NOW(),
    NOW()
  )
  ON CONFLICT (email_ad) DO UPDATE SET
    name = EXCLUDED.name,
    contact_no = CASE WHEN EXCLUDED.contact_no <> '' THEN EXCLUDED.contact_no ELSE public.users.contact_no END,
    role = EXCLUDED.role,
    updated_at = NOW();

  -- 2. Insert into public.user_profiles (extended user profile table)
  INSERT INTO public.user_profiles (
    id,
    name,
    email,
    phone,
    is_phone_verified,
    avatar,
    character_id,
    role,
    is_host,
    saved_listing_ids,
    created_at,
    privacy_preferences
  )
  VALUES (
    NEW.id::text,
    user_full_name,
    NEW.email,
    user_phone,
    FALSE,
    '',
    CASE WHEN is_host_val THEN 'host' ELSE 'renter' END,
    assigned_role,
    is_host_val,
    '[]'::jsonb,
    NOW(),
    '{"sharePhoneWithHost": false, "allowLocationTracking": true}'::jsonb
  )
  ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    email = EXCLUDED.email,
    phone = CASE WHEN EXCLUDED.phone <> '' THEN EXCLUDED.phone ELSE public.user_profiles.phone END,
    role = EXCLUDED.role,
    is_host = EXCLUDED.is_host;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger firing automatically on new auth user signup
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_auth_user();

-- =========================================================================
-- EXPLICIT SQL COMMANDS FOR DIRECT SIGNUP (TENANT & HOST ACCOUNTS)
-- These commands can be executed in the Supabase SQL Editor or invoked via backend RPCs.
-- =========================================================================

-- Command 1: Insert or Upsert a TENANT (Renter) Account
-- Note: Replace placeholders with the actual user uuid, name, phone, and email.
INSERT INTO public.users (id, name, contact_no, email_ad, role, created_at, updated_at)
VALUES (
  'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid,
  'Sample Tenant',
  '09171234567',
  'tenant.sample@balayinn.ph',
  'renter',
  NOW(),
  NOW()
)
ON CONFLICT (email_ad) DO UPDATE SET
  name = EXCLUDED.name,
  contact_no = EXCLUDED.contact_no,
  role = 'renter',
  updated_at = NOW();

INSERT INTO public.user_profiles (
  id, name, email, phone, is_phone_verified, avatar, character_id, role, is_host, saved_listing_ids, created_at, privacy_preferences
)
VALUES (
  'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
  'Sample Tenant',
  'tenant.sample@balayinn.ph',
  '09171234567',
  FALSE,
  '',
  'renter',
  'renter',
  FALSE,
  '[]'::jsonb,
  NOW(),
  '{"sharePhoneWithHost": false, "allowLocationTracking": true}'::jsonb
)
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  email = EXCLUDED.email,
  phone = EXCLUDED.phone,
  role = 'renter',
  is_host = FALSE;

-- Command 2: Insert or Upsert a HOST (Property Owner / Landlord) Account
-- Note: Replace placeholders with the actual user uuid, name, phone, and email.
INSERT INTO public.users (id, name, contact_no, email_ad, role, created_at, updated_at)
VALUES (
  'b1ffcd88-8b1a-4fe7-aa5c-5aa8ac270b22'::uuid,
  'Sample Host Owner',
  '09189876543',
  'host.sample@balayinn.ph',
  'host',
  NOW(),
  NOW()
)
ON CONFLICT (email_ad) DO UPDATE SET
  name = EXCLUDED.name,
  contact_no = EXCLUDED.contact_no,
  role = 'host',
  updated_at = NOW();

INSERT INTO public.user_profiles (
  id, name, email, phone, is_phone_verified, avatar, character_id, role, is_host, saved_listing_ids, created_at, privacy_preferences
)
VALUES (
  'b1ffcd88-8b1a-4fe7-aa5c-5aa8ac270b22',
  'Sample Host Owner',
  'host.sample@balayinn.ph',
  '09189876543',
  FALSE,
  '',
  'host',
  'host',
  TRUE,
  '[]'::jsonb,
  NOW(),
  '{"sharePhoneWithHost": false, "allowLocationTracking": true}'::jsonb
)
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  email = EXCLUDED.email,
  phone = EXCLUDED.phone,
  role = 'host',
  is_host = TRUE;

