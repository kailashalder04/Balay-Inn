package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleData
import com.example.model.BoardingHouse
import com.example.model.GenderPolicy
import com.example.model.Inquiry
import com.example.model.RoomType
import com.example.model.ViewingTour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppScreen {
    EXPLORE,
    DETAIL,
    SAVED,
    INQUIRIES,
    ADD_LISTING
}

data class FilterState(
    val searchQuery: String = "",
    val roomType: RoomType? = null,
    val genderPolicy: GenderPolicy? = null,
    val maxRent: Int = 350,
    val onlyVerified: Boolean = false,
    val onlyWifi: Boolean = false,
    val onlyNoCurfew: Boolean = false
)

class StayNestViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.EXPLORE)
    val currentScreen: StateFlow<AppScreen> = _currentScreen

    private val _selectedProperty = MutableStateFlow<BoardingHouse?>(null)
    val selectedProperty: StateFlow<BoardingHouse?> = _selectedProperty

    private val _allListings = MutableStateFlow(SampleData.initialListings)
    val allListings: StateFlow<List<BoardingHouse>> = _allListings

    private val _savedIds = MutableStateFlow<Set<String>>(setOf("bh-1", "bh-3"))
    val savedIds: StateFlow<Set<String>> = _savedIds

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    private val _inquiries = MutableStateFlow<List<Inquiry>>(
        listOf(
            Inquiry(
                id = "inq-1",
                propertyId = "bh-1",
                propertyTitle = "Pinecrest Scholar Boarding House",
                tenantName = "Alex Rivera",
                tenantPhone = "+1 (555) 019-2831",
                targetDate = "Nov 1, 2026",
                message = "Hello, is the 2nd floor corner room still vacant? Can I bring a desktop PC?",
                status = "Landlord Replied: Yes, power outlet provided!",
                submittedAt = "Yesterday"
            )
        )
    )
    val inquiries: StateFlow<List<Inquiry>> = _inquiries

    private val _viewingTours = MutableStateFlow<List<ViewingTour>>(
        listOf(
            ViewingTour(
                id = "tour-1",
                propertyId = "bh-2",
                propertyTitle = "Aurora Ladies Residence & Bedspace",
                date = "Tomorrow, 3:00 PM",
                timeSlot = "Afternoon Slot",
                contactPhone = "+1 (555) 019-2831",
                status = "Confirmed with Caretaker"
            )
        )
    )
    val viewingTours: StateFlow<List<ViewingTour>> = _viewingTours

    // Combine filters and listings to produce filtered list
    val filteredListings: StateFlow<List<BoardingHouse>> = combine(
        _allListings,
        _filterState,
        _savedIds
    ) { listings, filters, saved ->
        listings.filter { item ->
            val matchesSearch = filters.searchQuery.isBlank() ||
                    item.title.contains(filters.searchQuery, ignoreCase = true) ||
                    item.address.contains(filters.searchQuery, ignoreCase = true) ||
                    item.proximityText.contains(filters.searchQuery, ignoreCase = true) ||
                    item.city.contains(filters.searchQuery, ignoreCase = true)

            val matchesRoomType = filters.roomType == null || item.roomType == filters.roomType
            val matchesGender = filters.genderPolicy == null || item.genderPolicy == filters.genderPolicy
            val matchesRent = item.monthlyRent <= filters.maxRent
            val matchesVerified = !filters.onlyVerified || item.landlord.isVerified
            val matchesWifi = !filters.onlyWifi || item.utilitiesIncluded.any { it.contains("Wi-Fi", ignoreCase = true) }
            val matchesCurfew = !filters.onlyNoCurfew || item.curfewText.contains("No Curfew", ignoreCase = true)

            matchesSearch && matchesRoomType && matchesGender && matchesRent && matchesVerified && matchesWifi && matchesCurfew
        }.map { item ->
            item.copy(isSaved = saved.contains(item.id))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SampleData.initialListings)

    val savedListings: StateFlow<List<BoardingHouse>> = combine(
        _allListings,
        _savedIds
    ) { listings, saved ->
        listings.filter { saved.contains(it.id) }.map { it.copy(isSaved = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectProperty(property: BoardingHouse) {
        _selectedProperty.value = property.copy(isSaved = _savedIds.value.contains(property.id))
        _currentScreen.value = AppScreen.DETAIL
    }

    fun backToExplore() {
        _selectedProperty.value = null
        _currentScreen.value = AppScreen.EXPLORE
    }

    fun toggleSaveProperty(propertyId: String) {
        _savedIds.update { current ->
            if (current.contains(propertyId)) current - propertyId else current + propertyId
        }
        _selectedProperty.value?.let { current ->
            if (current.id == propertyId) {
                _selectedProperty.value = current.copy(isSaved = _savedIds.value.contains(propertyId))
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }

    fun setRoomType(type: RoomType?) {
        _filterState.update { it.copy(roomType = if (it.roomType == type) null else type) }
    }

    fun setGenderPolicy(policy: GenderPolicy?) {
        _filterState.update { it.copy(genderPolicy = if (it.genderPolicy == policy) null else policy) }
    }

    fun setMaxRent(max: Int) {
        _filterState.update { it.copy(maxRent = max) }
    }

    fun toggleOnlyVerified() {
        _filterState.update { it.copy(onlyVerified = !it.onlyVerified) }
    }

    fun toggleOnlyWifi() {
        _filterState.update { it.copy(onlyWifi = !it.onlyWifi) }
    }

    fun toggleOnlyNoCurfew() {
        _filterState.update { it.copy(onlyNoCurfew = !it.onlyNoCurfew) }
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun submitInquiry(propertyId: String, name: String, phone: String, targetDate: String, message: String): Boolean {
        val property = _allListings.value.find { it.id == propertyId } ?: return false
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        val newInquiry = Inquiry(
            id = "inq-${UUID.randomUUID().toString().take(6)}",
            propertyId = propertyId,
            propertyTitle = property.title,
            tenantName = name,
            tenantPhone = phone,
            targetDate = targetDate.ifBlank { "Flexible" },
            message = message,
            status = "Sent to ${property.landlord.name}",
            submittedAt = todayStr
        )
        _inquiries.update { listOf(newInquiry) + it }
        return true
    }

    fun scheduleViewing(propertyId: String, date: String, timeSlot: String, contactPhone: String): Boolean {
        val property = _allListings.value.find { it.id == propertyId } ?: return false
        val newTour = ViewingTour(
            id = "tour-${UUID.randomUUID().toString().take(6)}",
            propertyId = propertyId,
            propertyTitle = property.title,
            date = date,
            timeSlot = timeSlot,
            contactPhone = contactPhone,
            status = "Scheduled with ${property.landlord.name}"
        )
        _viewingTours.update { listOf(newTour) + it }
        return true
    }

    fun addNewListing(newHouse: BoardingHouse) {
        _allListings.update { listOf(newHouse) + it }
        _currentScreen.value = AppScreen.EXPLORE
    }
}
