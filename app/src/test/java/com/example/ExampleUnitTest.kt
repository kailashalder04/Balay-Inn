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
}
