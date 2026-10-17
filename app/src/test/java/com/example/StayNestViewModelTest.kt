package com.example

import com.example.model.BoardingHouse
import com.example.model.GenderPolicy
import com.example.model.LandlordInfo
import com.example.model.RoomType
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StayNestViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StayNestViewModelTest {

    private lateinit var viewModel: StayNestViewModel

    @Before
    fun setup() {
        viewModel = StayNestViewModel()
    }

    @Test
    fun initialState_hasListingsAndExploreScreen() {
        assertEquals(AppScreen.EXPLORE, viewModel.currentScreen.value)
        assertTrue(viewModel.allListings.value.isNotEmpty())
        assertTrue(viewModel.filteredListings.value.isNotEmpty())
    }

    @Test
    fun filterByRoomType_updatesFilterState() {
        viewModel.setRoomType(RoomType.STUDIO)
        assertEquals(RoomType.STUDIO, viewModel.filterState.value.roomType)

        // Toggle again clears it
        viewModel.setRoomType(RoomType.STUDIO)
        assertEquals(null, viewModel.filterState.value.roomType)
    }

    @Test
    fun toggleSaveProperty_addsAndRemovesFromSaved() {
        val testId = "bh-2"
        assertFalse(viewModel.savedIds.value.contains(testId))

        viewModel.toggleSaveProperty(testId)
        assertTrue(viewModel.savedIds.value.contains(testId))

        viewModel.toggleSaveProperty(testId)
        assertFalse(viewModel.savedIds.value.contains(testId))
    }

    @Test
    fun submitInquiry_addsToInquiriesList() {
        val countBefore = viewModel.inquiries.value.size
        val result = viewModel.submitInquiry(
            propertyId = "bh-1",
            name = "Jordan Lee",
            phone = "+1 555-0011",
            targetDate = "Dec 1",
            message = "Is deposit refundable?"
        )
        assertTrue(result)
        assertEquals(countBefore + 1, viewModel.inquiries.value.size)
        assertEquals("Jordan Lee", viewModel.inquiries.value.first().tenantName)
    }

    @Test
    fun scheduleViewing_addsToViewingTours() {
        val countBefore = viewModel.viewingTours.value.size
        val result = viewModel.scheduleViewing(
            propertyId = "bh-1",
            date = "Sunday 10 AM",
            timeSlot = "Morning",
            contactPhone = "+1 555-0011"
        )
        assertTrue(result)
        assertEquals(countBefore + 1, viewModel.viewingTours.value.size)
    }

    @Test
    fun addNewListing_prependsToListingsAndNavigatesToExplore() {
        val countBefore = viewModel.allListings.value.size
        val newHouse = BoardingHouse(
            id = "test-new",
            title = "Maple House",
            description = "Quiet place",
            roomType = RoomType.SINGLE,
            monthlyRent = 130,
            deposit = 130,
            address = "12 Maple St",
            city = "Campus Town",
            proximityText = "100m to gate",
            rating = 5.0f,
            reviewCount = 0,
            genderPolicy = GenderPolicy.ALL,
            availableRooms = 1,
            totalRooms = 5,
            curfewText = "No Curfew",
            utilitiesIncluded = listOf("Wi-Fi"),
            amenities = listOf("Desk"),
            houseRules = listOf("No loud music"),
            landlord = LandlordInfo("Mrs. Maple", "+1 555-9999")
        )

        viewModel.addNewListing(newHouse)
        assertEquals(countBefore + 1, viewModel.allListings.value.size)
        assertEquals(AppScreen.EXPLORE, viewModel.currentScreen.value)
    }
}
