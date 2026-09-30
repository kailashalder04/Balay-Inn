package com.example.data.seed

import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.PropertyImageEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.SavedPropertyEntity
import com.example.data.local.entity.SavedSearchEntity
import com.example.data.local.entity.TenantRequirementEntity
import com.example.data.local.entity.VerificationDocumentEntity
import com.example.data.local.entity.ViewingRequestEntity

object PropertyDataSeed {

    val sampleProperties = listOf(
        // 1. Panglao, Bohol - Tawala
        PropertyEntity(
            id = "prop_boh_01",
            title = "Modern Tropical Studio near Alona Beach",
            description = "Quiet, fully furnished garden studio apartment situated 5 minutes walk from Alona Beach. Features high-speed fiber internet, inverter air conditioner, private patio with lush tropical plants, and dedicated scooter parking. Safe residential compound with 24/7 solar security lighting.",
            propertyType = "Studio",
            monthlyRent = 18000.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_bohol",
            cityMunicipalityId = "bohol_panglao",
            barangayId = "bgy_pan_9", // Tawala
            neighborhood = "Alona Beach Area",
            nearbyLandmark = "Behind Alona Tropical Bar",
            streetAddressPrivate = "Lot 42, Hontanosas Road, Tawala",
            locationPrivacy = "APPROXIMATE",
            latitude = 9.5492,
            longitude = 123.7745,
            bedrooms = 0,
            bathrooms = 1,
            floorAreaSqm = 32.0,
            furnishedStatus = "Furnished",
            parkingAvailable = true,
            petPolicy = "Small Pets Only",
            availabilityDate = "Immediately",
            leaseDurationMonths = 6,
            advanceRentMonths = 1,
            securityDepositMonths = 2,
            associationDues = 1200.0,
            utilitiesIncluded = false,
            internetIncluded = true,
            maintenanceFees = 500.0,
            parkingFees = 0.0,
            amenitiesJson = "[\"Air Conditioning\", \"Fiber Internet\", \"Balcony / Patio\", \"Hot Water Heater\", \"Kitchenette\", \"Refrigerator\", \"Gated Compound\"]",
            contactPreferences = "In-App Messaging Only",
            status = "ACTIVE",
            ownerId = "usr_owner_1",
            posterId = "usr_rep_family",
            posterName = "Maria Santos",
            posterRole = "Family member (Authorized)",
            isPosterAuthorized = true,
            verificationLevel = 3,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true,
            viewsCount = 142,
            savesCount = 19
        ),

        // 2. Cebu City - Apas / IT Park
        PropertyEntity(
            id = "prop_ceb_01",
            title = "High-Floor Executive 1BR in Cebu IT Park",
            description = "Prime urban condo in Avida Towers Riala, Cebu IT Park. Steps away from multinational BPO offices, Sugbo Mercado, and Ayala Malls Central Bloc. Unobstructed mountain and city views, fitted with ergonomic workspace, high-speed fiber internet, and sleek kitchen.",
            propertyType = "Condominium",
            monthlyRent = 25000.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_cebu",
            cityMunicipalityId = "cebu_cebu_city",
            barangayId = "bgy_ceb_c_5", // Apas
            neighborhood = "Cebu IT Park",
            nearbyLandmark = "Across Sugbo Mercado",
            streetAddressPrivate = "Tower 3, Unit 2412, Jose Maria del Mar St.",
            locationPrivacy = "APPROXIMATE",
            latitude = 10.3298,
            longitude = 123.9061,
            bedrooms = 1,
            bathrooms = 1,
            floorAreaSqm = 40.0,
            furnishedStatus = "Furnished",
            parkingAvailable = true,
            petPolicy = "No Pets",
            availabilityDate = "Nov 1, 2026",
            leaseDurationMonths = 12,
            advanceRentMonths = 1,
            securityDepositMonths = 2,
            associationDues = 2800.0,
            utilitiesIncluded = false,
            internetIncluded = true,
            maintenanceFees = 0.0,
            parkingFees = 4000.0,
            amenitiesJson = "[\"Swimming Pool\", \"Fitness Gym\", \"24/7 Security\", \"Elevator\", \"Fiber WiFi\", \"Air Conditioning\", \"Intercom\", \"Microwave\", \"Washing Machine\"]",
            contactPreferences = "In-App Messaging Only",
            status = "ACTIVE",
            ownerId = "usr_owner_1",
            posterId = "usr_broker_marco",
            posterName = "Marco Villanueva, REB",
            posterRole = "Licensed broker (PRC #0028192)",
            isPosterAuthorized = true,
            verificationLevel = 5,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true,
            professionalVerified = true,
            viewsCount = 288,
            savesCount = 42
        ),

        // 3. Dauis, Bohol - Songculan
        PropertyEntity(
            id = "prop_boh_02",
            title = "Peaceful 2-Bedroom Coastal Townhouse in Dauis",
            description = "Spacious two-storey townhouse with sea breeze and panoramic view of the Tagbilaran strait. Only 8 minutes drive across the bridge to Tagbilaran downtown and 15 minutes to Panglao airport. Gated sub-division with quiet family-friendly surroundings.",
            propertyType = "Townhouse",
            monthlyRent = 22000.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_bohol",
            cityMunicipalityId = "bohol_dauis",
            barangayId = "bgy_dau_7", // Songculan
            neighborhood = "Songculan Heights",
            nearbyLandmark = "Near Songculan Barangay Hall",
            streetAddressPrivate = "Block 7 Lot 14, Villa Marcela, Songculan",
            locationPrivacy = "APPROXIMATE",
            latitude = 9.6382,
            longitude = 123.8689,
            bedrooms = 2,
            bathrooms = 2,
            floorAreaSqm = 78.0,
            furnishedStatus = "Semi-Furnished",
            parkingAvailable = true,
            petPolicy = "Pets Allowed",
            availabilityDate = "Immediately",
            leaseDurationMonths = 12,
            advanceRentMonths = 1,
            securityDepositMonths = 2,
            associationDues = 800.0,
            utilitiesIncluded = false,
            internetIncluded = false,
            maintenanceFees = 0.0,
            parkingFees = 0.0,
            amenitiesJson = "[\"Covered Carport\", \"Balcony\", \"Built-in Closets\", \"Pet Friendly\", \"Water Tank / Cistern\", \"Gated Community\"]",
            contactPreferences = "In-App Messaging Only",
            status = "ACTIVE",
            ownerId = "usr_owner_1",
            posterId = "usr_rep_caretaker",
            posterName = "Pedro Flores",
            posterRole = "Caretaker (Authorized)",
            isPosterAuthorized = true,
            verificationLevel = 2,
            phoneVerified = true,
            emailVerified = true,
            viewsCount = 95,
            savesCount = 11
        ),

        // 4. Tagbilaran City, Bohol - Cogon
        PropertyEntity(
            id = "prop_boh_03",
            title = "Modern 1BR Apartment near BQ Mall & Holy Name Univ",
            description = "Central Tagbilaran location ideal for young professionals or medical/university staff. Fully secured boutique apartment building with CCTV, backup generator, and fiber connection. Walking distance to grocery and public transport.",
            propertyType = "Apartment",
            monthlyRent = 13500.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_bohol",
            cityMunicipalityId = "bohol_tagbilaran",
            barangayId = "bgy_tag_6", // Cogon
            neighborhood = "Cogon District",
            nearbyLandmark = "Near Holy Name University Medical Center",
            streetAddressPrivate = "128 J.A. Clarin St, Cogon",
            locationPrivacy = "APPROXIMATE",
            latitude = 9.6514,
            longitude = 123.8562,
            bedrooms = 1,
            bathrooms = 1,
            floorAreaSqm = 36.0,
            furnishedStatus = "Furnished",
            parkingAvailable = true,
            petPolicy = "No Pets",
            availabilityDate = "Immediately",
            leaseDurationMonths = 6,
            advanceRentMonths = 1,
            securityDepositMonths = 1,
            associationDues = 500.0,
            utilitiesIncluded = false,
            internetIncluded = true,
            maintenanceFees = 300.0,
            parkingFees = 1000.0,
            amenitiesJson = "[\"Air Conditioning\", \"Fiber Internet\", \"CCTV Security\", \"Backup Generator\", \"Refrigerator\", \"Induction Cooktop\"]",
            contactPreferences = "In-App Messaging Only",
            status = "ACTIVE",
            ownerId = "usr_owner_1",
            posterId = "usr_owner_1",
            posterName = "Donato Yap",
            posterRole = "Property owner",
            isPosterAuthorized = true,
            verificationLevel = 4,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true,
            viewsCount = 210,
            savesCount = 28
        ),

        // 5. Cebu City - Lahug
        PropertyEntity(
            id = "prop_ceb_02",
            title = "Spacious 2BR Garden Condo in Lahug",
            description = "Located near JY Square Mall and Gorordo Avenue. Ground floor unit with direct walkout to a lush landscaped courtyard. Master bedroom with ensuite bath, full kitchen, and utility area. Peaceful community minutes from uptown Cebu.",
            propertyType = "Condominium",
            monthlyRent = 32000.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_cebu",
            cityMunicipalityId = "cebu_cebu_city",
            barangayId = "bgy_ceb_c_0", // Lahug
            neighborhood = "Salinas Drive / Lahug",
            nearbyLandmark = "Beside JY Square Mall",
            streetAddressPrivate = "Salinas Drive, Lahug, Cebu City",
            locationPrivacy = "APPROXIMATE",
            latitude = 10.3341,
            longitude = 123.8978,
            bedrooms = 2,
            bathrooms = 2,
            floorAreaSqm = 65.0,
            furnishedStatus = "Furnished",
            parkingAvailable = true,
            petPolicy = "Small Pets Only",
            availabilityDate = "Immediately",
            leaseDurationMonths = 12,
            advanceRentMonths = 2,
            securityDepositMonths = 2,
            associationDues = 3500.0,
            utilitiesIncluded = false,
            internetIncluded = false,
            maintenanceFees = 0.0,
            parkingFees = 3500.0,
            amenitiesJson = "[\"Swimming Pool\", \"Clubhouse\", \"Gym\", \"Garden\", \"24/7 Security\", \"Water Heater\", \"Washing Machine\", \"Air Conditioning\"]",
            contactPreferences = "In-App Messaging Only",
            status = "ACTIVE",
            ownerId = "usr_owner_1",
            posterId = "usr_broker_marco",
            posterName = "Marco Villanueva, REB",
            posterRole = "Licensed broker (PRC #0028192)",
            isPosterAuthorized = true,
            verificationLevel = 5,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true,
            professionalVerified = true,
            viewsCount = 184,
            savesCount = 31
        ),

        // 6. Panglao, Bohol - Doljo
        PropertyEntity(
            id = "prop_boh_04",
            title = "3-Bedroom Native Contemporary Villa with Private Pool",
            description = "Luxurious island living with high bamboo ceilings, mahogany fixtures, and a 20sqm private dip pool. Situated in calm North-Western Panglao near Doljo beach and sunset point. Solar powered backup and organic vegetable garden.",
            propertyType = "House",
            monthlyRent = 48000.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_bohol",
            cityMunicipalityId = "bohol_panglao",
            barangayId = "bgy_pan_4", // Doljo
            neighborhood = "Doljo Beach Road",
            nearbyLandmark = "Near Bellevue Resort Entrance",
            streetAddressPrivate = "Doljo Point, Panglao",
            locationPrivacy = "APPROXIMATE",
            latitude = 9.5881,
            longitude = 123.7294,
            bedrooms = 3,
            bathrooms = 3,
            floorAreaSqm = 145.0,
            furnishedStatus = "Furnished",
            parkingAvailable = true,
            petPolicy = "Pets Allowed",
            availabilityDate = "Immediately",
            leaseDurationMonths = 12,
            advanceRentMonths = 2,
            securityDepositMonths = 2,
            associationDues = 0.0,
            utilitiesIncluded = false,
            internetIncluded = true,
            maintenanceFees = 2000.0,
            parkingFees = 0.0,
            amenitiesJson = "[\"Private Pool\", \"Solar Power\", \"Lush Garden\", \"Pet Friendly\", \"Spacious Kitchen\", \"High-Speed WiFi\", \"Gated Perimeter\", \"Outdoor Shower\"]",
            contactPreferences = "In-App Messaging Only",
            status = "ACTIVE",
            ownerId = "usr_owner_1",
            posterId = "usr_rep_family",
            posterName = "Maria Santos",
            posterRole = "Family member (Authorized)",
            isPosterAuthorized = true,
            verificationLevel = 3,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true,
            viewsCount = 330,
            savesCount = 55
        ),

        // 7. Cebu City - Guadalupe
        PropertyEntity(
            id = "prop_ceb_03",
            title = "Affordable Budget Room for Female Students & Call Center Staff",
            description = "Clean, safe, gated ladies boarding room with individual study desk, wardrobe, and shared bathroom and pantry. 1 ride to Fuente Osmeña, Velez College, and Chong Hua Hospital.",
            propertyType = "Room",
            monthlyRent = 4500.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_cebu",
            cityMunicipalityId = "cebu_cebu_city",
            barangayId = "bgy_ceb_c_3", // Guadalupe
            neighborhood = "Guadalupe Commercial Area",
            nearbyLandmark = "Near Guadalupe Church",
            streetAddressPrivate = "V. Rama Avenue, Guadalupe",
            locationPrivacy = "APPROXIMATE",
            latitude = 10.3168,
            longitude = 123.8821,
            bedrooms = 1,
            bathrooms = 1,
            floorAreaSqm = 14.0,
            furnishedStatus = "Furnished",
            parkingAvailable = false,
            petPolicy = "No Pets",
            availabilityDate = "Immediately",
            leaseDurationMonths = 6,
            advanceRentMonths = 1,
            securityDepositMonths = 1,
            associationDues = 0.0,
            utilitiesIncluded = true,
            internetIncluded = true,
            maintenanceFees = 0.0,
            parkingFees = 0.0,
            amenitiesJson = "[\"WiFi Included\", \"Water & Electricity Included\", \"CCTV\", \"Study Desk\", \"Gated Access\"]",
            contactPreferences = "In-App Messaging Only",
            status = "ACTIVE",
            ownerId = "usr_owner_1",
            posterId = "usr_rep_caretaker",
            posterName = "Pedro Flores",
            posterRole = "Caretaker (Authorized)",
            isPosterAuthorized = true,
            verificationLevel = 2,
            phoneVerified = true,
            emailVerified = true,
            viewsCount = 112,
            savesCount = 16
        ),

        // 8. Anda, Bohol - Poblacion / White Beach
        PropertyEntity(
            id = "prop_boh_05",
            title = "Seaside Beachfront Bungalow in Quiet Eastern Bohol",
            description = "Tranquil 2-bedroom home directly facing the crystal clear Anda sea. Ideal for remote workers or retirees seeking tranquility far from city noise. Huge veranda, solar water heater, and reliable Starlink internet.",
            propertyType = "House",
            monthlyRent = 28000.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_bohol",
            cityMunicipalityId = "bohol_anda",
            barangayId = "bgy_and_0", // Poblacion
            neighborhood = "Anda White Beach",
            nearbyLandmark = "Beside Anda Quo Vadis Resort",
            streetAddressPrivate = "Coastal Highway, Anda",
            locationPrivacy = "APPROXIMATE",
            latitude = 9.7423,
            longitude = 124.5781,
            bedrooms = 2,
            bathrooms = 1,
            floorAreaSqm = 82.0,
            furnishedStatus = "Furnished",
            parkingAvailable = true,
            petPolicy = "Pets Allowed",
            availabilityDate = "Immediately",
            leaseDurationMonths = 6,
            advanceRentMonths = 1,
            securityDepositMonths = 2,
            associationDues = 0.0,
            utilitiesIncluded = false,
            internetIncluded = true,
            maintenanceFees = 1000.0,
            parkingFees = 0.0,
            amenitiesJson = "[\"Beachfront\", \"Starlink WiFi\", \"Veranda\", \"Solar Water Heater\", \"Pet Friendly\", \"Spacious Yard\"]",
            contactPreferences = "In-App Messaging Only",
            status = "ACTIVE",
            ownerId = "usr_owner_1",
            posterId = "usr_owner_1",
            posterName = "Donato Yap",
            posterRole = "Property owner",
            isPosterAuthorized = true,
            verificationLevel = 4,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true,
            viewsCount = 205,
            savesCount = 37
        )
    )

