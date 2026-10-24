package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Balay-Inn", appName)
  }

  @Test
  fun `test dark mode toggle state`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.MarketplaceViewModel(app)
    val initial = vm.isDarkMode.value
    vm.toggleDarkMode()
    assertEquals(!initial, vm.isDarkMode.value)
    vm.setDarkMode(initial)
    assertEquals(initial, vm.isDarkMode.value)
  }

  @Test
  fun `test database initialization and seeding`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.MarketplaceViewModel(app)
    kotlinx.coroutines.runBlocking {
      vm.repository.initializeDatabaseIfEmpty()
    }
    org.junit.Assert.assertNotNull(vm.currentUser.value)
    org.junit.Assert.assertNotNull(vm.allUsers.value)
  }

  @Test
  fun `test timestamp parser with ISO string and epoch number`() {
    val iso = "2026-10-01T07:01:19.446881+00:00"
    val parsed = com.example.data.remote.supabase.TimestampParser.toEpochMillis(iso)
    org.junit.Assert.assertTrue(parsed > 1700000000000L)

    val epoch = 1727766079000L
    val parsedEpoch = com.example.data.remote.supabase.TimestampParser.toEpochMillis(epoch)
    assertEquals(epoch, parsedEpoch)

    val nullParsed = com.example.data.remote.supabase.TimestampParser.toEpochMillis(null)
    org.junit.Assert.assertTrue(nullParsed > 0)
  }

  @Test
  fun `test supabase auth user entity conversion`() {
    val authUser = com.example.data.remote.supabase.SupabaseAuthUser(
      id = "usr_supa_123",
      email = "maria.santos@gmail.com",
      phone = "+63 917 555 1234",
      userMetadata = mapOf(
        "full_name" to "Maria Santos",
        "role" to "PROPERTY_OWNER",
        "phone" to "+63 917 555 1234"
      )
    )
    val entity = authUser.toUserEntity()
    assertEquals("usr_supa_123", entity.id)
    assertEquals("Maria Santos", entity.fullName)
    assertEquals("maria.santos@gmail.com", entity.email)
    assertEquals("PROPERTY_OWNER", entity.role)
  }

  @Test
  fun `test listed by filter state and updates`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.MarketplaceViewModel(app)
    assertEquals("All", vm.filterState.value.listedBy)
    assertEquals(false, vm.filterState.value.authorizedPosterOnly)

    vm.updateListedBy("Direct Owner")
    assertEquals("Direct Owner", vm.filterState.value.listedBy)

    vm.updateListedBy("Licensed Broker")
    assertEquals("Licensed Broker", vm.filterState.value.listedBy)

    vm.toggleAuthorizedPosterOnly()
    assertEquals(true, vm.filterState.value.authorizedPosterOnly)

    vm.resetFilters()
    assertEquals("All", vm.filterState.value.listedBy)
    assertEquals(false, vm.filterState.value.authorizedPosterOnly)
  }

  @Test
  fun `test viewModel logout`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.MarketplaceViewModel(app)
    vm.logout()
    org.junit.Assert.assertNull(vm.supabaseAuthUser.value)
    org.junit.Assert.assertNotNull(vm.currentUser.value)
  }

  @Test
  fun `test bacolod and its cities in seed data`() {
    val bacolodCities = com.example.data.seed.MarketplaceDataSeed.bacolodCities
    org.junit.Assert.assertTrue(bacolodCities.size >= 14)
    val cityNames = bacolodCities.map { it.name }
    org.junit.Assert.assertTrue(cityNames.contains("Bacolod City"))
    org.junit.Assert.assertTrue(cityNames.contains("Talisay City"))
    org.junit.Assert.assertTrue(cityNames.contains("Silay City"))
    org.junit.Assert.assertTrue(cityNames.contains("Bago City"))
    org.junit.Assert.assertTrue(cityNames.contains("Victorias City"))
    org.junit.Assert.assertTrue(cityNames.contains("Kabankalan City"))
  }

  @Test
  fun `test commute route guide destinations and quick badge`() {
    val bcdProp = com.example.data.seed.PropertyDataSeed.sampleProperties.first { it.id == "prop_bcd_01" }
    val destinations = com.example.data.model.CommuteGuideProvider.getCommuteDestinations(bcdProp)
    org.junit.Assert.assertTrue(destinations.isNotEmpty())
    val nearest = destinations.first()
    org.junit.Assert.assertTrue(nearest.distanceMeters > 0)
    org.junit.Assert.assertTrue(nearest.walkingTimeMinutes > 0)
    org.junit.Assert.assertTrue(nearest.transitRouteName.isNotBlank())

    val grouped = com.example.data.model.CommuteGuideProvider.getGroupedDestinations(bcdProp)
    org.junit.Assert.assertTrue(grouped.containsKey(com.example.data.model.DestinationGroup.TRANSIT))
    org.junit.Assert.assertTrue(grouped.containsKey(com.example.data.model.DestinationGroup.ESSENTIALS))
    org.junit.Assert.assertTrue(grouped.containsKey(com.example.data.model.DestinationGroup.UTILITY))

    val transitList = grouped[com.example.data.model.DestinationGroup.TRANSIT]!!
    org.junit.Assert.assertTrue(transitList.isNotEmpty())
    org.junit.Assert.assertTrue(transitList.first().subcategory.isNotBlank())

    val subcatMap = com.example.data.model.CommuteGuideProvider.getDestinationsBySubcategory(bcdProp, com.example.data.model.DestinationGroup.TRANSIT)
    org.junit.Assert.assertTrue(subcatMap.isNotEmpty())
    subcatMap.values.forEach { places ->
      org.junit.Assert.assertTrue("Subcategory places should be at most 3", places.size <= 3)
      for (i in 0 until places.size - 1) {
        org.junit.Assert.assertTrue("Places should be sorted by minimum distance", places[i].distanceMeters <= places[i + 1].distanceMeters)
      }
    }
    org.junit.Assert.assertTrue(transitList.first().formattedDistanceAndTime.contains("|"))

    val badge = com.example.data.model.CommuteGuideProvider.getQuickCommuteBadge(bcdProp)
    org.junit.Assert.assertTrue(badge.isNotBlank())
  }

  @Test
  fun `test 2 demo properties per property type and 10 total properties`() {
    val sample = com.example.data.seed.PropertyDataSeed.sampleProperties
    assertEquals(10, sample.size)

    val boardingHouses = sample.filter { it.propertyType.contains("Boarding", ignoreCase = true) }
    val studios = sample.filter { it.propertyType.equals("Studio", ignoreCase = true) }
    val condos = sample.filter { it.propertyType.contains("Condo", ignoreCase = true) }
    val apartments = sample.filter { it.propertyType.equals("Apartment", ignoreCase = true) }
    val townhouses = sample.filter { it.propertyType.equals("Townhouse", ignoreCase = true) }

    assertEquals(2, boardingHouses.size)
    assertEquals(2, studios.size)
    assertEquals(2, condos.size)
    assertEquals(2, apartments.size)
    assertEquals(2, townhouses.size)

    // Verify all 10 have images in sampleImages
    val imagePropIds = com.example.data.seed.PropertyDataSeed.sampleImages.map { it.propertyId }.toSet()
    sample.forEach { prop ->
      org.junit.Assert.assertTrue("Property ${prop.id} must have images", imagePropIds.contains(prop.id))
    }
  }

  @Test
  fun `launch MainActivity`() {
    val scenario = androidx.test.core.app.ActivityScenario.launch(MainActivity::class.java)
    org.junit.Assert.assertNotNull(scenario)
    scenario.onActivity { activity ->
      org.junit.Assert.assertNotNull(activity)
    }
    scenario.close()
  }
}
