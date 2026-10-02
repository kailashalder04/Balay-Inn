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
  fun `launch MainActivity`() {
    val scenario = androidx.test.core.app.ActivityScenario.launch(MainActivity::class.java)
    org.junit.Assert.assertNotNull(scenario)
    scenario.onActivity { activity ->
      org.junit.Assert.assertNotNull(activity)
    }
    scenario.close()
  }
}
