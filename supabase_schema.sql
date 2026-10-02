-- =========================================================================
-- Balay-Inn Rental Marketplace - Production Supabase PostgreSQL Schema
-- =========================================================================
-- Paste this script into your Supabase Dashboard -> SQL Editor and click "Run".

-- 1. Enable Required Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Master Geographic Hierarchy Tables
CREATE TABLE IF NOT EXISTS countries (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    code TEXT UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS regions (
    id TEXT PRIMARY KEY,
    country_id TEXT REFERENCES countries(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    code TEXT UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS provinces (
    id TEXT PRIMARY KEY,
    region_id TEXT REFERENCES regions(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    code TEXT UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS cities_municipalities (
    id TEXT PRIMARY KEY,
    province_id TEXT REFERENCES provinces(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    type TEXT NOT NULL DEFAULT 'City',
    code TEXT UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS barangays (
    id TEXT PRIMARY KEY,
    city_municipality_id TEXT REFERENCES cities_municipalities(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    code TEXT UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- 3. Rental Properties Table
CREATE TABLE IF NOT EXISTS properties (
    id TEXT PRIMARY KEY DEFAULT ('prop_' || uuid_generate_v4()::text),
    title TEXT NOT NULL,
    description TEXT DEFAULT '',
    property_type TEXT NOT NULL DEFAULT 'Condominium',
    monthly_rent NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    country_id TEXT DEFAULT 'ph',
    region_id TEXT DEFAULT 'reg_07',
    province_id TEXT DEFAULT 'prov_cebu',
    city_municipality_id TEXT DEFAULT 'city_cebu',
    barangay_id TEXT DEFAULT 'bgy_lahug',
    neighborhood TEXT DEFAULT '',
    nearby_landmark TEXT DEFAULT '',
    street_address_private TEXT DEFAULT '',
    location_privacy TEXT DEFAULT 'APPROXIMATE',
    latitude DOUBLE PRECISION NOT NULL DEFAULT 10.3157,
    longitude DOUBLE PRECISION NOT NULL DEFAULT 123.8854,
    bedrooms INTEGER DEFAULT 1,
    bathrooms INTEGER DEFAULT 1,
    floor_area_sqm NUMERIC(8, 2) DEFAULT 30.00,
    furnished_status TEXT DEFAULT 'Furnished',
    parking_available BOOLEAN DEFAULT FALSE,
    pet_policy TEXT DEFAULT 'No Pets',
    availability_date TEXT DEFAULT 'Immediate',
    lease_duration_months INTEGER DEFAULT 12,
    advance_rent_months INTEGER DEFAULT 1,
    security_deposit_months INTEGER DEFAULT 2,
    association_dues NUMERIC(10, 2) DEFAULT 0.00,
    utilities_included BOOLEAN DEFAULT FALSE,
    internet_included BOOLEAN DEFAULT FALSE,
    maintenance_fees NUMERIC(10, 2) DEFAULT 0.00,
    parking_fees NUMERIC(10, 2) DEFAULT 0.00,
    other_fees NUMERIC(10, 2) DEFAULT 0.00,
    amenities_json TEXT DEFAULT '[]',
    contact_preferences TEXT DEFAULT 'In-App Messaging Only',
    status TEXT DEFAULT 'ACTIVE',
    owner_id TEXT NOT NULL DEFAULT 'user_landlord_01',
    poster_id TEXT NOT NULL DEFAULT 'user_landlord_01',
    poster_name TEXT DEFAULT 'Balay-Inn Host',
    poster_role TEXT DEFAULT 'Property owner',
    is_poster_authorized BOOLEAN DEFAULT TRUE,
    verification_level INTEGER DEFAULT 2,
    phone_verified BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT TRUE,
    identity_submitted BOOLEAN DEFAULT FALSE,
    professional_verified BOOLEAN DEFAULT FALSE,
    property_visited BOOLEAN DEFAULT FALSE,
    property_docs_reviewed BOOLEAN DEFAULT FALSE,
    views_count INTEGER DEFAULT 0,
    saves_count INTEGER DEFAULT 0,
    created_at BIGINT DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT DEFAULT (extract(epoch from now()) * 1000)::bigint
);

-- Performance & Spatial Indexes
CREATE INDEX IF NOT EXISTS idx_properties_coords ON properties(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_properties_province ON properties(province_id);
CREATE INDEX IF NOT EXISTS idx_properties_city ON properties(city_municipality_id);
CREATE INDEX IF NOT EXISTS idx_properties_status ON properties(status);
CREATE INDEX IF NOT EXISTS idx_properties_rent ON properties(monthly_rent);

-- 4. Property Photos Table
CREATE TABLE IF NOT EXISTS property_images (
    id TEXT PRIMARY KEY DEFAULT ('img_' || uuid_generate_v4()::text),
    property_id TEXT REFERENCES properties(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    caption TEXT DEFAULT '',
    order_index INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_property_images_pid ON property_images(property_id);

-- 5. In-Person Viewing Requests Table
CREATE TABLE IF NOT EXISTS viewing_requests (
    id TEXT PRIMARY KEY DEFAULT ('viewing_' || uuid_generate_v4()::text),
    property_id TEXT REFERENCES properties(id) ON DELETE CASCADE,
    tenant_id TEXT NOT NULL,
    poster_id TEXT NOT NULL,
    preferred_date TEXT NOT NULL,
    preferred_time TEXT NOT NULL,
    number_of_visitors INTEGER DEFAULT 1,
    tenant_message TEXT DEFAULT '',
    poster_response TEXT DEFAULT '',
    status TEXT DEFAULT 'REQUESTED',
    created_at BIGINT DEFAULT (extract(epoch from now()) * 1000)::bigint,
    updated_at BIGINT DEFAULT (extract(epoch from now()) * 1000)::bigint
);

CREATE INDEX IF NOT EXISTS idx_viewing_property ON viewing_requests(property_id);
CREATE INDEX IF NOT EXISTS idx_viewing_tenant ON viewing_requests(tenant_id);

-- 6. Conversations & Messaging
CREATE TABLE IF NOT EXISTS conversations (
    id TEXT PRIMARY KEY DEFAULT ('conv_' || uuid_generate_v4()::text),
    property_id TEXT REFERENCES properties(id) ON DELETE CASCADE,
    property_title TEXT NOT NULL DEFAULT '',
    tenant_id TEXT NOT NULL,
    tenant_name TEXT NOT NULL,
    poster_id TEXT NOT NULL,
    poster_name TEXT NOT NULL,
    last_message TEXT DEFAULT '',
    last_message_time BIGINT DEFAULT (extract(epoch from now()) * 1000)::bigint
);

CREATE TABLE IF NOT EXISTS messages (
    id TEXT PRIMARY KEY DEFAULT ('msg_' || uuid_generate_v4()::text),
    conversation_id TEXT NOT NULL,
    sender_id TEXT NOT NULL,
    sender_name TEXT NOT NULL,
    message_text TEXT NOT NULL,
    timestamp BIGINT DEFAULT (extract(epoch from now()) * 1000)::bigint,
    is_read BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_messages_conv ON messages(conversation_id);

-- 7. Users Table
CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT DEFAULT '',
    role TEXT NOT NULL DEFAULT 'TENANT',
    relationship_to_property TEXT DEFAULT 'Property owner',
    is_authorized_by_owner BOOLEAN DEFAULT TRUE,
    preferred_language TEXT DEFAULT 'English',
    verification_level INTEGER DEFAULT 1,
    phone_verified BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT TRUE,
    identity_submitted BOOLEAN DEFAULT FALSE,
    professional_verified BOOLEAN DEFAULT FALSE,
    prc_number TEXT DEFAULT '',
    created_at BIGINT DEFAULT (extract(epoch from now()) * 1000)::bigint
);

-- 8. Verification Documents Table
CREATE TABLE IF NOT EXISTS verification_documents (
    id TEXT PRIMARY KEY DEFAULT ('doc_' || uuid_generate_v4()::text),
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    property_id TEXT REFERENCES properties(id) ON DELETE SET NULL,
    document_type TEXT NOT NULL, -- GOVERNMENT_ID, PRC_ID, AUTHORIZATION_LETTER, TITLE_TAX_DEC, UTILITY_BILL
    status TEXT NOT NULL DEFAULT 'SUBMITTED', -- SUBMITTED, UNDER_REVIEW, VERIFIED, REJECTED
    file_name TEXT NOT NULL,
    is_private_encrypted BOOLEAN DEFAULT TRUE,
    reviewer_notes TEXT DEFAULT '',
    submitted_at BIGINT DEFAULT (extract(epoch from now()) * 1000)::bigint,
    reviewed_at BIGINT
);

CREATE INDEX IF NOT EXISTS idx_verification_user ON verification_documents(user_id);
CREATE INDEX IF NOT EXISTS idx_verification_status ON verification_documents(status);

-- 9. Row Level Security (RLS) Policies
ALTER TABLE properties ENABLE ROW LEVEL SECURITY;
ALTER TABLE property_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE viewing_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE verification_documents ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public Read Active Properties" ON properties
    FOR SELECT USING (status = 'ACTIVE' OR auth.role() = 'authenticated');

CREATE POLICY "Public Read Property Images" ON property_images
    FOR SELECT USING (true);

CREATE POLICY "Viewings Read/Write Policy" ON viewing_requests
    FOR ALL USING (true);

CREATE POLICY "Conversations Read/Write Policy" ON conversations
    FOR ALL USING (true);

CREATE POLICY "Messages Read/Write Policy" ON messages
    FOR ALL USING (true);

CREATE POLICY "Users Read/Write Policy" ON users
    FOR ALL USING (true);

CREATE POLICY "Verification Docs Policy" ON verification_documents
    FOR ALL USING (true);

-- =========================================================================
-- 10. Automated Server-Side Gmail Notification Edge Function Hook Setup
-- =========================================================================
-- In your Supabase Dashboard:
-- 1. Deploy the Edge Function: `supabase functions deploy notify-gmail`
-- 2. Go to Database -> Webhooks -> "Create a new webhook":
--    - Name: "Notify Gmail on Viewing Request"
--      Table: `viewing_requests`
--      Events: INSERT, UPDATE
--      Type: Supabase Edge Function -> `notify-gmail`
--    - Name: "Notify Gmail on New Message"
--      Table: `messages`
--      Events: INSERT
--      Type: Supabase Edge Function -> `notify-gmail`
--    - Name: "Notify Gmail on Verification Update"
--      Table: `verification_documents`
--      Events: UPDATE
--      Type: Supabase Edge Function -> `notify-gmail`
-- =========================================================================
