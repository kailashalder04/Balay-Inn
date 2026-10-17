package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.BoardingHouse
import com.example.model.GenderPolicy
import com.example.model.LandlordInfo
import com.example.model.RoomType
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddListingScreen(
    onBack: () -> Unit,
    onSubmitListing: (BoardingHouse) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var roomType by remember { mutableStateOf(RoomType.SINGLE) }
    var monthlyRent by remember { mutableStateOf("120") }
    var deposit by remember { mutableStateOf("120") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Metro Academic Hub") }
    var proximityText by remember { mutableStateOf("200m to University Main Gate") }
    var genderPolicy by remember { mutableStateOf(GenderPolicy.ALL) }
    var curfewText by remember { mutableStateOf("10:00 PM Curfew") }
    var availableRooms by remember { mutableStateOf("2") }
    var totalRooms by remember { mutableStateOf("10") }
    var landlordName by remember { mutableStateOf("Grace Tan") }
    var landlordPhone by remember { mutableStateOf("+1 (555) 432-8910") }

    val commonUtilities = listOf("Fiber Wi-Fi", "Water", "Electricity", "Trash Collection", "Cooking Gas")
    val selectedUtilities = remember { mutableStateListOf("Fiber Wi-Fi", "Water") }

    val commonAmenities = listOf("Study Desk", "Air Conditioning", "Ceiling Fan", "CCTV Security", "Shared Kitchen", "En-suite Bathroom", "Balcony")
    val selectedAmenities = remember { mutableStateListOf("Study Desk", "Ceiling Fan", "Shared Kitchen") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List Boarding House / Room", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("add_listing_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .testTag("add_listing_scroll_view")
        ) {
            Text(
                text = "Property Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Listing Title *") },
                placeholder = { Text("e.g. St. Jude Student Boarding House") },
                modifier = Modifier.fillMaxWidth().testTag("add_title_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description *") },
                placeholder = { Text("Describe room features, study environment, nearby shops...") },
                modifier = Modifier.fillMaxWidth().testTag("add_desc_input"),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Room Type Selector
            Text("Room Type", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RoomType.entries.forEach { type ->
                    FilterChip(
                        selected = roomType == type,
                        onClick = { roomType = type },
                        label = { Text(type.displayName) },
                        leadingIcon = if (roomType == type) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rent & Deposit
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = monthlyRent,
                    onValueChange = { monthlyRent = it },
                    label = { Text("Monthly Rent ($) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("add_rent_input")
                )
                OutlinedTextField(
                    value = deposit,
                    onValueChange = { deposit = it },
                    label = { Text("Security Deposit ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("add_deposit_input")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Location & Proximity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Complete Street Address *") },
                placeholder = { Text("e.g. 88 College Lane, Gate 3") },
                modifier = Modifier.fillMaxWidth().testTag("add_address_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City / District") },
                    modifier = Modifier.weight(1f).testTag("add_city_input")
                )
                OutlinedTextField(
                    value = proximityText,
                    onValueChange = { proximityText = it },
                    label = { Text("Near Campus / Landmark") },
                    modifier = Modifier.weight(1.2f).testTag("add_proximity_input")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "House Policies & Rules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Gender Policy
            Text("Gender Policy", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GenderPolicy.entries.forEach { policy ->
                    FilterChip(
                        selected = genderPolicy == policy,
                        onClick = { genderPolicy = policy },
                        label = { Text(policy.displayName) },
                        leadingIcon = if (genderPolicy == policy) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = curfewText,
                onValueChange = { curfewText = it },
                label = { Text("Curfew Rule") },
                placeholder = { Text("e.g. 10:30 PM Curfew or No Curfew") },
                modifier = Modifier.fillMaxWidth().testTag("add_curfew_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = availableRooms,
                    onValueChange = { availableRooms = it },
                    label = { Text("Vacant Rooms") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = totalRooms,
                    onValueChange = { totalRooms = it },
                    label = { Text("Total Rooms") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Utilities Included
            Text("Bills Included in Rent", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                commonUtilities.forEach { util ->
                    val isChecked = selectedUtilities.contains(util)
                    FilterChip(
                        selected = isChecked,
                        onClick = {
                            if (isChecked) selectedUtilities.remove(util) else selectedUtilities.add(util)
                        },
                        label = { Text(util) },
                        leadingIcon = if (isChecked) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amenities
            Text("Amenities & Furnishings", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                commonAmenities.forEach { amenity ->
                    val isChecked = selectedAmenities.contains(amenity)
                    FilterChip(
                        selected = isChecked,
                        onClick = {
                            if (isChecked) selectedAmenities.remove(amenity) else selectedAmenities.add(amenity)
                        },
                        label = { Text(amenity) },
                        leadingIcon = if (isChecked) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // Landlord Info
            Text(
                text = "Host Contact Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = landlordName,
                onValueChange = { landlordName = it },
                label = { Text("Landlord / Caretaker Name *") },
                modifier = Modifier.fillMaxWidth().testTag("add_landlord_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = landlordPhone,
                onValueChange = { landlordPhone = it },
                label = { Text("Contact Phone *") },
                modifier = Modifier.fillMaxWidth().testTag("add_landlord_phone_input")
            )

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter a title for the boarding house"
                        return@Button
                    }
                    if (address.isBlank()) {
                        errorMessage = "Please enter the street address"
                        return@Button
                    }
                    val rentVal = monthlyRent.toIntOrNull() ?: 100
                    val depositVal = deposit.toIntOrNull() ?: rentVal
                    val vacVal = availableRooms.toIntOrNull() ?: 1
                    val totVal = totalRooms.toIntOrNull() ?: 10

                    val newHouse = BoardingHouse(
                        id = "bh-custom-${UUID.randomUUID().toString().take(6)}",
                        title = title.trim(),
                        description = description.ifBlank { "Clean, welcoming boarding accommodation with friendly tenants and responsible caretaker." },
                        roomType = roomType,
                        monthlyRent = rentVal,
                        deposit = depositVal,
                        address = address.trim(),
                        city = city.trim(),
                        proximityText = proximityText.trim(),
                        rating = 5.0f,
                        reviewCount = 1,
                        genderPolicy = genderPolicy,
                        availableRooms = vacVal,
                        totalRooms = totVal,
                        curfewText = curfewText.trim(),
                        utilitiesIncluded = selectedUtilities.toList(),
                        amenities = selectedAmenities.toList(),
                        houseRules = listOf("Respect fellow roommates", "Keep shared areas tidy", "Report maintenance immediately"),
                        landlord = LandlordInfo(
                            name = landlordName.trim(),
                            phone = landlordPhone.trim(),
                            isVerified = true,
                            responseTime = "Within 30 mins",
                            memberSince = "New Host"
                        )
                    )
                    onSubmitListing(newHouse)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("publish_listing_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Publish Room Listing",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
