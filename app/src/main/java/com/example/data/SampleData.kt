package com.example.data

import com.example.model.BoardingHouse
import com.example.model.GenderPolicy
import com.example.model.LandlordInfo
import com.example.model.RoomType

object SampleData {
    val initialListings = listOf(
        BoardingHouse(
            id = "bh-1",
            title = "Pinecrest Scholar Boarding House",
            description = "Quiet, study-oriented boarding house located just 3 blocks from the University North Gate. Features personal study lamps, high-speed fiber internet, and warm communal dining area.",
            roomType = RoomType.SINGLE,
            monthlyRent = 180,
            deposit = 180,
            address = "42 University Ave, North District",
            city = "Metro Academic Hub",
            proximityText = "250m to University Library",
            rating = 4.8f,
            reviewCount = 38,
            genderPolicy = GenderPolicy.ALL,
            availableRooms = 2,
            totalRooms = 12,
            curfewText = "10:30 PM Gate Close",
            utilitiesIncluded = listOf("Fiber Wi-Fi", "Water", "Trash Collection"),
            amenities = listOf("Study Desk", "Ceiling Fan", "Wardrobe", "CCTV Security", "Shared Kitchen", "Filtered Water Dispenser"),
            houseRules = listOf("Quiet hours after 10 PM", "No overnight visitors without notice", "Cooking allowed in kitchen area", "No smoking indoors"),
            landlord = LandlordInfo(
                name = "Mrs. Helen Santos",
                phone = "+1 (555) 234-5678",
                isVerified = true,
                responseTime = "Within 15 mins",
                memberSince = "2021"
            )
        ),
        BoardingHouse(
            id = "bh-2",
            title = "Aurora Ladies Residence & Bedspace",
            description = "Secure female-only boarding haven with 24/7 biometric access, in-house laundry service, and spacious air-conditioned rooms. Perfect for working ladies and medical students.",
            roomType = RoomType.BEDSPACE,
            monthlyRent = 95,
            deposit = 95,
            address = "18 Aurora Boulevard, Medical Enclave",
            city = "Metro Academic Hub",
            proximityText = "5 mins walk to Medical Center",
            rating = 4.9f,
            reviewCount = 52,
            genderPolicy = GenderPolicy.FEMALE_ONLY,
            availableRooms = 4,
            totalRooms = 20,
            curfewText = "11:00 PM Curfew",
            utilitiesIncluded = listOf("Electricity", "Water", "Wi-Fi", "Daily Cleaning"),
            amenities = listOf("Air Conditioning", "Individual Lockers", "Bunk with Privacy Curtain", "Microwave", "Laundry Room", "Biometric Lock"),
            houseRules = listOf("Strictly female guests only", "Keep footwear on outdoor rack", "No loud music after 9 PM"),
            landlord = LandlordInfo(
                name = "Auntie Clara Mendoza",
                phone = "+1 (555) 987-6543",
                isVerified = true,
                responseTime = "Instant response",
                memberSince = "2019"
            )
        ),
        BoardingHouse(
            id = "bh-3",
            title = "Summit Studio Pad & Loft",
            description = "Modern self-contained studio unit with private en-suite bathroom, kitchenette with induction cooker, and private balcony. Ideal for graduate students and young professionals.",
            roomType = RoomType.STUDIO,
            monthlyRent = 280,
            deposit = 300,
            address = "77 High St, Innovation Quarter",
            city = "Tech District",
            proximityText = "400m to Metro Train Station",
            rating = 4.7f,
            reviewCount = 24,
            genderPolicy = GenderPolicy.ALL,
            availableRooms = 1,
            totalRooms = 6,
            curfewText = "No Curfew (24/7 Smart Key)",
            utilitiesIncluded = listOf("Wi-Fi 300 Mbps"),
            amenities = listOf("En-suite Bathroom", "Mini Refrigerator", "Induction Cooktop", "Balcony", "Air Conditioning", "Intercom"),
            houseRules = listOf("Respect neighboring tenants", "Pets allowed with deposit", "Garbage segregation strictly enforced"),
            landlord = LandlordInfo(
                name = "Marcus Vance",
                phone = "+1 (555) 456-7890",
                isVerified = true,
                responseTime = "Within 2 hours",
                memberSince = "2022"
            )
        ),
        BoardingHouse(
            id = "bh-4",
            title = "Brotherhood Dorm & Boarding",
            description = "Budget-friendly male dormitory featuring gym access, motorcycle parking, and cafeteria downstairs serving home-cooked meals 3 times a day at discount rates.",
            roomType = RoomType.BOARDING,
            monthlyRent = 80,
            deposit = 80,
            address = "104 Varsity Row, Westside",
            city = "Metro Academic Hub",
            proximityText = "100m to Engineering Campus",
            rating = 4.5f,
            reviewCount = 41,
            genderPolicy = GenderPolicy.MALE_ONLY,
            availableRooms = 3,
            totalRooms = 16,
            curfewText = "11:30 PM Gate Lock",
            utilitiesIncluded = listOf("Water", "Fiber Wi-Fi", "Electricity (Fair Use)"),
            amenities = listOf("Covered Bike/Motorcycle Parking", "Mini Gym Area", "Hot & Cold Shower", "Common Study Lounge", "Canteen On-site"),
            houseRules = listOf("Clean common sink after use", "Guests must register at guard post", "No alcohol on premises"),
            landlord = LandlordInfo(
                name = "Kuya Ronald Tan",
                phone = "+1 (555) 321-7654",
                isVerified = true,
                responseTime = "Within 30 mins",
                memberSince = "2020"
            )
        ),
        BoardingHouse(
            id = "bh-5",
            title = "Green Oasis Co-Living & Rooms",
            description = "Eco-friendly, sunlit boarding house surrounded by garden greens. Features rooftop study deck, solar water heater, and spacious private rooms with natural ventilation.",
            roomType = RoomType.SINGLE,
            monthlyRent = 160,
            deposit = 160,
            address = "12 Gardenia Walk, East Valley",
            city = "Greenfield",
            proximityText = "600m to City College",
            rating = 4.9f,
            reviewCount = 19,
            genderPolicy = GenderPolicy.ALL,
            availableRooms = 2,
            totalRooms = 8,
            curfewText = "No Curfew (Keycard Access)",
            utilitiesIncluded = listOf("Solar Hot Water", "Wi-Fi", "Water"),
            amenities = listOf("Rooftop Deck", "Garden Hammocks", "Bicycle Racks", "Shared Kitchen", "Full Size Mirror", "Ceiling Fan"),
            houseRules = listOf("Composting guidelines apply", "Smoking strictly prohibited", "Quiet hours 11 PM - 7 AM"),
            landlord = LandlordInfo(
                name = "Dr. Teresa Lin",
                phone = "+1 (555) 789-0123",
                isVerified = true,
                responseTime = "Within 1 hour",
                memberSince = "2022"
            )
        ),
        BoardingHouse(
            id = "bh-6",
            title = "Central Point Deluxe Bedspace",
            description = "Air-conditioned 4-person sharing bedspace directly adjacent to downtown bus terminal. Equipped with wooden double-deckers, high privacy curtains, and individual power outlets.",
            roomType = RoomType.BEDSPACE,
            monthlyRent = 75,
            deposit = 50,
            address = "55 Terminal Lane, Downtown Central",
            city = "Downtown Central",
            proximityText = "50m to Bus & Jeepney Terminal",
            rating = 4.4f,
            reviewCount = 63,
            genderPolicy = GenderPolicy.ALL,
            availableRooms = 5,
            totalRooms = 24,
            curfewText = "24/7 Guard on Duty",
            utilitiesIncluded = listOf("Electricity", "Water", "Wi-Fi"),
            amenities = listOf("Air Conditioning", "Individual Power & Light", "Under-bed Storage", "24/7 Security Guard", "Purified Water"),
            houseRules = listOf("Label stored food items in shared fridge", "Lights out in sleeping area by 11 PM"),
            landlord = LandlordInfo(
                name = "Benjamin Cruz",
                phone = "+1 (555) 654-9870",
                isVerified = true,
                responseTime = "Within 10 mins",
                memberSince = "2018"
            )
        )
    )
}
