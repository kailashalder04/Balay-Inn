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
}
