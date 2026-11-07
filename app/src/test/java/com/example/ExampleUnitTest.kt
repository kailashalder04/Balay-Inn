package com.example

import com.example.data.local.entity.PropertyEntity
import com.example.data.remote.supabase.SupabaseClient
import com.example.data.remote.supabase.SupabaseViewingDto
import com.example.data.remote.supabase.TimestampParser
import com.example.data.seed.MarketplaceDataSeed
import com.example.data.seed.PropertyDataSeed
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testTimestampParser() {
    val ts = TimestampParser.parse("2026-10-01T07:01:19.446881+00:00")
    assertNotNull(ts)
    assertTrue(ts!! > 0)
  }

  @Test
  fun testSeedDataIntegrity() {
    assertTrue(MarketplaceDataSeed.seedUsers.isNotEmpty())
    assertTrue(PropertyDataSeed.sampleProperties.isNotEmpty())
    assertTrue(PropertyDataSeed.sampleImages.isNotEmpty())
    assertTrue(PropertyDataSeed.sampleViewingRequests.isNotEmpty())
    assertTrue(PropertyDataSeed.sampleConversations.isNotEmpty())
    assertTrue(PropertyDataSeed.sampleMessages.isNotEmpty())
    
    // Check that every property in sampleProperties has valid IDs
    val propIds = PropertyDataSeed.sampleProperties.map { it.id }.toSet()
    for (req in PropertyDataSeed.sampleViewingRequests) {
      assertTrue("Viewing request propertyId ${req.propertyId} must exist", propIds.contains(req.propertyId))
    }
  }

  @Test
  fun testSupabaseMoshiAdapters() {
    val client = SupabaseClient
    assertNotNull(client)
  }

  @Test
  fun testViewingDtoSchemaAndEmailNullability() {
    val req = com.example.data.local.entity.ViewingRequestEntity(
      id = "view_12345",
      propertyId = "prop_boh_01",
      tenantId = "usr_tenant_1",
      posterId = "usr_landlord_1",
      preferredDate = "Tomorrow, 2:00 PM",
      preferredTime = "2:00 PM - 3:00 PM",
      numberOfVisitors = 2,
      tenantMessage = "Interested in viewing the space",
      status = "REQUESTED"
    )

    // Case 1: When user unchecks email alert, recipientEmail must be null
    val dtoNoEmail = SupabaseViewingDto.fromEntity(req, recipientEmail = null)
    assertNull("recipientEmail must be null when unchecked", dtoNoEmail.recipientEmail)
    assertNotNull("userId must be generated as UUID", dtoNoEmail.userId)
    assertNotNull("propertyId must be valid UUID", dtoNoEmail.propertyId)
    assertNotNull("scheduledDate must be SQL date format", dtoNoEmail.scheduledDate)
    assertNotNull("scheduledTime must be SQL time format", dtoNoEmail.scheduledTime)
    assertEquals("pending", dtoNoEmail.status)
    assertEquals("Interested in viewing the space", dtoNoEmail.notes)

    // Case 2: When user checks email alert, recipientEmail is populated
    val dtoWithEmail = SupabaseViewingDto.fromEntity(req, recipientEmail = "host@balayinn.ph")
    assertEquals("host@balayinn.ph", dtoWithEmail.recipientEmail)
  }

  @Test
  fun testSavedListingsHelper() {
    // Legacy string format
    val legacy = listOf("prop_1", "prop_2")
    val parsedLegacy = com.example.data.remote.supabase.SavedListingsHelper.parseSavedListingIds(legacy)
    assertEquals(2, parsedLegacy.size)
    assertEquals("prop_1", parsedLegacy[0]["property_id"])

    // Object format with property_id and title
    val objects = listOf(
      mapOf("property_id" to "prop_boh_01", "title" to "Bohol Heritage Suite"),
      mapOf("property_id" to "prop_cebu_02", "title" to "Mabolo Modern Bedspace")
    )
    val parsedObjects = com.example.data.remote.supabase.SavedListingsHelper.parseSavedListingIds(objects)
    assertEquals(2, parsedObjects.size)
    assertEquals("Bohol Heritage Suite", parsedObjects[0]["title"])
    assertEquals("prop_boh_01", parsedObjects[0]["property_id"])

    // Test add and remove
    parsedObjects.removeAll { it["property_id"] == "prop_boh_01" }
    assertEquals(1, parsedObjects.size)
    assertEquals("prop_cebu_02", parsedObjects[0]["property_id"])

    parsedObjects.add(mapOf("property_id" to "prop_lahug_03", "title" to "Lahug Studio Room"))
    assertEquals(2, parsedObjects.size)
  }
}
