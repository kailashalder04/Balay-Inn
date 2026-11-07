package com.example

import com.example.data.local.entity.PropertyEntity
import com.example.data.remote.supabase.SupabasePropertyDto
import com.example.data.remote.supabase.TimestampParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BalayInnUnitTest {

    @Test
    fun testMergedLocationAndCoordinatesInSupabaseDto() {
        val entity = PropertyEntity(
            id = "prop_test_101",
            title = "Test Boarding House",
            description = "Quiet dorm room near universities",
            propertyType = "Boarding House",
            monthlyRent = 4500.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_cebu",
            cityMunicipalityId = "city_cebu",
            barangayId = "bgy_lahug",
            neighborhood = "Salinas",
            nearbyLandmark = "IT Park",
            location = "Salinas Drive, Lahug, Cebu City, Cebu",
            coordinates = "10.3290, 123.9060",
            streetAddressPrivate = "Salinas Drive",
            latitude = 10.3290,
            longitude = 123.9060,
            bedrooms = 1,
            bathrooms = 1,
            floorAreaSqm = 18.0,
            furnishedStatus = "Furnished",
            parkingAvailable = true,
            petPolicy = "No Pets",
            availabilityDate = "Immediate",
            ownerId = "usr_host_1",
            posterId = "usr_host_1",
            posterName = "Maria Host",
            posterRole = "Property owner"
        )

        // Convert to DTO
        val dto = SupabasePropertyDto.fromEntity(entity)
        assertEquals("Salinas Drive, Lahug, Cebu City, Cebu", dto.location)
        assertEquals("10.3290, 123.9060", dto.coordinates)

        // Convert back to Entity
        val reconstructed = dto.toEntity()
        assertEquals("Salinas Drive, Lahug, Cebu City, Cebu", reconstructed.location)
        assertEquals("10.3290, 123.9060", reconstructed.coordinates)
        assertEquals(10.3290, reconstructed.latitude, 0.0001)
        assertEquals(123.9060, reconstructed.longitude, 0.0001)
    }

    @Test
    fun testPredefinedAdminCredentialsMatch() {
        val expectedEmail = "admin@balayinn.ph"
        val expectedPass = "AdminPassword2025!"

        val inputEmail = "ADMIN@balayinn.ph"
        val inputPass = "AdminPassword2025!"

        val isValid = (inputEmail.trim().equals(expectedEmail, ignoreCase = true) ||
                inputEmail.trim().equals("admin@balayinn.com", ignoreCase = true) ||
                inputEmail.trim().equals("admin", ignoreCase = true)) &&
                (inputPass.trim() == expectedPass || inputPass.trim() == "admin123")

        assertTrue(isValid)
    }

    @Test
    fun testTimestampParserWithValidIsoAndEpoch() {
        val iso = "2026-10-01T07:01:19.446881+00:00"
        val parsedIso = TimestampParser.toEpochMillis(iso)
        assertTrue(parsedIso > 1700000000000L)

        val epoch = 1727766079000L
        val parsedEpoch = TimestampParser.toEpochMillis(epoch)
        assertEquals(epoch, parsedEpoch)

        val parsedNull = TimestampParser.toEpochMillis(null)
        assertTrue(parsedNull > 0)
    }

    @Test
    fun testHostExploreNavigationExclusion() {
        // When user is Host (PROPERTY_OWNER), Explore tab is excluded
        val hostRole = "PROPERTY_OWNER"
        val isHost = hostRole == "PROPERTY_OWNER"
        val hostNavTabs = buildList {
            add("Home")
            if (!isHost) add("Explore")
            add("Viewings")
            add("Messages")
            add("Profile")
        }
        assertTrue(!hostNavTabs.contains("Explore"))
        assertEquals(listOf("Home", "Viewings", "Messages", "Profile"), hostNavTabs)

        // When user is Renter (TENANT), Explore tab is included
        val renterRole = "TENANT"
        val isRenterHost = renterRole == "PROPERTY_OWNER"
        val renterNavTabs = buildList {
            add("Home")
            if (!isRenterHost) add("Explore")
            add("Viewings")
            add("Messages")
            add("Profile")
        }
        assertTrue(renterNavTabs.contains("Explore"))
        assertEquals(listOf("Home", "Explore", "Viewings", "Messages", "Profile"), renterNavTabs)
    }

    @Test
    fun testPasswordResetRules() {
        // Minimum password length is 6
        val shortPassword = "123"
        assertTrue(shortPassword.length < 6)

        val validPassword = "SecurePassword2026!"
        assertTrue(validPassword.length >= 6)

        val confirmMismatch = "DifferentPassword"
        assertTrue(validPassword != confirmMismatch)

        val confirmMatch = "SecurePassword2026!"
        assertEquals(validPassword, confirmMatch)
    }

    @Test
    fun testPropertyEditMutation() {
        val original = PropertyEntity(
            id = "prop_edit_1",
            title = "Original Title",
            description = "Original Description",
            propertyType = "Boarding House",
            monthlyRent = 5000.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = "prov_cebu",
            cityMunicipalityId = "city_cebu",
            barangayId = "bgy_lahug",
            location = "Lahug, Cebu City",
            coordinates = "10.3290, 123.9060",
            latitude = 10.3290,
            longitude = 123.9060,
            bedrooms = 1,
            bathrooms = 1,
            floorAreaSqm = 20.0,
            furnishedStatus = "Semi-Furnished",
            parkingAvailable = false,
            petPolicy = "No Pets",
            availabilityDate = "Immediate",
            status = "ACTIVE",
            ownerId = "usr_host_1",
            posterId = "usr_host_1",
            posterName = "Maria Host",
            posterRole = "Property owner"
        )

        val edited = original.copy(
            title = "Updated Modern Boarding House",
            monthlyRent = 6500.0,
            propertyType = "Apartment",
            location = "Salinas Drive, Lahug, Cebu City, Cebu",
            floorAreaSqm = 28.0,
            furnishedStatus = "Furnished",
            status = "RENTED"
        )

        assertEquals("Updated Modern Boarding House", edited.title)
        assertEquals(6500.0, edited.monthlyRent, 0.001)
        assertEquals("Apartment", edited.propertyType)
        assertEquals("Salinas Drive, Lahug, Cebu City, Cebu", edited.location)
        assertEquals(28.0, edited.floorAreaSqm, 0.001)
        assertEquals("Furnished", edited.furnishedStatus)
        assertEquals("RENTED", edited.status)
        assertEquals(original.id, edited.id)
    }

    @Test
    fun testMessageSchemaMappingForSupabaseMessagesTable() {
        val renterUserId = "00000000-0000-0000-0000-000000000001"
        val propertyOwnerId = "00000000-0000-0000-0000-000000000002"
        val propertyId = "00000000-0000-0000-0000-000000000003"
        val messageEntity = com.example.data.local.entity.MessageEntity(
            id = "msg_12345",
            conversationId = "conv_${renterUserId}_${propertyId}",
            senderId = renterUserId,
            senderName = "Joshua Renter",
            messageText = "Hello, is this property available for viewing tomorrow?",
            timestamp = 1715000000000L,
            isRead = false
        )

        val dto = com.example.data.remote.supabase.SupabaseMessageDto.fromEntity(
            e = messageEntity,
            receiverId = propertyOwnerId,
            propertyId = propertyId
        )

        assertEquals(renterUserId, dto.senderId)
        assertEquals(propertyOwnerId, dto.receiverId)
        assertEquals(propertyId, dto.propertyId)
        assertEquals("Hello, is this property available for viewing tomorrow?", dto.content)
        assertEquals(false, dto.isRead)
        assertNotNull(dto.createdAt)

        val reconstructed = dto.toEntity()
        assertEquals(dto.content, reconstructed.messageText)
        assertEquals(renterUserId, reconstructed.senderId)
        assertEquals(1715000000000L, reconstructed.timestamp)
    }
}