    val sampleImages = listOf(
        // prop_boh_01 (Panglao Modern Tropical Studio near Alona Beach - 5 photos)
        PropertyImageEntity("img_boh_01_1", "prop_boh_01", "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1200&auto=format&fit=crop&q=80", true, "Bright living studio space", 0),
        PropertyImageEntity("img_boh_01_2", "prop_boh_01", "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=1200&auto=format&fit=crop&q=80", false, "Cozy bed with patio garden view", 1),
        PropertyImageEntity("img_boh_01_3", "prop_boh_01", "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=1200&auto=format&fit=crop&q=80", false, "Clean modern bathroom with rainfall shower", 2),
        PropertyImageEntity("img_boh_01_4", "prop_boh_01", "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=1200&auto=format&fit=crop&q=80", false, "Fully equipped kitchen nook", 3),
        PropertyImageEntity("img_boh_01_5", "prop_boh_01", "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=1200&auto=format&fit=crop&q=80", false, "Tropical garden compound & scooter parking", 4),

        // prop_ceb_01 (IT Park Condo Executive 1BR - 5 photos)
        PropertyImageEntity("img_ceb_01_1", "prop_ceb_01", "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=1200&auto=format&fit=crop&q=80", true, "Modern living area overlooking IT Park", 0),
        PropertyImageEntity("img_ceb_01_2", "prop_ceb_01", "https://images.unsplash.com/photo-1505691938895-1758d7feb511?w=1200&auto=format&fit=crop&q=80", false, "Contemporary bedroom suite with skyline view", 1),
        PropertyImageEntity("img_ceb_01_3", "prop_ceb_01", "https://images.unsplash.com/photo-1556912173-3bb406ef7e77?w=1200&auto=format&fit=crop&q=80", false, "Equipped kitchen corner & quartz counter", 2),
        PropertyImageEntity("img_ceb_01_4", "prop_ceb_01", "https://images.unsplash.com/photo-1584622781564-1d987f7333c1?w=1200&auto=format&fit=crop&q=80", false, "Tiled bathroom with glass enclosure", 3),
        PropertyImageEntity("img_ceb_01_5", "prop_ceb_01", "https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?w=1200&auto=format&fit=crop&q=80", false, "Condo lap pool & fitness center", 4),

        // prop_boh_02 (Dauis Townhouse 3BR - 5 photos)
        PropertyImageEntity("img_boh_02_1", "prop_boh_02", "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=1200&auto=format&fit=crop&q=80", true, "Two-storey coastal facade & carport", 0),
        PropertyImageEntity("img_boh_02_2", "prop_boh_02", "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=1200&auto=format&fit=crop&q=80", false, "Spacious dining and living room", 1),
        PropertyImageEntity("img_boh_02_3", "prop_boh_02", "https://images.unsplash.com/photo-1616594039964-ae9021a400a0?w=1200&auto=format&fit=crop&q=80", false, "Master bedroom with balcony view", 2),
        PropertyImageEntity("img_boh_02_4", "prop_boh_02", "https://images.unsplash.com/photo-1507089947368-19c1da9775ae?w=1200&auto=format&fit=crop&q=80", false, "Second guest bedroom", 3),
        PropertyImageEntity("img_boh_02_5", "prop_boh_02", "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=1200&auto=format&fit=crop&q=80", false, "Wooden cabinets and cooking range", 4),

        // prop_boh_03 (Tagbilaran Cogon 1BR - 4 photos)
        PropertyImageEntity("img_boh_03_1", "prop_boh_03", "https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=1200&auto=format&fit=crop&q=80", true, "Furnished one bedroom unit", 0),
        PropertyImageEntity("img_boh_03_2", "prop_boh_03", "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=1200&auto=format&fit=crop&q=80", false, "Cozy bedroom with wooden flooring", 1),
        PropertyImageEntity("img_boh_03_3", "prop_boh_03", "https://images.unsplash.com/photo-1507652313519-d4e9174996dd?w=1200&auto=format&fit=crop&q=80", false, "Functional kitchen & dining table", 2),
        PropertyImageEntity("img_boh_03_4", "prop_boh_03", "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=1200&auto=format&fit=crop&q=80", false, "Ensuite bathroom with water heater", 3),

        // prop_ceb_02 (Lahug Garden 2BR Condo - 5 photos)
        PropertyImageEntity("img_ceb_02_1", "prop_ceb_02", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=1200&auto=format&fit=crop&q=80", true, "Garden view living room", 0),
        PropertyImageEntity("img_ceb_02_2", "prop_ceb_02", "https://images.unsplash.com/photo-1600565193348-f74bd3c7ccdf?w=1200&auto=format&fit=crop&q=80", false, "Master bedroom with patio access", 1),
        PropertyImageEntity("img_ceb_02_3", "prop_ceb_02", "https://images.unsplash.com/photo-1600585154526-990dced4db0d?w=1200&auto=format&fit=crop&q=80", false, "Modern chef's kitchen with island", 2),
        PropertyImageEntity("img_ceb_02_4", "prop_ceb_02", "https://images.unsplash.com/photo-1600573472550-8090b5e0745e?w=1200&auto=format&fit=crop&q=80", false, "Second bedroom / executive workspace", 3),
        PropertyImageEntity("img_ceb_02_5", "prop_ceb_02", "https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?w=1200&auto=format&fit=crop&q=80", false, "Courtyard pool and clubhouse", 4),

        // prop_boh_04 (Doljo Villa with Pool - 5 photos)
        PropertyImageEntity("img_boh_04_1", "prop_boh_04", "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=1200&auto=format&fit=crop&q=80", true, "Private pool villa exterior", 0),
        PropertyImageEntity("img_boh_04_2", "prop_boh_04", "https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=1200&auto=format&fit=crop&q=80", false, "Outdoor tropical pavilion and deck", 1),
        PropertyImageEntity("img_boh_04_3", "prop_boh_04", "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=1200&auto=format&fit=crop&q=80", false, "Native bamboo cathedral living room", 2),
        PropertyImageEntity("img_boh_04_4", "prop_boh_04", "https://images.unsplash.com/photo-1600585152220-90363fe7e115?w=1200&auto=format&fit=crop&q=80", false, "Master bedroom with outdoor stone bath", 3),
        PropertyImageEntity("img_boh_04_5", "prop_boh_04", "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b?w=1200&auto=format&fit=crop&q=80", false, "Spacious open concept kitchen", 4),

        // prop_ceb_03 (Guadalupe Room - 4 photos)
        PropertyImageEntity("img_ceb_03_1", "prop_ceb_03", "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=1200&auto=format&fit=crop&q=80", true, "Cozy single room with study desk", 0),
        PropertyImageEntity("img_ceb_03_2", "prop_ceb_03", "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=1200&auto=format&fit=crop&q=80", false, "Sunlit study table and bookshelf", 1),
        PropertyImageEntity("img_ceb_03_3", "prop_ceb_03", "https://images.unsplash.com/photo-1556912173-3bb406ef7e77?w=1200&auto=format&fit=crop&q=80", false, "Shared kitchen pantry area", 2),
        PropertyImageEntity("img_ceb_03_4", "prop_ceb_03", "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=1200&auto=format&fit=crop&q=80", false, "Clean communal bathroom", 3),

        // prop_boh_05 (Anda Beachfront Bungalow - 5 photos)
        PropertyImageEntity("img_boh_05_1", "prop_boh_05", "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=1200&auto=format&fit=crop&q=80", true, "Tropical beachfront cottage and white sand", 0),
        PropertyImageEntity("img_boh_05_2", "prop_boh_05", "https://images.unsplash.com/photo-1510798831971-661eb04b3739?w=1200&auto=format&fit=crop&q=80", false, "Large wooden oceanfront veranda", 1),
        PropertyImageEntity("img_boh_05_3", "prop_boh_05", "https://images.unsplash.com/photo-1540518614846-7ede433c4ef4?w=1200&auto=format&fit=crop&q=80", false, "Breezy bedroom with ocean vista", 2),
        PropertyImageEntity("img_boh_05_4", "prop_boh_05", "https://images.unsplash.com/photo-1507089947368-19c1da9775ae?w=1200&auto=format&fit=crop&q=80", false, "Second guest bedroom", 3),
        PropertyImageEntity("img_boh_05_5", "prop_boh_05", "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=1200&auto=format&fit=crop&q=80", false, "Rustic dining room and kitchen", 4)
    )

    val sampleViewingRequests = listOf(
        ViewingRequestEntity(
            id = "view_01",
            propertyId = "prop_boh_01",
            tenantId = "usr_tenant_1",
            posterId = "usr_rep_family",
            preferredDate = "Tomorrow, 2:00 PM",
            preferredTime = "2:00 PM - 3:00 PM",
            numberOfVisitors = 2,
            tenantMessage = "Hello Ma'am Maria, my partner and I would love to check the internet speed and water pressure for our remote work.",
            posterResponse = "Confirmed! I will meet you at the compound gate at 2:00 PM.",
            status = "CONFIRMED"
        ),
        ViewingRequestEntity(
            id = "view_02",
            propertyId = "prop_ceb_01",
            tenantId = "usr_tenant_2",
            posterId = "usr_broker_marco",
            preferredDate = "Saturday, 10:00 AM",
            preferredTime = "10:00 AM - 11:00 AM",
            numberOfVisitors = 1,
            tenantMessage = "Hi Sir Marco, I work at JP Morgan in IT park. Can we schedule a viewing this Saturday morning?",
            posterResponse = "",
            status = "REQUESTED"
        )
    )

    val sampleConversations = listOf(
        ConversationEntity(
            id = "conv_01",
            propertyId = "prop_boh_01",
            propertyTitle = "Modern Tropical Studio near Alona Beach",
            tenantId = "usr_tenant_1",
            tenantName = "Joshua Alcantara",
            posterId = "usr_rep_family",
            posterName = "Maria Santos (Representative)",
            lastMessageText = "Thank you! See you tomorrow at 2:00 PM.",
            lastMessageTime = System.currentTimeMillis() - 3600000,
            unreadForTenant = 0,
            unreadForPoster = 0
        )
    )

    val sampleMessages = listOf(
        MessageEntity(
            id = "msg_01",
            conversationId = "conv_01",
            senderId = "usr_tenant_1",
            senderName = "Joshua Alcantara",
            messageText = "Hello po Ma'am Maria, is this unit still available for 6 months lease starting next week?",
            timestamp = System.currentTimeMillis() - 7200000,
            isRead = true
        ),
        MessageEntity(
            id = "msg_02",
            conversationId = "conv_01",
            senderId = "usr_rep_family",
            senderName = "Maria Santos",
            messageText = "Hello Joshua! Yes, the studio is vacant and ready for move-in. I've accepted your viewing request for tomorrow.",
            timestamp = System.currentTimeMillis() - 5400000,
            isRead = true
        ),
        MessageEntity(
            id = "msg_03",
            conversationId = "conv_01",
            senderId = "usr_tenant_1",
            senderName = "Joshua Alcantara",
            messageText = "Thank you! See you tomorrow at 2:00 PM.",
            timestamp = System.currentTimeMillis() - 3600000,
            isRead = true
        )
    )

    val sampleRequirements = listOf(
        TenantRequirementEntity(
            id = "req_01",
            userId = "usr_tenant_1",
            profileName = "Island Remote Work Base in Bohol",
            preferredProvinceId = "prov_bohol",
            preferredCityId = "bohol_panglao",
            preferredLocationName = "Panglao or Dauis, Bohol",
            minBudget = 10000.0,
            maxBudget = 22000.0,
            propertyTypesJson = "[\"Studio\", \"Apartment\", \"Townhouse\"]",
            minBedrooms = 1,
            minBathrooms = 1,
            furnishedRequired = true,
            parkingRequired = true,
            petFriendlyRequired = true,
            moveInDate = "Within 2 weeks",
            notes = "Need stable WiFi and pet friendly for 1 small dog."
        ),
        TenantRequirementEntity(
            id = "req_02",
            userId = "usr_tenant_2",
            profileName = "Cebu IT Park Tech Job Condo",
            preferredProvinceId = "prov_cebu",
            preferredCityId = "cebu_cebu_city",
            preferredLocationName = "Cebu City (IT Park, Lahug)",
            minBudget = 18000.0,
            maxBudget = 28000.0,
            propertyTypesJson = "[\"Condominium\", \"Studio\"]",
            minBedrooms = 1,
            minBathrooms = 1,
            furnishedRequired = true,
            parkingRequired = false,
            petFriendlyRequired = false,
            moveInDate = "Nov 1, 2026",
            notes = "Walking distance to IT Park call centers and BPOs."
        )
    )

    val sampleSavedSearches = listOf(
        SavedSearchEntity(
            id = "saved_s_01",
            userId = "usr_tenant_1",
            searchName = "Bohol Furnished Rentals under ₱25,000",
            queryJson = "{\"provinceId\":\"prov_bohol\",\"maxRent\":25000,\"furnished\":true}",
            frequency = "Daily",
            matchesCount = 4
        )
    )

    val sampleNotifications = listOf(
        NotificationEntity(
            id = "notif_01",
            userId = "usr_tenant_1",
            title = "Viewing Request Confirmed",
            message = "Maria Santos accepted your viewing for 'Modern Tropical Studio near Alona Beach' for tomorrow at 2:00 PM.",
            category = "VIEWING",
            relatedPropertyId = "prop_boh_01",
            isRead = false
        ),
        NotificationEntity(
            id = "notif_02",
            userId = "usr_tenant_1",
            title = "New Matching Property in Panglao",
            message = "A new 3-Bedroom Villa in Doljo, Panglao was just listed that matches your requirement profile.",
            category = "MATCH",
            relatedPropertyId = "prop_boh_04",
            isRead = false
        )
    )

    val sampleReports = listOf(
        ReportEntity(
            id = "rep_01",
            reporterId = "usr_tenant_2",
            targetType = "PROPERTY",
            targetId = "prop_boh_01",
            reason = "MISLEADING_PHOTOS",
            details = "Sample demo audit test check for moderation queue.",
            status = "PENDING"
        )
    )

    val sampleDocuments = listOf(
        VerificationDocumentEntity(
            id = "doc_01",
            userId = "usr_broker_marco",
            documentType = "PRC_ID",
            status = "VERIFIED",
            fileName = "prc_license_card_villanueva.pdf",
            reviewerNotes = "PRC License REB #0028192 verified with PRC official database. Valid until 2028."
        ),
        VerificationDocumentEntity(
            id = "doc_02",
            userId = "usr_rep_family",
            propertyId = "prop_boh_01",
            documentType = "AUTHORIZATION_LETTER",
            status = "VERIFIED",
            fileName = "special_power_of_attorney_santos.pdf",
            reviewerNotes = "Notarized authorization letter from property owner Donato Yap confirmed."
        )
    )
}
